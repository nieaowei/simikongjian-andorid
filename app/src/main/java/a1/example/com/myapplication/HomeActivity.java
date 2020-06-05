package a1.example.com.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import a1.example.com.myapplication.Model.UserModel;
import a1.example.com.myapplication.Util.MyWriteUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.pic_btn)
    ConstraintLayout picBtn;
    @BindView(R.id.weekday_btn)
    ConstraintLayout weekdayBtn;
    @BindView(R.id.happy_btn)
    ConstraintLayout happyBtn;
    @BindView(R.id.myself_btn)
    ConstraintLayout myselfBtn;
    @BindView(R.id.shop_btn)
    ConstraintLayout shopBtn;
    @BindView(R.id.manage_btn)
    ConstraintLayout manageBtn;
    @BindView(R.id.foot_btn)
    ConstraintLayout footBtn;
    @BindView(R.id.friend_btn)
    ConstraintLayout friendBtn;
    @BindView(R.id.setting_btn)
    ConstraintLayout settingBtn;
    String USERNAME = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        notice();
    }
    private void notice(){
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
            OutputStream os= httpURLConnection.getOutputStream();

            SharedPreferences preferences = getSharedPreferences("user",0);
            String USERNAME = preferences.getString("username","");

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
                Log.d("【sb】",sb.toString());
                JSONArray array = JSONArray.parseArray(sb.toString());
                for (int i=0; i<array.size(); i++){
                    com.alibaba.fastjson.JSONObject jsonObject = array.getJSONObject(i);
                    String strDate = jsonObject.getString("birthday");
                    String mytext = jsonObject.getString("shenfen");
                    if (strDate.equals(getStringDateShort())){
                        Toast.makeText(this, "【纪念日提醒】" + mytext, Toast.LENGTH_SHORT).show();
                    }
                }
                httpURLConnection.disconnect();

            }else{
                Log.w("HTTP","Connction failed"+httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getStringDateShort(){
        Date curr = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(curr);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, MainActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.pic_btn, R.id.weekday_btn,R.id.happy_btn,R.id.myself_btn,R.id.shop_btn,R.id.manage_btn,R.id.foot_btn,R.id.friend_btn,R.id.setting_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.pic_btn):
                // 照片管理
                picture();
                break;
            case R.id.weekday_btn:
                // 节假日管理
                weekday();
                break;
            case R.id.happy_btn:
                // 心情日记
                happy();
                break;
            case R.id.myself_btn:
                // 个人信息
                personalInfo();
                break;
            case R.id.shop_btn:
                //心情商城
                gameShop();
                break;
            case R.id.manage_btn:
                // 服装搭配
                myclothes();
                break;
            case R.id.foot_btn:
                // 记录痕迹
                Myfoot();
                break;
            case R.id.friend_btn:
                // 朋友圈
                Myfriends();
                break;
            case R.id.setting_btn:
                // 我的设置
                UserSetting();
                break;
        }
    }

    private void UserSetting() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, SettingActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void Myfriends() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, MyFriendsActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void myclothes() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, MyClothesActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void Myfoot() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, MyFootActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void gameShop() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, GameShopActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void personalInfo() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, PersonalInfoActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void happy() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, HappyNoteActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }
// 节假日
    private void weekday() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, WeekDayActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    /**
     * 照片管理
     */
    private void picture() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, PictureActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }
}
