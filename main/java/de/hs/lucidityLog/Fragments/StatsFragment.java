package de.hs.lucidityLog.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import de.hs.lucidityLog.R;
import de.hs.lucidityLog.StatsActivity;
import de.hs.lucidityLog.database.AppDatabase;
import de.hs.lucidityLog.database.Entry;
import de.hs.lucidityLog.myCalendar.MyCalendar;
import de.hs.lucidityLog.ui.StatsActivityUI;

public class StatsFragment extends Fragment {
    private StatsActivityUI ui;
    private View view;
    private int year;
    private int month;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_stats_slide, container, false);

        Bundle bundle = getArguments();
        int currentNumber = bundle.getInt("currentNumber");
        int todayNumber = bundle.getInt("todayNumber");
        int difference = currentNumber - todayNumber;

        Calendar cal = MyCalendar.getCalendar();
        cal.add(Calendar.MONTH, difference);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;

        ui = new StatsActivityUI((StatsActivity) getActivity(), view, year, month);
        ui.drawPieChart();
        ui.drawLineChart();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        AppDatabase.prepareDB(getActivity().getApplicationContext());

        LiveData<Integer> liveEntryNightsNumber = AppDatabase.db.entryDao().getNightsWithEntriesCountByMonth(year, month);
        liveEntryNightsNumber.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer newNumber) {
                ui.updateOtherNights(newNumber);
            }
        });

        LiveData<Integer> liveLucidNightsNumber = AppDatabase.db.entryDao().getLucidNightsCount(year, month);
        liveLucidNightsNumber.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer newNumber) {
                ui.updateLucidNights(newNumber);
            }
        });

        LiveData<List<Entry>> liveEntries = AppDatabase.db.entryDao().getEntriesByMonth(year, month);
        liveEntries.observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(List<Entry> entries) {
                ui.updateLineChart(entries);
            }
        });

        LiveData<Integer> liveLucidNumber = AppDatabase.db.entryDao().getLucidNumberByMonth(year, month, true);
        liveLucidNumber.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer newNumber) {
                ui.updateLucidNumber(newNumber);
            }
        });

        LiveData<Integer> liveNormalNumber = AppDatabase.db.entryDao().getLucidNumberByMonth(year, month, false);
        liveNormalNumber.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer newNumber) {
                ui.updateNormalNumber(newNumber);
            }
        });
    }
}
