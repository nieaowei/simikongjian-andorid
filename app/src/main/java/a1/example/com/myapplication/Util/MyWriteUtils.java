package a1.example.com.myapplication.Util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyWriteUtils {

    // public static String MyURL = "http://192.168.0.103:8080/myproject";
    public static String MyURL = "http://118.178.195.221:8080/myproject";

    public static String getURL(){
        try {
            //根据域名查找主机的IP地址(由于可能有多个服务器，ip地址并不是唯一的，要想获取准确的，还需要获取所有的地址才行)
            InetAddress inetAddress=InetAddress.getByName("www.baidu.com");
            System.out.println(inetAddress);//结果：14.215.177.38
            //反向查找主机名
            InetAddress byName = InetAddress.getByName("113.105.245.103");
            System.out.println("反向查找主机名：   "+byName.getHostName());//如果没有主机名，会返回IP地址

            //得到主机的所有地址
            InetAddress[] inetAddresses=InetAddress.getAllByName("www.taobao.com");
            for (InetAddress address : inetAddresses) {
                System.out.println(address);
            }

            //getLocalHost获取当前主机名和IP地址
            InetAddress me = InetAddress.getLocalHost();//得到主机名/IP地址 的形式
            System.out.println(me);//如果电脑没有联网，会返回127.0.0.1
            System.out.println(me.getHostName());//得到主机名
            return me.toString();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static String ip(){
        if(MyURL == null || MyURL.length() == 0 || "unknown".equalsIgnoreCase(MyURL)) {
            //MyURL = MyURL.getRemoteAddr();
            if(MyURL.equals("127.0.0.1") || MyURL.equals("0:0:0:0:0:0:0:1")){
                //根据网卡取本机配置的IP
                InetAddress inet=null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                MyURL= inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        /*if(MyURL!=null && MyURL.length()>15){ //"***.***.***.***".length() = 15
            if(MyURL.indexOf(",")>0){
                MyURL = MyURL.substring(0,MyURL.indexOf(","));
            }
        }*/
        return MyURL;
    }

    public static int differentDays(Date date1, Date date2){
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        int day1 = calendar1.get(Calendar.DAY_OF_YEAR);
//        System.out.println(day1);
        int day2 = calendar2.get(Calendar.DAY_OF_YEAR);
//        System.out.println(day2);
        int year1 = calendar1.get(Calendar.YEAR);
        int year2 = calendar2.get(Calendar.YEAR);

        if (year1 != year2)  //不同年
        {
            int timeDistance = 0;
            for (int i = year1 ; i < year2 ;i++){ //闰年
                if (i%4==0 && i%100!=0||i%400==0){
                    timeDistance += 366;
                }else { // 不是闰年
                    timeDistance += 365;
                }
            }
            return  timeDistance + (day2-day1);
        }else{// 同年
            return day2-day1;
        }

    }

    public  static long differentDayMillisecond (String bir,String today)
    {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        long diff=0l;
        try {
            long d1 = formater.parse(bir).getTime();
            long d2 = formater.parse(today).getTime();
            //diff=(Math.abs(d1-d2) / (1000 * 60 * 60 * 24));
            diff=(d1-d2)/(1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //day = day%365;
        return diff;
    }

    public static Map<String, Object> jsonToMap(String content) {
        content = content.trim();
        Map<String, Object> result = new HashMap<>();
        try {

            if (content.charAt(0) == '[') {
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Object value = jsonArray.get(i);
                    if (value instanceof JSONArray || value instanceof JSONObject) {
                        result.put(i + "", jsonToMap(value.toString().trim()));
                    } else {
                        result.put(i + "", jsonArray.getString(i));
                    }
                }
            } else if (content.charAt(0) == '{'){
                JSONObject jsonObject = new JSONObject(content);
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Object value = jsonObject.get(key);
                    if (value instanceof JSONArray || value instanceof JSONObject) {
                        result.put(key, jsonToMap(value.toString().trim()));
                    } else {
                        result.put(key, value.toString().trim());
                    }
                }
            }else {
                Log.e("异常", "json2Map: 字符串格式错误");
            }
        } catch (JSONException e) {
            Log.e("异常", "json2Map: ", e);
            result = null;
        }
        return result;
    }
}
