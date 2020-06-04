package a1.example.com.myapplication;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;



import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import a1.example.com.myapplication.Adapter.UserDeleteDialog;
import a1.example.com.myapplication.Adapter.UserFriendAdapter;
import a1.example.com.myapplication.Model.UserFriendModel;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RequestUtils;

public class MyFriendsListActivity extends AppCompatActivity {

    private List<UserFriendModel> bookList = new ArrayList<>();
    String USERNAME = "";
    String RESULT = "";
    String TEXTNAME = "";
    private UserFriendAdapter adapterListView;
    private ListView listView;
    String FRIENDNAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends_list);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals("user")){
            String username = intent.getStringExtra("username");
            String textname = intent.getStringExtra("textname");
            USERNAME = username;
            TEXTNAME = textname;
        }
        initData();
    }

    private void initData() {
        myFriendsListRequest();
        adapterListView = new UserFriendAdapter(MyFriendsListActivity.this,R.layout.item_my_friends,bookList);
        listView = (ListView)findViewById(R.id.list_my_friends);
        listView.setAdapter(adapterListView);
        //设置listview点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserFriendModel book = bookList.get(i);
                FRIENDNAME = book.friendname;
                deleteUser();
            }
        });
    }

    UserDeleteDialog createUserDialog;
    private void deleteUser() {
        createUserDialog = new UserDeleteDialog(this,R.style.AppTheme,new MyOnClickListener());
        createUserDialog.show();
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.delete_user_friend_btn:
                    deleteUserFriends();
                    break;
                case R.id.canel_delete_btn:
                    createUserDialog.dismiss();
                    break;
            }
        }
    }

    private void deleteUserFriends() {
        URL url = null;
        String urlStr = MyWriteUtils.MyURL+"/delUserFriends";
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
            //UserInfoModel userModel = new UserInfoModel() ;
            //dataPost类是自定义的数据交互对象，只有两个成员变量
            JSONObject userJSON = new JSONObject();
            userJSON.put("username",USERNAME);
            userJSON.put("friendname",FRIENDNAME);
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

                    Toast.makeText(MyFriendsListActivity.this, "删除完成！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(MyFriendsListActivity.this, MyFriendsListActivity.class);
                    intent.setAction("user");
                    intent.putExtra("username",USERNAME);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(MyFriendsListActivity.this, "设置失败,请检查网络！", Toast.LENGTH_SHORT).show();
                };
                httpURLConnection.disconnect();

            }else{
                Log.w("HTTP","Connction failed"+httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void myFriendsListRequest() {
        URL url = null;
        String urlStr = MyWriteUtils.MyURL+"/selectUserFriends";
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
            //UserInfoModel userModel = new UserInfoModel() ;
            //dataPost类是自定义的数据交互对象，只有两个成员变量
            JSONObject userJSON = new JSONObject();
            userJSON.put("username",USERNAME);
            String content = String.valueOf(userJSON);

            os.write(content.getBytes());
            os.flush();
            os.close();

            //将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端，并获取访问状态
            if(HttpURLConnection.HTTP_OK==httpURLConnection.getResponseCode()){

                InputStream inputStream = httpURLConnection.getInputStream();
                String result1 = RequestUtils.stremToString(inputStream);
                RESULT = result1;
                if (!RESULT.equals("")){
                    Map<String, Object> map = MyWriteUtils.jsonToMap(RESULT);
                    Iterator iterator = map.keySet().iterator();
                    int length = map.size();
                    for(int i = 0 ; i<length;i++){
//                        Object aa = map.values().iterator().next();
                        Map<String, Object> aaa = (Map<String, Object>)map.values().iterator().next();
                        iterator.next();
                        iterator.remove();
                        UserFriendModel happyNoteModel = new UserFriendModel();
                        String friendname = (String) aaa.get("friendname");
                        happyNoteModel.setFriendname(friendname);
                        happyNoteModel.setUsername(USERNAME);
                        bookList.add(happyNoteModel);
                    }
                    //Toast.makeText(WeekDayBirthdayActivity.this, "设置完成！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MyFriendsListActivity.this, "设置失败,请检查网络！", Toast.LENGTH_SHORT).show();
                };
                httpURLConnection.disconnect();

            }else{
                Log.w("HTTP","Connction failed"+httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        Intent intent = new Intent();
        intent.setClass(MyFriendsListActivity.this, MyFriendsActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }
}
