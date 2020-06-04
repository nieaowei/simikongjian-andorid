package a1.example.com.myapplication;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import a1.example.com.myapplication.Model.UserModel;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RecordUserFootUtil;
import a1.example.com.myapplication.Util.RequestUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HappyActivity extends AppCompatActivity {
    @BindView(R.id.happy_pass)
    EditText happyPass;
    @BindView(R.id.happy_ok_btn)
    Button happyOk;
    @BindView(R.id.happy_reset_btn)
    Button happyReset;
    @BindView(R.id.happy_close_btn)
    ImageView happyClose;

    String USERNAME = "";
    String RESULT = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy);
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
            super.onBackPressed();//注释掉这行,back键不退出activity
            Intent intent = new Intent();
            intent.setClass(HappyActivity.this, HomeActivity.class);
            intent.setAction("user");
            intent.putExtra("username",USERNAME);
            startActivity(intent);
            finish();
        }

        @OnClick({R.id.happy_ok_btn, R.id.happy_reset_btn,R.id.happy_close_btn})
        public void onClick(View view) {
            switch (view.getId()) {
                case (R.id.happy_ok_btn):
                    loginHappy();
                    //Toast.makeText(HomeActivity.this, "1！", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.happy_reset_btn:

                    // 节假日管理
                    happyPass.setText("");
                    break;
                case R.id.happy_close_btn:
                    // 节假日管理
                    Intent intent = new Intent();
                    intent.setClass(HappyActivity.this, HomeActivity.class);
                    intent.setAction("user");
                    intent.putExtra("username",USERNAME);
                    startActivity(intent);
                    finish();
                    //Toast.makeText(HomeActivity.this, "2！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    // 输入密码进入心情日记
    private void loginHappy() {
        if(happyPass.getText().toString().trim().equals("")){
            Toast.makeText(HappyActivity.this, "请输入密码！", Toast.LENGTH_SHORT).show();
            return;
        }
        URL url = null;
        String urlpath = MyWriteUtils.MyURL+"/loginnote";
        try {
            url=new URL(urlpath);
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
            UserModel userModel = new UserModel() ;
            //dataPost类是自定义的数据交互对象，只有两个成员变量
            JSONObject userJSON = new JSONObject();
            userJSON.put("noteuser",USERNAME);
            userJSON.put("notepass",happyPass.getText().toString().trim());
            String content = String.valueOf(userJSON);

            os.write(content.getBytes());
            os.flush();
            os.close();

            //将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端，并获取访问状态
            if(HttpURLConnection.HTTP_OK==httpURLConnection.getResponseCode()){

                InputStream inputStream = httpURLConnection.getInputStream();
                String result1 = RequestUtils.stremToString(inputStream);
                RESULT = result1;
                if (RESULT.equals("success")){
                    Intent intent = new Intent();
                    intent.setClass(HappyActivity.this, HappyNoteActivity.class);
                    intent.setAction("user");
                    intent.putExtra("result", RESULT);
                    intent.putExtra("username", USERNAME);
                    startActivity(intent);
                    finish();
                    String userFoot = "进入心情日记";
                    RecordUserFootUtil.recordUserFoot(USERNAME,userFoot);
                    //Toast.makeText(WeekDayBirthdayActivity.this, "设置完成！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(HappyActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                };
                httpURLConnection.disconnect();

            }else{
                Log.w("HTTP","Connction failed"+httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //RESULT = MyHttpRequest.getServiceInfo(url);
    }

}
