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

public class AboutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.about_title);

        drawerLayout = findViewById(R.id.about_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.about_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(4).setChecked(true);
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
                i = new Intent(this, StatsActivity.class);
                startActivity(i);
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
                break;
        }

        return true;
    }
}
