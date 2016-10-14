package me.brucezz.cardstackview;

import android.animation.ValueAnimator;

/**
 * Created by brucezz on 2016-10-12.
 * Github: https://github.com/brucezz
 * Email: im.brucezz@gmail.com
 */

public class AnimatorUpdateListenerImpl implements ValueAnimator.AnimatorUpdateListener {

    private CardHolder mRunning;
    private CardHolder mDragging;
    private int mStart, mMid, mEnd;

    public AnimatorUpdateListenerImpl(CardHolder running, CardHolder dragging, int start, int mid, int end) {
        mRunning = running;
        mDragging = dragging;
        mStart = start;
        mMid = mid;
        mEnd = end;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float value = (float) animation.getAnimatedValue();
        mRunning.updateView(mDragging, mStart, mMid, mEnd, value);
    }
}
