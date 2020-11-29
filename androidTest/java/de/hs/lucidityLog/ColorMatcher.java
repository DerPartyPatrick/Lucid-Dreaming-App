package de.hs.lucidityLog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
/**
 * Klasse stellt Methode zum Vergleich der Farbe eines Objekthintergrundes zur Verfügung
 * @author Quelle:
 * https://stackoverflow.com/questions/45246696/android-espresso-linearlayout-get-background-color-from-position-on-list
 */
public class ColorMatcher implements Matcher<View> {
    private final int matchColor;

    /**
     * Konstruktor mit matchColor als Übergabe
     * @param matchColor
     */
    public ColorMatcher(int matchColor) {
        this.matchColor = matchColor;
    }

    /**
     * Vergleicht ob Farbe des übergebenen Objekts
     * mit Klassenvariable matchColor übereinstimmt
     * @param item Zu vergleichendes Objekt
     * @return true wenn gleiche Farbe
     */
    @Override
    public boolean matches(Object item) {
        Context context = ((View)item).getContext();
        int c2 = context.getColor(this.matchColor);
        int c1 = ((ColorDrawable)((View)item).getBackground()).getColor();
        return c1 == c2;
    }

    /**
     * wird nicht genutzt
     */
    @Override
    public void describeMismatch(Object item, Description mismatchDescription) {

    }

    /**
     * wird nicht genutzt
     */
    @Override
    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {

    }

    /**
     * wird nicht genutzt
     */
    @Override
    public void describeTo(Description description) {
        description.appendText("with text color: ");
    }
}