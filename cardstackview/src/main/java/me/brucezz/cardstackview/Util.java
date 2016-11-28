package me.brucezz.cardstackview;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by brucezz on 2016-09-26.
 * Github: https://github.com/brucezz
 * Email: im.brucezz@gmail.com
 */

public class Util {

    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
}
