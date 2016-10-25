package me.brucezz.cardstackview;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by brucezz on 2016-10-12.
 * Github: https://github.com/brucezz
 * Email: im.brucezz@gmail.com
 */

public abstract class CardAdapter {
    /**
     * 会在 {@link CardStackView} 初始化的时候把所有的 View 都初始化添加进去
     */
    public abstract View getView(View oldView, int position, ViewGroup parent);

    /**
     * 获取 View 的数量
     */
    public abstract int getItemCount();

    /**
     * 获取每一个 View 的高度
     */
    public abstract int getCardHeight();

    /**
     * View 最小间隔
     */
    public abstract int getMinCardSpan();

    private final DataSetObservable mObservable = new DataSetObservable();

    public final void notifyDataSetChanged() {
        mObservable.notifyChanged();
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        mObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mObservable.unregisterObserver(observer);
    }
}
