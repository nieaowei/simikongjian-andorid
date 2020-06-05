package a1.example.com.myapplication;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import a1.example.com.myapplication.Model.UserModel;
import a1.example.com.myapplication.Util.MyWriteUtils;

public class MyNewIntenService extends IntentService {
    public MyNewIntenService(){
        super("service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

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

                    Notification.Builder builder = new Notification.Builder(this);
                    builder.setContentTitle("notice");
                    builder.setContentText(mytext);
                    builder.setSmallIcon(R.drawable.login_bg_icon);
                    Intent notifyIntent = new Intent(this, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this,2,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);
                    Notification notificationCompat = builder.build();
                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
                    managerCompat.notify(3,notificationCompat);
                    Toast.makeText(this,mytext,Toast.LENGTH_SHORT).show();
                }
                httpURLConnection.disconnect();

            }else{
                Log.w("HTTP","Connction failed"+httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
