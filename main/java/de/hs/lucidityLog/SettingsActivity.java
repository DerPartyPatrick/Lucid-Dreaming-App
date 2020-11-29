package de.hs.lucidityLog;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import de.hs.lucidityLog.Fragments.SettingsFragment;
import de.hs.lucidityLog.database.AppDatabase;
import de.hs.lucidityLog.database.Entry;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.settings_title);

        drawerLayout = findViewById(R.id.settings_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.settings_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();

        Button exportButton = findViewById(R.id.export_button);
        exportButton.setOnClickListener((view) -> onExport());

        Switch sw = findViewById(R.id.reset_switch);
        Button resetButton = findViewById(R.id.reset_button);
        sw.setOnCheckedChangeListener((view, state) -> onSwitchChanged(state, resetButton));
        resetButton.setOnClickListener((view) -> onReset());
    }

    private void onReset() {
        new Thread(() -> {
            AppDatabase.prepareDB(getApplicationContext());
            AppDatabase.db.entryDao().nukeTable();

            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "database resetted", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void onSwitchChanged(boolean state, Button button) {
        int colorId;

        if(state) {
            colorId = R.color.colorPrimary;
            button.setClickable(true);
        }
        else {
            colorId = R.color.colorAccent;
            button.setClickable(false);
        }

        ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(getResources().getColor(colorId)));
    }

    private void onExport() {
        new Thread(() -> {
            AppDatabase.prepareDB(this.getApplicationContext());
            List<Entry> entries = AppDatabase.db.entryDao().getAllEntries();
            Iterator<Entry> it = entries.iterator();
            String[] data = new String[entries.size()];
            int index = 0;

            while(it.hasNext()) {
                Entry entry = it.next();
                String entryText = "\nyear: " + entry.year
                        + "\nmonth: " + entry.month
                        + "\nday: " + entry.day
                        + "\nid: " + entry.id;

                if(entry.lucid) {
                    entryText += "\nlucid";
                }
                else {
                    entryText += "\nnon-lucid";
                }

                entryText = entryText
                        + "\n\ntitle: " + entry.title
                        + "\n\n" + entry.text
                        + "\n-----------------------------"
                        + "\n\n\n";
                data[index] = entryText;
                index++;
            }


            String fileName = "DreamJournal.txt";
            FileOutputStream fos = null;
            File file;

            try {
                file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
                file.createNewFile();

                fos = new FileOutputStream(file);

                for(int i=0; i<data.length; i++) {
                    fos.write(data[i].getBytes());
                }
                runOnUiThread(() -> {
                    Toast.makeText(this.getApplicationContext(), "saved to DOCUMENTS", Toast.LENGTH_SHORT).show();
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();
    }


    @Override
    public void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(3).setChecked(true);
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
                break;
            case R.id.nav_about:
                i = new Intent(this, AboutActivity.class);
                startActivity(i);
                break;
        }

        return true;
    }
}
