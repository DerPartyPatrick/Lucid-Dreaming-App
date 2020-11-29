package de.hs.lucidityLog.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import de.hs.lucidityLog.MainActivity;
import de.hs.lucidityLog.R;
import de.hs.lucidityLog.database.AppDatabase;
import de.hs.lucidityLog.database.Entry;
import de.hs.lucidityLog.myCalendar.MyCalendar;
import de.hs.lucidityLog.ui.MainActivityUI;

public class MainFragment extends Fragment {
    private MainActivityUI ui;
    private View view;
    private int year;
    private int month;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main_slide, container, false);

        Bundle bundle = getArguments();
        int currentNumber = bundle.getInt("currentNumber");
        int todayNumber = bundle.getInt("todayNumber");
        int difference = currentNumber - todayNumber;

        //calculating year and month for this page
        Calendar cal = MyCalendar.getCalendar();
        cal.add(Calendar.MONTH, difference);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;

        ui = new MainActivityUI((MainActivity) getActivity(), view, year, month);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppDatabase.prepareDB(getActivity().getApplicationContext());

        LiveData<List<Entry>> liveEntries = AppDatabase.db.entryDao().getEntriesByMonth(year, month);
        ui.removeAll();
        ui.draw(new ArrayList<>());
        liveEntries.observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(List<Entry> entries) {
                ui.removeAll();
                ui.draw(entries);
            }
        });
    }
}
