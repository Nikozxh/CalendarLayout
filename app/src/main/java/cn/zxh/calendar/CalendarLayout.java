package cn.zxh.calendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


/**
 * Created by Administrator on 2018/8/31.
 */
public class CalendarLayout extends LinearLayout{
    private ViewPager mCalendarVp;
    private LinearLayout mTitleLl;
    private TextView mMonthYearTv;
    private boolean mTxtBold = false;
    private Date mCurDate;
    private Date mSelectedDate;
    private Date mTodayDate;
    private DisPlayMode mDisplayMode = DisPlayMode.MONTH;
    private int mTitleTxtSize = 14;
    private int mCalendarTxtSize = 14;
    private int mYearMonthTxtSize = 14;
    private int mYearMonthTxtColor = Color.DKGRAY;
    private int mTitleTxtColor = Color.DKGRAY;
    private int mWorkDayTxtColor = Color.BLACK;
    private int mWeekendTxtColor = Color.LTGRAY;
    private int mTodayCircleColor = ContextCompat.getColor(getContext(), R.color.theme);
    private boolean mIncludeToday = true;
    private int mTodayTxtColor = Color.WHITE;
    private int mSelectedCircleColor = ContextCompat.getColor(getContext(), R.color.focusColor);
    private CalendarPageAdapter mPageAdpter;

    private int lastPeriodNum = 1;
    private int mCurPos = 1;
    private int mSelectedPos = 1;

    private int itemViewHeight = -1;
    private int itemViewWidth = -1;
    private List<DrawMarkRegion> mDrawMarkRegions = new ArrayList<>();
    private OnDateSelectedListener mOnDateSelectedListener;
    private int nowPageIndex = 1;
    /**
     * 时间区间
     * */
    public enum SelectType{
        ALL,HISTORY,FUTURE
    }
    /**
     * 显示模式
     * */
    public enum DisPlayMode{
        MONTH,WEEK
    }

    public enum WeekDays{
        NOWEEKDAY,SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY;
        public static WeekDays valueOf(int value) {    //    手写的从int到enum的转换函数
            switch (value) {
                case 1:
                    return SUNDAY;
                case 2:
                    return MONDAY;
                case 3:
                    return TUESDAY;
                case 4:
                    return WEDNESDAY;
                case 5:
                    return THURSDAY;
                case 6:
                    return FRIDAY;
                case 7:
                    return SATURDAY;
                default:
                    return SUNDAY;
            }
        }
        public static String getChineseName(WeekDays day){
            switch (day.ordinal()) {
                case 1:
                    return "周日";
                case 2:
                    return "周一";
                case 3:
                    return "周二";
                case 4:
                    return "周三";
                case 5:
                    return "周四";
                case 6:
                    return "周五";
                case 7:
                    return "周六";
                default:
                    return "周日";
            }
        }
    }
    private ArrayList<CalendarAdapter> mAdapterList = new ArrayList<>();
    private WeekDays mFristWeekDay = WeekDays.SUNDAY;
    private SelectType mSelectType = SelectType.ALL;

    public CalendarLayout(Context context) {
        super(context);
        initView(context,null);
    }

    public CalendarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context,null);
    }

    public CalendarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CalendarLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context,null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(itemViewWidth<0){
            itemViewWidth = (getMeasuredWidth()-Tools.dip2px(getContext(),10)*8)/7;
        }
        if(itemViewHeight<0){
            itemViewHeight = itemViewWidth;
        }
    }

    private void initView(Context context,Date focusDate){
        super.setOrientation(VERTICAL);
        mCalendarVp = new ViewPager(context){
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                View view = this.getChildAt(0);
                if(view!=null){
                    if (view != null) {
                        // measure the current child view with the specified measure spec
                        view.measure(widthMeasureSpec, heightMeasureSpec);
                    }
                    int result = 0;
                    int specMode = MeasureSpec.getMode(heightMeasureSpec);
                    int specSize = MeasureSpec.getSize(heightMeasureSpec);

                    if (specMode == MeasureSpec.EXACTLY) {
                        result = specSize;
                    } else {
                        // set the height from the base view if available
                        if (view != null) {
                            result = view.getMeasuredHeight();
                        }
                        if (specMode == MeasureSpec.AT_MOST) {
                            result = Math.min(result, specSize);
                        }
                    }
                    setMeasuredDimension(getMeasuredWidth(), result);
                }
            }
        };
        mTitleLl =  new LinearLayout(context);
        mCurDate = new Date(System.currentTimeMillis());
        Calendar mCurCal = Calendar.getInstance();
        if(focusDate!=null)
            mCurCal.setTime(focusDate);
        else
            mCurCal.setTime(mCurDate);
        Calendar todayCalendar = Calendar.getInstance();
        if(focusDate!=null)
            todayCalendar.setTime(focusDate);
        else
            todayCalendar.setTime(new Date());

        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);
        todayCalendar.set(Calendar.MILLISECOND,0);
        mTodayDate =  todayCalendar.getTime();
        mSelectedDate = mTodayDate;

        int mDisplayYear = mCurCal.get(Calendar.YEAR);
        int mDisplayMonth = mCurCal.get(Calendar.MONTH);
        int mDisplayWeek = mCurCal.get(Calendar.WEEK_OF_YEAR);

        LayoutParams lps = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mCalendarVp.setLayoutParams(lps);
        mTitleLl.setLayoutParams(lps);
        mTitleLl.setPadding(0,20,0,20);

        addView(mTitleLl,0);
        addView(mCalendarVp,1);

        if(mDisplayMode ==DisPlayMode.MONTH) {
            mMonthYearTv = new TextView(context);
            mMonthYearTv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            mMonthYearTv.setGravity(Gravity.CENTER);
            int monthNum = mDisplayMonth+1;
            mMonthYearTv.setText(mDisplayYear + "年" + monthNum + "月");
            mMonthYearTv.setPadding(0, 20, 0, 20);
            mMonthYearTv.setTextSize(mYearMonthTxtSize);
            mMonthYearTv.setTextColor(mYearMonthTxtColor);
            addView(mMonthYearTv,0);

            Calendar lastMonCal = Calendar.getInstance();
            lastMonCal.setTime(mTodayDate);
            lastMonCal.add(Calendar.MONTH,-1);
            ArrayList<Date> lastMonthDateList = getDateOfMonthList(lastMonCal.get(Calendar.YEAR),lastMonCal.get(Calendar.MONTH));
            CalendarAdapter mLastMonthAdapter = new CalendarAdapter();
            mLastMonthAdapter.mDateList = lastMonthDateList;
            mLastMonthAdapter.periodMarkValue = lastMonCal.get(Calendar.MONTH);
            mAdapterList.add(mLastMonthAdapter);

            ArrayList<Date> thisMonthDateList = getDateOfMonthList(mDisplayYear,mDisplayMonth);
            CalendarAdapter mThisMonthAdapter = new CalendarAdapter();
            mThisMonthAdapter.mDateList = thisMonthDateList;
            mThisMonthAdapter.periodMarkValue = mDisplayMonth;
            mAdapterList.add(mThisMonthAdapter);

            Calendar nextMonCal = Calendar.getInstance();
            nextMonCal.setTime(mTodayDate);
            nextMonCal.add(Calendar.MONTH, 1);
            ArrayList<Date> nextMonthDateList = getDateOfMonthList(nextMonCal.get(Calendar.YEAR),nextMonCal.get(Calendar.MONTH));
            CalendarAdapter mNextMonthAdapter = new CalendarAdapter();
            mNextMonthAdapter.mDateList = nextMonthDateList;
            mNextMonthAdapter.periodMarkValue = nextMonCal.get(Calendar.MONTH);
            mAdapterList.add(mNextMonthAdapter);
        }
        else{
            Calendar lastWeekCal = Calendar.getInstance();
            lastWeekCal.setTime(mTodayDate);
            lastWeekCal.add(Calendar.WEEK_OF_YEAR,-1);
            ArrayList<Date> lastWeekDateList = getDateOfWeekList(lastWeekCal.get(Calendar.YEAR),lastWeekCal.get(Calendar.WEEK_OF_YEAR));
            CalendarAdapter mLastWeekAdapter = new CalendarAdapter();
            mLastWeekAdapter.mDateList = lastWeekDateList;
            mLastWeekAdapter.periodMarkValue = lastWeekCal.get(Calendar.WEEK_OF_YEAR);
            mAdapterList.add(mLastWeekAdapter);

            ArrayList<Date> thisWeekDateList = getDateOfWeekList(mDisplayYear,mDisplayWeek);
            CalendarAdapter mThisWeekAdapter = new CalendarAdapter();
            mThisWeekAdapter.mDateList = thisWeekDateList;
            mThisWeekAdapter.periodMarkValue = mDisplayWeek;
            mAdapterList.add(mThisWeekAdapter);

            Calendar nextWeekCal = Calendar.getInstance();
            nextWeekCal.setTime(mTodayDate);
            nextWeekCal.add(Calendar.WEEK_OF_YEAR, 1);
            ArrayList<Date> nextWeekDateList = getDateOfWeekList(nextWeekCal.get(Calendar.YEAR),nextWeekCal.get(Calendar.WEEK_OF_YEAR));
            CalendarAdapter mNextWeekAdapter = new CalendarAdapter();
            mNextWeekAdapter.mDateList = nextWeekDateList;
            mNextWeekAdapter.periodMarkValue = nextWeekCal.get(Calendar.WEEK_OF_YEAR);
            mAdapterList.add(mNextWeekAdapter);
        }
        mPageAdpter = new CalendarPageAdapter();
        mCalendarVp.setAdapter(mPageAdpter);
        int firstPos = mFristWeekDay.ordinal();
        for(int i = firstPos;i<firstPos+7;i++){
            int nowWeekDay = i%7;
            if(nowWeekDay==0)
                nowWeekDay = 7;
            TextView tv = new TextView(getContext());
            LayoutParams tvlps = new LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvlps.weight = 1;
            tv.setLayoutParams(tvlps);
            tv.setText(WeekDays.getChineseName(WeekDays.valueOf(nowWeekDay)));
            tv.setTextSize(mTitleTxtSize);
            tv.setTextColor(mTitleTxtColor);
            tv.setGravity(Gravity.CENTER);
            mTitleLl.addView(tv);
        }
        mCalendarVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                nowPageIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//        若viewpager滑动未停止，直接返回
                if (state != ViewPager.SCROLL_STATE_IDLE) return;
                mSelectedPos += nowPageIndex - 1;
                int offset = nowPageIndex - 1;

                if(nowPageIndex==0&&mSelectedPos==0){
                    if(mDisplayMode ==DisPlayMode.MONTH){
                        Calendar lastMonCal = Calendar.getInstance();
                        lastMonCal.setTime(mTodayDate);
                        lastMonCal.add(Calendar.MONTH, -1 - mCurPos);

                        ArrayList<Date> lastMonthDateList = getDateOfMonthList(lastMonCal.get(Calendar.YEAR),lastMonCal.get(Calendar.MONTH));
                        CalendarAdapter mLastMonthAdapter = new CalendarAdapter();
                        mLastMonthAdapter.mDateList = lastMonthDateList;
                        mLastMonthAdapter.periodMarkValue = lastMonCal.get(Calendar.MONTH);
                        mAdapterList.add(0,mLastMonthAdapter);
                    }
                    else{
                        Calendar lastWeekCal = Calendar.getInstance();
                        lastWeekCal.setTime(mTodayDate);
                        lastWeekCal.add(Calendar.WEEK_OF_YEAR, -1 - mCurPos);
                        ArrayList<Date> lastWeekDateList = getDateOfWeekList(lastWeekCal.get(Calendar.YEAR),lastWeekCal.get(Calendar.WEEK_OF_YEAR));
                        CalendarAdapter mLastWeekAdapter = new CalendarAdapter();

                        mLastWeekAdapter.mDateList = lastWeekDateList;
                        mLastWeekAdapter.periodMarkValue = lastWeekCal.get(Calendar.WEEK_OF_YEAR);
                        mAdapterList.add(0,mLastWeekAdapter);

                    }
                    mCurPos++;
                    mSelectedPos++;
                }
                if(nowPageIndex==2&&mSelectedPos>=mAdapterList.size()-1){
                    if(mDisplayMode ==DisPlayMode.MONTH){
                        Calendar nextMonCal = Calendar.getInstance();
                        nextMonCal.setTime(mTodayDate);
                        nextMonCal.add(Calendar.MONTH, mSelectedPos+1 - mCurPos);

                        ArrayList<Date> nextMonthDateList = getDateOfMonthList(nextMonCal.get(Calendar.YEAR),nextMonCal.get(Calendar.MONTH));
                        CalendarAdapter mNextMonthAdapter = new CalendarAdapter();
                        mNextMonthAdapter.mDateList = nextMonthDateList;
                        mNextMonthAdapter.periodMarkValue = nextMonCal.get(Calendar.MONTH);
                        mAdapterList.add(mNextMonthAdapter);
                    }
                    else{
                        Calendar nextWeekCal = Calendar.getInstance();
                        nextWeekCal.setTime(mTodayDate);
                        nextWeekCal.add(Calendar.WEEK_OF_YEAR, mSelectedPos+1  - mCurPos);
                        ArrayList<Date> nextWeekDateList = getDateOfWeekList(nextWeekCal.get(Calendar.YEAR),nextWeekCal.get(Calendar.WEEK_OF_YEAR));
                        CalendarAdapter mNextWeekAdapter = new CalendarAdapter();

                        mNextWeekAdapter.mDateList = nextWeekDateList;
                        mNextWeekAdapter.periodMarkValue = nextWeekCal.get(Calendar.WEEK_OF_YEAR);
                        mAdapterList.add(mNextWeekAdapter);
                    }
                }
                if(mDisplayMode ==DisPlayMode.MONTH) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(mTodayDate);
                    cal.add(Calendar.MONTH,mSelectedPos-mCurPos);
                    int monthNum = cal.get(Calendar.MONTH)+1;
                    mMonthYearTv.setText(cal.get(Calendar.YEAR)+"年"+monthNum+"月");

                    cal.setTime(mSelectedDate);
                    cal.add(Calendar.MONTH,offset);
                    mSelectedDate = cal.getTime();
                    if(mOnDateSelectedListener!=null){
                        mOnDateSelectedListener.onDateSelected(mSelectedDate);
                    }
                }
                else{
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(mSelectedDate);
                    cal.add(Calendar.WEEK_OF_YEAR,offset);
                    mSelectedDate = cal.getTime();
                    if(mOnDateSelectedListener!=null){
                        mOnDateSelectedListener.onDateSelected(mSelectedDate);
                    }
                }
                if(nowPageIndex!=1)
                    mPageAdpter.resetList();

            }
        });
        mCalendarVp.setCurrentItem(1,false);
    }
//    /**
//     * @brief 作为窗口弹出
//     * */
//    public void showPopAt(View view, int width, int height, int gravity, int offsetX, int offsetY) {
//        mPopWin = new PopupWindow(getContext());
//        if(getParent()!=null){
//            ViewParent parent = getParent();
//            if(parent instanceof  ViewGroup)
//            ((ViewGroup)parent).removeView(this);
//            else if(parent instanceof  PopupWindow)
//                ((PopupWindow) parent).setContentView(null);
//            this.setBackgroundColor(Color.WHITE);
//        }
//        mPopWin.setFocusable(true);
//        mPopWin.setOutsideTouchable(true);
//        mPopWin.setContentView(this);
//        ViewGroup.LayoutParams lps = new ViewGroup.LayoutParams(width,height);
//        setLayoutParams(lps);
//        mPopWin.showAtLocation(view,gravity,offsetX,offsetY);
//    }


    public class CalendarItemView extends View {

        public Date dateNow;
        public int periodMarkValue = 0;

        public CalendarItemView(Context context) {
            super(context);
        }

        public CalendarItemView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public CalendarItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int viewHeight = CalendarItemView.this.getHeight();
            int viewWidth = CalendarItemView.this.getWidth();
            Paint mCalendarDayTxtPaint = new Paint();
            mCalendarDayTxtPaint.setTextSize(Tools.sp2px(getContext(),mCalendarTxtSize));
            mCalendarDayTxtPaint.setAntiAlias(true);
            mCalendarDayTxtPaint.setFakeBoldText(mTxtBold);
            Calendar nowCal = Calendar.getInstance();
            nowCal.setTime(dateNow);
            Calendar todayCal = Calendar.getInstance();
            todayCal.setTime(mTodayDate);
            Calendar selectdCal = Calendar.getInstance();
            selectdCal.setTime(mSelectedDate);

            long nowTime = dateNow.getTime();
            long todayTime = mTodayDate.getTime();
            long selectedTime = mSelectedDate.getTime();
            if(nowTime!=todayTime&&nowTime==selectedTime){
                Paint mCalendarDayCirclePaint = new Paint();
                mCalendarDayCirclePaint.setAntiAlias(true);
                mCalendarDayCirclePaint.setStyle(Paint.Style.FILL);
                mCalendarDayCirclePaint.setColor(mSelectedCircleColor);
                canvas.drawCircle(viewWidth/2,viewWidth/2,viewWidth/2,mCalendarDayCirclePaint);
            }
            else if(nowTime!=selectedTime&&nowTime==todayTime){
                Paint mCalendarDayCirclePaint = new Paint();
                mCalendarDayCirclePaint.setAntiAlias(true);
                mCalendarDayCirclePaint.setStyle(Paint.Style.STROKE);
                mCalendarDayCirclePaint.setStrokeWidth(2);
                mCalendarDayCirclePaint.setColor(mTodayCircleColor);
                canvas.drawCircle(viewWidth / 2,viewWidth/2, viewWidth / 2-1, mCalendarDayCirclePaint);
            }
            int markValue =mDisplayMode == DisPlayMode.MONTH?nowCal.get(Calendar.MONTH):nowCal.get(Calendar.WEEK_OF_YEAR);
            if(nowTime==selectedTime&&nowTime==todayTime) {
                Paint mCalendarDayCirclePaint = new Paint();
                mCalendarDayCirclePaint.setAntiAlias(true);
                mCalendarDayCirclePaint.setStyle(Paint.Style.FILL);
                mCalendarDayCirclePaint.setColor(mTodayCircleColor);
                canvas.drawCircle(viewWidth / 2,viewWidth/2, viewWidth / 2, mCalendarDayCirclePaint);

                mCalendarDayTxtPaint.setColor(mTodayTxtColor);
            }
            else if(markValue!=periodMarkValue||nowCal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY||
                    nowCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                mCalendarDayTxtPaint.setColor(mWeekendTxtColor);
            }
            else{
                switch (mSelectType){
                    case ALL:
                        mCalendarDayTxtPaint.setColor(mWorkDayTxtColor);
                        break;
                    case HISTORY:
                        long dateMills = dateNow.getTime();
                        long todayMills = mTodayDate.getTime();
                        if(dateMills<todayMills){
                            mCalendarDayTxtPaint.setColor(mWorkDayTxtColor);
                        }
                        else if(dateMills>=todayMills){
                            if(mIncludeToday&&dateMills==todayMills){
                                mCalendarDayTxtPaint.setColor(mWorkDayTxtColor);
                            }
                            else{
                                mCalendarDayTxtPaint.setColor(mWeekendTxtColor);
                            }
                        }
                        else{
                            mCalendarDayTxtPaint.setColor(mWorkDayTxtColor);
                        }
                        break;
                    case FUTURE:
                        long dateMill = dateNow.getTime();
                        long todayMill = mTodayDate.getTime();
                        if(dateMill>todayMill){
                            mCalendarDayTxtPaint.setColor(mWorkDayTxtColor);
                        }
                        else if(dateMill<=todayMill){
                            if(mIncludeToday&&dateMill==todayMill){
                                mCalendarDayTxtPaint.setColor(mWorkDayTxtColor);
                            }
                            else{
                                mCalendarDayTxtPaint.setColor(mWeekendTxtColor);
                            }
                        }
                        else{
                            mCalendarDayTxtPaint.setColor(mWorkDayTxtColor);
                        }
                        break;
                }
            }
            int day = nowCal.get(Calendar.DAY_OF_MONTH);
            String dayStr = ""+day;
            mCalendarDayTxtPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(dayStr,viewWidth / 2 ,Tools.getBaseLine(mCalendarDayTxtPaint,viewWidth/2),mCalendarDayTxtPaint);
            if(mDrawMarkRegions.size()>0){
                for(DrawMarkRegion region: mDrawMarkRegions){
                    region.onDraw(canvas,viewWidth,viewHeight,dateNow);
                }
            }
        }
    }
    /***
     *  日期发生变更回调
     * */
    public interface OnDateSelectedListener{
        void onDateSelected(Date selectedDate);
    }
    /***
     *  绘制执行回调
     * */
    public interface DrawMarkRegion{
        /**
         * 区域顶部已经绘制完成日历日期
         */
        void onDraw(Canvas canvas, int viewWidth, int viewHeight, Date date);
    }
    /**
     * 设置日历显示的日期的置灰类型
     **/
    public void setSelectType(SelectType type){
        mSelectType = type;
    }

    public class CalendarAdapter extends BaseAdapter{
        public ArrayList<Date> mDateList;
        public int periodMarkValue = 0;
        @Override
        public int getCount() {
            return mDateList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CalendarItemView itemView = new CalendarItemView(parent.getContext());
            itemView.periodMarkValue = periodMarkValue;
            itemView.setLayoutParams(new AdapterView.LayoutParams(itemViewWidth,itemViewHeight));
            itemView.dateNow = mDateList.get(position);
            MySelectOnClickListener listener = new MySelectOnClickListener();
            listener.dateNow = itemView.dateNow;
            listener.adapter = this;
            itemView.setOnClickListener(listener);
            return itemView;
        }
    }

    private ArrayList<Date> getDateOfMonthList(int year,int month){
        ArrayList<Date> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);

        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayofweek < mFristWeekDay.ordinal()) {
            dayofweek += 7;
        }
        calendar.add(Calendar.DAY_OF_MONTH,  mFristWeekDay.ordinal() - dayofweek);
        dateList.add(calendar.getTime());
        for(int i=0;i<41;i++){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dateList.add(calendar.getTime());
        }
        return dateList;
    }

    private ArrayList<Date> getDateOfWeekList(int year,int week){
        ArrayList<Date> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.WEEK_OF_YEAR,week);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayofweek < mFristWeekDay.ordinal()) {
            dayofweek += 7;
        }
        calendar.add(Calendar.DAY_OF_MONTH,  mFristWeekDay.ordinal() - dayofweek);
        dateList.add(calendar.getTime());
        for(int i=0;i<6;i++){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dateList.add(calendar.getTime());
        }
        return dateList;
    }

    private class MySelectOnClickListener implements OnClickListener{
        public Date dateNow;
        public CalendarAdapter adapter;
        @Override
        public void onClick(View v) {
            mSelectedDate = dateNow;
            if(mOnDateSelectedListener!=null)
            mOnDateSelectedListener.onDateSelected(dateNow);
            adapter.notifyDataSetChanged();
        }
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        this.mOnDateSelectedListener = onDateSelectedListener;
        mOnDateSelectedListener.onDateSelected(mSelectedDate);
    }

    @Override
    public void setOrientation( int orientation) {
        //super.setOrientation(orientation);
    }

    private class CalendarPageAdapter extends PagerAdapter {
        private int currentPosition = 0;

        protected ArrayList<GridView> views;
        private List<CalendarAdapter> mCalendarAdapterList;
        public CalendarPageAdapter() {
            views = new ArrayList<>();
            mCalendarAdapterList = (ArrayList<CalendarAdapter>)mAdapterList.clone();
//          如果数据大于一条
            for (CalendarAdapter adapter:mCalendarAdapterList) {
                views.add(getItemView(adapter));
            }
        }

        public void resetList(){
            try {
                mCalendarAdapterList = mAdapterList.subList(mSelectedPos-1,mSelectedPos+2);

                for (int i = 0;i<views.size();i++) {
                    CalendarAdapter adapter = mCalendarAdapterList.get(i);
                    views.get(i).setAdapter(adapter);
                }
                mCalendarVp.setCurrentItem(1,false);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        protected GridView getItemView(CalendarAdapter adapter) {
            GridView mGv = new GridView(getContext());
            ViewGroup.LayoutParams lps = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mGv.setLayoutParams(lps);
            mGv.setNumColumns(7);
            mGv.setGravity(Gravity.CENTER);
            mGv.setSelector(new ColorDrawable(Color.TRANSPARENT));
            mGv.setAdapter(adapter);
            return mGv;
        }
    }

    /**
     * 设置日历单元item字体大小
     * */
    public void setItemViewSize(int itemViewWidth,int itemViewHeight) {
        Calendar mCurCal = Calendar.getInstance();
        mCurCal.setTime(mCurDate);
        int mDisplayYear = mCurCal.get(Calendar.YEAR);
        int mDisplayMonth = mCurCal.get(Calendar.MONTH);
        int mDisplayWeek = mCurCal.get(Calendar.WEEK_OF_YEAR);
        this.itemViewHeight = itemViewHeight;
        this.itemViewWidth = itemViewWidth;
        if(mDisplayMode ==DisPlayMode.MONTH) {
            mAdapterList.clear();
            Calendar lastMonCal = Calendar.getInstance();
            lastMonCal.setTime(mTodayDate);
            lastMonCal.add(Calendar.MONTH,-1);
            ArrayList<Date> lastMonthDateList = getDateOfMonthList(lastMonCal.get(Calendar.YEAR),lastMonCal.get(Calendar.MONTH));
            CalendarAdapter mLastMonthAdapter = new CalendarAdapter();
            mLastMonthAdapter.mDateList = lastMonthDateList;
            mLastMonthAdapter.periodMarkValue = lastMonCal.get(Calendar.MONTH);
            mAdapterList.add(mLastMonthAdapter);

            ArrayList<Date> thisMonthDateList = getDateOfMonthList(mDisplayYear,mDisplayMonth);
            CalendarAdapter mThisMonthAdapter = new CalendarAdapter();
            mThisMonthAdapter.mDateList = thisMonthDateList;
            mThisMonthAdapter.periodMarkValue = mDisplayMonth;
            mAdapterList.add(mThisMonthAdapter);

            Calendar nextMonCal = Calendar.getInstance();
            nextMonCal.setTime(mTodayDate);
            nextMonCal.add(Calendar.MONTH, 1);
            ArrayList<Date> nextMonthDateList = getDateOfMonthList(nextMonCal.get(Calendar.YEAR),nextMonCal.get(Calendar.MONTH));
            CalendarAdapter mNextMonthAdapter = new CalendarAdapter();
            mNextMonthAdapter.mDateList = nextMonthDateList;
            mNextMonthAdapter.periodMarkValue = nextMonCal.get(Calendar.MONTH);
            mAdapterList.add(mNextMonthAdapter);
        }
        else{
            Calendar lastWeekCal = Calendar.getInstance();
            lastWeekCal.setTime(mTodayDate);
            lastWeekCal.add(Calendar.WEEK_OF_YEAR,-1);
            ArrayList<Date> lastWeekDateList = getDateOfWeekList(lastWeekCal.get(Calendar.YEAR),lastWeekCal.get(Calendar.WEEK_OF_YEAR));
            CalendarAdapter mLastWeekAdapter = new CalendarAdapter();
            mLastWeekAdapter.mDateList = lastWeekDateList;
            mLastWeekAdapter.periodMarkValue = lastWeekCal.get(Calendar.WEEK_OF_YEAR);
            mAdapterList.add(mLastWeekAdapter);

            ArrayList<Date> thisWeekDateList = getDateOfWeekList(mDisplayYear,mDisplayWeek);
            CalendarAdapter mThisWeekAdapter = new CalendarAdapter();
            mThisWeekAdapter.mDateList = thisWeekDateList;
            mThisWeekAdapter.periodMarkValue = mDisplayWeek;
            mAdapterList.add(mThisWeekAdapter);

            Calendar nextWeekCal = Calendar.getInstance();
            nextWeekCal.setTime(mTodayDate);
            nextWeekCal.add(Calendar.WEEK_OF_YEAR, 1);
            ArrayList<Date> nextWeekDateList = getDateOfWeekList(nextWeekCal.get(Calendar.YEAR),nextWeekCal.get(Calendar.WEEK_OF_YEAR));
            CalendarAdapter mNextWeekAdapter = new CalendarAdapter();
            mNextWeekAdapter.mDateList = nextWeekDateList;
            mNextWeekAdapter.periodMarkValue = nextWeekCal.get(Calendar.WEEK_OF_YEAR);
            mAdapterList.add(mNextWeekAdapter);
        }
    }
    /**
     * 设置显示为周或月
     * */
    public void setDisplayMode(DisPlayMode displayMode) {
        this.mDisplayMode = displayMode;
    }
    /**
     * 设置日历第一天
     * */
    public void setFristWeekDay(WeekDays fristWeekDay) {
        this.mFristWeekDay = fristWeekDay;
    }

    public DisPlayMode getDispalyMode(){
        return mDisplayMode;
    }
    /**
     * 设置是否可选今天
     * */
    public void setIncludeToday(boolean includeToday) {
        this.mIncludeToday = includeToday;
    }

    public void resetView(){
        removeView(mTitleLl);
        removeView(mCalendarVp);
        if(mMonthYearTv!=null){
            removeView(mMonthYearTv);
            mMonthYearTv = null;
        }
        mAdapterList.clear();
        initView(getContext(),null);
        mCurPos = 1;
        mSelectedPos = 1;
        nowPageIndex = 1;
    }
    /**
     * 日历跳转
     * */
    public void skipToDate(Date toSkipDate){
        try{
            mSelectedDate = toSkipDate;
            Calendar toSkipTime = Calendar.getInstance();
            toSkipTime.setTime(toSkipDate);
            Calendar toDayTime = Calendar.getInstance();
            toDayTime.setTime(mTodayDate);
            int year = toSkipTime.get(Calendar.YEAR);
            int todayYear =toDayTime.get(Calendar.YEAR);
            if(mDisplayMode==DisPlayMode.MONTH){
                int month = toSkipTime.get(Calendar.MONTH);
                int todayMonth = toDayTime.get(Calendar.MONTH);
                int offset = 12*(todayYear-year)+todayMonth - month;
                removeView(mTitleLl);
                removeView(mCalendarVp);
                removeView(mMonthYearTv);
                mAdapterList.clear();
                mMonthYearTv = null;
                mSelectedPos = 1;
                nowPageIndex = 1;
                initView(getContext(),toSkipDate);
                mCurPos = 1+offset;
                mSelectedPos = 1;
                nowPageIndex = 1;

                mSelectedDate = toSkipTime.getTime();
                mTodayDate = toDayTime.getTime();
                mOnDateSelectedListener.onDateSelected(mSelectedDate);
            }
            else{
                long toSkipMills = toSkipDate.getTime();
                long nowTime = mTodayDate.getTime();
                Calendar firstDayCal = Calendar.getInstance();
                firstDayCal.setTime(mTodayDate);
                int weekNum = firstDayCal.get(Calendar.DAY_OF_WEEK);
                long deltaMills = nowTime - toSkipMills - weekNum*3600*1000*24;
                long oneWeek = 3600*1000*24*7;
                int toSkipWeek = (int)(deltaMills/(oneWeek));
                if(deltaMills>0)
                    toSkipWeek+=1;
                removeView(mTitleLl);
                removeView(mCalendarVp);
                removeView(mMonthYearTv);
                mAdapterList.clear();
                mMonthYearTv = null;
                mSelectedPos = 1;
                nowPageIndex = 1;
                initView(getContext(),toSkipDate);
                mCurPos = 1+toSkipWeek;
                mSelectedPos = 1;
                nowPageIndex = 1;
                mSelectedDate = toSkipTime.getTime();
                mTodayDate = toDayTime.getTime();
                mOnDateSelectedListener.onDateSelected(mSelectedDate);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setDrawMarkRegions(@NonNull List<DrawMarkRegion> regions){
        mDrawMarkRegions = regions;
    }
}
