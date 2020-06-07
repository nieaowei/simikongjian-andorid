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

import a1.example.com.myapplication.Adapter.HappyNoteAdapter;
import a1.example.com.myapplication.Model.HappyNoteModel;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RecordUserFootUtil;
import a1.example.com.myapplication.Util.RequestUtils;
import butterknife.ButterKnife;

public class HappyNoteMineActivity extends AppCompatActivity {

    private List<HappyNoteModel> bookList = new ArrayList<>();
    private HappyNoteAdapter adapterListView;
    private ListView listView;
    String USERNAME = "";
    String RESULT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy_note_mine);
        //ButterKnife.bind(this);
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
//        for (int i = 0;i<40;i++){
//            HappyNoteModel book = new HappyNoteModel();
//            bookList.add(book);
//        }
        myHappyNoteRequest();
        adapterListView = new HappyNoteAdapter(HappyNoteMineActivity.this,R.layout.item_happy_note,bookList);
        listView = (ListView)findViewById(R.id.list_happy_note);
        listView.setAdapter(adapterListView);
        //设置listview点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HappyNoteModel book = bookList.get(i);
                Toast.makeText(HappyNoteMineActivity.this,book.getNote().toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        Intent intent = new Intent();
        intent.setClass(HappyNoteMineActivity.this, HappyNoteActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void myHappyNoteRequest() {
        URL url = null;
        String urlStr = MyWriteUtils.MyURL+"/selectnote";
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
                        HappyNoteModel happyNoteModel = new HappyNoteModel();
                        String noteuser = (String) aaa.get("noteuser");
                        String note = (String) aaa.get("note");
                        String writetime = (String) aaa.get("writetime");
                        String notetitle = (String) aaa.get("notetitle");
                        String address = (String)aaa.get("address");
                        String weather = (String)aaa.get("weather");
                        happyNoteModel.setNote(note);
                        happyNoteModel.setNotetitle(notetitle);
                        happyNoteModel.setNoteuser(noteuser);
                        happyNoteModel.setWritetime(writetime);
                        happyNoteModel.setAddress(address);
                        happyNoteModel.setWeather(weather);
                        bookList.add(happyNoteModel);
                    }
                    String userFoot = "查看了自己的日记";
                    RecordUserFootUtil.recordUserFoot(USERNAME,userFoot);
                    //Toast.makeText(WeekDayBirthdayActivity.this, "设置完成！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(HappyNoteMineActivity.this, "设置失败,请检查网络！", Toast.LENGTH_SHORT).show();
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
