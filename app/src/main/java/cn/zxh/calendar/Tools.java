package cn.zxh.calendar;

import android.content.Context;
import android.graphics.Paint;

public class Tools {
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    /**
     * 校准文字位置
     * */
    public static int getBaseLine(Paint textPaint, int centerY){
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

        return (int) (centerY - top/2 - bottom/2);//基线中间点的y轴计算公式
    }
}
