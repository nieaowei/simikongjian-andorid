package a1.example.com.myapplication.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import a1.example.com.myapplication.MyFriendsActivity;
import a1.example.com.myapplication.PictureViewActivity;
import a1.example.com.myapplication.R;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RecordUserFootUtil;
import a1.example.com.myapplication.Util.RequestUtils;


public class SendShareDialog extends AppCompatActivity {

    /**
     * 上下文对象 *
     */
    Activity context;

    public EditText my_friends_shares_message;

    public Button send_myfriends;

    public Button canel_send_btn;

    public Button send_select_pic_btn;

    public View.OnClickListener mClickListener;

    public ImageView imageView;

    String PICURL="";

    String USERNAME = "";
    String RESULT = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 指定布局
        this.setContentView(R.layout.dialog_user_share);

        Intent intent = getIntent();
        String action = intent.getAction();


        send_myfriends = (Button) findViewById(R.id.send_myfriends);
        canel_send_btn = (Button) findViewById(R.id.canel_send_btn);
        my_friends_shares_message = (EditText) findViewById(R.id.my_friends_shares_message);
        send_select_pic_btn = (Button) findViewById(R.id.send_select_pic_btn);
        imageView = (ImageView) findViewById(R.id.imageView);

        if (action.equals("user")) {
            String username = intent.getStringExtra("username");
            USERNAME = username;
//            ACTION =
        }

        if (action.equals("pic")){
            String url = intent.getStringExtra("pic");

            SharedPreferences sp = getSharedPreferences("sendshare", MODE_PRIVATE);

            if ("".equals(url)){
                PICURL=sp.getString("pic","");
            }else{
                PICURL=url;
            }
            String username = intent.getStringExtra("username");
            USERNAME = username;
            String s = sp.getString("msg","");
            my_friends_shares_message.setText(s);
            Glide.with(this).load(PICURL).into(imageView);


        }

        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.send_myfriends:
                        String my_friends_shares_message1 = my_friends_shares_message.getText().toString().trim();
                        if (my_friends_shares_message1.equals("")) {
                            Toast.makeText(SendShareDialog.this, "请输入您要发送的内容！", Toast.LENGTH_SHORT).show();
                        } else {
                            requestSendMyshares(my_friends_shares_message1,PICURL);
                        }
                        break;
                    case R.id.canel_send_btn:
                        Intent intent4 = new Intent();
                        intent4.setClass(SendShareDialog.this, MyFriendsActivity.class);
                        intent4.setAction("user");
                        intent4.putExtra("username", USERNAME);
//                    intent3.putExtra("action","friends");
                        startActivity(intent4);
                        finish();
                        break;
                    case R.id.send_select_pic_btn: {
                        saveData();
                        Intent intent3 = new Intent();
                        intent3.setClass(SendShareDialog.this, PictureViewActivity.class);
                        intent3.setAction("friends");
                        intent3.putExtra("username", USERNAME);
//                    intent3.putExtra("action","friends");
                        startActivity(intent3);
                        finish();
                        break;
                    }
                }
            }
        };
        // 为按钮绑定点击事件监听器
        send_myfriends.setOnClickListener(mClickListener);
        canel_send_btn.setOnClickListener(mClickListener);
        send_select_pic_btn.setOnClickListener(mClickListener);
    }

    private void requestSendMyshares(String my_friends_shares_message,String pic) {
        {
            URL url = null;
            String urlStr = MyWriteUtils.MyURL + "/addShares";
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
                //UserInfoModel userModel = new UserInfoModel() ;
                //dataPost类是自定义的数据交互对象，只有两个成员变量
                JSONObject userJSON = new JSONObject();
                userJSON.put("username", USERNAME);
                userJSON.put("shares", my_friends_shares_message);
                userJSON.put("sharesurl", pic);
                String content = String.valueOf(userJSON);

                os.write(content.getBytes());
                os.flush();
                os.close();

                //将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端，并获取访问状态
                if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()) {

                    InputStream inputStream = httpURLConnection.getInputStream();
                    String result1 = RequestUtils.stremToString(inputStream);
                    RESULT = result1;
                    if (RESULT.equals("success")) {
                        Toast.makeText(SendShareDialog.this, "发送成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SendShareDialog.this, "服务器异常！", Toast.LENGTH_SHORT).show();
                    }
                    httpURLConnection.disconnect();
                    String userFoot = "发了朋友圈";
                    RecordUserFootUtil.recordUserFoot(USERNAME, userFoot);
                } else {
                    Log.w("HTTP", "Connction failed" + httpURLConnection.getResponseCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    private void saveData(){
        SharedPreferences sp = getSharedPreferences("sendshare", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("msg",my_friends_shares_message.getText().toString().trim());
        edit.putString("pic",PICURL);

        edit.commit();
    }
}
