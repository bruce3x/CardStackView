package me.brucezz.cardstackview;

import android.animation.Animator;

/**
 * Created by brucezz on 2016-10-12.
 * Github: https://github.com/brucezz
 * Email: im.brucezz@gmail.com
 */

public class AnimationCancelListener implements Animator.AnimatorListener {
    private CardHolder mRunning;
    private CardHolder mDragging;

    public AnimationCancelListener(CardHolder running, CardHolder dragging) {
        mRunning = running;
        mDragging = dragging;
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        if (mDragging != null && !mRunning.mDrawOrderUpdated) {
            mRunning.updateDrawOrder(mDragging);
        }
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
