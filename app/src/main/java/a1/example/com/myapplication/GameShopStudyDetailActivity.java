package a1.example.com.myapplication;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;



import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import a1.example.com.myapplication.Model.StoryDetailModel;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RecordUserFootUtil;
import a1.example.com.myapplication.Util.RequestUtils;

public class GameShopStudyDetailActivity extends AppCompatActivity {

    private List<StoryDetailModel> storyDetailModels = new ArrayList<>();
    String USERNAME = "";
    String RESULT = "";
    String TEXTNAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_shop_study_detail);

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
        requestStoryDetail();
        if (storyDetailModels.size()>0){
            byte[] bitmapArray;
            String aaa = "";
            bitmapArray = Base64.decode(storyDetailModels.get(0).getKeys(), Base64.DEFAULT);
            try {
                aaa = new String(bitmapArray, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            TextView text = (TextView) this.findViewById(R.id.study_detial);
            text.setText(aaa);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        Intent intent = new Intent();
        intent.setClass(GameShopStudyDetailActivity.this, GameShopStudyActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void requestStoryDetail() {
        URL url = null;
        String urlStr = MyWriteUtils.MyURL+"/selectStudyDetail";
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
            userJSON.put("textname",TEXTNAME);
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
                    RESULT.replaceAll("\\[]", "");
                    String str= RESULT.replaceAll("\"", "");
                    String[] arr = str.split(",");
                    for(int i=0;i<arr.length;i++){
                        StoryDetailModel storyDetailModel = new StoryDetailModel();
                        String a = "";
                        if (i==0){
                            a = arr[i].substring(1,arr[i].length());
                            storyDetailModel.setKeys(a);
                        }else if (i==arr.length-1){
                            a = arr[i].substring(0,arr[i].length()-1);
                        }else{
                            a = arr[i];
                        }
                        storyDetailModel.setKeys(a);
                        storyDetailModels.add(storyDetailModel);
                    }
                    String userFoot = "学习了"+TEXTNAME;
                    RecordUserFootUtil.recordUserFoot(USERNAME,userFoot);
                }else{
                    Toast.makeText(GameShopStudyDetailActivity.this, "设置失败,请检查网络！", Toast.LENGTH_SHORT).show();
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
