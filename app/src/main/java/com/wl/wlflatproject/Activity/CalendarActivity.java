package com.wl.wlflatproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.wl.wlflatproject.Bean.CalendarParam;
import com.wl.wlflatproject.MUtils.LunarUtils;
import com.wl.wlflatproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalendarActivity extends AppCompatActivity implements CalendarView.OnCalendarSelectListener {

    public static final String PARAM = "param";
    ScrollView sl = null;
    LinearLayout ll=null;
    @BindView(R.id.current_month_tv)
    TextView currentMonthTv;
    @BindView(R.id.current_year_tv)
    TextView currentYearTv;
    @BindView(R.id.calendar_view)
    CalendarView calendarView;
    @BindView(R.id.lunar_date_tv)
    TextView lunarDateTv;
    @BindView(R.id.temp_tv)
    TextView tempTv;
    @BindView(R.id.weather_tv)
    TextView weatherTv;
    @BindView(R.id.gan_zhi_tv)
    TextView ganZhiTv;
    @BindView(R.id.location_tv)
    TextView locationTv;
    @BindView(R.id.last_v)
    View last_v;
    @BindView(R.id.next_v)
    View next_v;
    private int currentYear;
    private int lastSelectYear=0;
    private CalendarParam mParam;

    public static void start(Context context, CalendarParam param) {
        Intent starter = new Intent(context, CalendarActivity.class);
        starter.putExtra(PARAM, param);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);
        mParam = (CalendarParam) getIntent().getSerializableExtra(PARAM);
        initView();
        hideBottomUIMenu();
        setFinishOnTouchOutside(true);
    }

    private void initView() {
        calendarView.setSelectCalendarRange(1950,1,1,2050,12,31);
        calendarView.setOnCalendarSelectListener(this);
        //当前月
        currentMonthTv.setText(calendarView.getCurMonth() + "月");
        //当前年
        currentYearTv.setText(calendarView.getCurYear() + "年");
        currentYear=calendarView.getCurYear();
        lastSelectYear=calendarView.getCurYear();
        //猪 贰零壹玖 润 六月 小廿八 己亥 辛未 戊辰
        String[] lunar = LunarUtils.getLunar(calendarView.getCurYear() + "",
                calendarView.getCurMonth() + "",
                calendarView.getCurDay() + "");
        //农历
        lunarDateTv.setText(String.format("%s-%s-%s", "农历", lunar[3] + "月", lunar[4]));
        //温度
        tempTv.setText(mParam.temp + "℃");
        //天气
        weatherTv.setText(mParam.weather);
        //天干地支
        ganZhiTv.setText(String.format("%s %s %s", lunar[5] + lunar[0] + "年", lunar[6] + "月", lunar[7] + "日"));
        //地点
        locationTv.setText(mParam.location);
        last_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.scrollToPre();
            }
        });
        next_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentYear==2050){
                    return;
                }
                calendarView.scrollToNext();
//                public void scrollToCalendar(int year, int month, int day);//滚动到指定日期
            }
        });
        currentYearTv.setOnClickListener(new View.OnClickListener() {

            private PopupWindow popupWindow;

            @Override
            public void onClick(View view) {
                if(popupWindow==null){
                    View inflate = View.inflate(CalendarActivity.this, R.layout.data_rc, null);
                    ll=inflate.findViewById(R.id.date_ll);
                    sl=inflate.findViewById(R.id.date_sl);
                    int data=1971;
                    for(int x=0;x<79;x++){
                        TextView textView = new TextView(CalendarActivity.this);
                        textView.setGravity(Gravity.CENTER);
                        textView.setWidth(142);
                        textView.setHeight(77);
                        textView.setText(data+"");
                        textView.setTextSize(14);
                        textView.setTextColor(Color.parseColor("#4e5969"));
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String selectYear = textView.getText().toString();
                                calendarView.scrollToCalendar(Integer.parseInt(selectYear),calendarView.getCurMonth(),calendarView.getCurDay());
                                popupWindow.dismiss();
                            }
                        });
                        data++;
                        ll.addView(textView);
                    }
                    popupWindow = new PopupWindow(inflate, 172,440, true);
                }
                TextView textView= (TextView) ll.getChildAt(currentYear-1971);
                textView.setTextColor(Color.parseColor("#1d2129"));
                textView.setBackgroundColor(Color.parseColor("#f2f3f5"));
                if(lastSelectYear!=currentYear){
                    TextView textView1= (TextView) ll.getChildAt(lastSelectYear-1971);
                    textView1.setTextColor(Color.parseColor("#4e5969"));
                    textView1.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                popupWindow.showAsDropDown(currentYearTv,-20,0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sl.scrollTo(0,(currentYear-1973)*77);
                        lastSelectYear=currentYear;
                    }
                },500);
            }
        });
    }


    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        //月
        currentMonthTv.setText(calendar.getMonth() + "月");
        //年
        currentYearTv.setText(calendar.getYear() + "年");
        currentYear=calendar.getYear();
        Calendar lunarCalendar = calendar.getLunarCalendar();
        //猪 贰零壹玖 润 六月 小廿八 己亥 辛未 戊辰
        String[] lunar = LunarUtils.getLunar(calendar.getYear() + "",
                calendar.getMonth() + "",
                calendar.getDay() + "");
        //农历
        lunarDateTv.setText(String.format("%s-%s-%s", "农历", lunar[3] + "月", lunar[4]));
        //天干地支
        ganZhiTv.setText(String.format("%s %s %s", lunar[5] + lunar[0] + "年", lunar[6] + "月", lunar[7] + "日"));
        //周
        if (calendar.isCurrentDay()) {
//            currentWeekTv.setText(DateUtils.getInstance().getWeekday2(System.currentTimeMillis()));
        } else {
            String weekStr = "周日";
            switch (calendar.getWeek()) {
                case 0:
                    weekStr = "周日";
                    break;
                case 1:
                    weekStr = "周一";
                    break;
                case 2:
                    weekStr = "周二";
                    break;
                case 3:
                    weekStr = "周三";
                    break;
                case 4:
                    weekStr = "周四";
                    break;
                case 5:
                    weekStr = "周五";
                    break;
                case 6:
                    weekStr = "周六";
                    break;
                default:
            }
        }

    }
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
