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

import a1.example.com.myapplication.Adapter.PictureAdapter;
import a1.example.com.myapplication.Adapter.SendShareDialog;
import a1.example.com.myapplication.Model.PictureModel;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RequestUtils;

public class PictureViewActivity extends AppCompatActivity {
    private List<PictureModel> pictureList = new ArrayList<>();
    private PictureAdapter adapterListView;
    private ListView listView;
    String USERNAME = "";
    String RESULT = "";
    String ACTION = "";
    String PICURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals("user")) {
            String username = intent.getStringExtra("username");
            USERNAME = username;
//            ACTION =
        }


        if (action.equals("friends")) {
            ACTION = "friends";
            String username = intent.getStringExtra("username");
            USERNAME = username;

        }
        initData();

    }

    private void initData() {
        myPictureRequest();
        adapterListView = new PictureAdapter(PictureViewActivity.this, R.layout.item_picture_view, pictureList);
        listView = (ListView) findViewById(R.id.list_my_picture);
        listView.setAdapter(adapterListView);
        //设置listview点击事件

        if(ACTION.equals("friends")){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent3 = new Intent();
                    intent3.setClass(PictureViewActivity.this, SendShareDialog.class);
                    intent3.setAction("pic");
                    intent3.putExtra("username", USERNAME);
                    intent3.putExtra("pic",pictureList.get(i).url);
                    startActivity(intent3);
                    finish();

                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        if (ACTION.equals("friends")) {
            Intent intent = new Intent();
            intent.setClass(PictureViewActivity.this, SendShareDialog.class);
            intent.setAction("pic");
            intent.putExtra("username", USERNAME);
            intent.putExtra("pic", PICURL);

            startActivity(intent);
            finish();
        } else {

            Intent intent = new Intent();
            intent.setClass(PictureViewActivity.this, PictureActivity.class);
            intent.setAction("user");
            intent.putExtra("username", USERNAME);
            startActivity(intent);
            finish();
        }
    }

    private void myPictureRequest() {
        URL url = null;
        String urlStr = MyWriteUtils.MyURL + "/selectImage?username=" + USERNAME;
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
            /*JSONObject userJSON = new JSONObject();
            userJSON.put("username",USERNAME);
            String content = String.valueOf(userJSON);

            os.write(content.getBytes());*/
            os.flush();
            os.close();

            //将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端，并获取访问状态
            if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()) {

                InputStream inputStream = httpURLConnection.getInputStream();
                String result1 = RequestUtils.stremToString(inputStream);
                RESULT = result1;
                if (!RESULT.equals("")) {
//                    RESULT.replaceAll("\\[]", "");
//                    String str= RESULT.replaceAll("\"", "");
                    String[] arr = RESULT.split(",");
//                    for(int i=0;i<arr.length;i++){
//                        PictureModel pictureModel = new PictureModel();
//                        String a = "";
//                        if (i==0){
//                            a = arr[i].substring(1,arr[i].length());
//                            pictureModel.setKeys(a);
//                        }else if (i==arr.length-1){
//                            a = arr[i].substring(0,arr[i].length()-1);
//                        }else{
//                            a = arr[i];
//                        }
//                        pictureModel.setKeys(a);
//                        pictureList.add(pictureModel);
//                    }
                    for (String str :
                            arr) {
                        PictureModel pi = new PictureModel();
                        pi.url = str;
                        pictureList.add(pi);
                    }

                } else {
                    Toast.makeText(PictureViewActivity.this, "设置失败,请检查网络！", Toast.LENGTH_SHORT).show();
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
}
