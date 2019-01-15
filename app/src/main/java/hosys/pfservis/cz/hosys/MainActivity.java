package hosys.pfservis.cz.hosys;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;

import static android.R.attr.x;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

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
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements HosysHtmlText {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_HOSYS_PAGE = "hosys_page";

        private WebView webView;

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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            webView = (WebView) rootView.findViewById(R.id.web);

            HosysPage hosysPage = HosysPage.valueOf(getArguments().getString(ARG_HOSYS_PAGE));


            webView.loadData("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><body>" +
                    getString(R.string.download_data_format, HosysConfig.SERVER + hosysPage.getPage()) + "</body></html>",
                    "text/html; charset=UTF-8", null);

            hosysRefres();

            return rootView;
        }

        public void hosysRefres() {
            HosysPage hosysPage = HosysPage.valueOf(getArguments().getString(ARG_HOSYS_PAGE));
            HosysDownloader hd = new HosysDownloader(this);

            hd.execute(hosysPage);
        }

        @Override
        public void processHtmlText(HosysHtmlProcesor hosysHtmlProcesor) {
            if (hosysHtmlProcesor.getException() != null) {
                String errorMsg = "Nepodařilo se načíst data ze serveru " + HosysConfig.SERVER +
                        " (" + hosysHtmlProcesor.getException().getMessage() + ")";

                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            } else {
                String htmlPage = HosysPageHelper.buildWebViewPage(hosysHtmlProcesor);

                webView.loadData(htmlPage, "text/html; charset=UTF-8", null);
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(HosysPage.values()[position]);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return HosysPage.values().length;
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
