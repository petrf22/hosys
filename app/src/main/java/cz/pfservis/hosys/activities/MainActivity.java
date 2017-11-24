package cz.pfservis.hosys.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import cz.pfservis.hosys.enums.HosysPage;
import cz.pfservis.hosys.enums.HosysPageHelper;
import hosys.pfservis.cz.hosys.R;

import static android.support.v7.widget.AppCompatDrawableManager.get;
import static hosys.pfservis.cz.hosys.R.id.container;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private SharedPreferences.OnSharedPreferenceChangeListener listenerPrefs = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.equals(getString(R.string.pref_soutez_value))) {
                String soutezLabelKey = getString(R.string.pref_soutez_label);
                String stringLabel = prefs.getString(soutezLabelKey, getString(R.string.pref_soutez_label_default));
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

                toolbar.setSubtitle(stringLabel);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String soutezLabelKey = getString(R.string.pref_soutez_label);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(listenerPrefs);

        String stringLabel = prefs.getString(soutezLabelKey, getString(R.string.pref_soutez_label_default));

        toolbar.setSubtitle(stringLabel);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, AppCompatPreferenceActivity.SettingsActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.action_about) {
            AboutDialog about = new AboutDialog(this);
            about.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //about.setTitle(getString(R.string.action_about));
            about.show();
            return true;
        } else if (id == R.id.action_refresh) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            boolean prefRefreshAllPages = prefs.getBoolean("pref_refresh_all_pages", false);

            if (prefRefreshAllPages) {
                for (Fragment fragmentX : getSupportFragmentManager().getFragments()) {
                    PlaceholderFragment fragment = (PlaceholderFragment) fragmentX;

                    fragment.hosysRefres();
                }

                return true;
            } else {
                return false;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements HosysHtmlText, OnRefreshListener {
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

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(HosysPage hosysPage) {
            PlaceholderFragment fragment = new PlaceholderFragment();
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            swipeWeb = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeWeb);
            swipeWeb.setOnRefreshListener(this);
//            swipeWeb.setColorScheme(android.R.color.holo_blue_bright,
//                    android.R.color.holo_green_light,
//                    android.R.color.holo_orange_light,
//                    android.R.color.holo_red_light);
            webView = (WebView) rootView.findViewById(R.id.web);

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

            String pageName = HosysConfig.SERVER + hosysHtmlProcesor.getHosysPage().getPage();

            if (hosysHtmlProcesor.getException() != null) {
                if (htmlData == null) {
                    htmlData = getString(R.string.downloading_data_error_format, pageName, hosysHtmlProcesor.getException().getMessage());
                }

                String errorMsg = "Nepodařilo se načíst data ze serveru " + HosysConfig.SERVER +
                        " (" + hosysHtmlProcesor.getException().getMessage() + ")";

                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
            } else {
                Date date = new Date();
                String dateTime = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, HosysConfig.CZECH).format(date);

                htmlData = HosysPageHelper.buildWebViewPage(hosysHtmlProcesor);
                htmlData = htmlData.replace("</body", getString(R.string.downloaded_data_info_format, pageName, dateTime) + "</body");

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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final int countTabs = HosysPage.values().length;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(HosysPage.values()[position]);
        }

        @Override
        public int getCount() {
            return countTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_title_rozpis);
                case 1:
                    return getString(R.string.tab_title_tabulky);
                case 2:
                    return getString(R.string.tab_title_soutez);
            }

            return null;
        }
    }
}
