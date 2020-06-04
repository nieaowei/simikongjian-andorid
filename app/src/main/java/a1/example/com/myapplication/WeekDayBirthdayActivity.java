package a1.example.com.myapplication;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import a1.example.com.myapplication.Model.UserModel;
import a1.example.com.myapplication.Model.WeekDayModel;
import a1.example.com.myapplication.Util.DateTimeUtils;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RecordUserFootUtil;
import a1.example.com.myapplication.Util.RequestUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeekDayBirthdayActivity extends AppCompatActivity {

    @BindView(R.id.friend_name)
    EditText friendBtn;
    @BindView(R.id.date_btn)
    TextView dateBtn;
    @BindView(R.id.set_date_btn)
    Button saveDateBtn;
    @BindView(R.id.close_set_btn)
    Button closeSetBtn;
    String RESULT = "";
    String USERNAME = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_day_birthday);
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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(WeekDayBirthdayActivity.this, WeekDayActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }


    @OnClick({R.id.date_btn, R.id.set_date_btn,R.id.close_set_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date_btn:
                DateTimeUtils.showDateAndTimeDialog(getSupportFragmentManager(), new DateTimeUtils.GetDataListener() {
                    @Override
                    public void setSelectDate(String value) {
//                        设置时间
//                        这里校验一下，如果可以用再设置
                        dateBtn.setText(value);
                    }
                }, false);
                break;
            case R.id.set_date_btn:
                setFriendDate(friendBtn.getText().toString().trim(),dateBtn.getText().toString().trim());
                break;
            case R.id.close_set_btn:
                friendBtn.setText("");
                dateBtn.setText("");
                break;
        }
    }

    private void setFriendDate(String friend,String birthday) {
        URL url = null;
        String urlStr = MyWriteUtils.MyURL+"/weekdayBirthday";
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
            WeekDayModel weekday = new WeekDayModel();
            weekday.setShenfen(friend);
            weekday.setBirthday(birthday);
            weekday.setCreateby("1");
            UserModel userModel = new UserModel() ;
            //dataPost类是自定义的数据交互对象，只有两个成员变量
            JSONObject userJSON = new JSONObject();
            userJSON.put("shenfen",friend);
            userJSON.put("birthday",birthday);
            userJSON.put("createby",USERNAME);
            userJSON.put("weekday",new Gson().toJson(weekday));
            String content = String.valueOf(userJSON);

            os.write(content.getBytes());
            os.flush();
            os.close();

            //将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端，并获取访问状态
            if(HttpURLConnection.HTTP_OK==httpURLConnection.getResponseCode()){
                InputStream inputStream = httpURLConnection.getInputStream();
                String result1 = RequestUtils.stream2jarray(inputStream);

                RESULT = result1;
                if (RESULT.equals("1")){
                    Toast.makeText(WeekDayBirthdayActivity.this, "设置完成！", Toast.LENGTH_SHORT).show();
                    String userFoot = "设置了好友生日";
                    RecordUserFootUtil.recordUserFoot(USERNAME,userFoot);
                }else{
                    Toast.makeText(WeekDayBirthdayActivity.this, "设置失败,请检查网络！", Toast.LENGTH_SHORT).show();
                };
                httpURLConnection.disconnect();

            }else{
                Log.w("HTTP","Connction failed"+httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
