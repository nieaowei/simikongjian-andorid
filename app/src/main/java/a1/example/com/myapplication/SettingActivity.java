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
import android.os.Message;
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


import com.bumptech.glide.Glide;

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

import a1.example.com.myapplication.Adapter.UploadHeadDialog;
import a1.example.com.myapplication.Adapter.UserPassDialog;
import a1.example.com.myapplication.Model.PictureModel;
import a1.example.com.myapplication.Util.MyPictureUtils;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RecordUserFootUtil;
import a1.example.com.myapplication.Util.RequestUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.change_head_btn)
    RelativeLayout changeHeadBtn;
    @BindView(R.id.update_pass_btn)
    RelativeLayout updatePassBtn;
    @BindView(R.id.change_user_btn)
    RelativeLayout changeUserBtn;
    @BindView(R.id.out_soft)
    RelativeLayout outSoft;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.my_head)
    ImageView imageView;

    public EditText user_name_ok;

    public EditText username_pass;

    public EditText username_pass_ok;
    private static final int IMAGE_RESULT_CODE = 102;
    private List<PictureModel> bookList = new ArrayList<>();
    String USERNAME = "";
    String RESULT = "";
    String RESULT2 = "";
    String filepath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        USERNAME = username;
        initData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this, HomeActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void initData() {
        //refresh();
        selectHead();
//        Bitmap bitmap = MyPictureUtils.stringtoBitmap(bookList.get(0).getKeys());
//        imageView.setImageBitmap(bitmap);
        Glide.with(this).load(bookList.get(0).url).into(imageView);
    }

    @OnClick({R.id.change_head_btn, R.id.update_pass_btn,R.id.change_user_btn,R.id.out_soft,R.id.back_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.change_head_btn):
                changeHead(view);
                break;
            case R.id.update_pass_btn:
                updatePass(view);
                break;
            case R.id.change_user_btn:
                super.onBackPressed();//注释掉这行,back键不退出activity
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.out_soft:
                finish();
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    UploadHeadDialog uploadHeadDialog;
    private void changeHead(View view) {
        uploadHeadDialog = new UploadHeadDialog(this,R.style.AppTheme,new MyOnClickListener());
        uploadHeadDialog.show();

    }

    UserPassDialog createUserDialog;
    public void updatePass(View view) {
        createUserDialog = new UserPassDialog(this,R.style.AppTheme,new MyOnClickListener());
        createUserDialog.show();
        createUserDialog.user_name_ok.setText(USERNAME);
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.ok_btn:
                    String user_name_ok = createUserDialog.user_name_ok.getText().toString().trim();
                    String username_pass = createUserDialog.username_pass.getText().toString().trim();
                    String username_pass_ok = createUserDialog.username_pass_ok.getText().toString().trim();

                    if (!username_pass.equals(username_pass_ok)){
                        Toast.makeText(SettingActivity.this, "两次输入密码不一致！", Toast.LENGTH_SHORT).show();
                    }else {
                        updatePass(user_name_ok,username_pass_ok);
                    }

                    break;
                case R.id.return_btn:
                    createUserDialog.dismiss();
                    break;
                case R.id.select_head_btn:
                    openSysAlbum();
                    break;
                case R.id.upload_head_btn:
                    uploadPic();
                    break;
            }
        }
    }

    private void uploadPic() {
        String uploadUrl = MyWriteUtils.MyURL+"/uploadHead";
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

            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            // 取得文件的FileInputStream
            FileInputStream fStream = new FileInputStream(filepath);
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
                    imageView.setImageBitmap(bitmap2);
                    //uploadHeadDialog.head_image.setImageBitmap(bitmap2);
                    uploadHeadDialog.head_url.setText("上传成功！");
                    String userFoot = "更换头像";
                    RecordUserFootUtil.recordUserFoot(USERNAME,userFoot);
                    Intent intent = new Intent();
                    intent.setClass(SettingActivity.this, SettingActivity.class);
                    intent.putExtra("username",USERNAME);
                    startActivity(intent);
                    finish();
                }else{
                    uploadHeadDialog.head_image.setImageBitmap(bitmap2);
                    uploadHeadDialog.head_url.setText("上传失败,请稍后重新选择!");
                }
                //该干的都干完了,记得把连接断了
                httpURLConnection.disconnect();
                /*String userFoot = "更换头像";
                RecordUserFootUtil.recordUserFoot(USERNAME,userFoot);*/
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            setTitle(e.getMessage());
        }
    }
    public void refresh() {

        onCreate(null);

    }

    private void openSysAlbum() {
        if (ContextCompat.checkSelfPermission(SettingActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        } else {
            //打开相册
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_RESULT_CODE); // 打开相册
        }
    }

    public void updatePass(String user_name_ok, String username_pass_ok) {
        String urlpath = MyWriteUtils.MyURL+"/updatePass?username="+user_name_ok+"&password="+ username_pass_ok;
        try{
            URL url = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10 * 1000);
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream inputStream = conn.getInputStream();
                String result = RequestUtils.stremToString(inputStream);
                RESULT2 = result;
                System.out.println("=====================服务器返回的信息：：" + result);
                boolean isLoginsuccess=false;
                if (result.contains("success")) {
                    isLoginsuccess=true;
                    conn.disconnect();
                }
                Message msg = Message.obtain();
                msg.obj=isLoginsuccess;
                //handler.sendMessage(msg);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        //RESULT = MyHttpRequest.getServiceInfo(url);
        if (RESULT2.equals("1")){
            String userFoot = "修改了密码";
            RecordUserFootUtil.recordUserFoot(USERNAME,userFoot);
            Toast.makeText(SettingActivity.this, "修改密码成功！", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(SettingActivity.this, "账号密码错误，请重新登陆！", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectHead() {
        URL url = null;
        String urlStr = MyWriteUtils.MyURL+"/selectHead";
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
                    PictureModel pictureModel = new PictureModel();
                    pictureModel.url=RESULT;
                    bookList.add(pictureModel);
//                    RESULT.replaceAll("\\[]", "");
//                    String str= RESULT.replaceAll("\"", "");
//                    String[] arr = str.split(",");
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
//                        bookList.add(pictureModel);
//                    }
                    //Toast.makeText(WeekDayBirthdayActivity.this, "设置完成！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SettingActivity.this, "请求失败,请检查网络！", Toast.LENGTH_SHORT).show();
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

    /**
     * android 4.4以前的处理方式
     * @param data
     */
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
            uploadHeadDialog.head_image.setImageBitmap(bitmap2);
            uploadHeadDialog.head_url.setText(imagePath);
        } else {
            Toast.makeText(this, "获取相册图片失败", Toast.LENGTH_SHORT).show();
        }
    }
}
