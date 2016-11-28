package me.brucezz.cardstackview;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Arrays;
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
    private ViewGroup mParent;

    public CardFactory(ViewGroup parent, Options options, int count) {
        mParent = parent;
        mOptions = options;
        for (int i = 0; i < count; i++) {
            mCardHolders.add(null);// 添加 null 占位
        }
    }

    public void add(int orderIndex, int childIndex, View child) {
        if (mCardHolders.size() > orderIndex && mCardHolders.get(orderIndex) != null) {
            throw new IllegalArgumentException("You have put a card to order #%d" + orderIndex);
        }
        mCardHolders.set(orderIndex, new CardHolder(mParent, child, childIndex, orderIndex, mOptions));
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
        Integer[] orders = new Integer[mCardHolders.size()];
        for (int i = 0; i < mCardHolders.size(); i++) {
            int childIndex = mCardHolders.get(i).mChildIndex;
            orders[childIndex] = i;
        }

        return Arrays.asList(orders);
    }
}
