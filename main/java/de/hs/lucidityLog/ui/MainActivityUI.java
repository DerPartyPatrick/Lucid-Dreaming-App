package de.hs.lucidityLog.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import de.hs.lucidityLog.EntryEditorActivity;
import de.hs.lucidityLog.MainActivity;
import de.hs.lucidityLog.R;
import de.hs.lucidityLog.calculations.PxDpConverter;
import de.hs.lucidityLog.database.AppDatabase;
import de.hs.lucidityLog.database.Entry;
import de.hs.lucidityLog.myCalendar.MyCalendar;

public class MainActivityUI {
    private MainActivity act;
    private View fragmentView;
    private Day[] days;
    private ConstraintLayout cl;
    private int year;
    private int month;
    private int currentDate;
    private int currentMonth;
    private int currentYear;

    private final static int BUTTON_SIZE = 55;

    public MainActivityUI(MainActivity act, View fragmentView, int year, int month) {
        this.act = act;
        this.year = year;
        this.month = month;
        this.fragmentView = fragmentView;
        Calendar cal = MyCalendar.getCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        int numDays = cal.getActualMaximum(Calendar.DATE);
        days = new Day[numDays];
        cl = fragmentView.findViewById(R.id.main_cl);
        ((TextView) fragmentView.findViewById(R.id.main_month)).setText("< " + getMonthName(month) + " " + String.valueOf(year) + " >");

        currentDate = MyCalendar.getCalendar().get(Calendar.DATE);
        currentMonth = MyCalendar.getCalendar().get(Calendar.MONTH)+1;
        currentYear = MyCalendar.getCalendar().get(Calendar.YEAR);

        //testing
        cl.setTag("" + month);
    }

    private String getMonthName(int m) {
        switch (m) {
            case 1:
                return act.getResources().getString(R.string.jan);
            case 2:
                return act.getResources().getString(R.string.feb);
            case 3:
                return act.getResources().getString(R.string.mar);
            case 4:
                return act.getResources().getString(R.string.apr);
            case 5:
                return act.getResources().getString(R.string.may);
            case 6:
                return act.getResources().getString(R.string.jun);
            case 7:
                return act.getResources().getString(R.string.jul);
            case 8:
                return act.getResources().getString(R.string.aug);
            case 9:
                return act.getResources().getString(R.string.sep);
            case 10:
                return act.getResources().getString(R.string.oct);
            case 11:
                return act.getResources().getString(R.string.nov);
            case 12:
                return act.getResources().getString(R.string.dec);
            default:
                return act.getResources().getString(R.string.jan);
        }
    }

    public void removeAll() {
        if(days[0] != null) {
            for(int i=days.length-1; i>=0; i--) {
                Day day = days[i];
                cl.removeView(day.plusButton);

                Iterator<Button> it = day.entryButtons.iterator();
                while(it.hasNext()) {
                    cl.removeView(it.next());
                }

                cl.removeView(day.date);
            }
        }
    }

    public void draw(List<Entry> entries) {
        for(int i=0; i<days.length; i++) {
            days[i] = new Day();
        }

        drawDates();
        drawEntries(entries);
        drawPlusButtons();
        changeDateConstraint();
    }

    private void drawDates() {
        int lastDateId = -1;

        for(int i=0; i<days.length; i++) {
            days[i].date = new TextView(act);
            TextView date = days[i].date;

            date.setId(View.generateViewId());
            date.setText(String.valueOf(i+1));
            date.setTextSize(24);

            date.setTag("day" + date.getText().toString()); //testing

            if(i+1 == currentDate && year == currentYear && month == currentMonth) {
                date.setTextColor(act.getResources().getColor(R.color.colorPrimary));
            }
            else {
                date.setTextColor(act.getResources().getColor(R.color.colorWhite));
            }

            cl.addView(date);

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            date.setLayoutParams(params);

            ConstraintSet set = new ConstraintSet();
            set.clone(cl);
            set.connect(date.getId(), ConstraintSet.LEFT,
                    cl.getId(), ConstraintSet.LEFT, PxDpConverter.dpToPx(8));

            if(lastDateId == -1) {
                set.connect(date.getId(), ConstraintSet.TOP, R.id.main_month, ConstraintSet.BOTTOM, PxDpConverter.dpToPx(24));
            }
            else {
                set.connect(date.getId(), ConstraintSet.TOP, lastDateId, ConstraintSet.BOTTOM, PxDpConverter.dpToPx(30));
            }

            set.applyTo(cl);
            lastDateId = date.getId();
        }
    }

    private void drawEntries(List<Entry> entries) {
        Iterator<Entry> it = entries.iterator();

        while(it.hasNext()) {
            Entry entry = it.next();
            Day day = days[entry.day - 1];

            if(entry.id > day.maxSQLId) {
                day.maxSQLId = entry.id;
            }

            drawEntryButton(entry, day);
        }
    }

    private void drawEntryButton(Entry entry, Day day) {
        Button button = new Button(act);
        day.entryButtons.add(button);
        button.setId(View.generateViewId());
        button.setTag("entry" + entry.id + entry.year + entry.month + entry.day); //testing
        button.setOnClickListener((view) -> onEntryClick(entry));


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(act);
        if(sharedPreferences.getBoolean("pref_fast_delete", false)) {
            button.setOnLongClickListener((view) -> onLongEntryClick(entry, day));
        }

        if(entry.lucid) {
            button.setBackgroundResource(R.drawable.button_lucid);
        }
        else {
            button.setBackgroundResource(R.drawable.button_normal);
        }

        cl.addView(button);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                PxDpConverter.dpToPx(BUTTON_SIZE),
                PxDpConverter.dpToPx(BUTTON_SIZE));
        button.setLayoutParams(params);

        ConstraintSet set = new ConstraintSet();
        set.clone(cl);

        if((day.entryButtons.size()-1) % 4 == 0) {
            set.connect(button.getId(), ConstraintSet.LEFT, R.id.journal_guideline, ConstraintSet.RIGHT, PxDpConverter.dpToPx(0));

            if(day.lastButtonId == -1) {
                set.connect(button.getId(), ConstraintSet.TOP, day.date.getId(), ConstraintSet.TOP, PxDpConverter.dpToPx(-5));
            }
            else {
                set.connect(button.getId(), ConstraintSet.TOP, day.lastButtonId, ConstraintSet.BOTTOM, PxDpConverter.dpToPx(2));
            }
        }
        else {
            set.connect(button.getId(), ConstraintSet.LEFT, day.lastButtonId, ConstraintSet.RIGHT, PxDpConverter.dpToPx(4));
            set.connect(button.getId(), ConstraintSet.TOP, day.lastButtonId, ConstraintSet.TOP, PxDpConverter.dpToPx(0));
        }

        set.applyTo(cl);
        day.lastButtonId = button.getId();
    }

    private void drawPlusButtons() {
        for(int i=0; i<days.length; i++) {
            Day day = days[i];
            day.plusButton = new Button(act);
            day.plusButton.setId(View.generateViewId());
            day.plusButton.setTag("plus" + day.date.getText().toString()); //testing
            day.plusButton.setBackgroundResource(R.drawable.button_plus);
            final int date = i+1;
            day.plusButton.setOnClickListener((view) -> onPlusClick(year, month, date, day.maxSQLId));

            cl.addView(day.plusButton);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    PxDpConverter.dpToPx(BUTTON_SIZE),
                    PxDpConverter.dpToPx(BUTTON_SIZE));
            day.plusButton.setLayoutParams(params);

            changePlusConstraint(day);
        }
    }

    private void changePlusConstraint(Day day) {
        ConstraintSet set = new ConstraintSet();
        set.clone(cl);

        if(day.entryButtons.isEmpty()) {
            set.connect(day.plusButton.getId(), ConstraintSet.LEFT, R.id.journal_guideline, ConstraintSet.RIGHT, PxDpConverter.dpToPx(0));
            set.connect(day.plusButton.getId(), ConstraintSet.TOP, day.date.getId(), ConstraintSet.TOP, PxDpConverter.dpToPx(-5));
        }
        else {
            int lastEntryId = day.lastButtonId;
            set.connect(day.plusButton.getId(), ConstraintSet.LEFT, lastEntryId, ConstraintSet.RIGHT, PxDpConverter.dpToPx(8));
            set.connect(day.plusButton.getId(), ConstraintSet.TOP, lastEntryId, ConstraintSet.TOP, PxDpConverter.dpToPx(0));
        }

        set.applyTo(cl);
    }

    private void changeDateConstraint() {
        for(int i=1; i<days.length; i++) {
            Button day1 = days[i-1].plusButton;
            TextView day2 = days[i].date;

            ConstraintSet set = new ConstraintSet();
            set.clone(cl);
            set.connect(day2.getId(), ConstraintSet.TOP, day1.getId(), ConstraintSet.BOTTOM, PxDpConverter.dpToPx(8));
            set.applyTo(cl);
        }
    }

    private void onEntryClick(Entry entry) {
        Intent i = new Intent(act, EntryEditorActivity.class);
        i.putExtra("entry", entry);
        act.startActivity(i);
    }

    private boolean onLongEntryClick(Entry entry, Day day) {
        new Thread(() -> {
            AppDatabase.prepareDB(act.getApplicationContext());
            AppDatabase.db.entryDao().deleteEntry(entry);

            act.runOnUiThread(() -> {
                Toast t = Toast.makeText(act.getApplicationContext(), R.string.entry_deleted, Toast.LENGTH_SHORT);
                t.show();
            });
        }).start();

        return true;
    }

    private void onPlusClick(int year, int month, int day, int maxId) {
        int newEntryNumber = maxId+1;

        Intent i = new Intent(act, EntryEditorActivity.class);
        i.putExtra("year", year);
        i.putExtra("month", month);
        i.putExtra("day", day);
        i.putExtra("entryNumber", newEntryNumber);
        act.startActivity(i);
    }

    //view container
    private class Day {
        public TextView date;
        public ArrayList<Button> entryButtons;
        public Button plusButton;
        public int lastButtonId = -1;
        public int maxSQLId = 0;

        public Day() {
            entryButtons = new ArrayList<>();
        }
    }
}
