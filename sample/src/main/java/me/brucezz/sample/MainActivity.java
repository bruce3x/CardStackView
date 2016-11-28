package me.brucezz.sample;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jaeger.library.StatusBarUtil;
import java.util.Arrays;
import java.util.List;
import me.brucezz.cardstackview.CardStackView;

public class MainActivity extends AppCompatActivity {

    public static final int REQ_DETAIL = 0x233;
    Toolbar mToolbar;

    CardStackView mCardStackView;
    SimpleCardAdapter mCardAdapter;
    private List<Pair<Integer, Card>> mCards;

    private AnimationHelper mHelper;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mCardStackView = (CardStackView) findViewById(R.id.card_stack_view);
        mCardStackView.setOnCardClickListener(new CardStackView.OnCardClickListener() {
            @Override
            public void onClick(View view, int realIndex, int initialIndex) {
                toggleAnimation(view, initialIndex);
            }
        });
        mCardStackView.setOnPositionChangedListener(new CardStackView.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(List<Integer> position) {
                StringBuilder sb = new StringBuilder();
                for (Integer integer : position) {
                    sb.append(integer).append(" ");
                }
                mPreferences.edit().putString("order", sb.toString()).apply();
            }
        });

        mPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String order = mPreferences.getString("order", "");
        int[] orders;
        if (TextUtils.isEmpty(order)) {
            orders = new int[] { 0, 1, 2, 3 };
        } else {
            String[] ordersArr = order.trim().split(" ");
            orders = new int[ordersArr.length];
            for (int i = 0; i < ordersArr.length; i++) {
                orders[i] = Integer.valueOf(ordersArr[i]);
            }
        }
        mCards = initCards(orders);
        mCardAdapter = new SimpleCardAdapter(this, mCards);

        mCardStackView.setAdapter(mCardAdapter);

        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(mPreferences.getString("order", ""));
            }
        });
    }

    private List<Pair<Integer, Card>> initCards(int... orders) {
        return Arrays.asList(Pair.create(orders[0], new Card(0xFF00BACF, R.drawable.knowledge, "知识")),
            Pair.create(orders[1], new Card(0xFFE85D72, R.drawable.calendar, "日程")),
            Pair.create(orders[2], new Card(0xFF17B084, R.drawable.task, "任务")),
            Pair.create(orders[3], new Card(0xFF2196F3, R.drawable.post, "动态")));
    }

    private void toggleAnimation(final View view, final int index) {
        mHelper = new AnimationHelper(view, index);

        mHelper.startAnimation(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return;

        if (requestCode == REQ_DETAIL && resultCode == RESULT_OK) {
            mHelper.startAnimation(true);
        } else {
            mHelper.resetViews();
        }
    }

    private class AnimationHelper {

        private int mIndex;

        private CardView mCardView;
        private ImageView mImageView;
        private TextView mTitle;
        private TextView mFoo;

        private int startLeft, endLeft;
        private int startRight, endRight;
        private int startTop, endTop;
        private int startBottom, endBottom;

        private float startRadius, endRadius;

        private @ColorInt int startColor, endColor;
        private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();

        private float startElevation;
        private float endElevation;

        private static final long ANIMATION_DURATION_CARD = 500L;
        private static final long ANIMATION_DURATION_ALPHA = 300L;

        public AnimationHelper(View view, int index) {
            mIndex = index;
            mCardView = ((CardView) view);
            mImageView = (ImageView) mCardView.findViewById(R.id.card_image);
            mTitle = (TextView) mCardView.findViewById(R.id.card_title);
            mFoo = (TextView) mCardView.findViewById(R.id.card_foo);

            startLeft = mCardView.getLeft();
            startRight = mCardView.getRight();
            startTop = mCardView.getTop();
            startBottom = mCardView.getBottom();

            endLeft = 0;
            endRight = mCardView.getWidth() + 2 * mCardView.getLeft();
            endTop = 0;
            endBottom = mCardView.getHeight();

            startRadius = mCardView.getRadius();
            endRadius = 1f;// radius 减到 0 会自动产生透明度变化

            startColor = getResources().getColor(R.color.colorPrimaryDark);
            endColor = mCards.get(mIndex).second.mBgColor;

            startElevation = mCardView.getMaxCardElevation();
            endElevation = 0f;
        }

        @ColorInt
        private int getColor(float fraction) {
            return (int) mArgbEvaluator.evaluate(fraction, startColor, endColor);
        }

        private int getInterpolation(int start, int end, float fraction) {
            return (int) (start + fraction * (end - start));
        }

        private int getInterpolation(float start, float end, float fraction) {
            return (int) (start + fraction * (end - start));
        }

        private float getElevation(float start, float end, float fraction, float threshold) {
            if (fraction <= threshold) return start;

            return start + (end - start) * (fraction - threshold) / (1 - threshold);
        }

        public void startAnimation(final boolean reverse) {
            ValueAnimator animator =
                ValueAnimator.ofFloat(reverse ? 1f : 0f, reverse ? 0f : 1f).setDuration(ANIMATION_DURATION_CARD);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    update((float) animation.getAnimatedValue());
                }
            });

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mCardStackView.setSkipLayout(true);
                    mCardStackView.setSkipTouch(true);
                    if (!reverse) {
                        mToolbar.animate().alpha(0f).setDuration(ANIMATION_DURATION_ALPHA).start();
                        mTitle.animate().alpha(0f).setDuration(ANIMATION_DURATION_ALPHA).start();
                        mFoo.animate().alpha(0f).setDuration(ANIMATION_DURATION_ALPHA).start();
                    }

                    animOtherCards(mIndex, reverse);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (reverse) {
                        mToolbar.animate().alpha(1f).setDuration(ANIMATION_DURATION_ALPHA).start();
                        mTitle.animate().alpha(1f).setDuration(ANIMATION_DURATION_ALPHA).start();
                        mFoo.animate().alpha(1f).setDuration(ANIMATION_DURATION_ALPHA).start();
                    } else {
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("card", mCards.get(mIndex).second);
                        startActivityForResult(intent, REQ_DETAIL);
                        overridePendingTransition(0, 0);
                    }
                    mCardStackView.post(new Runnable() {
                        @Override
                        public void run() {
                            mCardStackView.setSkipLayout(false);
                            mCardStackView.setSkipTouch(false);
                        }
                    });
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        }

        private void update(float fraction) {
            mCardView.setRadius(getInterpolation(startRadius, endRadius, fraction));
            int right = getInterpolation(startRight, endRight, fraction);
            int left = getInterpolation(startLeft, endLeft, fraction);
            int top = getInterpolation(startTop, endTop, fraction);
            int bottom = getInterpolation(startBottom, endBottom, fraction);
            mCardView.getLayoutParams().width = right - left;
            mCardView.layout(left, top, right, bottom);

            /**
             * 设置了一个阈值 threshold， 进度超过阈值之后才开始进行插值。
             * 也就是等到其他卡片都收起来之后，再对当前卡片做阴影动画，避免在 5.x 绘制层级问题。
             *
             * 最终阴影变为0，避免了在 4.x 上 CardView 自带边距绘制阴影，导致无法无缝切换的问题。
             */
            float elevation =
                getElevation(startElevation, endElevation, fraction, ANIMATION_DURATION_ALPHA * 1f / ANIMATION_DURATION_CARD);
            mCardView.setMaxCardElevation(elevation);
            mCardView.setCardElevation(elevation);

            mCardView.requestLayout();
            StatusBarUtil.setColor(MainActivity.this, getColor(fraction));
        }

        /**
         * 不作动画直接重置视图
         */
        public void resetViews() {
            for (int i = 0; i < mCardStackView.getChildCount(); i++) {
                if (i == mIndex) continue;
                mCardStackView.getChildAt(i).setTranslationY(0f);
            }

            update(0f);
            mTitle.setAlpha(1f);
            mFoo.setAlpha(1f);
            mToolbar.setAlpha(1f);
        }

        /**
         * 其他卡片直接进行简单的 translation 动画沿 Y 轴移动即可
         */
        private void animOtherCards(int idx, boolean reverse) {
            for (int i = 0; i < mCardStackView.getChildCount(); i++) {
                if (i == idx) continue;

                View view = mCardStackView.getChildAt(i);
                if (reverse) {
                    view.animate().translationY(0f).setDuration(AnimationHelper.ANIMATION_DURATION_CARD).start();
                } else {
                    int start = view.getTop();
                    int end = ((ViewGroup) view.getParent()).getBottom();
                    view.animate().translationY(end - start).setDuration(AnimationHelper.ANIMATION_DURATION_CARD).start();
                }
            }
        }
    }
}
