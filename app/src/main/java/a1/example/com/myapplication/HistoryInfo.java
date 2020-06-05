package a1.example.com.myapplication;

import android.content.Intent;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import a1.example.com.myapplication.Adapter.HistoryInfoAdapter;
import a1.example.com.myapplication.Adapter.UserDeleteDialog;
import a1.example.com.myapplication.Adapter.UserFriendAdapter;
import a1.example.com.myapplication.Model.UserFriendModel;
import a1.example.com.myapplication.Model.UserInfoModel;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RequestUtils;

public class HistoryInfo extends AppCompatActivity {


    private List<UserInfoModel> userInfoModels = new ArrayList<>();
    String USERNAME = "";
    String RESULT = "";
    private HistoryInfoAdapter adapterListView;
    private ListView listView;
    String FRIENDNAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_info);

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
        initData();
    }

    private void initData() {
        myHistoryRequest();
        adapterListView = new HistoryInfoAdapter(HistoryInfo.this,R.layout.item_history,userInfoModels);
        listView = (ListView)findViewById(R.id.history_list);
        listView.setAdapter(adapterListView);
    }


    private void myHistoryRequest(){
        String urlpath = MyWriteUtils.MyURL+"/selectHistory?username="+USERNAME;
        try{
            URL url = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20 * 1000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream inputStream = conn.getInputStream();
                String result = RequestUtils.stremToString(inputStream);
                RESULT = result;
                JSONObject js = JSON.parseObject(RESULT);
                JSONArray jsonArray = js.getJSONArray("data");
                userInfoModels = jsonArray.toJavaList(UserInfoModel.class);
                Log.d("das","大四");
//                for (UserInfoModel jsonObject:
//                        userInfoModels) {
//                    UserInfoModel userInfoModel=new UserInfoModel();
//                    userInfoModel.setUserweight();
//                    userInfoModel.setUserheight("userheight");
//                    userInfoModel.setUserfavorite("userfavorite");
//                    userInfoModel.setUpdatedt("updatedt");
//
//                    userInfoModels.add(userInfoModel);
//                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        Intent intent = new Intent();
        intent.setClass(HistoryInfo.this, PersonalInfoActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

}