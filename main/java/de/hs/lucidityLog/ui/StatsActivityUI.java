package de.hs.lucidityLog.ui;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import de.hs.lucidityLog.R;
import de.hs.lucidityLog.StatsActivity;
import de.hs.lucidityLog.database.Entry;
import de.hs.lucidityLog.myCalendar.MyCalendar;

public class StatsActivityUI {
    private StatsActivity act;
    private View fragmentView;
    private int year;
    private int month;
    private ConstraintLayout cl;

    private int lucidNights = 0;
    private int normalNights = 0;
    private int emptyNights;
    private int daysInMonth;

    private PieChart pie;
    private List<PieEntry> pieEntries;
    private LineChart lineChart;
    private List<com.github.mikephil.charting.data.Entry> lineEntriesLucid;
    private List<com.github.mikephil.charting.data.Entry> lineEntriesNormal;

    private int[] lucidWeeks;
    private int[] normalWeeks;

    public StatsActivityUI(StatsActivity act, View fragmentView, int year, int month) {
        this.act = act;
        this.fragmentView = fragmentView;
        this.year = year;
        this.month = month;
        cl = fragmentView.findViewById(R.id.stats_cl);
        ((TextView) fragmentView.findViewById(R.id.stats_month)).setText("< " + getMonthName(month) + " " + String.valueOf(year) + " >");

        Calendar cal = MyCalendar.getCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        daysInMonth = cal.getActualMaximum(Calendar.DATE);
        emptyNights = daysInMonth;
        pieEntries = new ArrayList<>();

        lucidWeeks = new int[4];
        normalWeeks = new int[4];
        lineEntriesLucid = new ArrayList<>();
        lineEntriesNormal = new ArrayList<>();
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

    public void updateLucidNumber(int newNumber) {
        ((TextView)(fragmentView.findViewById(R.id.stats_lucid_number))).setText(String.valueOf(newNumber));
    }

    public void updateNormalNumber(int newNumber) {
        ((TextView)(fragmentView.findViewById(R.id.stats_normal_number))).setText(String.valueOf(newNumber));
    }

    public void updateLineChart(List<Entry> entries) {
        for(int i=0; i<lucidWeeks.length; i++) {
            lucidWeeks[i] = 0;
            normalWeeks[i] = 0;
        }

        Iterator<Entry> it = entries.iterator();

        while(it.hasNext()) {
            Entry e = it.next();
            int week = (e.day-1) / 7;

            if(week > 3) {
                week = 3;
            }

            if(e.lucid) {
                lucidWeeks[week]++;
            }
            else {
                normalWeeks[week]++;
            }
        }

        for(int i=0; i<lineEntriesLucid.size(); i++) {
            lineEntriesLucid.get(i).setY(lucidWeeks[i]);
            lineEntriesNormal.get(i).setY(normalWeeks[i]);
        }


        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    public void drawLineChart() {
        lineChart = fragmentView.findViewById(R.id.line_chart);
        lineChart.setTouchEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.getAxisLeft().setAxisMinimum(0);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setGranularity(1);
        lineChart.getXAxis().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setGranularity(1);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);

        addWeekValues();

        LineDataSet setLucid = new LineDataSet(lineEntriesLucid, "Lucid");
        LineDataSet setNormal = new LineDataSet(lineEntriesNormal, "Normal");
        setLucid.setDrawCircleHole(false);
        setNormal.setDrawCircleHole(false);
        setLucid.setColor(act.getResources().getColor(R.color.colorPrimary));
        setNormal.setColor(act.getResources().getColor(R.color.lineNormal));
        setLucid.setCircleColor(act.getResources().getColor(R.color.colorPrimary));
        setNormal.setCircleColor(act.getResources().getColor(R.color.lineNormal));
        setLucid.setDrawValues(false);
        setNormal.setDrawValues(false);

        LineData data = new LineData();
        data.addDataSet(setNormal);
        data.addDataSet(setLucid);
        lineChart.setData(data);
        lineChart.invalidate();
    }

    private void addWeekValues() {
        for(int i=0; i<normalWeeks.length; i++) {
            lineEntriesNormal.add(new com.github.mikephil.charting.data.Entry(i+1, normalWeeks[i]));
            lineEntriesLucid.add(new com.github.mikephil.charting.data.Entry(i+1, lucidWeeks[i]));
        }
    }

    public void updateLucidNights(int newLucidNights) {
        lucidNights = newLucidNights;
        normalNights = (daysInMonth - emptyNights) - lucidNights;

        updateOrCreatePieEntry(act.getResources().getString(R.string.lucid), lucidNights);
        updateOrCreatePieEntry(act.getResources().getString(R.string.non_lucid), normalNights);
        removeZeroes();
        setPieColors();
        pie.notifyDataSetChanged();
        pie.invalidate();
    }

    public void updateOtherNights(int newEntryNights) {
        emptyNights = daysInMonth - newEntryNights;
        normalNights = newEntryNights - lucidNights;

        updateOrCreatePieEntry(act.getResources().getString(R.string.empty), emptyNights);
        updateOrCreatePieEntry(act.getResources().getString(R.string.non_lucid), normalNights);
        removeZeroes();
        setPieColors();
        pie.notifyDataSetChanged();
        pie.invalidate();
    }

    private void updateOrCreatePieEntry(String label, int value) {
        PieEntry entry = null;
        Iterator<PieEntry> it = pieEntries.iterator();

        while(it.hasNext()) {
            PieEntry e = it.next();
            if(e.getLabel().equals(label)) {
                entry = e;
            }
        }

        if(entry == null) {
            entry = new PieEntry(value, label);
            pieEntries.add(entry);
        }
        else {
            entry.setY(value);
        }
    }

    private void removeZeroes() {
        for(PieEntry e: new ArrayList<>(pieEntries)) {
            if(e.getValue()==0) {
                pieEntries.remove(e);
            }
        }
    }

    public void drawPieChart() {
        pie = fragmentView.findViewById(R.id.pie_chart);
        pie.setTouchEnabled(false);
        pie.getLegend().setEnabled(false);
        pie.getDescription().setEnabled(false);
        pie.setUsePercentValues(true);
        pie.setCenterText(act.getResources().getString(R.string.nights));
        pie.setCenterTextColor(Color.WHITE);
        pie.setHoleColor(Color.TRANSPARENT);
        pie.setEntryLabelColor(Color.BLACK);
        pie.setCenterTextSize(18);

        pieEntries.add(new PieEntry(emptyNights, act.getResources().getString(R.string.empty)));
        PieDataSet set = new PieDataSet(pieEntries, "initial");
        set.setColors(new int[] {R.color.pieEmpty, R.color.pieNormal, R.color.pieLucid}, act);
        PieData data = new PieData(set);
        pie.setData(data);
        pie.invalidate();
    }

    private void setPieColors() {
        PieDataSet set = (PieDataSet) pie.getData().getDataSet();

        try {
            if(pieEntries.get(1).getLabel().equals(act.getResources().getString(R.string.lucid))) {
                set.setColors(new int[]{R.color.pieEmpty, R.color.pieLucid, R.color.pieNormal}, act);
            }
            else {
                set.setColors(new int[]{R.color.pieEmpty, R.color.pieNormal, R.color.pieLucid}, act);
            }
        }
        catch (IndexOutOfBoundsException e) { }
    }
}
