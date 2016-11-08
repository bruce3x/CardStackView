package me.brucezz.sample;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by david on 16/5/26.
 * Email: huangdiv5@gmail.com
 * GitHub: https://github.com/alighters
 */
public class DisplayUtil {

    private static final String TAG = DisplayUtil.class.getSimpleName();

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidthPixel() {
        return getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeightPixel() {
        return getDisplayMetrics().heightPixels;
    }

    /**
     * 获取 显示信息
     */
    public static DisplayMetrics getDisplayMetrics() {
        return mContext.getResources().getDisplayMetrics();
    }

    /**
     * px转dp
     */
    public static int px2Dp(int px) {
        return (int) (px / getDisplayMetrics().density);
    }

    /**
     * px转dp
     */
    public static int dp2Px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getDisplayMetrics());
    }

    /**
     * sx转dp
     */
    public static int sp2Px(float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getDisplayMetrics());
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Resources.NotFoundException exception) {
        }
        return statusBarHeight;
    }

    /**
     * 获取导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
