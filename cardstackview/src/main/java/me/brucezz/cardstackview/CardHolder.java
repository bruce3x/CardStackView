package me.brucezz.cardstackview;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

/**
 * Created by brucezz on 2016-10-12.
 * Github: https://github.com/brucezz
 * Email: im.brucezz@gmail.com
 */

public class CardHolder {
    private static final String TAG = "CardHolder";

    public ViewGroup mParent;

    public View mView;
    /**
     * 在 ViewGroup 中的位置
     */
    public int mChildIndex;
    /**
     * 卡片位置
     */
    public int mRealIndex;

    /**
     * 绘制顺序
     */
    public int mDrawOrder;

    /**
     * 绘制顺序是否已更新
     */
    public boolean mDrawOrderUpdated;

    /**
     * 正在执行的动画
     */
    public ValueAnimator mRunningAnimator;

    public Options mOptions;

    public CardHolder(ViewGroup parent, View view, int childIndex, Options options) {
        this.mParent = parent;
        this.mView = view;
        this.mChildIndex = childIndex;
        this.mRealIndex = childIndex;
        this.mDrawOrder = childIndex;
        this.mOptions = options;
    }

    public int getFixedLeft() {
        return mParent.getPaddingLeft();
    }

    public int getFixedRight() {
        return mParent.getWidth() - mParent.getPaddingRight();
    }

    public int getFixedTop() {
        return mParent.getPaddingTop() + mRealIndex * mOptions.CARD_SPAN_CURRENT;
    }

    public int getFixedBottom() {
        return getFixedTop() + mOptions.CARD_HEIGHT;
    }

    /**
     * 固定位置
     */
    public void layoutFixed() {
        this.mView.layout(getFixedLeft(), getFixedTop(), getFixedRight(), getFixedBottom());
    }

    /**
     * View 重置到某位置
     */
    public void reset() {
        checkIfAnimatorRunning();

        final int start = this.mView.getTop();
        final int end = getFixedTop();

        Log.d(TAG, String.format("reset: View %s reset to %d", this.mView.getTag(), this.mRealIndex));

        mRunningAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(mOptions.CARD_RESET_DURATION);
        mRunningAnimator.addUpdateListener(new AnimatorUpdateListenerImpl(this, null, start, end, end));
        mRunningAnimator.start();
    }

    /**
     * 如果正在执行动画，取消掉
     */
    private void checkIfAnimatorRunning() {
        if (isAnimating()) {
            Log.w(TAG, String.format("checkIfAnimatorRunning: card #%d is animating!", mRealIndex));
            mRunningAnimator.cancel();
        }
    }

    /**
     * 两段位移运动，根据进度计算更新位置
     */
    public void updateView(CardHolder dragging, int start, int mid, int end, float percent) {

        int firstDown = mid - start;
        int thenUp = mid - end;

        int total = firstDown + thenUp;
        float corner = ((float) firstDown) / total; // 转折点

        if (dragging != null && percent > corner && !mDrawOrderUpdated) {
            // 更新卡片的绘制顺序
            updateDrawOrder(dragging);
        }

        int dy;
        if (percent <= corner) {
            dy = (int) (percent / corner * firstDown);
        } else {
            dy = (int) (firstDown - (percent - corner) / (1 - corner) * thenUp);
        }

        mView.layout(getFixedLeft(), start + dy, getFixedRight(), start + dy + mOptions.CARD_HEIGHT);
    }

    /**
     * 更新卡片的绘制顺序， 跟 mRealIndex 是一致的
     */
    public void updateDrawOrder(CardHolder dragging) {
        this.mDrawOrder = this.mRealIndex;
        if (dragging == null) return;
        dragging.mDrawOrder = dragging.mRealIndex;
    }

    /**
     * 触发交换动画
     */
    public void onSwap(final CardHolder dragging) {
        checkIfAnimatorRunning();

        boolean up = dragging.mRealIndex < this.mRealIndex;

        final int directionFlag = up ? 1 : -1;
        final int downDistance =
            (int) (mOptions.CARD_HEIGHT - (1 - mOptions.CARD_SWAP_THRESHOLD) * mOptions.CARD_SPAN_CURRENT * directionFlag);
        final int upDistance =
            (int) (mOptions.CARD_HEIGHT + mOptions.CARD_SWAP_THRESHOLD * mOptions.CARD_SPAN_CURRENT * directionFlag);

        final int start = this.mView.getTop();
        final int mid = getFixedTop() + downDistance;
        final int end = mid - upDistance;

        mDrawOrderUpdated = false;

        Log.d(TAG, "onSwap: start=" + start + ", mid=" + mid + ", end=" + end);
        mRunningAnimator = ValueAnimator.ofFloat(0f, 1f);
        mRunningAnimator.setDuration(mOptions.CARD_SWAP_DURATION);
        mRunningAnimator.addUpdateListener(new AnimatorUpdateListenerImpl(this, dragging, start, mid, end));
        mRunningAnimator.addListener(new AnimatorListenerImpl(this, dragging));
        mRunningAnimator.setInterpolator(new OvershootInterpolator(1f));
        mRunningAnimator.start();
    }

    /**
     * 判断是否在执行动画
     */
    private boolean isAnimating() {
        return mRunningAnimator != null && mRunningAnimator.isRunning();
    }

    /**
     * 被选中时上浮动画
     */
    public void onSelect() {

        final int start = this.mView.getTop();
        final int end = start - mOptions.CARD_FLOAT_UP;

        mRunningAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(mOptions.CARD_FLOAT_DURATION);
        mRunningAnimator.addUpdateListener(new AnimatorUpdateListenerImpl(this, null, start, end, end));
        mRunningAnimator.start();
    }
}
