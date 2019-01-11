package cz.pfservis.hosys.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import cz.pfservis.hosys.activities.fragments.HosysFragment;
import cz.pfservis.hosys.activities.fragments.HtmlFragment;
import cz.pfservis.hosys.enums.HosysPage;
import hosys.pfservis.cz.hosys.R;

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
                    HosysFragment fragment = (HosysFragment) fragmentX;

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
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    // return RozpisFragment.newInstance(HosysPage.FRAGMENTS[position]);
                case 1:
                case 2:
                    return HtmlFragment.newInstance(HosysPage.FRAGMENTS[position]);
            }

            throw new IllegalStateException("Nepodporovaná hodnota");
        }

        @Override
        public int getCount() {
            return HosysPage.FRAGMENTS.length;
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
