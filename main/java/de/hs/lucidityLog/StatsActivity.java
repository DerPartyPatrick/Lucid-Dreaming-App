package de.hs.lucidityLog;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import de.hs.lucidityLog.Fragments.StatsFragment;

public class StatsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int NUM_PAGES = 1000;
    private int todayNumber;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.statistics_title);

        drawerLayout = findViewById(R.id.stats_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.stats_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = findViewById(R.id.stats_pager);
        pagerAdapter = new StatsActivity.ScreenSlidePagerAdapter(this);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(pagerAdapter);
        todayNumber = NUM_PAGES/2;
        viewPager.setCurrentItem(todayNumber, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }


    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            Fragment f = new StatsFragment();
            Bundle b = new Bundle();
            b.putInt("currentNumber", position);
            b.putInt("todayNumber", todayNumber);
            f.setArguments(b);
            return f;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent i;
        menuItem.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);

        switch (menuItem.getItemId()) {
            case R.id.nav_journal:
                i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.nav_stats:
                break;
            case R.id.nav_tutorial:
                i = new Intent(this, TutorialActivity.class);
                startActivity(i);
                break;
            case R.id.nav_settings:
                i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_about:
                i = new Intent(this, AboutActivity.class);
                startActivity(i);
                break;
        }

        return true;
    }
}
