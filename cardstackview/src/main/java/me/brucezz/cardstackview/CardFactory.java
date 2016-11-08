package me.brucezz.cardstackview;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by brucezz on 2016-10-12.
 * Github: https://github.com/brucezz
 * Email: im.brucezz@gmail.com
 */

public class CardFactory {
    private ArrayList<CardHolder> mCardHolders = new ArrayList<>();

    private Options mOptions;

    public CardFactory(ViewGroup parent, Options options) {
        mOptions = options;
        for (int i = 0; i < parent.getChildCount(); i++) {
            mCardHolders.add(new CardHolder(parent, parent.getChildAt(i), i, mOptions));
        }
    }

    public int size() {
        return mCardHolders.size();
    }

    public CardHolder get(int index) {
        return mCardHolders.get(index);
    }

    public CardHolder findByView(@NonNull View view) {
        for (CardHolder holder : mCardHolders) {
            if (view.equals(holder.mView)) {
                return holder;
            }
        }
        return null;
    }

    public CardHolder findByDrawOrder(int order) {
        for (CardHolder holder : mCardHolders) {
            if (order == holder.mDrawOrder) {
                return holder;
            }
        }
        return null;
    }

    public CardHolder findByRealIndex(int index) {
        for (CardHolder holder : mCardHolders) {
            if (index == holder.mRealIndex) {
                return holder;
            }
        }
        return null;
    }

    public CardHolder findByTouch(float touchY) {
        if (mCardHolders.size() == 0) return null;

        int index;
        int count = mCardHolders.size() - 1;
        int span = count * mOptions.CARD_SPAN_CURRENT;
        if (touchY > span + mOptions.CARD_HEIGHT) {
            return null;
        } else if (touchY > span) {
            index = count;
        } else {
            index = (int) Math.floor(touchY / mOptions.CARD_SPAN_CURRENT);
        }

        return findByRealIndex(index);
    }

    public void swapRealIndex(int one, int another) {
        Collections.swap(mCardHolders, one, another);
        mCardHolders.get(one).mRealIndex = one;
        mCardHolders.get(another).mRealIndex = another;
    }

    public List<Integer> getAllPosition() {
        List<Integer> all = new ArrayList<>();
        for (CardHolder holder : mCardHolders) {
            all.add(holder.mChildIndex);
        }

        return all;
    }
}
