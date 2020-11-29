package de.hs.lucidityLog;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import de.hs.lucidityLog.database.AppDatabase;
import de.hs.lucidityLog.database.Entry;
import de.hs.lucidityLog.testing.MyIdlingRessource;

public class EntryEditorActivity extends AppCompatActivity {
    private EditText title;
    private EditText text;
    private Intent intent;
    private Entry entry;
    private int year;
    private int month;
    private int day;
    private int id;
    private boolean lucid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_editor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        title = findViewById(R.id.editor_title);
        text = findViewById(R.id.editor_text);
        AppDatabase.prepareDB(getApplicationContext());

        intent = getIntent();
        entry = (Entry) intent.getSerializableExtra("entry");
        year = intent.getIntExtra("year", 0);
        month = intent.getIntExtra("month", 0);
        day = intent.getIntExtra("day", 0);
        id = intent.getIntExtra("entryNumber", 0);

        if(entry != null) {
            lucid = entry.lucid;
            title.setText(entry.title);
            text.setText(entry.text);

            setToolbarText(entry.year, entry.month, entry.day);
        }
        else {
            setToolbarText(year, month, day);
        }

        Button lucidButton = findViewById(R.id.editor_button);

        if(lucid) {
            lucidButton.setTag("lucid"); //testing
            lucidButton.setBackgroundResource(R.drawable.editor_pressed);
            ViewCompat.setBackgroundTintList(title, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            ViewCompat.setBackgroundTintList(text, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        }
        else {
            lucidButton.setTag("nonlucid"); //testing
            lucidButton.setBackgroundResource(R.drawable.editor_not_pressed);
            ViewCompat.setBackgroundTintList(title, ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            ViewCompat.setBackgroundTintList(text, ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        }

        lucidButton.setOnClickListener((view) -> {
            lucid = !lucid;

            if(lucid) {
                view.setTag("lucid");
                view.setBackgroundResource(R.drawable.editor_pressed);
                Toast.makeText(getApplicationContext(), R.string.set_to_lucid, Toast.LENGTH_SHORT).show();
                ViewCompat.setBackgroundTintList(title, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                ViewCompat.setBackgroundTintList(text, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
            }
            else {
                view.setTag("nonlucid");
                view.setBackgroundResource(R.drawable.editor_not_pressed);
                Toast.makeText(getApplicationContext(), R.string.set_to_non_lucid, Toast.LENGTH_SHORT).show();
                ViewCompat.setBackgroundTintList(title, ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                ViewCompat.setBackgroundTintList(text, ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.editor_toolbar_buttons, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.editor_bin) {
            title.setText("");
            text.setText("");
        }
        finish();
        return super.onOptionsItemSelected(item);
    }


    private void setToolbarText(int year, int month, int day) {
        String y = String.valueOf(year);
        String m;
        String d;

        if(month < 10) {
            m = "0" + String.valueOf(month);
        }
        else {
            m = String.valueOf(month);
        }

        if(day < 10) {
            d = "0" + String.valueOf(day);
        }
        else {
            d = String.valueOf(day);
        }

        String title = d + "." + m + "." + y;
        getSupportActionBar().setTitle(title);
    }


    @Override
    protected void onStop() {
        super.onStop();

        String tit = title.getText().toString();
        String txt = text.getText().toString();

        if(tit.equals("") && txt.equals("")) {
            if(entry != null) {
                deleteEntry();
            }
        }
        else {
            if(entry == null) {
                createEntry(tit, txt);
            }
            else {
                entry.title = tit;
                entry.text = txt;
                entry.lucid = lucid;
                updateEntry();
            }
        }
    }

    private void updateEntry() {
        MyIdlingRessource.getIdlingResource().increment();
        new Thread(() -> {
            AppDatabase.db.entryDao().updateEntry(entry);
            MyIdlingRessource.getIdlingResource().decrement();
        }).start();
    }

    private void deleteEntry() {
        MyIdlingRessource.getIdlingResource().increment();
        new Thread(() -> {
            AppDatabase.db.entryDao().deleteEntry(entry);
            MyIdlingRessource.getIdlingResource().decrement();
        }).start();
    }

    private void createEntry(String tit, String txt) {
        entry = new Entry(id, year, month, day, tit, txt, lucid);

        MyIdlingRessource.getIdlingResource().increment();
        new Thread(() -> {
            AppDatabase.db.entryDao().insertEntry(entry);
            MyIdlingRessource.getIdlingResource().decrement();
        }).start();
    }
}
