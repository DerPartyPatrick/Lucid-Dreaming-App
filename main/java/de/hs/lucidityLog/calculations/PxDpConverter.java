package de.hs.lucidityLog.calculations;

import android.content.res.Resources;

//source: https://stackoverflow.com/questions/8295986/how-to-calculate-dp-from-pixels-in-android-programmatically

public class PxDpConverter {
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
