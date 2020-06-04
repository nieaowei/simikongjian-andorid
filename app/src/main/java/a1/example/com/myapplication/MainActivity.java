package a1.example.com.myapplication;

import android.content.Intent;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;



import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import a1.example.com.myapplication.Model.UserModel;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RequestUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登陆页面
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.zhuce_btn)
    Button zhuceBtn;
    @BindView(R.id.user_name)
    EditText userName;
    @BindView(R.id.user_pass)
    EditText userPass;
    String RESULT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @OnClick({R.id.login_btn, R.id.zhuce_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                login();
                break;
            case R.id.zhuce_btn:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    // 登录请求处理逻辑
    private void login() {
        if(userName.getText().toString().trim().equals("")){
            Toast.makeText(MainActivity.this, "账号不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }else if(userPass.getText().toString().trim().equals("")){
            Toast.makeText(MainActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        UserModel userModel = new UserModel();
        userModel.setUsername(userName.getText().toString().trim());
        String urlpath = MyWriteUtils.MyURL+"/login?username="+(userName.getText().toString().trim())+"&password="+userPass.getText().toString().trim();
        try{
        URL url = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20 * 1000);
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream inputStream = conn.getInputStream();
                String result = RequestUtils.stremToString(inputStream);
                RESULT = result;
                System.out.println("=====================服务器返回的信息：：" + result);
                boolean isLoginsuccess=false;
                if (result.contains("success")) {
                    isLoginsuccess=true;
                    conn.disconnect();
                }
                Message msg = Message.obtain();
                msg.obj=isLoginsuccess;
                //handler.sendMessage(msg);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        //RESULT = MyHttpRequest.getServiceInfo(url);
        if (RESULT.equals("1")){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, HomeActivity.class);
            intent.setAction("user");
            intent.putExtra("username",userModel.getUsername());
            startActivity(intent);
            finish();
        }else if(RESULT.equals("")){
            Toast.makeText(MainActivity.this, "服务器异常！", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "账号密码错误，请重新登陆！", Toast.LENGTH_SHORT).show();
        }
    }

}

