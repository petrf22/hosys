package hosys.pfservis.cz.hosys;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by petr on 4.10.16.
 */
public class HosysDownloader  extends AsyncTask<HosysPage, Integer, HosysHtmlProcesor> {
    private static final String LOG_TAG = "HosysDownloader";
    private HosysHtmlText hosysHtmlText;

    public HosysDownloader (HosysHtmlText hosysHtmlText)
    {
        this.hosysHtmlText = hosysHtmlText;
    }

    @Override
    protected HosysHtmlProcesor doInBackground(HosysPage... hosysPages) {
        Validate.notEmpty(hosysPages);

        if (hosysPages.length != 1) {
            throw new IllegalArgumentException("Musí být zadán jen jeden objekt HosysPage.");
        }

        HosysPage hosysPage = hosysPages[0];
        HosysHtmlProcesor hosysHtmlProcesor = new HosysHtmlProcesor(hosysPage);

        try {
            String cookie = getCookie(hosysPage);
            String htmlData = getHtmlData(cookie, hosysPage);

            hosysHtmlProcesor.setHtmlData(htmlData);
        } catch (IOException e) {
            hosysHtmlProcesor.setException(e);

            if (e instanceof MalformedURLException) {
                Log.e(LOG_TAG, "Chybná URL adresa.", e);
            } else {
                Log.e(LOG_TAG, "Nepodařilo se načíst stránku.", e);
            }
        } catch (NotSetCookieException e) {
            hosysHtmlProcesor.setException(e);

            Log.e(LOG_TAG, "Nepodařilo se nsatvit cookie.", e);
        }

        return hosysHtmlProcesor;
    }

    String postUrl = HosysConfig.DATA_PAGE_URL;


    private String getHtmlData(String cookie, HosysPage hosysPage) throws IOException {
        Validate.notEmpty(cookie);

        HttpURLConnection conn = prepareConnection(HosysConfig.DATA_PAGE_URL);
        conn.addRequestProperty("Cookie", cookie);
        conn.setRequestMethod("POST");
        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        String postData = HosysPageHelper.getPostData(hosysPage);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postData);
        wr.flush ();
        wr.close ();

        conn.connect();

        // conn.getResponseCode());

        InputStream is = conn.getInputStream();

        if ("gzip".equals(conn.getContentEncoding())) {
            is = new GZIPInputStream(is);
        }

        return IOUtils.toString(is, "UTF-8");
    }

    private String getCookie(HosysPage hosysPage) throws IOException, NotSetCookieException {
        String urlPage = HosysConfig.SERVER + hosysPage.getPage();
        HttpURLConnection conn = prepareConnection(urlPage);

        conn.setRequestMethod("HEAD");

        conn.connect();

        Map<String, List<String>> headerFields = conn.getHeaderFields();


        if (headerFields != null) {
            List<String> cookies = headerFields.get("Set-Cookie");

            if (cookies != null) {
                for (String cookie: cookies) {
                    return cookie;
                }
            }
        }


        throw new NotSetCookieException();
    }

    private HttpURLConnection prepareConnection(String urlPage) throws IOException {
        URL url = new URL(urlPage);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.setReadTimeout(15*1000); // give it 15 seconds to respond
        conn.setConnectTimeout(15*1000); // give it 15 seconds to connect
        conn.setUseCaches(false);

        return conn;
    }


    @Override
    protected void onPostExecute(HosysHtmlProcesor htmlText) {
        hosysHtmlText.processHtmlText(htmlText);
    }
}