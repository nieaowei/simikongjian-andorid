package a1.example.com.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import a1.example.com.myapplication.Model.WeatherModel;
import a1.example.com.myapplication.Util.GsonUtil;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.OkHttpUtil;
import a1.example.com.myapplication.Util.RecordUserFootUtil;
import a1.example.com.myapplication.Util.RequestUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HappyNoteWriteActivity extends AppCompatActivity {
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.tv_weather)
    TextView myWeather;
    @BindView(R.id.tv_city)
    TextView myCity;
    @BindView(R.id.my_note_title)
    EditText myNoteTitle;
    @BindView(R.id.my_note_message)
    EditText myNoteMessage;
    @BindView(R.id.commit_my_note)
    Button commitMyNote;

    String USERNAME = "";
    String RESULT = "";
    String type = "";//天气
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy_note_write);

        ButterKnife.bind(this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

//        Intent intent = getIntent();
//        String action = intent.getAction();
//        if(action.equals("user")){
//            String username = intent.getStringExtra("username");
//            USERNAME = username;
//        }

        SharedPreferences preferences = getSharedPreferences("user",0);
        USERNAME = preferences.getString("username","");
        getCity();
        getWeather();
    }

    private void getWeather(){
        URL url = null;
        String urlStr = MyWriteUtils.MyURL+"/selectWether";
        String result="";//要返回的结果

        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put("username",USERNAME);
        try {
            result = OkHttpUtil.doPostHttpRequest2(urlStr, jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("[result]", result);
        JSONArray jsonArray = JSONArray.parseArray(result);
        com.alibaba.fastjson.JSONObject data = jsonArray.getJSONObject(0);
        String date = data.getString("ymd");
        String week = data.getString("week");
        type = data.getString("type");
        myWeather.append(date + " " + week + " " + type);
    }

    private void getCity(){
        String strUrl = "http://whois.pconline.com.cn/ipJson.jsp?json=true";
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        try {
            URL url = new URL(strUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "GB2312");
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(60000);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200){
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream(), "GB2312"));
                String line = null;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = reader.readLine()) != null){
                    stringBuffer.append(line);
                }
                Log.d("【str】", stringBuffer.toString());
                jsonObject = JSON.parseObject(stringBuffer.toString());
                reader.close();
            }
            httpURLConnection.disconnect();
        }catch (Exception e)
        {
            e.printStackTrace();
            setTitle(e.getMessage());
        }
        Log.d("【jsonObject】", jsonObject.toJSONString());
        String city = jsonObject.getString("city");
        Log.d("【city】", city);
        myCity.append(city + "\n");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        Intent intent = new Intent();
        intent.setClass(HappyNoteWriteActivity.this,HappyNoteActivity .class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.back_btn,R.id.commit_my_note})
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.back_btn):
                onBackPressed();
                break;
            case R.id.commit_my_note:
                commitMyNote(myNoteTitle.getText().toString().trim(),
                        myNoteMessage.getText().toString().trim(),
                        myCity.getText().toString().trim(),
                        type);
                break;
        }
    }

    private void commitMyNote(String myNoteTitles,
                              String myNoteMessages,
                              String myCity,
                              String myWeather) {
        URL url = null;
        String urlStr = MyWriteUtils.MyURL+"/writenote";
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
            OutputStream os= httpURLConnection.getOutputStream();

            //dataPost类是自定义的数据交互对象，只有两个成员变量
            JSONObject userJSON = new JSONObject();
            userJSON.put("notetitle",myNoteTitles);
            userJSON.put("note",myNoteMessages);
            userJSON.put("noteuser",USERNAME);
            userJSON.put("address",myCity);
            userJSON.put("weather",myWeather);
            Log.d("【userJSON】",userJSON.toString());
            String content = String.valueOf(userJSON);

            os.write(content.getBytes());
            os.flush();
            os.close();

            //将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端，并获取访问状态
            if(HttpURLConnection.HTTP_OK==httpURLConnection.getResponseCode()){
                InputStream inputStream = httpURLConnection.getInputStream();
                String result1 = RequestUtils.stream2jarray(inputStream);

                RESULT = result1;
                if (RESULT.equals("success")){
                    myNoteTitle.setText("");
                    myNoteMessage.setText("");
                    Toast.makeText(HappyNoteWriteActivity.this, "您已经上传！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(HappyNoteWriteActivity.this, "上传失败,请检查网络！", Toast.LENGTH_SHORT).show();
                };
                httpURLConnection.disconnect();
                String userFoot = "写日记";
                RecordUserFootUtil.recordUserFoot(USERNAME,userFoot);
            }else{
                Log.w("HTTP","Connction failed"+httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
