package de.hs.lucidityLog;

import android.content.Intent;
import android.view.View;
import android.widget.ScrollView;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import de.hs.lucidityLog.database.AppDatabase;
import de.hs.lucidityLog.database.Entry;
import de.hs.lucidityLog.database.EntryDao;
import de.hs.lucidityLog.myCalendar.MyCalendar;
import de.hs.lucidityLog.testing.MyIdlingRessource;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.intent.Intents.*;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.*;
import static androidx.test.espresso.intent.matcher.IntentMatchers.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;

/**
 * Klasse MainTest beinhaltet UI-Tests zur MainActivity.
 * Tests zur Korrekten Darstellung der Daten und erfolgreiche Ausführung von Intents.
 * @author Hauptverantwortlich: Pascal Piora, Mitwirkend: Patrick Behrens
 */
@RunWith(AndroidJUnit4.class)
public class MainTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(
        MainActivity.class,
        true,
        false); //don't launch Activity before Test

    private CountingIdlingResource idlingResource;

    @Mock
    private AppDatabase db;
    @Mock
    private EntryDao entryDao;
    @Mock
    private LiveData<List<Entry>> liveData;
    @Mock
    private Calendar calendar;

    @Captor
    private ArgumentCaptor<Observer<List<Entry>>> observerCaptor;

    private int year = 2020;
    private int month = 8;
    private String monthName = "August";
    private int day = 28;
    private int maxDays = 30;
    private Entry entry;

    /**
     * Vor Ausführung der Tests. Aufbau DB, Kalender, Mocks und idlingRessource
     */
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        idlingResource = MyIdlingRessource.getIdlingResource();
        Espresso.registerIdlingResources(idlingResource);

        AppDatabase.db = db;
        Mockito.when(db.entryDao()).thenReturn(entryDao);
        Mockito.when(entryDao.getEntriesByMonth(any(Integer.class), any(Integer.class))).thenReturn(liveData);

        MyCalendar.setCalendar(calendar);

        Intents.init();
    }

    /**
     * Releasen der Intents nach Ausführung der Tests
     */
    @After
    public void after() {
        Intents.release();
    }

    /**
     * Test rund um die richtige Darstellung des korrekten Datums,
     * Aufruf des Intents zum Anlegen eines Eintrags mit Überprüfung auf korretes Datum
     */
    @Test
    public void test1() {
        test1beforeLaunch();
        activityRule.launchActivity(new Intent());
        test1afterLaunch();
    }

    /**
     * Vor Test 1 Darstellung von Jahr, Monat und Tagen
     */
    private void test1beforeLaunch() {
        int month = this.month-1;
        Mockito.when(calendar.get(Calendar.YEAR)).thenReturn(year);
        Mockito.when(calendar.get(Calendar.MONTH)).thenReturn(month).thenReturn(month);
        Mockito.when(calendar.get(Calendar.DATE)).thenReturn(day);
        Mockito.when(calendar.getActualMaximum(any(Integer.class))).thenReturn(maxDays);
    }

    /**
     * Aufruf der Methoden zum Testen des richtigen Datums, der korrekten Buttons
     * und des Intents zum Anlegen eines Eintrags
     */
    private void test1afterLaunch() {
        checkDate("< " + monthName + " " + year + " >", month);
        checkDefaultButtons(month, maxDays, false);
        clickPlus(day, month);
        checkPlusIntent(year, month, day, 1);
    }

    /**
     * Test des Aufrufs eines vorhandenen luziden Eintrags
     */
    @Test
    public void test2() {
        test2beforeLaunch();
        activityRule.launchActivity(new Intent());
        test2afterLaunch();
    }

    /**
     * Vor Test 2 Darstellung von Jahr, Monat und Tagen
     */
    private void test2beforeLaunch() {
        int month = this.month-1;
        Mockito.when(calendar.get(Calendar.YEAR)).thenReturn(year);
        Mockito.when(calendar.get(Calendar.MONTH))
                .thenReturn(month).thenReturn(month)
                .thenReturn(month-1).thenReturn(month-1);
        Mockito.when(calendar.get(Calendar.DATE)).thenReturn(day);
        Mockito.when(calendar.getActualMaximum(any(Integer.class)))
                .thenReturn(maxDays).thenReturn(maxDays+5);
    }

    /**
     * Eintrag erzeugen (luzid), Klick auf den Eintrag und Überprüfung des Intents
     */
    private void test2afterLaunch() {
        setEntry(true);
        clickEntryButton(1, year, month, day);
        checkEntryIntent(this.entry);
    }

    /**
     * Test des Aufrufs eines vorhandenen nicht luziden Eintrags
     */
    @Test
    public void test3() {
        test3beforeLaunch();
        activityRule.launchActivity(new Intent());
        test3afterLaunch();
    }

    /**
     * Vor Test 3 Darstellung von Jahr, Monat und Tagen
     */
    private void test3beforeLaunch() {
        int month = this.month-1;
        Mockito.when(calendar.get(Calendar.YEAR)).thenReturn(year);
        Mockito.when(calendar.get(Calendar.MONTH))
                .thenReturn(month).thenReturn(month)
                .thenReturn(month-1).thenReturn(month-1);
        Mockito.when(calendar.get(Calendar.DATE)).thenReturn(day);
        Mockito.when(calendar.getActualMaximum(any(Integer.class)))
                .thenReturn(maxDays).thenReturn(maxDays+5);
    }

    /**
     * Eintrag erzeugen (nicht luzid), Klick auf den Eintrag und Überprüfung des Intents
     */
    private void test3afterLaunch() {
        setEntry(false);
        clickEntryButton(1, year, month, day);
        checkEntryIntent(this.entry);
    }

    /**
     * Überprüfung des Übergebenen Datums mit dem aktuell angezeigtem
     * @param date Datum als String
     * @param month Monat als Zahl
     */
    private void checkDate(String date, int month) {
        onView(allOf(withId(R.id.main_month), withFragment(month)))
                .check(matches(withText(date)));
    }

    /**
     * Überprüfung auf korrekt angezeigte Buttons
     * @param month aktueller Monat als Zahl
     * @param maxDays Anzahl max. Tage des Monats
     * @param hasEntries Gibt es am Einträge am Tag
     */
    private void checkDefaultButtons(int month, int maxDays, boolean hasEntries) {
        int dayAfterLast = maxDays + 1;
        tagDoesNotExist("day0", month);
        tagDoesNotExist("day" + dayAfterLast, month);
        tagDoesNotExist("plus0", month);
        tagDoesNotExist("plus" + dayAfterLast, month);
        tagDoesNotExist("entry0", month);
        tagDoesNotExist("entry" + dayAfterLast, month);

        for(int i=1; i<=maxDays; i++) {
            int textColor;
            if(i==day) textColor = R.color.colorPrimary;
            else textColor = R.color.colorWhite;

            onView(allOf(withTagValue(is("day" + i)), withFragment(month)))
                    .perform(scrollTo())
                    .check(matches(hasTextColor(textColor)));

            onView(allOf(withTagValue(is("plus" + i)), withFragment(month)))
                    .perform(scrollTo())
                    .check(matches(isDisplayed()));

            if(!hasEntries) tagDoesNotExist("entry" + i, month);
        }
    }

    /**
     * Ausführung eines Klicks um einen neuen Eintrag anzulegen
     * @param day Der Tag an dem ein Eintrag angelegt werden soll
     * @param month aktueller Monat
     */
    private void clickPlus(int day, int month) {
        onView(allOf(withTagValue(is("plus" + day)), withFragment(month)))
                .perform(scrollTo(), click());
    }

    /**
     * Überprüfung ob Intent zum Anlegen eines neuen EIntrags korrekt verlaufen
     * @param year aktuelles Jahr
     * @param month aktueller Monat
     * @param day aktueller Tag
     * @param entryNumber Nummer des Eintrags
     */
    private void checkPlusIntent(int year, int month, int day, int entryNumber) {
        intended(allOf(hasComponent(hasClassName(EntryEditorActivity.class.getName())), allOf(
                hasExtra("year", year),
                hasExtra("month", month),
                hasExtra("day", day),
                hasExtra("entryNumber", entryNumber),
                not(hasExtra("entry", any()))
        )));
    }

    /**
     * Überprüfung ob Intent zum angeklickten Eintrag korrekt erfolgt
     * @param entry ausgewählter Eintrag
     */
    private void checkEntryIntent(Entry entry) {
        intended(allOf(hasComponent(hasClassName(EntryEditorActivity.class.getName())), allOf(
                hasExtra("entry", entry))
        ));
    }

    /**
     * Setzen eines neuen Eintrags
     * @param lucid Eintrag entweder luzide oder nicht
     */
    private void setEntry(boolean lucid) {
        Mockito.verify(liveData).observe(any(), observerCaptor.capture());
        Observer<List<Entry>> observer = observerCaptor.getValue();
        List<Entry> entries = new ArrayList<>();
        entry = new Entry(1, year, month, day, "title", "text", lucid);
        entries.add(entry);

        try {
            activityRule.runOnUiThread(() -> {
                observer.onChanged(entries);
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * Klick auf einen vorhandenen Eintrag
     * @param id ID des Eintrags
     * @param year aktuelles Jahr
     * @param month aktueller Monat
     * @param day aktueller Tag
     */
    private void clickEntryButton(int id, int year, int month, int day) {
        onView(allOf(withTagValue(is("entry" + id + year + month + day)), withFragment(month)))
                .perform(scrollTo(), click());
    }

    /**
     * Überprüfung ob übergebener Tag im Fragment existiert
     * @param tag zu überprüfender Tag
     * @param fragment zu prüfendes Fragement
     */
    private void tagDoesNotExist(String tag, int fragment) {
        onView(allOf(withTagValue(is(tag)), withFragment(fragment)))
                .check(doesNotExist());
    }

    private Matcher<View> withFragment(int month) {
        return withParent(allOf(withId(R.id.main_cl), withTagValue(is(Integer.toString(month)))));
    }
}





















