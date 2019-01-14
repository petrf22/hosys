package cz.pfservis.hosys.activities.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

import cz.pfservis.hosys.DiskCache;
import cz.pfservis.hosys.HosysConfig;
import cz.pfservis.hosys.HosysDownloader;
import cz.pfservis.hosys.HosysHtmlProcesor;
import cz.pfservis.hosys.HosysHtmlText;
import cz.pfservis.hosys.HosysPageHelper;
import cz.pfservis.hosys.enums.HosysPage;
import hosys.pfservis.cz.hosys.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class HtmlFragment extends Fragment implements HosysHtmlText, HosysFragment, OnRefreshListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_HOSYS_PAGE = "hosys_page";
    private SwipeRefreshLayout swipeWeb;
    private WebView webView;
    private String htmlData;
    private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            // listener implementation

            if (isAdded() && key.equals(getString(R.string.pref_soutez_value))) {
                clearHtmlData();
            }
        }
    };

    public HtmlFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HtmlFragment newInstance(HosysPage hosysPage) {
        HtmlFragment fragment = new HtmlFragment();
        Bundle args = new Bundle();

        args.putString(ARG_HOSYS_PAGE, hosysPage.toString());
        fragment.setArguments(args);

        return fragment;
    }

    private HosysPage getHosysPageArgument() {
        return HosysPage.valueOf(getArguments().getString(ARG_HOSYS_PAGE));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Not implemented here
                return false;
            case R.id.action_refresh:
                hosysRefres();

                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_html, container, false);
        String page = getArguments().getString(ARG_HOSYS_PAGE);

        swipeWeb = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeWeb);
        swipeWeb.setOnRefreshListener(this);
        webView = (WebView) rootView.findViewById(R.id.webView);

        // Enable Javascript
        WebSettings webSettings = webView.getSettings();
        //webSettings.setJavaScriptEnabled(true);
        //webSettings.setMinimumFontSize(80);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Force links and redirects to open in the WebView instead of in a browser
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(listener);

        boolean prefRefreshOnStart = prefs.getBoolean("pref_refresh_on_start", false);

        if (savedInstanceState != null) {
            htmlData = savedInstanceState.getString("htmlData");
        } else if (!prefRefreshOnStart) {
            HosysPage hosysPage = getHosysPageArgument();

            htmlData = DiskCache.readCacheHtmlData(hosysPage, getContext());
        }

        if (htmlData == null) {
            hosysRefres();
        } else {
            webViewLoadData();
        }

        return rootView;
    }

    public void hosysRefres() {
        Context context = getContext();

        swipeWeb.setRefreshing(true);

        if (htmlData == null) {
            HosysPage hosysPage = getHosysPageArgument();

            htmlData = getString(R.string.downloading_data_format, HosysConfig.SERVER + hosysPage.getPage());

            webViewLoadData();
        }


        HosysPage hosysPage = HosysPage.valueOf(getArguments().getString(ARG_HOSYS_PAGE));
        String soutezKey = context.getString(R.string.pref_soutez_value);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String soutez = pref.getString(soutezKey, "");
        HosysDownloader hd = new HosysDownloader(this, soutez, context);

        hd.execute(hosysPage);
    }

    public void clearHtmlData() {
        htmlData = null;
    }

    @Override
    public void processHtmlText(HosysHtmlProcesor hosysHtmlProcesor) {
        if (getActivity() == null) {
            // Padá příkaz:
            //    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
            //    ... getString(R.string.downloaded_data_info_format, pageName, dateTime)
            return;
        }

        if (hosysHtmlProcesor.getException() != null) {
            if (htmlData == null) {
                htmlData = getString(R.string.downloading_data_error_format, hosysHtmlProcesor.getException().getMessage());
            }

            String errorMsg = "Nepodařilo se načíst data ze serveru " + HosysConfig.SERVER +
                    " (" + hosysHtmlProcesor.getException().getMessage() + ")";

            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
        } else {
            Date date = new Date();
            String dateTime = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, HosysConfig.CZECH).format(date);

            htmlData = HosysPageHelper.buildWebViewPage(hosysHtmlProcesor);
            htmlData = htmlData.replace("</body", getString(R.string.downloaded_data_info_format, dateTime) + "</body");

            DiskCache.writeCacheHtmlData(htmlData, hosysHtmlProcesor.getHosysPage(), getContext());
        }

        webViewLoadData();

        swipeWeb.setRefreshing(false);
    }

    private void webViewLoadData() {
        // http://stackoverflow.com/questions/4096783/android-webview-1st-loaddata-works-fine-subsequent-calls-do-not-update-disp
        // http://stackoverflow.com/questions/8999797/webview-doesnt-load-my-html-the-second-time
        webView.loadUrl("about:blank"); // bug: až při druhém načtení zobrazí funkce loadData správně obsah
        webView.loadData(htmlData, getString(R.string.html_data_encoding), HosysConfig.UTF8);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("htmlData", htmlData);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed()) {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }

        if (htmlData == null && getView() != null) {
            hosysRefres();
        }
    }

    @Override
    public void onRefresh() {
        hosysRefres();
    }
}
