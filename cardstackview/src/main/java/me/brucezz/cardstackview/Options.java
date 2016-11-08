package me.brucezz.cardstackview;

/**
 * Created by brucezz on 2016-10-12.
 * Github: https://github.com/brucezz
 * Email: im.brucezz@gmail.com
 */

public class Options {
    /**
     * 卡片当前绘制的间距
     */
    public int CARD_SPAN_CURRENT;
    /**
     * 卡片正常情况下的间距
     */
    public int CARD_SPAN_NORMAL;
    /**
     * 卡片正常情况下最小间距
     */
    public int CARD_SPAN_NORMAL_MIN;
    /**
     * 卡片间距可偏移的距离
     */
    public int CARD_SPAN_OFFSET;
    /**
     * 滑动时卡片间距可偏移的幅度
     */
    public float CARD_SPAN_OFFSET_PERCENT = .33f;
    /**
     * 卡片的高度
     */
    public int CARD_HEIGHT;
    /**
     * 卡片的宽度
     */
    public int CARD_WIDTH;

    /**
     * 长按时卡片浮起的高度
     */
    public int CARD_FLOAT_UP;
    /**
     * 长按卡片浮起的幅度
     */
    public float CARD_FLOAT_UP_PERCENT = 0.33f;
    /**
     * 卡片移动触发交换动画的距离阈值
     */
    public float CARD_SWAP_THRESHOLD = 0.65f;
    /**
     * 交换动画时长
     */
    public long CARD_SWAP_DURATION = 300L;
    /**
     * 重置动画时长
     */
    public long CARD_RESET_DURATION = 150L;
    /**
     * 浮起动画时长
     */
    public long CARD_FLOAT_DURATION = 150L;
    /**
     * 卡片间距重置动画时长
     */
    public long SPAN_RESET_DURATION = 300L;
    /**
     * 点击卡片之后移除其他卡片
     */
    public boolean CLICK_TO_SEPARATE = true;
    /**
     * 移出屏幕的动画时长
     */
    public long CARD_SLIP_OUT = 500L;

    @Override
    public String toString() {
        return "Options{" +
            ", \nCARD_SPAN_CURRENT=" + CARD_SPAN_CURRENT +
            ", \nCARD_SPAN_NORMAL=" + CARD_SPAN_NORMAL +
            ", \nCARD_SPAN_NORMAL_MIN=" + CARD_SPAN_NORMAL_MIN +
            ", \nCARD_SPAN_OFFSET=" + CARD_SPAN_OFFSET +
            ", \nCARD_SPAN_OFFSET_PERCENT=" + CARD_SPAN_OFFSET_PERCENT +
            ", \nCARD_HEIGHT=" + CARD_HEIGHT +
            ", \nCARD_WIDTH=" + CARD_WIDTH +
            ", \nCARD_FLOAT_UP=" + CARD_FLOAT_UP +
            ", \nCARD_FLOAT_UP_PERCENT=" + CARD_FLOAT_UP_PERCENT +
            ", \nCARD_SWAP_THRESHOLD=" + CARD_SWAP_THRESHOLD +
            ", \nCARD_SWAP_DURATION=" + CARD_SWAP_DURATION +
            ", \nCARD_RESET_DURATION=" + CARD_RESET_DURATION +
            ", \nCARD_FLOAT_DURATION=" + CARD_FLOAT_DURATION +
            ", \nSPAN_RESET_DURATION=" + SPAN_RESET_DURATION +
            "\n}";
    }
}
