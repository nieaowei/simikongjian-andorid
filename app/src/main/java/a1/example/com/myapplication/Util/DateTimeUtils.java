package a1.example.com.myapplication.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.sdsmdg.tastytoast.TastyToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class DateTimeUtils {

    public static int FROM_START_TIME = 101;

    public static int FROM_END_TIME = 102;

    /**
     * @param FragmentManager act
     * @param listener        回调
     * @param date            传空标识不需要日期
     * @return 返回日期时间
     */
    public static String setTimeDialog(FragmentManager FragmentManager, final GetDataListener listener, final String date) {

        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                        Log.e("时间：", hourOfDay + "：" + minute);
//                        这里时间要改为双位数 都要改为双位数
                        if (TextUtils.isEmpty(date)) {
                            listener.setSelectDate(getDoubleDate(hourOfDay) + ":" + getDoubleDate(minute));
                        } else {
                            listener.setSelectDate(date + " " + getDoubleDate(hourOfDay) + ":" + getDoubleDate(minute));
                        }
                    }
                })
//                用当时日期来算
                .setStartTime(Integer.valueOf(getSimpalDateAll().get(3)), Integer.valueOf(getSimpalDateAll().get(4)))
                .setDoneText("确定")
                .setCancelText("取消")
                .setThemeLight();
        rtpd.show(FragmentManager, "time");
        return "";
    }

    public interface GetDataListener {

        void setSelectDate(String value);

    }

    /**
     * 日期时间选择器
     *
     * @param FragmentManager 需要
     * @param listener        回调
     * @param needTime        是否需要时间选择器
     */
    public static void showDateAndTimeDialog(final FragmentManager FragmentManager, final GetDataListener listener, final boolean needTime) {

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        Log.e("日期：", year + "-" + getDoubleDate(monthOfYear + 1) + "-" + getDoubleDate(dayOfMonth));
                        if (needTime) {
                            setTimeDialog(FragmentManager, listener, year + "-" + getDoubleDate((monthOfYear + 1)) + "-" + getDoubleDate(dayOfMonth));
                        } else {
                            listener.setSelectDate(year + "-" + getDoubleDate((monthOfYear + 1)) + "-" + getDoubleDate(dayOfMonth));
                        }
                    }
                })
//                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(Integer.valueOf(getSimpalDateAll().get(0)), Integer.valueOf(getSimpalDateAll().get(1)), Integer.valueOf(getSimpalDateAll().get(2)))
//                范围
                .setDateRange(null, getNowTime())
                .setDoneText("确定")
                .setCancelText("取消")
                .setThemeLight();
        cdp.show(FragmentManager, "date");

    }

    /**
     * 日期时间双位格式化
     *
     * @param value
     * @return
     */
    private static String getDoubleDate(int value) {
        String str = String.valueOf(value);

        if (str.length() > 1) {
            return str;
        } else {
            return "0" + str;
        }


    }

    private static MonthAdapter.CalendarDay getNowTime() {

        return new MonthAdapter.CalendarDay(Integer.valueOf(getSimpalDateAll().get(0)), Integer.valueOf(getSimpalDateAll().get(1)), Integer.valueOf(getSimpalDateAll().get(2)));

    }

    private static List<String> getSimpalDateAll() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        String dateNow = simpleDateFormat.format(new Date());
        String[] strings = dateNow.split("-");
//        这里的月份要减一
        strings[1] = Integer.valueOf(strings[1]) - 1 + "";
        return Arrays.asList(strings);

    }

    /**
     * @param start   开始时间
     * @param endTime 结束时间
     * @param value   设置时间
     * @param from    当前选择分类 0：开始时间， 1：结束时间
     * @return 是否正确
     */
    public static boolean checkDateTime(Context context, String start, String endTime, String value, int from) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//年-月-日 时-分
        try {
            Date datevalue = dateFormat.parse(value);//传入时间
            Date date1 = dateFormat.parse(start);//开始时间
            Date date2 = dateFormat.parse(endTime);//结束时间
            if (from == FROM_START_TIME) {
                if (date2.getTime() < datevalue.getTime()) {
//                    开始时间不能大于结束时间，
                    TastyToast.makeText(context, "开始时间不可大于结束时间!", Toast.LENGTH_LONG, TastyToast.WARNING);
                    return false;
                }
            } else {
//                默认我传入的时间已经是最大的了，这个逻辑
                if (date2.getTime() < datevalue.getTime()) {
//                    结束时间不能大于昂前日期
                    TastyToast.makeText(context, "结束时间不可大于当前时间!", Toast.LENGTH_LONG, TastyToast.WARNING);
                    return false;
                } else if (datevalue.getTime() < date1.getTime()) {
//                    结束时间不能小于开始时间
                    TastyToast.makeText(context, "结束时间不可小于开始时间!", Toast.LENGTH_LONG, TastyToast.WARNING);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    }
