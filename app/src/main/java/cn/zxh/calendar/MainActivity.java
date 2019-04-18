package cn.zxh.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTv ;
    private CalendarLayout calLayout;
    private TextView mTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calLayout = findViewById(R.id.main_call);
        calLayout.setItemViewSize(Tools.dip2px(MainActivity.this,30),Tools.dip2px(MainActivity.this,50));
        ArrayList<Long> mDateList = new ArrayList<>();
        final Calendar today = Calendar.getInstance();

        today.setTime(new Date());
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND,0);
        mDateList.add(today.getTime().getTime());

        mTv = findViewById(R.id.main_content_tv);
        mTitleTv = findViewById(R.id.main_title);
        findViewById(R.id.main_exchange).setOnClickListener(this);
        findViewById(R.id.main_popup).setOnClickListener(this);

        //默认选中当天的效果
        ArrayList<CalendarLayout.DrawMarkRegion> regions = new ArrayList<>();
        CalendarLayout.DrawMarkRegion region = TestMarker.drawDotBeneath(Color.parseColor("#00FFFF"),10,mDateList);
        regions.add(region);
        //2019年休息日加班日
        CalendarLayout.DrawMarkRegion region2 = TestMarker.drawTextTopRightCorner(Color.parseColor("#FF1493"),Color.parseColor("#00FA9A"),15,getHolidayList(),getWorkingDayList());
        regions.add(region2);
        calLayout.setDrawMarkRegions(regions);

        //调整日历显示置灰区间
        calLayout.setSelectType(CalendarLayout.SelectType.ALL);
        //当选中的时间点为今天触发的事件
        calLayout.setOnDateSelectedListener(new CalendarLayout.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date selectedDate) {
                if(selectedDate.getTime() == today.getTime().getTime()){
                    mTv.setText("今天是个值得纪念的日子");
                }
                else{
                    mTv.setText("");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.main_exchange://切换
                if(calLayout.getDispalyMode().ordinal()==CalendarLayout.DisPlayMode.MONTH.ordinal()){
                    calLayout.setDisplayMode(CalendarLayout.DisPlayMode.WEEK);
                    mTitleTv.setText("周视图");
                }
                else {
                    calLayout.setDisplayMode(CalendarLayout.DisPlayMode.MONTH);
                    mTitleTv.setText("月视图");
                }
                calLayout.resetView();
                break;
            case R.id.main_popup://选择日期
                DatePickerPopWin mPopWin = new DatePickerPopWin.Builder(MainActivity.this, new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(Date seleDate) {
                        calLayout.skipToDate(seleDate);
                    }
                }).textConfirm("确认") //text of confirm button
                        .textCancel("取消") //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(23) // pick view text size
                        .minYear(1990) //min year in loop
                        .maxYear(2040) // max year in loop
                        .dateChose("2019-04-17") // date chose when init popwindow
                        .build();
                mPopWin.showPopWin(MainActivity.this);
                mPopWin.setDateScrollListener(new DatePickerPopWin.OnDateScrollListener() {
                                                  @Override
                                                  public void onDateScrollListener(Date date) {
//                                                      Toast.makeText(MainActivity.this,date.toString(),Toast.LENGTH_LONG).show();
                                                  }
                                              }
                );
                break;
        }
    }

    private ArrayList<Long> getHolidayList(){
        ArrayList<Long> holidays = new ArrayList<>();
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        //元旦
        calendar.set(Calendar.MONTH,0);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        holidays.add(calendar.getTime().getTime());
        //春节
        calendar.set(Calendar.MONTH,1);
        for(int i = 4;i<=10;i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            holidays.add(calendar.getTime().getTime());
        }
        //清明
        calendar.set(Calendar.MONTH,3);
        for(int i = 5;i<=7;i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            holidays.add(calendar.getTime().getTime());
        }
        //劳动
        calendar.set(Calendar.MONTH,4);
        for(int i = 1;i<=4;i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            holidays.add(calendar.getTime().getTime());
        }
        //端午
        calendar.set(Calendar.MONTH,5);
        for(int i = 7;i<=9;i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            holidays.add(calendar.getTime().getTime());
        }
        //中秋
        calendar.set(Calendar.MONTH,8);
        for(int i = 13;i<=15;i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            holidays.add(calendar.getTime().getTime());
        }
        //国庆
        calendar.set(Calendar.MONTH,9);
        for(int i = 1;i<=7;i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            holidays.add(calendar.getTime().getTime());
        }
        return holidays;
    }

    private ArrayList<Long> getWorkingDayList(){
        ArrayList<Long> workdays = new ArrayList<>();
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        //春节
        calendar.set(Calendar.MONTH,1);
        for(int i = 2;i<=3;i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            workdays.add(calendar.getTime().getTime());
        }
        //劳动
        calendar.set(Calendar.MONTH,3);
        calendar.set(Calendar.DAY_OF_MONTH, 28);
        workdays.add(calendar.getTime().getTime());
        calendar.set(Calendar.MONTH,4);
        calendar.set(Calendar.DAY_OF_MONTH, 5);
        workdays.add(calendar.getTime().getTime());
        //国庆
        calendar.set(Calendar.MONTH,8);
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        workdays.add(calendar.getTime().getTime());
        calendar.set(Calendar.MONTH,9);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        workdays.add(calendar.getTime().getTime());
        return workdays;
    }
}
