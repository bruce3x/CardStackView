package me.brucezz.sample;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

/**
 * Created by brucezz on 2016-10-12.
 * Github: https://github.com/brucezz
 * Email: im.brucezz@gmail.com
 */

public class Card {

    @ColorInt int mBgColor;
    @DrawableRes int mImage;
    String mTitle;

    public Card(int bgColor, int image, String title) {
        mBgColor = bgColor;
        mImage = image;
        mTitle = title;
    }
}
