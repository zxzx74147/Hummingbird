package com.xbirder.bike.hummingbird.cycling;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.core.net.base.HttpResponse;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;
import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.xbirder.bike.hummingbird.login.LoginRequest;
import com.xbirder.bike.hummingbird.main.MainActivity;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2015/8/26.
 */
public class CyclingRecords extends BaseActivity{

    private BarChart mBarChart;
    private BarData mBarData;
    private RadioButton weekRadio, monthRadio, yearRadio;
    private Boolean hasWeedData, hasMonthData, hasYearData;
    private TextView chartTitle;
    private TextView mTodayDisTextView, mTodayTimeTextView, mTotalTextView, mTotalCostTextView;
    private float mLocalDistance = 0;//公里
    private int mLocalTime = 0;
    private String mTodayStr;
    private JSONArray mWeekData, mMonthData;
    private JSONObject mYearData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cycling_data);

        mTodayStr = getTodayString();
        mBarChart = (BarChart) findViewById(R.id.chart);

        weekRadio = (RadioButton)findViewById(R.id.btn_0);
        monthRadio = (RadioButton)findViewById(R.id.btn_1);
        yearRadio = (RadioButton)findViewById(R.id.btn_2);

        hasWeedData = false;
        hasMonthData = false;
        hasYearData = false;

        String localDisStr = AccountManager.sharedInstance().getStoreDistance();
        if (localDisStr == null || localDisStr == "") {
            localDisStr = "0";
        }
        mLocalDistance = Integer.parseInt(localDisStr) * 100.0f / 1000.0f;

        String localTimeStr = AccountManager.sharedInstance().getStoreRuntime();
        if (localTimeStr == null || localTimeStr == "") {
            localTimeStr = "0";
        }
        mLocalTime = Integer.parseInt(localTimeStr);

        chartTitle = (TextView)findViewById(R.id.tv_data);
        mTodayDisTextView = (TextView)findViewById(R.id.today_mileage_data);
        mTotalTextView = (TextView)findViewById(R.id.total_mileage_data);
        mTodayTimeTextView = (TextView)findViewById(R.id.today_time_data);
        mTotalCostTextView = (TextView)findViewById(R.id.reduce_carbon_emissions_data);

        RadioGroup group = (RadioGroup)this.findViewById(R.id.time_selection_RadioGroup);

        //绑定一个匿名监听器
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                if (arg1 == weekRadio.getId()) {
                    if (hasWeedData) {
                        mBarData = getBarDataWeek();
                        showBarChart(mBarChart, mBarData);
                    } else {
                        getWeekDataFromServer();
                    }
                } else if (arg1 == monthRadio.getId()) {
                    if (hasMonthData) {
                        mBarData = getBarDataMonth();
                        showBarChart(mBarChart, mBarData);
                    } else {
                        getMonthDataFromServer();
                    }
                    mBarChart.setDrawYValues(false);
                } else if (arg1 == yearRadio.getId()) {
                    if (hasYearData) {
                        mBarData = getBarDataYear();
                        showBarChart(mBarChart, mBarData);
                    } else {
                        getYearDataFromServer();
                    }
                }
            }
        });

        if (hasWeedData) {
            mBarData = getBarDataWeek();
            showBarChart(mBarChart, mBarData);
        } else {
            getWeekDataFromServer();
        }
    }

    private void getWeekDataFromServer() {
        DayRecordRequest request = new DayRecordRequest(new HttpResponse.Listener<JSONObject>() {
            @Override
            public void onResponse(HttpResponse<JSONObject> response) {
                if (response.isSuccess()) {
                    try {
                        if (response.result.getString("error").equals("0")) {
                            hasWeedData = true;
                            JSONArray recordList = response.result.getJSONArray("record");
                            JSONObject sumObject = response.result.getJSONObject("sum");

                            String totalDisStr = sumObject.getString("distance");
                            String todayTimeStr;
                            if (totalDisStr == null || totalDisStr == "") {
                                totalDisStr = "0";
                            }
                            float finalDis = Float.parseFloat(totalDisStr) / 1000.0f + mLocalDistance;
                            String finalDisStr = String.valueOf(finalDis);
                            mTotalTextView.setText(finalDisStr);

                            float finalCost = 0.27f * finalDis;
                            DecimalFormat decimalFormat=new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                            String finalCostStr = decimalFormat.format(finalCost);
                            mTotalCostTextView.setText(finalCostStr);

                            if (recordList != null) {
                                mWeekData = recordList;
                                for (int i = 0; i < recordList.length(); i++) {
                                    JSONObject record = recordList.getJSONObject(i);
                                    String data = record.getString("date");
                                    int distance = record.getInt("distance");
                                    int time = record.getInt("duration");

                                    if (data != null && data.equals(mTodayStr)) {
                                        float todayDis = distance / 1000.0f + mLocalDistance;
                                        String finalTodayDisStr = String.valueOf(todayDis);
                                        mTodayDisTextView.setText(finalTodayDisStr);

                                        mTodayTimeTextView.setText(getTimeString(time+mLocalTime));
                                    }
                                }
                                mBarData = getBarDataWeek();
                                showBarChart(mBarChart, mBarData);
                            }
                        } else {
                            toast("失败");
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if(1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        //所在周星期一的日期
        String imptimeBegin = sdf.format(cal.getTime());

        cal.add(Calendar.DATE, 6);
        //所在周星期日的日期
        String imptimeEnd = sdf.format(cal.getTime());
        String token = AccountManager.sharedInstance().getToken();
        request.setParam(imptimeBegin, imptimeEnd, token);
        sendRequest(request);
    }

    private void getMonthDataFromServer() {
        DayRecordRequest request = new DayRecordRequest(new HttpResponse.Listener<JSONObject>() {
            @Override
            public void onResponse(HttpResponse<JSONObject> response) {
                if (response.isSuccess()) {
                    try {
                        if (response.result.getString("error").equals("0")) {
                            hasMonthData = true;
                            JSONArray recordList = response.result.getJSONArray("record");

                            if (recordList != null) {
                                mMonthData = recordList;
                                mBarData = getBarDataMonth();
                                showBarChart(mBarChart, mBarData);
                                mBarChart.setDrawYValues(false);
                            }
                        } else {
                            toast("失败");
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
        Date today = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        //当前月的最后一天
        cal.set(Calendar.DATE, 1);
        cal.roll(Calendar.DATE, -1);
        Date endTime=cal.getTime();
        String imptimeEnd = sdf.format(endTime);
        //当前月的第一天
        cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
        Date beginTime=cal.getTime();
        String imptimeBegin=sdf.format(beginTime);

        String token = AccountManager.sharedInstance().getToken();
        request.setParam(imptimeBegin, imptimeEnd, token);
        sendRequest(request);
    }

    private void getYearDataFromServer() {
        MonthRecordRequest request = new MonthRecordRequest(new HttpResponse.Listener<JSONObject>() {
            @Override
            public void onResponse(HttpResponse<JSONObject> response) {
                if (response.isSuccess()) {
                    try {
                        if (response.result.getString("error").equals("0")) {
                            hasYearData = true;

                            JSONObject recordList = response.result.getJSONObject("record");

                            if (recordList != null) {
                                mYearData = recordList;
                                mBarData = getBarDataYear();
                                showBarChart(mBarChart, mBarData);
                            }
                        } else {
                            toast("失败");
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        String imptimeEnd = sdf.format(cal.getTime());
        int month = cal.get(Calendar.MONTH);
        cal.add(Calendar.MONTH, - month);
        String imptimeBegin = sdf.format(cal.getTime());

        String token = AccountManager.sharedInstance().getToken();
        request.setParam(imptimeBegin, imptimeEnd, token);
        sendRequest(request);
    }

    private void showBarChart(BarChart barChart, BarData barData) {
        barChart.setDescription("");// 数据描述

        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("对不起，当前没有数据");
        barChart.setDrawVerticalGrid(false);
        barChart.setDrawGridBackground(false); // 是否显示表格颜色

        barChart.setDrawYValues(true);

        barChart.setTouchEnabled(true); // 设置是否可以触摸
        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(true);// 是否可以缩放
        barChart.setPinchZoom(false);//

        barChart.setDrawBorder(true);
        barChart.setDrawBarShadow(false);

        barChart.setData(barData); // 设置数据

        Legend mLegend = barChart.getLegend(); // 设置比例图标示

        mLegend.setForm(Legend.LegendForm.LINE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.BLACK);// 颜色

        YLabels y = barChart.getYLabels(); // y轴的标示
        y.setTextColor(Color.BLACK);
        y.setTypeface(Typeface.DEFAULT);
        y.setTextSize(12F);
        y.setLabelCount(6); // y轴上的标签的显示的个数

        XLabels x = barChart.getXLabels(); // x轴显示的标签
        x.setTextColor(Color.BLACK);
        x.setPosition(XLabels.XLabelPosition.BOTTOM.BOTTOM);
        x.setCenterXLabelText(true);
        x.setTextSize(12F);
       // x.setTypeface(mTf);
        barChart.animateY(2000); // 立即执行的动画,y轴
    }

    private BarData getBarDataWeek() {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd"); //设置时间格式
        SimpleDateFormat sdfWeek = new SimpleDateFormat("yyyyMMdd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
                if(1 == dayWeek) {
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                }
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        //所在周星期一的日期
        String imptimeBegin = sdf.format(cal.getTime());
        String monthStart = sdfWeek.format(cal.getTime());

        cal.add(Calendar.DATE, 6);
        //所在周星期日的日期
        String imptimeEnd = sdf.format(cal.getTime());
        String monthEnd = sdfWeek.format(cal.getTime());

        String pimptimeBegin = imptimeBegin.substring(imptimeBegin.indexOf(".") + 1);
        String pimptimeEnd = imptimeEnd.substring(imptimeEnd.indexOf(".") + 1);

        pimptimeBegin = pimptimeBegin.replaceFirst("^0+", "");
        pimptimeEnd = pimptimeEnd.replaceFirst("^0+", "");

        int edge = Integer.parseInt(monthEnd) - Integer.parseInt(monthStart) + 1;

        chartTitle.setText("一周低碳统计 KM/DAY (" + pimptimeBegin + "-" + pimptimeEnd + ")");

        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("MON");
        xValues.add("TUES");
        xValues.add("WED");
        xValues.add("THUR");
        xValues.add("FRI");
        xValues.add("SAR");
        xValues.add("SUN");

        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

        int count = mWeekData.length();

        for (int i = 0; i < 7; i++) {
            yValues.add(new BarEntry(0, i));
        }

        for (int i = 0; i < count; i++) {
            float todayDis = 0;
            try {
                JSONObject record = mWeekData.getJSONObject(i);
                int distance = record.getInt("distance");

                int dateNum = record.getInt("date");
                int index = dateNum - Integer.parseInt(monthStart);
                todayDis = distance / 1000.0f;
                yValues.set(index, new BarEntry(todayDis, index));
            } catch (Exception e) {

            }
        }
        // y轴的数据集合
        BarDataSet barDataSet = new BarDataSet(yValues, "KM/DAY");
        barDataSet.setBarSpacePercent(85);
        barDataSet.setColor(Color.rgb(242, 90, 35));

        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
        barDataSets.add(barDataSet); // add the datasets

        BarData barData = new BarData(xValues, barDataSets);

        return barData;
    }

    private BarData getBarDataMonth() {
        Date today = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        //当前月的最后一天
        cal.set(Calendar.DATE, 1);
        cal.roll(Calendar.DATE, -1);
        Date endTime=cal.getTime();
        String imptimeEnd = sdf.format(endTime);
        //当前月的第一天
        cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
        Date beginTime=cal.getTime();
        String imptimeBegin=sdf.format(beginTime);

        String pimptimeBegin = imptimeBegin.substring(imptimeBegin.indexOf(".") + 1);
        String pimptimeEnd = imptimeEnd.substring(imptimeEnd.indexOf(".") + 1);

        pimptimeBegin = pimptimeBegin.replaceFirst("^0+", "");
        pimptimeEnd = pimptimeEnd.replaceFirst("^0+", "");

        chartTitle.setText("当月低碳统计 KM/DAY (" + pimptimeBegin + "-" + pimptimeEnd + ")");

        SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyyMMdd"); //设置时间格式
        String monthEnd = sdfMonth.format(endTime);
        String monthStart = sdfMonth.format(beginTime);
        int edge = Integer.parseInt(monthEnd) - Integer.parseInt(monthStart) + 1;

        ArrayList<String> xValues = new ArrayList<String>();
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

        int count = mMonthData.length();

        for (int i = 0; i < edge; i++) {
            xValues.add(""+i);
            yValues.add(new BarEntry(0, i));
        }

        for (int i = 0; i < count; i++) {
            float todayDis = 0;
            try {
                JSONObject record = mMonthData.getJSONObject(i);
                int distance = record.getInt("distance");

                int dateNum = record.getInt("date");
                int index = dateNum - Integer.parseInt(monthStart);
                todayDis = distance / 1000.0f;
                yValues.set(index, new BarEntry(todayDis, index));
            } catch (Exception e) {

            }
        }

        // y轴的数据集合
        BarDataSet barDataSet = new BarDataSet(yValues, "KM/DAY");
        barDataSet.setBarSpacePercent(85);
        barDataSet.setColor(Color.rgb(242, 90, 35));

        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
        barDataSets.add(barDataSet); // add the datasets

        BarData barData = new BarData(xValues, barDataSets);

        return barData;
    }


    private BarData getBarDataYear() {
        Date today = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);

        String imptimeEnd = sdf.format(today);

        String pimptimeEnd = imptimeEnd.substring(imptimeEnd.indexOf(".") + 1,imptimeEnd.lastIndexOf('.'));

        pimptimeEnd = pimptimeEnd.replaceFirst("^0+", "");

        chartTitle.setText("当年低碳统计 KM/MONTH (1-" + pimptimeEnd + ")");

        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("Jan");
        xValues.add("Feb");
        xValues.add("Mar");
        xValues.add("Apr");
        xValues.add("May");
        xValues.add("Jun");
        xValues.add("Jul");
        xValues.add("Aug");
        xValues.add("Sep");
        xValues.add("Oct");
        xValues.add("Nov");
        xValues.add("Dec");

        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

        for (int i = 0; i < 12; i++) {
            float value = 0;
            yValues.add(new BarEntry(value, i));
        }

        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyyMM"); //设置时间格式
        Calendar calYear = Calendar.getInstance();
        calYear.setTime(today);
        int month = calYear.get(Calendar.MONTH);
        calYear.add(Calendar.MONTH, - month);
        String imptimeBegin = sdfYear.format(calYear.getTime());
        int monthBegin = Integer.parseInt(imptimeBegin);

        JSONArray namesArray = mYearData.names();
        int count = namesArray.length();
        for (int i = 0; i < count; i++) {
            try {
                String key = namesArray.getString(i);
                JSONObject obj = mYearData.getJSONObject(key);
                String disStr = obj.getString("distance");
                int edge = Integer.parseInt(key) - monthBegin;
                float edgeDis = Integer.parseInt(disStr) / 1000.0f;
                yValues.set(edge, new BarEntry(edgeDis, edge));
            } catch (Exception e) {

            }
        }

        // y轴的数据集合
        BarDataSet barDataSet = new BarDataSet(yValues, "KM/MONTH");
        barDataSet.setBarSpacePercent(85);
        barDataSet.setColor(Color.rgb(242, 90, 35));

        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
        barDataSets.add(barDataSet); // add the datasets

        BarData barData = new BarData(xValues, barDataSets);

        return barData;
    }

    private String getTodayString () {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        String todayStr = sdf.format(cal.getTime());
        return todayStr;
    }

    private String getTimeString(int seconds) {
        int sec = seconds;
        int fSec = sec % 60;
        sec /= 60;
        int fMin = sec % 60;
        sec /= 60;
        int fHour = sec;
        String timeStr = String.valueOf(fHour) + ":" + String.valueOf(fMin) + ":" + String.valueOf(fSec);
        return timeStr;
    }
}
