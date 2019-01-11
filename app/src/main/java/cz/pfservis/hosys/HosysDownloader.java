package cz.pfservis.hosys;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;

import cz.pfservis.hosys.dto.HtmlDataDto;
import cz.pfservis.hosys.dto.RozpisDto;
import cz.pfservis.hosys.enums.HosysPage;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by petr on 4.10.16.
 */
public class HosysDownloader extends AsyncTask<HosysPage, Integer, HosysHtmlProcesor> {
    private static final String TAG = "HosysDownloader";
    private static final int CACHE_SIZE = 10 * 1024 * 1024; // 10 MiB

    private final HosysHtmlText hosysHtmlText;
    private final String soutezKey;
    private final Context context;
    private final OkHttpClient client;

    public HosysDownloader (HosysHtmlText hosysHtmlText, String soutezKey, Context context) {
        this.hosysHtmlText = hosysHtmlText;
        this.soutezKey = soutezKey;
        this.context = context;

        this.client = new OkHttpClient.Builder()
                .addInterceptor(new OkHttpLoggingInterceptor())
                .cache(new Cache(context.getCacheDir(), CACHE_SIZE))
                .connectionSpecs(Collections.singletonList(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).build()))
                .build();
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
            Object jsonData = getHtmlData(hosysPage);
            hosysHtmlProcesor.setJsonObject(jsonData);

            switch (hosysPage) {
//                case rozpis:
//                    // Zpracuje JSON objekt
//                    break;
                case rozpis:
                    String htmlDataRozpis = HosysPageHelper.createRozpisHtmlTable((RozpisDto[]) jsonData);
                    String cssDataRozpis = getCssFromInternet(hosysPage);

                    hosysHtmlProcesor.setHtml(htmlDataRozpis);
                    hosysHtmlProcesor.setCss(cssDataRozpis);
                    break;
                case tabulky:
                case soutez:
                    String htmlData = ((HtmlDataDto) jsonData).getHtml();
                    String cssData = getCssFromInternet(hosysPage);

                    hosysHtmlProcesor.setHtml(htmlData);
                    hosysHtmlProcesor.setCss(cssData);
                    break;
                case soutezNastaveni:
                    break;
                default:
                    throw new IllegalStateException("Nepodporovaný parametr " + hosysPage);
            }

        } catch (Exception e) {
            if (e instanceof MalformedURLException) {
                Log.e(TAG, "Chybná URL adresa.", e);
            } else {
                Log.e(TAG, "Nepodařilo se načíst stránku.", e);
            }

            hosysHtmlProcesor.setException(e);
        }

        return hosysHtmlProcesor;
    }

    private <T> T getHtmlData(HosysPage hosysPage) throws IOException {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Integer dnyZpet = pref.getInt("pref_rozpis_pocet_dnu_dozadu", 7) * -1;
        Integer dnyDopredu = pref.getInt("pref_rozpis_pocet_dnu_dopredu", 21);
        String apiUrl = String.format(hosysPage.getPage(), soutezKey);
        HttpUrl.Builder httpUrl = HttpUrl.parse(apiUrl).newBuilder();

        switch (hosysPage) {
            case rozpis:
                httpUrl.addQueryParameter("soutez", soutezKey)
                       .addQueryParameter("dayMin", String.valueOf(dnyZpet))
                       .addQueryParameter("dayMax", String.valueOf(dnyDopredu));
                break;
            case tabulky:
            case soutez:
                // "soutezKey" je součádtí API URL
                break;
            case soutezNastaveni:
                break;
            default:
                throw new IllegalStateException("Nepodporovaný parametr " + hosysPage);
        }


        Request request = new Request.Builder()
                .header("accept", "application/json")
                .url(httpUrl.build())
                .build();

        Log.d(TAG, "URL adresa: " + request.url().toString());

//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    throw new IOException("Unexpected code " + response);
//                } else {
//                    // do something wih the result
//                    String responeText = response.body().string();
//                    Log.d(TAG, "responeText: " + responeText);
//                }
//            }
//        });

        Response response = client.newCall(request).execute();

//        String responeText = response.body().string();
//        Log.d(TAG, "responeText: " + responeText);

        return hosysPage.fromJson(response.body().charStream());
    }

    private String getCssFromInternet(HosysPage hosysPage) {
        String cssText;

        try {
            Request request = new Request.Builder()
                    .url(hosysPage.getCssUrl())
                    .build();

            Response response = client.newCall(request).execute();

            cssText = response.body().string();

            DiskCache.writeCacheCssData(cssText, hosysPage, context);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            cssText = null;
        } catch (IOException e) {
            e.printStackTrace();
            cssText = null;
        }

        if (StringUtils.isBlank(cssText)) {
            cssText = DiskCache.readCacheCssData(hosysPage, context);
        }

        return cssText;
    }

    @Override
    protected void onPostExecute(HosysHtmlProcesor htmlText) {
        hosysHtmlText.processHtmlText(htmlText);
    }
}