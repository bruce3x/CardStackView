package me.brucezz.cardstackview;

/**
 * Created by brucezz on 2016-10-12.
 * Github: https://github.com/brucezz
 * Email: im.brucezz@gmail.com
 */

/**
 * 模拟滑动的阻力的计算
 *
 * 计算函数图像： http://ww4.sinaimg.cn/large/801b780agw1f8pkoxfwa8j20xa0ooq5a.jpg
 */
public class SlidingResistanceCalculator {

    private float mMaxInput;
    private float mMaxOutput;

    /**
     * @param maxInput  最大输入值(估计)
     * @param maxOutput 最大输出值(趋近于它)
     */
    public SlidingResistanceCalculator(float maxInput, float maxOutput) {
        mMaxInput = maxInput;
        mMaxOutput = maxOutput;
    }

    public float getOutput(float input) {
        return (float) (mMaxOutput * (2 / (1 + Math.exp(-5 * input / mMaxInput)) - 1));
    }

    public void setMaxInput(float maxInput) {
        mMaxInput = maxInput;
    }

    public void setMaxOutput(float maxOutput) {
        mMaxOutput = maxOutput;
    }
}
