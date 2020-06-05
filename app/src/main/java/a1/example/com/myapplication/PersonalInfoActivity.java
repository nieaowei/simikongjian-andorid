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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import a1.example.com.myapplication.Model.UserInfoModel;
import a1.example.com.myapplication.Model.UserModel;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RecordUserFootUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalInfoActivity extends AppCompatActivity {

    @BindView(R.id.my_height)
    EditText myHeight;
    @BindView(R.id.my_weight)
    EditText myWeight;
    @BindView(R.id.my_favorite)
    EditText myFavorite;
    @BindView(R.id.update_my_info)
    Button updateMyinfo;
    @BindView(R.id.commit_my_info)
    Button commitMyinfo;

    @BindView(R.id.history_btn)
    Button history_btn;

    @BindView(R.id.lastupdate)
    TextView lastupdate;

    UserInfoModel userInfoModel = new UserInfoModel();
    String USERNAME = "";
    String RESULT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        ButterKnife.bind(this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals("user")) {
            String username = intent.getStringExtra("username");
            USERNAME = username;
        }
        initDate();
    }

    private void initDate() {
        myInfoRequest();
        if (userInfoModel != null) {
            myHeight.setText(userInfoModel.getUserheight());
            myFavorite.setText(userInfoModel.getUserfavorite());
            myWeight.setText(userInfoModel.getUserweight());
            lastupdate.setText("上一次更新时间: "+userInfoModel.getUpdatedt());
        }
    }

    private void myInfoRequest() {
        URL url = null;
        String urlStr = MyWriteUtils.MyURL + "/selectInfoByName";
        String result = "";//要返回的结果

        try {
            url = new URL(urlStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
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
            OutputStream os = httpURLConnection.getOutputStream();
            UserModel userModel = new UserModel();
            //dataPost类是自定义的数据交互对象，只有两个成员变量
            JSONObject userJSON = new JSONObject();
            userJSON.put("username", USERNAME);
            String content = String.valueOf(userJSON);

            os.write(content.getBytes());
            os.flush();
            os.close();

            //将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端，并获取访问状态
            if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()) {
                StringBuffer sb = new StringBuffer();
                String readLine = new String();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine);
                }

                RESULT = sb.toString();

                if (!RESULT.equals("")) {
                    Map<String, Object> map = MyWriteUtils.jsonToMap(RESULT);
                    Iterator iterator = map.keySet().iterator();
                    int length = map.size();
                    for (int i = 0; i < length; i++) {
//                        Object aa = map.values().iterator().next();
                        Map<String, Object> aaa = (Map<String, Object>) map.values().iterator().next();
                        iterator.next();
                        iterator.remove();

                        String userfavorite = (String) aaa.get("userfavorite");
                        String userheight = (String) aaa.get("userheight");
                        String updatedt = (String) aaa.get("updatedt");
                        String userweight = (String) aaa.get("userweight");
                        userInfoModel.setUpdatedt(updatedt);
                        userInfoModel.setUserfavorite(userfavorite);
                        userInfoModel.setUserheight(userheight);
                        userInfoModel.setUserweight(userweight);
                        userInfoModel.setUsername(USERNAME);
                        //bookList.add(happyNoteModel);
                    }
                    //Toast.makeText(WeekDayBirthdayActivity.this, "设置完成！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PersonalInfoActivity.this, "设置失败,请检查网络！", Toast.LENGTH_SHORT).show();
                }
                ;
                httpURLConnection.disconnect();

            } else {
                Log.w("HTTP", "Connction failed" + httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        Intent intent = new Intent();
        intent.setClass(PersonalInfoActivity.this, HomeActivity.class);
        intent.setAction("user");
        intent.putExtra("username", USERNAME);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.update_my_info, R.id.commit_my_info,R.id.history_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.update_my_info):
                //修改
                myHeight.setEnabled(true);
                myWeight.setEnabled(true);
                myFavorite.setEnabled(true);
                break;
            case R.id.commit_my_info:
                // 节假日管理
                commitInfo();
                break;

            case R.id.history_btn:
                Intent intent = new Intent();
                intent.setClass(PersonalInfoActivity.this, HistoryInfo.class);
                intent.setAction("user");
                intent.putExtra("username",USERNAME);
                startActivity(intent);
                break;
        }
    }

    private void commitInfo() {
        URL url = null;
        String urlStr = MyWriteUtils.MyURL + "/insertInfo";
        String result = "";//要返回的结果

        try {
            url = new URL(urlStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
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
            OutputStream os = httpURLConnection.getOutputStream();
            UserInfoModel userModel = new UserInfoModel();
            userModel.setUsername(USERNAME);
            userModel.setUserweight(myWeight.getText().toString().trim());
            userModel.setUserheight(myHeight.getText().toString().trim());
            userModel.setUserfavorite(myFavorite.getText().toString().trim());

            //dataPost类是自定义的数据交互对象，只有两个成员变量
            JSONObject userJSON = new JSONObject();

            userJSON.put("userinfo", new Gson().toJson(userModel));

            String content = String.valueOf(userJSON);

            os.write(content.getBytes());
            os.flush();
            os.close();

            //将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端，并获取访问状态
            if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()) {
                StringBuffer sb = new StringBuffer();
                String readLine = new String();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine);
                }
                RESULT = sb.toString();

                if (RESULT.equals("success")) {

                    Toast.makeText(PersonalInfoActivity.this, "设置完成！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PersonalInfoActivity.this, "设置失败,请检查网络！", Toast.LENGTH_SHORT).show();
                }
                ;
                httpURLConnection.disconnect();
                String userFoot = "修改个人信息";
                RecordUserFootUtil.recordUserFoot(USERNAME, userFoot);
            } else {
                Log.w("HTTP", "Connction failed" + httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Object> getObjectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        Class<?> clazz = obj.getClass();
        System.out.println(clazz);
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(obj);
            if (value == null) {
                value = "";
            }
            map.put(fieldName, value);
        }
        return map;
    }
}
