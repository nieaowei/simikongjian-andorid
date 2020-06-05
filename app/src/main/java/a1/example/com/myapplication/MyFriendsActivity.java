package a1.example.com.myapplication;

import android.content.Intent;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;



import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import a1.example.com.myapplication.Adapter.AddFriendsDialog;
import a1.example.com.myapplication.Adapter.SendShareDialog;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RecordUserFootUtil;
import a1.example.com.myapplication.Util.RequestUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyFriendsActivity extends AppCompatActivity {

    @BindView(R.id.friend_share)
    RelativeLayout friendShareBtn;
    @BindView(R.id.my_share)
    RelativeLayout myShareBtn;
    @BindView(R.id.add_friend)
    RelativeLayout addFriendBtn;
    @BindView(R.id.friend_list)
    RelativeLayout friendList;
    @BindView(R.id.back_btn)
    ImageView backBtn;

    String USERNAME = "";
    String RESULT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);

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

    @OnClick({R.id.friend_share,R.id.my_share, R.id.add_friend,R.id.friend_list,R.id.back_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.friend_share):
                Intent intent = new Intent();
                intent.setClass(MyFriendsActivity.this, MyFriendsSharesActivity.class);
                intent.setAction("user");
                intent.putExtra("username",USERNAME);
                startActivity(intent);
                finish();
                break;
            case (R.id.my_share):
                sendMyFriends();
                break;
            case R.id.add_friend:
                addFriend();
                break;
            case R.id.friend_list:
                Intent intent4 = new Intent();
                intent4.setClass(MyFriendsActivity.this, MyFriendsListActivity.class);
                intent4.setAction("user");
                intent4.putExtra("username",USERNAME);
                startActivity(intent4);
                finish();
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    private void sendMyFriends() {
        Intent intent3 = new Intent();
        intent3.setClass(MyFriendsActivity.this, SendShareDialog.class);
        intent3.setAction("user");
        intent3.putExtra("username",USERNAME);
//                    intent3.putExtra("action","friends");
        startActivity(intent3);
        finish();
    }

    AddFriendsDialog createUserDialog;
    private void addFriend() {
        createUserDialog = new AddFriendsDialog(this,R.style.AppTheme,new MyOnClickListener());
        createUserDialog.show();
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.add_friend_dialog_btn:
                    String friendsName = createUserDialog.inputFriendText.getText().toString().trim();
                    if (friendsName.equals("")){
                        Toast.makeText(MyFriendsActivity.this, "请输入您要添加的账号！", Toast.LENGTH_SHORT).show();
                    }else{
                        requestAddFriends(friendsName);
                    }
                    break;

            }
        }
    }



    private void requestAddFriends(String friendsName) {
        {
            URL url = null;
            String urlStr = MyWriteUtils.MyURL+"/addUserFriends";
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
                userJSON.put("friendname",friendsName);
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
                        Toast.makeText(MyFriendsActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
                    }else if (RESULT.equals("nobody")){
                        Toast.makeText(MyFriendsActivity.this, "查无此人，请检查账号！", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MyFriendsActivity.this, "服务器异常！", Toast.LENGTH_SHORT).show();
                    }
                    httpURLConnection.disconnect();
                    String userFoot = "添加了好友"+friendsName;
                    RecordUserFootUtil.recordUserFoot(USERNAME,userFoot);
                }else{
                    Log.w("HTTP","Connction failed"+httpURLConnection.getResponseCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        Intent intent = new Intent();
        intent.setClass(MyFriendsActivity.this, HomeActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }
}
