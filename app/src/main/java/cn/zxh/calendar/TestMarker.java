package cn.zxh.calendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Date;

public class TestMarker {
    /**
     * 位于日期正下方绘制点
     */
    public static CalendarLayout.DrawMarkRegion drawDotBeneath(final int color, final int radius, final ArrayList<Long> dateList) {
        return new CalendarLayout.DrawMarkRegion() {
            @Override
            public void onDraw(Canvas canvas, int viewWidth, int viewHeight, Date date) {
                if (dateList.contains(date.getTime())) {
                    //留下正方形区域位于正下方中心绘制点
                    int dotCenter = viewWidth + (viewHeight - viewWidth) / 2;
                    Paint dotPaint = new Paint();
                    dotPaint.setAntiAlias(true);
                    dotPaint.setColor(color);
                    canvas.drawCircle(viewWidth / 2, dotCenter, radius, dotPaint);
                }
            }
        };
    }

    /**
     * 位于日期右上方绘制
     */
    public static CalendarLayout.DrawMarkRegion drawTextTopRightCorner(final int holidayColor, final int workdayColor, final int radius, final ArrayList<Long> holidayList, final ArrayList<Long> workingList) {
        return new CalendarLayout.DrawMarkRegion() {
            @Override
            public void onDraw(Canvas canvas, int viewWidth, int viewHeight, Date date) {
                if (holidayList.contains(date.getTime())) {
                    int dotCenterX = viewWidth - radius;
                    Paint dotPaint = new Paint();
                    dotPaint.setAntiAlias(true);
                    dotPaint.setColor(holidayColor);
                    canvas.drawCircle(dotCenterX, radius, radius, dotPaint);
                    Paint textPaint = new Paint();
                    textPaint.setColor(Color.WHITE);
                    textPaint.setTextSize(20);
                    textPaint.setTextAlign(Paint.Align.CENTER);
                    textPaint.setAntiAlias(true);
                    canvas.drawText("假", dotCenterX, Tools.getBaseLine(textPaint,radius), textPaint);
                } else if (workingList.contains(date.getTime())) {
                    int dotCenterX = viewWidth - radius;
                    Paint dotPaint = new Paint();
                    dotPaint.setAntiAlias(true);
                    dotPaint.setColor(workdayColor);
                    canvas.drawCircle(dotCenterX, radius, radius, dotPaint);
                    Paint textPaint = new Paint();
                    textPaint.setColor(Color.WHITE);
                    textPaint.setTextSize(20);
                    textPaint.setTextAlign(Paint.Align.CENTER);
                    textPaint.setAntiAlias(true);
                    canvas.drawText("班", dotCenterX, Tools.getBaseLine(textPaint,radius), textPaint);
                }
            }
        };
    }
}
