package a1.example.com.myapplication;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import a1.example.com.myapplication.Adapter.PictureClothAdapter;
import a1.example.com.myapplication.Model.PictureModel;
import a1.example.com.myapplication.Model.WeatherModel;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RequestUtils;

public class ClothFenxiActivity extends AppCompatActivity {

    public TextView week;
    public TextView fx;
    public TextView type;
    public TextView low;
    public TextView high;
    public TextView notice;
    public TextView jintian;
    String USERNAME = "";
    String RESULT = "";
    String PARAM = "";
    WeatherModel weatherModel = new WeatherModel();
    private List<PictureModel> pictureList = new ArrayList<>();
    private PictureClothAdapter adapterListView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_fenxi);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        weatherModel = (WeatherModel) getIntent().getSerializableExtra("weatherModel");

        week = (TextView) findViewById(R.id.week);
        fx = (TextView) findViewById(R.id.fx);
        type = (TextView) findViewById(R.id.type);
        low = (TextView) findViewById(R.id.low);
        high = (TextView) findViewById(R.id.high);
        notice = (TextView) findViewById(R.id.notice);
        jintian = (TextView)findViewById(R.id.jintian);

        week.setText(weatherModel.getWeek());
        fx.setText(weatherModel.getFx());
        type.setText(weatherModel.getType());
        low.setText(weatherModel.getLow());
        high.setText(weatherModel.getHigh());
        notice.setText(weatherModel.getNotice());
        if (weatherModel.getType().contains("晴")){
            PARAM = "111";
        }else{
            PARAM = "222";
        }
        initPictureView();
        adapterListView = new PictureClothAdapter(ClothFenxiActivity.this,R.layout.item_picture_cloth_view,pictureList);
        listView = (ListView)findViewById(R.id.list_my_picture_hh);
        listView.setAdapter(adapterListView);
    }

    private void initPictureView() {
        URL url = null;
        String urlStr = MyWriteUtils.MyURL+"/selectCloths";
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
            userJSON.put("param",PARAM);
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
                        PictureModel pictureModel = new PictureModel();
                        String a = "";
                        if (i==0){
                            a = arr[i].substring(1,arr[i].length());
                            pictureModel.setKeys(a);
                        }else if (i==arr.length-1){
                            a = arr[i].substring(0,arr[i].length()-1);
                        }else{
                            a = arr[i];
                        }
                        pictureModel.setKeys(a);
                        pictureList.add(pictureModel);
                    }

                }else{
                    Toast.makeText(ClothFenxiActivity.this, "设置失败,请检查网络！", Toast.LENGTH_SHORT).show();
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
