package cz.pfservis.hosys;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import cz.pfservis.hosys.enums.HosysPage;
import cz.pfservis.hosys.enums.HosysPageHelper;
import cz.pfservis.hosys.exception.NotSetCookieException;

import static android.R.attr.x;

/**
 * Created by petr on 4.10.16.
 */
public class HosysDownloader extends AsyncTask<HosysPage, Integer, HosysHtmlProcesor> {
    private static final String TAG = "HosysDownloader";
    private HosysHtmlText hosysHtmlText;
    private String soutezKey;
    private Context context;

    public HosysDownloader (HosysHtmlText hosysHtmlText, String soutezKey, Context context) {
        this.hosysHtmlText = hosysHtmlText;
        this.soutezKey = soutezKey;
        this.context = context;
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
            String cssData = getCssFromInternet(hosysPage);

            hosysHtmlProcesor.setHtml(htmlData);
            hosysHtmlProcesor.setCss(cssData);
        } catch (IOException e) {
            hosysHtmlProcesor.setException(e);

            if (e instanceof MalformedURLException) {
                Log.e(TAG, "Chybná URL adresa.", e);
            } else {
                Log.e(TAG, "Nepodařilo se načíst stránku.", e);
            }
        } catch (NotSetCookieException e) {
            hosysHtmlProcesor.setException(e);

            Log.e(TAG, "Nepodařilo se nsatvit cookie.", e);
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

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Integer dnyZpet = pref.getInt("pref_rozpis_pocet_dnu_dozadu", 7);
        Integer dnyDopredu = pref.getInt("pref_rozpis_pocet_dnu_dopredu", 21);
        String postData = HosysPageHelper.getPostData(hosysPage, soutezKey, dnyZpet, dnyDopredu);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postData);
        wr.flush ();
        wr.close ();

        conn.connect();

        int response = conn.getResponseCode();

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "The response is: " + response);

            for (Map.Entry<String, List<String>> es : conn.getHeaderFields().entrySet()) {
                Log.d(TAG, "HTTP header: " + es.getKey() + "=" + es.getValue());
            }
        }

        InputStream is = null;
        String htmlText = null;

        try {
            is = conn.getInputStream();

            if ("gzip".equals(conn.getContentEncoding())) {
                is = new GZIPInputStream(is);
            }

            htmlText = IOUtils.toString(is, HosysConfig.UTF8);
        } finally {
            IOUtils.closeQuietly(is);
        }

        return htmlText;
    }

    private String getCookie(HosysPage hosysPage) throws IOException, NotSetCookieException {
        String urlPage = HosysConfig.SERVER + hosysPage.getPage();
        HttpURLConnection conn = prepareConnection(urlPage);

        conn.setRequestMethod("HEAD");

        conn.connect();

        int response = conn.getResponseCode();
        Log.d(TAG, "The response is: " + response);

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
        conn.setRequestProperty("Accept-Charset", HosysConfig.UTF8);
        conn.setReadTimeout(10*1000); // give it 15 seconds to respond
        conn.setConnectTimeout(15*1000); // give it 15 seconds to connect
        conn.setUseCaches(false);

        return conn;
    }

    private String getCssFromInternet(HosysPage hosysPage) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String etagKey = "hosysPage." + hosysPage.toString() + ".Etag";
        String etagValue = pref.getString(etagKey, null);
        Log.d(TAG, "etagValue (before): " + etagValue);

        String cssText = DiskCache.readCacheCssData(hosysPage, context);

        try {
            URL url = new URL(hosysPage.getCssUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("User-Agent", "Android.Hosys.pfservis.cz");
            conn.setRequestProperty("Host", "hosys.pfservis.cz");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Charset", HosysConfig.UTF8);

            if (etagValue != null && cssText != null) {
                conn.setRequestProperty("If-None-Match", etagValue);
            }

            conn.setRequestMethod("GET");
            conn.setReadTimeout(5*1000); // give it 15 seconds to respond
            conn.setConnectTimeout(5*1000); // give it 15 seconds to connect
            conn.setUseCaches(false);
            conn.connect();

            int response = conn.getResponseCode();
            Log.d(TAG, "response: " + response);


            if (response == 200) { // Not Modified
                etagValue = conn.getHeaderField("ETag");
                Log.d(TAG, "etagValue (after): " + etagValue);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString(etagKey, etagValue);
                editor.commit();

                InputStream is = null;

                try {
                    is = conn.getInputStream();

                    if ("gzip".equals(conn.getContentEncoding())) {
                        is = new GZIPInputStream(is);
                    }

                    cssText = IOUtils.toString(is, HosysConfig.UTF8);

                    DiskCache.writeCacheCssData(cssText, hosysPage, context);
                } finally {
                    IOUtils.closeQuietly(is);
                }
//            } else if (response == 304) { // Not Modified
//            } else { // asi chybka
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            cssText = null;
        } catch (IOException e) {
            e.printStackTrace();
            cssText = null;
        }

        return cssText == null ? DiskCache.readCacheCssData(hosysPage, context) : cssText;
    }

    @Override
    protected void onPostExecute(HosysHtmlProcesor htmlText) {
        hosysHtmlText.processHtmlText(htmlText);
    }
}