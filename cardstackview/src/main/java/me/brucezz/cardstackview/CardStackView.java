package me.brucezz.cardstackview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import java.util.List;

/**
 * Created by brucezz on 2016-10-08.
 * Github: https://github.com/brucezz
 * Email: im.brucezz@gmail.com
 */

public class CardStackView extends ViewGroup {
    private static final String TAG = "CardStackView";

    private final DataSetObserver mObserver = new CardStackViewDataObserver();

    private CardFactory mCardFactory;
    private Options mOptions;

    private CardAdapter mCardAdapter;

    private ViewDragHelper mViewDragHelper;
    private GestureDetectorCompat mGestureDetector;
    private OnCardClickListener mOnCardClickListener;
    private OnPositionChangedListener mOnPositionChangedListener;
    /**
     * 阻力滑动计算
     */
    private SlidingResistanceCalculator mSlidingResistanceCalculator;

    private CardHolder mSelected;

    private boolean mSkipLayout = false;
    private boolean mSkipTouch = false;
    private int mCardHeight;
    private int mCardMinSpan;

    public void setSkipLayout(boolean skipLayout) {
        mSkipLayout = skipLayout;
    }

    public void setSkipTouch(boolean skipTouch) {
        mSkipTouch = skipTouch;
    }

    public CardStackView(Context context) {
        this(context, null);
    }

    public CardStackView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardStackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CardStackView, defStyleAttr, 0);
        mCardHeight = ta.getDimensionPixelSize(R.styleable.CardStackView_card_height, Util.dp2px(getContext(), 160));
        mCardMinSpan = ta.getDimensionPixelSize(R.styleable.CardStackView_card_min_span, Util.dp2px(getContext(), 40));
        ta.recycle();

        initTouchCallback();

        setChildrenDrawingOrderEnabled(true);
        setClipToPadding(false);
    }

    private void initViews() {
        mOptions = new Options();
        mCardFactory = new CardFactory(this, mOptions, mCardAdapter.getItemCount());

        for (int i = 0; i < mCardAdapter.getItemCount(); i++) {
            View child = mCardAdapter.getView(null, i, this);
            this.addView(child);
            mCardFactory.add(mCardAdapter.getOrder(i), i, child);
        }

        mSlidingResistanceCalculator = new SlidingResistanceCalculator(2000f, mOptions.CARD_SPAN_OFFSET);

        updateOptions();
    }

    private void initTouchCallback() {

        ViewDragHelper.Callback dragCallback = new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return false;
            }

            @Override
            public void onViewReleased(final View releasedChild, float xvel, float yvel) {
                CardHolder view = mCardFactory.findByView(releasedChild);
                if (view != null) {
                    view.reset();
                }

                mSelected = null;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                //return left;
                CardHolder holder = mCardFactory.findByView(child);
                return holder != null ? holder.getFixedLeft() : left;
            }

            @Override
            public int getOrderedChildIndex(int index) {
                return getChildDrawingOrder(mCardFactory.size(), index);
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                if (changedView != mSelected.mView) return;

                CardHolder holder = mCardFactory.findByView(changedView);
                if (holder == null) return;

                int diff = top - holder.getFixedTop(); // 位移距离

                float threshold = mOptions.CARD_SWAP_THRESHOLD * mOptions.CARD_SPAN_CURRENT;

                if (Math.abs(diff) < threshold) return;

                // 如果是两个边缘的卡片，不交换
                if ((diff > 0 && holder.mRealIndex < mCardFactory.size() - 1) || (diff < 0 && holder.mRealIndex > 0)) {
                    int directionFlag = diff > 0 ? 1 : -1;

                    mCardFactory.findByRealIndex(holder.mRealIndex + directionFlag).onSwap(holder);

                    mCardFactory.swapRealIndex(holder.mRealIndex, holder.mRealIndex + directionFlag);
                    if (mOnPositionChangedListener != null) {
                        mOnPositionChangedListener.onPositionChanged(mCardFactory.getAllPosition());
                    }
                }
            }
        };

        mViewDragHelper = ViewDragHelper.create(this, dragCallback);

        GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float distance = e2.getY() - e1.getY();

                if (mSelected == null) {
                    mOptions.CARD_SPAN_CURRENT =
                        (int) (mOptions.CARD_SPAN_NORMAL + mSlidingResistanceCalculator.getOutput(distance));
                    updateCardPosition();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                //Log.d(TAG, String.format("onLongPress: %f, %f", e.getX(), e.getY()));
                mSelected = mCardFactory.findByTouch(e.getY() - getPaddingTop());
                if (mSelected != null) {
                    mSelected.onSelect();
                    mViewDragHelper.captureChildView(mSelected.mView, 0);
                }
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                final CardHolder touch = mCardFactory.findByTouch(e.getY() - getPaddingTop());
                if (touch != null && mOnCardClickListener != null) {
                    mOnCardClickListener.onClick(touch.mView, touch.mRealIndex, touch.mChildIndex);
                }
                return true;
            }
        };
        mGestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
    }

    /**
     * 计算一些常量值
     */
    private void updateOptions() {
        int parentH = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int parentW = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

        mOptions.CARD_HEIGHT = mCardHeight;
        mOptions.CARD_WIDTH = parentW;

        mOptions.CARD_SPAN_NORMAL_MIN = mCardMinSpan;
        int span = (parentH - mOptions.CARD_HEIGHT) / (mCardAdapter.getItemCount() - 1);
        mOptions.CARD_SPAN_NORMAL = Math.max(span, mOptions.CARD_SPAN_NORMAL_MIN);

        mOptions.CARD_SPAN_CURRENT = mOptions.CARD_SPAN_NORMAL;
        mOptions.CARD_SPAN_OFFSET = (int) (mOptions.CARD_SPAN_NORMAL * mOptions.CARD_SPAN_OFFSET_PERCENT);

        mOptions.CARD_FLOAT_UP = (int) (mOptions.CARD_SPAN_NORMAL * mOptions.CARD_FLOAT_UP_PERCENT);

        if (mSlidingResistanceCalculator != null) {
            mSlidingResistanceCalculator.setMaxOutput(mOptions.CARD_SPAN_OFFSET);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mSkipTouch) return false;

        if (event.getAction() == MotionEvent.ACTION_UP && mSelected == null) {
            resetCardSpan();
        }

        mViewDragHelper.processTouchEvent(event);

        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int order) {
        CardHolder holder = mCardFactory.findByDrawOrder(order);
        return holder != null ? holder.mChildIndex : 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = 0, height = 0;

        int maxWidth = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
        }

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            // wrap_content  use min span
            if (existsValidChild()) {
                width = maxWidth;
                height = mOptions.CARD_SPAN_NORMAL_MIN * (getChildCount() - 1) + mOptions.CARD_HEIGHT;
            }
            width += (getPaddingLeft() + getPaddingRight());
            height += (getPaddingTop() + getPaddingBottom());
        } else {
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
        //Log.d(TAG, "onMeasure: w="+width+", h="+height);
        updateOptions();
    }

    /**
     * 存在有效的卡片 View
     */
    private boolean existsValidChild() {
        return mOptions != null && mCardFactory != null && mCardFactory.size() != 0;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mSkipLayout) return;
        updateCardPosition();
    }

    /**
     * 更新卡片位置
     */
    public void updateCardPosition() {
        if (!existsValidChild()) return;

        for (int i = 0; i < mCardFactory.size(); i++) {
            CardHolder holder = mCardFactory.get(i);
            if (!holder.isAnimating()) {
                holder.layoutFixed();
            }
        }
    }

    public void setAdapter(CardAdapter adapter) {
        if (mCardAdapter != null) {
            mCardAdapter.unregisterDataSetObserver(mObserver);
        }

        this.mCardAdapter = adapter;
        if (mCardAdapter != null && mCardAdapter.getItemCount() > 0) {
            mCardAdapter.registerDataSetObserver(mObserver);
            initViews();
        }
    }

    public void setOnCardClickListener(OnCardClickListener listener) {
        this.mOnCardClickListener = listener;
    }

    public void setOnPositionChangedListener(OnPositionChangedListener onPositionChangedListener) {
        mOnPositionChangedListener = onPositionChangedListener;
    }

    /**
     * 重置卡片间距
     */
    private void resetCardSpan() {
        ValueAnimator spanResetAnimator = ValueAnimator.ofInt(mOptions.CARD_SPAN_CURRENT, mOptions.CARD_SPAN_NORMAL);
        spanResetAnimator.setDuration(mOptions.SPAN_RESET_DURATION);
        spanResetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOptions.CARD_SPAN_CURRENT = (int) animation.getAnimatedValue();
                updateCardPosition();
            }
        });
        spanResetAnimator.setInterpolator(new OvershootInterpolator());
        spanResetAnimator.start();
    }

    public interface OnCardClickListener {

        /**
         * @param view         被点击的 View
         * @param realIndex    当前 View 的位置
         * @param initialIndex 初始化时 View 的位置
         */
        void onClick(View view, int realIndex, int initialIndex);
    }

    public interface OnPositionChangedListener {
        /**
         * @param position 原始卡片对应当前排序的位置
         *                 如原始第 1 张卡片现在排在第 3 个，则 position.get(1) = 3
         */
        void onPositionChanged(List<Integer> position);
    }

    private class CardStackViewDataObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            for (int i = 0; i < mCardFactory.size(); i++) {
                CardHolder holder = mCardFactory.get(i);
                holder.mView = mCardAdapter.getView(holder.mView, holder.mChildIndex, CardStackView.this);
            }
        }

        @Override
        public void onInvalidated() {
            for (int i = 0; i < mCardFactory.size(); i++) {
                CardHolder holder = mCardFactory.get(i);
                holder.mView.postInvalidate();
            }
        }
    }
}
