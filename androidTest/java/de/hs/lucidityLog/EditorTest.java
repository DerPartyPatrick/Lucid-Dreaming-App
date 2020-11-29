package de.hs.lucidityLog;

import android.content.Intent;
import android.widget.TextView;

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

import androidx.test.espresso.Espresso;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import de.hs.lucidityLog.database.AppDatabase;
import de.hs.lucidityLog.database.Entry;
import de.hs.lucidityLog.database.EntryDao;
import de.hs.lucidityLog.testing.MyIdlingRessource;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

/**
 * Klasse zum Testen der EntryEditorActivity. Erstellung neuer Einträge,
 * Verändern und Löschen vorhandener Einträge. Interaktion mit DB.
 * @author Hauptverantwortlich: Patrick Behrens, Mitwirkend: Pascal Piora
 */
@RunWith(AndroidJUnit4.class)
public class EditorTest {
    @Rule
    public ActivityTestRule<EntryEditorActivity> activityRule = new ActivityTestRule<>(
            EntryEditorActivity.class,
            true,
            false);

    private CountingIdlingResource idlingResource;
    private Intent intent;

    private int year = 2020;
    private int month = 8;
    private int day = 2;
    private int id = 0;
    private String title = "test title";
    private String text = "test text";

    @Mock
    private AppDatabase db;
    @Mock
    private EntryDao entryDao;

    @Captor
    private ArgumentCaptor<Entry> entryCaptor;

    /**
     * Vor Ausführung der Tests, Aufbau DB, Mocks, idlingResource
     */
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        idlingResource = MyIdlingRessource.getIdlingResource();
        Espresso.registerIdlingResources(idlingResource);

        AppDatabase.db = db;
        Mockito.when(db.entryDao()).thenReturn(entryDao);

        Intents.init();

        intent = new Intent();
        intent.putExtra("year", year);
        intent.putExtra("month", month);
        intent.putExtra("day", day);
        intent.putExtra("id", id);
    }

    /**
     * Releasen der Intents nach Ausführung der Tests
     */
    @After
    public void after() {
        Intents.release();
    }

    /**
     * Test des Default Designs. Korrektes Datum, Farbe des Toolbar, richtiges Icon (luzid/ nicht luzid)
     * Verwerfen des Eintrags und Prüfung ob DB Interaktion erfolgt
     */
    @Test
    public void test1() {
        activityRule.launchActivity(intent);

        checkDate("02.08.2020");
        checkToolbarColor(R.color.colorAccent);
        checkButtonIcon("nonlucid");
        checkTitle("");
        checkText("");
        clickId(R.id.editor_button);
        checkButtonIcon("lucid");
        checkToolbarColor(R.color.colorPrimary);
        clickId(R.id.editor_bin);
        check_db_noInteraction();
        checkFinish();
    }

    /**
     * Test der DB Interaktion bei Aufruf und Schließen des Editors
     */
    @Test
    public void test2() {
        activityRule.launchActivity(intent);

        clickId(R.id.editor_button);
        clickId(R.id.editor_confirm);
        check_db_noInteraction();
        checkFinish();
    }

    /**
     * Test ob Einfügen eines neuen Eintrags in DB klappt
     */
    @Test
    public void test3() {
        activityRule.launchActivity(intent);

        enterTitle(title);
        enterText(text);
        clickId(R.id.editor_confirm);
        check_db_insert(new Entry(id, year, month, day, title, text, false));
        checkFinish();
    }

    /**
     * Test ob vorhandener Eintrag beim Löschen erfolgreich aus DB entfernt wird
     */
    @Test
    public void test4() {
        Entry entry = new Entry(id, year, month, day, title, text, true);
        setEntry(entry);
        activityRule.launchActivity(intent);

        checkTitle(title);
        checkText(text);
        clickId(R.id.editor_bin);
        check_db_delete(entry);
        checkFinish();
    }

    /**
     * Test ob Einfügen eines neuen Eintrags in DB klappt
     */
    @Test
    public void test5() {
        Entry entry = new Entry(id, year, month, day, title, text, true);
        setEntry(entry);
        activityRule.launchActivity(intent);

        clickId(R.id.editor_confirm);
        check_db_update(entry);
        checkFinish();
    }

    /**
     * Test ob Ändern/Erweitern des Titels und Texts richtig in DB geändert wird
     */
    @Test
    public void test6() {
        Entry entry = new Entry(id, year, month, day, title, text, true);
        setEntry(entry);
        activityRule.launchActivity(intent);

        enterText("heyhey");
        enterTitle("heyhey");
        clickId(R.id.editor_confirm);
        entry.title = entry.title + "heyhey";
        entry.text = entry.text + "heyhey";
        check_db_update(entry);
        checkFinish();
    }

    /**
     * Test ob Icon geändert wird bei Wechsel von luzidem zu nicht luzidem Traum
     * und Prüfung ob bei Änderung auch DB Eintrag geändert wird
     */
    @Test
    public void test7() {
        Entry entry = new Entry(id, year, month, day, title, text, true);
        setEntry(entry);
        activityRule.launchActivity(intent);

        checkButtonIcon("lucid");
        checkToolbarColor(R.color.colorPrimary);
        clickId(R.id.editor_button);
        checkButtonIcon("nonlucid");
        checkToolbarColor(R.color.colorAccent);
        clickId(R.id.editor_confirm);
        entry.lucid = false;
        check_db_update(entry);
        checkFinish();
    }

    /**
     * Test ob Ersetzen des Titels und Texts in DB richtig geändert wird
     */
    @Test
    public void test8() {
        Entry entry = new Entry(id, year, month, day, title, text, true);
        setEntry(entry);
        activityRule.launchActivity(intent);

        replaceTxt("heyhey");
        replaceTtle("heyhey");
        clickId(R.id.editor_confirm);
        entry.title = "heyhey";
        entry.text = "heyhey";
        check_db_update(entry);
        checkFinish();
    }

    /**
     * Test ob Eintrag gelöscht wird bei leerem Titel und Text
     */
    @Test
    public void test9() {
        Entry entry = new Entry(id, year, month, day, title, text, true);
        setEntry(entry);
        activityRule.launchActivity(intent);

        replaceTtle("");
        replaceTxt("");

        clickId(R.id.editor_confirm);
        check_db_delete(entry);
        checkFinish();
    }

    /**
     * Setzen eines Eintrags
     */
    private void setEntry(Entry entry) {
        intent.putExtra("entry", entry);
    }

    /**
     * Überprüfung korrektes Datum
     */
    private void checkDate(String date) {
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(date)));
    }

    /**
     * Überprüfung korrekte Farbe der Toolbar
     */
    private void checkToolbarColor(int colorId) {
        onView(withId(R.id.toolbar))
                .check(matches(new ColorMatcher(colorId)));
    }

    /**
     * Überprüfung korrektes Icon des Editor-Buttons
     */
    private void checkButtonIcon(String tag) {
        onView(withId(R.id.editor_button))
                .check(matches(withTagValue(is(tag))));
    }

    /**
     * Überprüfung auf korrekten Titel
     */
    private void checkTitle(String title) {
        onView(withId(R.id.editor_title))
                .check(matches(withText(title)));
    }

    /**
     * Überprüfung auf korrekt angezeigten Text
     */
    private void checkText(String text) {
        onView(withId(R.id.editor_text))
                .check(matches(withText(text)));
    }

    /**
     * Klick auf den Button mit der übergebenen ID
     * @param id ID eines Buttons
     */
    private void clickId(int id) {
        onView(withId(id))
                .perform(click());
    }

    /**
     * Überprüfung ob eine Interaktion mit der Datenbank erfolgt
     */
    private void check_db_noInteraction() {
        Mockito.verify(entryDao, Mockito.never()).deleteEntry(any());
        Mockito.verify(entryDao, Mockito.never()).insertEntry(any());
        Mockito.verify(entryDao, Mockito.never()).updateEntry(any());
        Mockito.verify(entryDao, Mockito.never()).nukeTable();
    }

    /**
     * Überprüfung des Einfügens eines Eintrags in die Datenbank
     * @param expected Eintrag der in der DB erwartet wird
     */
    private void check_db_insert(Entry expected) {
        Mockito.verify(entryDao, Mockito.never()).deleteEntry(any());
        Mockito.verify(entryDao, Mockito.never()).updateEntry(any());
        Mockito.verify(entryDao, Mockito.never()).nukeTable();

        Mockito.verify(entryDao).insertEntry(entryCaptor.capture());
        Entry entry = entryCaptor.getValue();
        assertTrue(entry.toString(), entry.equals(expected));
    }

    /**
     * Überprüfung des Löschen eines EIntrags aus der Datenbank
     * @param expected Eintrag der gelöscht sein sollte
     */
    private void check_db_delete(Entry expected) {
        Mockito.verify(entryDao, Mockito.never()).insertEntry(any());
        Mockito.verify(entryDao, Mockito.never()).updateEntry(any());
        Mockito.verify(entryDao, Mockito.never()).nukeTable();

        Mockito.verify(entryDao).deleteEntry(entryCaptor.capture());
        Entry entry = entryCaptor.getValue();
        assertTrue(entry.toString(), entry.equals(expected));
    }

    /**
     * Überprüfung der Veränderung eines Eintrags in der Datenbank
     * @param expected Eintrag der verändert werden sollte
     */
    private void check_db_update(Entry expected) {
        Mockito.verify(entryDao, Mockito.never()).deleteEntry(any());
        Mockito.verify(entryDao, Mockito.never()).insertEntry(any());
        Mockito.verify(entryDao, Mockito.never()).nukeTable();

        Mockito.verify(entryDao).updateEntry(entryCaptor.capture());
        Entry entry = entryCaptor.getValue();
        assertTrue(entry.toString(), entry.equals(expected));
    }

    /**
     * Überprüfung ob Activity abgeschlossen
     */
    private void checkFinish() {
        assertTrue(activityRule.getActivity().isFinishing());
    }

    /**
     * Eintragung eines Texts
     * @param text Text der angezeigt/eingetragen werden soll
     */
    private void enterText(String text) {
        onView(withId(R.id.editor_text))
                .perform(typeText(text));
    }

    /**
     * Eintragung eines Titels
     * @param title Titel der angezeigt/eingetragen werden soll
     */
    private void enterTitle(String title) {
        onView(withId(R.id.editor_title))
                .perform(typeText(title));
    }

    /**
     * Ersetzung des Texts
     * @param text Text der angezeigt/eingetragen werden soll
     */
    private void replaceTxt(String text) {
        onView(withId(R.id.editor_text))
                .perform(clearText(), typeText(text));
    }

    /**
     * Ersetzung des Titels
     * @param title Titel der angezeigt/eingetragen werden soll
     */
    private void replaceTtle(String title) {
        onView(withId(R.id.editor_title))
                .perform(clearText(), typeText(title));
    }
}
