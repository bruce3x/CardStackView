package me.brucezz.sample;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private List<Card> mCards;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            modifyData();
            mHandler.sendEmptyMessageDelayed(0, 2000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mCardStackView = (CardStackView) findViewById(R.id.card_stack_view);
        mCardStackView.setOnCardClickListener(new CardStackView.OnCardClickListener() {
            @Override
            public void onClick(View view, int realIndex, int initialIndex) {
                //Toast.makeText(MainActivity.this, "点击了第" + realIndex + "个卡片 => " + mCards.get(initialIndex).mTitle,
                //    Toast.LENGTH_SHORT).show();

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
                Log.d("TAG", "onPositionChanged: " + sb.toString());
            }
        });

        mCards = fakeCards();
        mCardAdapter = new SimpleCardAdapter(this, mCards);

        mCardStackView.setAdapter(mCardAdapter);

        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
    }

    private List<Card> fakeCards() {

        return Arrays.asList(new Card(0xFF2196F3, R.drawable.post, "动态"), new Card(0xFF17B084, R.drawable.task, "任务"),
            new Card(0xFFE85D72, R.drawable.calendar, "日程"), new Card(0xFF00BACF, R.drawable.knowledge, "知识"));
    }

    private void modifyData() {
        for (int i = 0; i < mCards.size(); i++) {
            mCards.get(i).mTitle += String.valueOf(i);
        }
        mCardAdapter.notifyDataSetChanged();
    }

    private AnimationHelper mHelper;

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

    public void animOtherCards(int idx, boolean reverse) {
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

    private class AnimationHelper {

        public int mIndex;

        public CardView mCardView;
        public TextView mTitle;
        public ImageView mImageView;

        public int startLeft;
        public int endLeft;
        public int startRight;
        public int endRight;
        public int startTop;
        public int endTop;
        public int startBottom;
        public int endBottom;

        public float startRadius;
        public float endRadius;

        public @ColorInt int startColor;
        public @ColorInt int endColor;
        private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();

        public static final long ANIMATION_DURATION_CARD = 5000L;
        public static final long ANIMATION_DURATION_ALPHA = 300L;

        public AnimationHelper(View view, int index) {
            mIndex = index;
            mCardView = ((CardView) view);
            mTitle = (TextView) mCardView.findViewById(R.id.card_title);
            mImageView = (ImageView) mCardView.findViewById(R.id.card_image);

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
            endColor = mCards.get(mIndex).mBgColor;
        }

        public int getLeft(float fraction) {
            return (int) (startLeft + fraction * (endLeft - startLeft));
        }

        public int getRight(float fraction) {
            return (int) (startRight + fraction * (endRight - startRight));
        }

        public int getTop(float fraction) {
            return (int) (startTop + fraction * (endTop - startTop));
        }

        public int getBottom(float fraction) {
            return (int) (startBottom + fraction * (endBottom - startBottom));
        }

        public float getRadius(float fraction) {
            return startRadius + fraction * (endRadius - startRadius);
        }

        @ColorInt
        public int getColor(float fraction) {
            return (int) mArgbEvaluator.evaluate(fraction, startColor, endColor);
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
                    if (!reverse) {
                        mTitle.animate().alpha(0f).setDuration(ANIMATION_DURATION_ALPHA).start();
                        mToolbar.animate().alpha(0f).setDuration(ANIMATION_DURATION_ALPHA).start();
                    }

                    animOtherCards(mIndex, reverse);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (reverse) {
                        mTitle.animate().alpha(1f).setDuration(ANIMATION_DURATION_ALPHA).start();
                        mToolbar.animate().alpha(1f).setDuration(ANIMATION_DURATION_ALPHA).start();
                    } else {
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("card", mCards.get(mIndex));
                        startActivityForResult(intent, REQ_DETAIL);
                        overridePendingTransition(0, 0);
                    }
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
            mCardView.setRadius(getRadius(fraction));
            mCardView.getLayoutParams().width = getRight(fraction) - getLeft(fraction);
            mCardView.layout(getLeft(fraction), getTop(fraction), getRight(fraction), getBottom(fraction));
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
            //mImageView.postInvalidate();
            mTitle.setAlpha(1f);
            mToolbar.setAlpha(1f);
        }
    }
}
