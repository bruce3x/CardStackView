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
     * 获取卡片的排序位置
     * 如 原始第 1 张卡片，现在要摆放在第 3 的位置
     * 则 getOrder(1) return 3;
     *
     * @param position 原始卡片的位置
     * @return 当前排序所处的位置
     */
    public abstract int getOrder(int position);

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
