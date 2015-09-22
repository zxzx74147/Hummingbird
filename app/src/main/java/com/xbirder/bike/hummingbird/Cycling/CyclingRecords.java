package com.xbirder.bike.hummingbird.Cycling;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.github.mikephil.charting.charts.BarChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Administrator on 2015/8/26.
 */
public class CyclingRecords extends BaseActivity{

    private BarChart mBarChart;
    private BarData mBarData;
    private RadioButton radio1,radio2,radio3;
    private TextView chartTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cycling_data);

        mBarChart = (BarChart) findViewById(R.id.chart);

        radio1 = (RadioButton)findViewById(R.id.btn_0);
        radio2 = (RadioButton)findViewById(R.id.btn_1);
        radio3 = (RadioButton)findViewById(R.id.btn_2);


        chartTitle = (TextView)findViewById(R.id.tv_data);

        RadioGroup group = (RadioGroup)this.findViewById(R.id.time_selection_RadioGroup);
        //绑定一个匿名监听器
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                //获取变更后的选中项的ID
                //int radioButtonId = arg0.getCheckedRadioButtonId();
                if(arg1 == radio1.getId())
                {
                    mBarData = getBarDataWeek(7, 30);
                    showBarChart(mBarChart, mBarData);


                }else if(arg1 == radio2.getId()){
                    mBarData = getBarDataMonth(30, 30);
                    showBarChart(mBarChart, mBarData);
                    mBarChart.setDrawYValues(false);

                }else if(arg1 == radio3.getId()){
                    mBarData = getBarDataYear(12, 30);
                    showBarChart(mBarChart, mBarData);
                }
            }
        });

        mBarData = getBarDataWeek(7, 30);
        showBarChart(mBarChart, mBarData);
    }
    private void showBarChart(BarChart barChart, BarData barData) {

        barChart.setDrawVerticalGrid(false); // 是否显示水平的表格

        barChart.setDescription("");// 数据描述

        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("You need to provide data for the chart.");
        //barChart.setDrawVerticalGrid(false);
        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        //barChart.setGridColor(Color.WHITE & 0x70FFFFFF);
        //barChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度

        barChart.setDrawYValues(true);

        barChart.setTouchEnabled(false ); // 设置是否可以触摸
        barChart.setDragEnabled(false);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放

        barChart.setPinchZoom(false);//

//      barChart.setBackgroundColor();// 设置背景
        barChart.setDrawBorder(false);
        barChart.setDrawBarShadow(false);

        barChart.setData(barData); // 设置数据

        Legend mLegend = barChart.getLegend(); // 设置比例图标示

        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.BLACK);// 颜色

        YLabels y = barChart.getYLabels(); // y轴的标示
        y.setTextColor(Color.BLACK);
       // y.setTypeface(mTf);
        y.setTextSize(16F);
        y.setLabelCount(4); // y轴上的标签的显示的个数

        XLabels x = barChart.getXLabels(); // x轴显示的标签
        x.setTextColor(Color.BLACK);
        x.setPosition(XLabels.XLabelPosition.BOTTOM.BOTTOM);
        x.setCenterXLabelText(true);
        x.setTextSize(16F);
       // x.setTypeface(mTf);
        barChart.animateY(2000); // 立即执行的动画,x轴
    }

    private BarData getBarDataWeek(int count, float range) {
        Date today = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
                if(1 == dayWeek) {
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                }
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        //所在周星期一的日期
        String imptimeBegin = sdf.format(cal.getTime());

        cal.add(Calendar.DATE, 6);
        //所在周星期日的日期
        String imptimeEnd = sdf.format(cal.getTime());

        String pimptimeBegin = imptimeBegin.substring(imptimeBegin.indexOf(".") + 1);
        String pimptimeEnd = imptimeEnd.substring(imptimeEnd.indexOf(".") + 1);

        pimptimeBegin = pimptimeBegin.replaceFirst("^0+", "");
        pimptimeEnd = pimptimeEnd.replaceFirst("^0+", "");

        chartTitle.setText("一周低碳统计 KM/DAY (" + pimptimeBegin +"-" + pimptimeEnd +")");

        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("MON");
        xValues.add("TUES");
        xValues.add("WED");
        xValues.add("THUR");
        xValues.add("FRI");
        xValues.add("SAR");
        xValues.add("SUN");

        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

        for (int i = 0; i < count; i++) {
            float value = (float) (Math.random() * range/*100以内的随机数*/) + 3;
            yValues.add(new BarEntry(value, i));
        }
        // y轴的数据集合
        BarDataSet barDataSet = new BarDataSet(yValues, "KM/DAY");
        barDataSet.setBarSpacePercent(50);
        barDataSet.setColor(Color.rgb(0, 188, 223));

        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
        barDataSets.add(barDataSet); // add the datasets

        BarData barData = new BarData(xValues, barDataSets);

        return barData;
    }

    private BarData getBarDataMonth(int count, float range) {
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

        chartTitle.setText("当月低碳统计 KM/DAY (" + pimptimeBegin +"-" + pimptimeEnd +")");

        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xValues.add(""+i);
        }
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

        for (int i = 0; i < count; i++) {
            float value = (float) (Math.random() * range/*100以内的随机数*/) + 3;
            yValues.add(new BarEntry(value, i));
        }
        // y轴的数据集合
        BarDataSet barDataSet = new BarDataSet(yValues, "KM/DAY");
        barDataSet.setBarSpacePercent(50);
        barDataSet.setColor(Color.rgb(0, 188, 223));

        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
        barDataSets.add(barDataSet); // add the datasets

        BarData barData = new BarData(xValues, barDataSets);

        return barData;
    }


    private BarData getBarDataYear(int count, float range) {
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

        for (int i = 0; i < count; i++) {
            float value = (float) (Math.random() * range/*100以内的随机数*/) + 3;
            yValues.add(new BarEntry(value, i));
        }
        // y轴的数据集合
        BarDataSet barDataSet = new BarDataSet(yValues, "KM/MONTH");
        barDataSet.setBarSpacePercent(50);
        barDataSet.setColor(Color.rgb(0, 188, 223));

        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
        barDataSets.add(barDataSet); // add the datasets

        BarData barData = new BarData(xValues, barDataSets);

        return barData;
    }
}
