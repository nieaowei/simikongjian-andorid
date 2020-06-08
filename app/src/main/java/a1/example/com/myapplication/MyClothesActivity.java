package a1.example.com.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;



import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import a1.example.com.myapplication.Model.WeatherModel;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RequestUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyClothesActivity extends AppCompatActivity {
    /*@BindView(R.id.my_cloth)
    RelativeLayout my_cloth;*/
    @BindView(R.id.weather_fenxi)
    RelativeLayout weather_fenxi;
    @BindView(R.id.back_btn)
    ImageView back_btn;
    private List<WeatherModel> bookList = new ArrayList<>();
    String filepath = "";
    private static final int IMAGE_RESULT_CODE = 102;
    String USERNAME = "";
    String RESULT = "";
    WeatherModel weatherModel = new WeatherModel();
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_clothes);

        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        /*String action = intent.getAction();
        if(action.equals("user")){*/
            String username = intent.getStringExtra("username");
            USERNAME = username;
        //}
    }

    @OnClick({ R.id.weather_fenxi,R.id.back_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            /*case (R.id.my_cloth):
                uploadMyCloth(view);
                break;*/
            case R.id.weather_fenxi:
                saveWeather();
                //if (check) {
                    selectWeather();
                    if (bookList.size()==0){
                        Toast.makeText(MyClothesActivity.this, "请检查网络！", Toast.LENGTH_SHORT).show();
                    }else{
                        weatherModel = bookList.get(0);
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("weatherModel", weatherModel);
                        intent.setClass(MyClothesActivity.this, ClothFenxiActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
               // }
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    private void saveWeather() {
        URL url = null;
        String urlStr = MyWriteUtils.MyURL+"/saveWether";
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
                if (result1.equals("success")){
                    check = true;
                }else{
                    check = false;
                    //Toast.makeText(MyClothesActivity.this, "请检查网络！", Toast.LENGTH_SHORT).show();
                };
                httpURLConnection.disconnect();

            }else{
                Log.w("HTTP","Connction failed"+httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectWeather() {
        URL url = null;
        String urlStr = MyWriteUtils.MyURL+"/selectWether";
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
                Log.d("RESULT",result1);

                if (!RESULT.equals("")){
                    Map<String, Object> map = MyWriteUtils.jsonToMap(RESULT);
                    Iterator iterator = map.keySet().iterator();
                    int length = map.size();
                    for(int i = 0 ; i<length;i++){
//                        Object aa = map.values().iterator().next();
                        Map<String, Object> aaa = (Map<String, Object>)map.values().iterator().next();
                        iterator.next();
                        iterator.remove();
                        WeatherModel happyNoteModel = new WeatherModel();
                        String date = (String) aaa.get("date");
                        String ymd = (String) aaa.get("ymd");
                        String high = (String) aaa.get("high");
                        String sunrise = (String) aaa.get("sunrise");
                        String fx = (String) aaa.get("fx");
                        String week = (String) aaa.get("week");
                        String low = (String) aaa.get("low");
                        String fl = (String) aaa.get("fl");
                        String sunset = (String) aaa.get("sunset");
                        String aqi = (String) aaa.get("aqi");
                        String type = (String) aaa.get("type");
                        String notice = (String) aaa.get("notice");
                        happyNoteModel.setAqi(aqi);
                        happyNoteModel.setDate(date);
                        happyNoteModel.setFl(fl);
                        happyNoteModel.setFx(fx);
                        happyNoteModel.setHigh(high);
                        happyNoteModel.setYmd(ymd);
                        happyNoteModel.setSunrise(sunrise);
                        happyNoteModel.setWeek(week);
                        happyNoteModel.setLow(low);
                        happyNoteModel.setSunset(sunset);
                        happyNoteModel.setType(type);
                        happyNoteModel.setNotice(notice);
                        bookList.add(happyNoteModel);
                    }
                    //Toast.makeText(WeekDayBirthdayActivity.this, "设置完成！", Toast.LENGTH_SHORT).show();
                }else if (RESULT.equals("[]")){
                    Toast.makeText(MyClothesActivity.this, "请检查网络！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MyClothesActivity.this, "设置失败,请检查网络！", Toast.LENGTH_SHORT).show();
                };
                httpURLConnection.disconnect();

            }else{
                Log.w("HTTP","Connction failed"+httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*UploadClothDialog uploadClothDialog;
    private void uploadMyCloth(View view) {
        uploadClothDialog = new UploadClothDialog(this,R.style.AppTheme,new MyClothesActivity.MyOnClickListener());
        uploadClothDialog.show();
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.select_cloth_btn:
                    openSysAlbum();
                    break;
                case R.id.upload_cloth_btn:
                    uploadCloth();
                    break;
            }
        }
    }

    private void uploadCloth() {
        String uploadUrl = MyWriteUtils.MyURL+"/uploadCloth";
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);

            httpURLConnection.setConnectTimeout(50000);
            httpURLConnection.setReadTimeout(50000);

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type","image/jpg");

            OutputStream os= httpURLConnection.getOutputStream();
            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            // 取得文件的FileInputStream
            FileInputStream fStream = new FileInputStream(filepath);

            //JSONObject userJSON = new JSONObject();
            //userJSON.put("username",USERNAME);
            //String content = String.valueOf(userJSON);

            //os.write(content.getBytes());
            // 设置每次写入1024bytes
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int length = -1;
            // 从文件读取数据至缓冲区
            while ((length = fStream.read(buffer)) != -1) {
                // 将资料写入DataOutputStream中
                dos.write(buffer, 0, length);
            }

            dos.flush();
            fStream.close();
            dos.close();
            int i = httpURLConnection.getResponseCode();
            if(httpURLConnection.getResponseCode() == 200){
                //logger.info("文件上传成功！上传文件为：" + oldFilePath);
                InputStream inputStream = httpURLConnection.getInputStream();
                //获取响应
                String result = RequestUtils.stremToString(inputStream);
                Bitmap bitmap2= BitmapFactory.decodeFile("");
                if (result.contains("success")){
                    //uploadHeadDialog.head_image.setImageBitmap(bitmap2);
                    uploadClothDialog.cloth_url.setText("上传成功！");
                    String userFoot = "上传衣服";
                    RecordUserFootUtil.recordUserFoot(USERNAME,userFoot);
                    *//*Intent intent = new Intent();
                    intent.setClass(MyClothesActivity.this, SettingActivity.class);
                    intent.putExtra("username",USERNAME);
                    startActivity(intent);
                    finish();*//*
                }else{
                    uploadClothDialog.cloth_image.setImageBitmap(bitmap2);
                    uploadClothDialog.cloth_url.setText("上传失败,请稍后重新选择!");
                }
                //该干的都干完了,记得把连接断了
                httpURLConnection.disconnect();
               *//* String userFoot = "上传衣服";
                RecordUserFootUtil.recordUserFoot(USERNAME,userFoot);*//*
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            setTitle(e.getMessage());
        }
    }*/

    /*private void openSysAlbum() {
        if (ContextCompat.checkSelfPermission(MyClothesActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MyClothesActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        } else {
            //打开相册
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_RESULT_CODE); // 打开相册
        }
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        Intent intent = new Intent();
        intent.setClass(MyClothesActivity.this, HomeActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case IMAGE_RESULT_CODE:
                if (resultCode == RESULT_OK) { // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }

                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        // 根据图片路径显示图片
        displayImage(imagePath);
    }

    *//**
     * android 4.4以前的处理方式
     * @param data
     *//*
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            String imgFile = ImageRarActivity.getimage(imagePath).getPath();
            filepath = imagePath;
            Bitmap bitmap2= BitmapFactory.decodeFile(imgFile);
            uploadClothDialog.cloth_image.setImageBitmap(bitmap2);
            uploadClothDialog.cloth_url.setText(imagePath);
        } else {
            Toast.makeText(this, "获取相册图片失败", Toast.LENGTH_SHORT).show();
        }
    }*/
}
