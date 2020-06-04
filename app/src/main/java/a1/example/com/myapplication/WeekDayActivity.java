package a1.example.com.myapplication;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import a1.example.com.myapplication.Model.UserModel;
import a1.example.com.myapplication.Model.WeekDayModel;
import a1.example.com.myapplication.Util.MyWriteUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeekDayActivity extends AppCompatActivity {

    @BindView(R.id.set_birthday_btn)
    RelativeLayout birthdayBtn;
    @BindView(R.id.my_day_btn)
    RelativeLayout myDayBtn;
    @BindView(R.id.holiday_btn)
    RelativeLayout holidayBtn;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    String USERNAME = "";
    String RESULT = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_day);
        ButterKnife.bind(this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals("user")){
            String username = intent.getStringExtra("username");
            USERNAME = username;
        }
    }

        @OnClick({R.id.set_birthday_btn, R.id.my_day_btn,R.id.holiday_btn,R.id.back_btn})
        public void onClick(View view) {
            switch (view.getId()) {
                case (R.id.set_birthday_btn):
                    setBirthday();
                    break;
                case R.id.my_day_btn:
                    myDay();
                    break;
                case R.id.holiday_btn:
                    holiday();
                    break;
                case R.id.back_btn:
                    Intent intent = new Intent();
                    intent.setClass(WeekDayActivity.this, HomeActivity.class);
                    intent.setAction("user");
                    intent.putExtra("username",USERNAME);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(WeekDayActivity.this, HomeActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    // 节假日
    private void holiday() {
        Intent intent = new Intent();
        intent.setClass(WeekDayActivity.this, WeekDayHolidayActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }
    // 我的特殊日
    private void myDay() {
        myDayRequest();
    }
    WeekDayModel data= new WeekDayModel();
    private void myDayRequest() {
        URL url = null;
        String urlStr = MyWriteUtils.MyURL+"/selectWeekDay";
        String result="";//要返回的结果

        try {
            url=new URL(urlStr);
            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();

            httpURLConnection.setConnectTimeout(50000);//设置连接超时时间，单位ms
            httpURLConnection.setReadTimeout(50000);//设置读取超时时间，单位ms

            //设置是否向httpURLConnection输出，因为post请求参数要放在http正文内，所以要设置为true
            httpURLConnection.setDoOutput(true);

            //设置是否从httpURLConnection读入，默认是false
            httpURLConnection.setDoInput(true);

            //POST请求不能用缓存，设置为false
            httpURLConnection.setUseCaches(false);

            //传送的内容是可序列化的
            //如果不设置此项，传送序列化对象时，当WEB服务默认的不是这种类型时，会抛出java.io.EOFException错误

            //设置请求方法是POST
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.connect();
            //连接服务器
            //httpURLConnection.connect();

            //getOutputStream会隐含调用connect()，所以不用写上述的httpURLConnection.connect()也行。
            //得到httpURLConnection的输出流
            OutputStream  os= httpURLConnection.getOutputStream();
            UserModel userModel = new UserModel() ;
            //dataPost类是自定义的数据交互对象，只有两个成员变量
            JSONObject userJSON = new JSONObject();
            userJSON.put("createby",USERNAME);
            String content = String.valueOf(userJSON);

            os.write(content.getBytes());
            os.flush();
            os.close();

            //将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端，并获取访问状态
            if(HttpURLConnection.HTTP_OK==httpURLConnection.getResponseCode()){
                StringBuffer sb=new StringBuffer();
                String readLine=new String();
                BufferedReader responseReader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
                while((readLine=responseReader.readLine())!=null){
                    sb.append(readLine);
                }

                RESULT = sb.toString();
                if (!RESULT.equals("")){
                    Intent intent = new Intent();
                    intent.setClass(WeekDayActivity.this, WeekDayMyDayActivity.class);
                    intent.setAction("user");
                    intent.putExtra("result", RESULT);
                    intent.putExtra("username", USERNAME);
                    startActivity(intent);
                    finish();
                    //Toast.makeText(WeekDayBirthdayActivity.this, "设置完成！", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(WeekDayBirthdayActivity.this, "设置失败,请检查网络！", Toast.LENGTH_SHORT).show();
                };
                httpURLConnection.disconnect();

            }else{
                Log.w("HTTP","Connction failed"+httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 设置生日
    private void setBirthday() {
        Intent intent = new Intent();
        intent.setClass(WeekDayActivity.this, WeekDayBirthdayActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private static String readMyInputStream(InputStream inputStream) throws Exception {
        byte[] result;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bout.write(buffer, 0, len);
        }
        bout.close();
        inputStream.close();
        String result5 = new String(bout.toByteArray(),"UTF-8");

        return result5;
    }

    Map<String, Object> jsonToMap(String content) {
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

    public static Map<String, Object> getObjectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        Class<?> clazz = obj.getClass();
        System.out.println(clazz);
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(obj);
            if (value == null){
                value = "";
            }
            map.put(fieldName, value);
        }
        return map;
    }
}
