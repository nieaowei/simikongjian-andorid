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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RecordUserFootUtil;
import a1.example.com.myapplication.Util.RequestUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UploadActivity extends AppCompatActivity {

    private static final int IMAGE_RESULT_CODE = 102;
    @BindView(R.id.close_btn)
    ImageView closeBtn;
    @BindView(R.id.select_pic_btn)
    Button lookBtn;
    @BindView(R.id.upload_pic_btn)
    Button backBtn;
    @BindView(R.id.image)
    ImageView imageBtn;
    @BindView(R.id.file_url)
    EditText fileUrlBtn;

    private String imgString = "";
    String USERNAME = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT > 9) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        Intent intent = new Intent();
        intent.setClass(UploadActivity.this, PictureActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.close_btn, R.id.select_pic_btn,R.id.upload_pic_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.close_btn):
                Intent intent = new Intent();
                intent.setClass(UploadActivity.this, PictureActivity.class);
                intent.setAction("user");
                intent.putExtra("username",USERNAME);
                startActivity(intent);
                finish();
                break;
            case R.id.select_pic_btn:
                openSysAlbum();
                //Toast.makeText(UploadActivity.this, "2！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.upload_pic_btn:
                uploadPic();
                //Toast.makeText(UploadActivity.this, "3！", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    // 文件路径
    String filepath = "";
//    final String strUrl = "http://192.168.137.1:8080/food/uploadImage";

    private void uploadPic() {
        String uploadUrl = MyWriteUtils.MyURL+"/uploadImage?username="+USERNAME;
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
                    imageBtn.setImageBitmap(bitmap2);
                    fileUrlBtn.setText("上传成功！");
                    String userFoot = "上传照片";
                    RecordUserFootUtil.recordUserFoot(USERNAME,userFoot);
                }else{
                    imageBtn.setImageBitmap(bitmap2);
                    fileUrlBtn.setText("上传失败,请稍后重新选择!");
                }
                //该干的都干完了,记得把连接断了
                httpURLConnection.disconnect();
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            setTitle(e.getMessage());
        }
    }

    private void openSysAlbum() {
        if (ContextCompat.checkSelfPermission(UploadActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        } else {
            //打开相册
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_RESULT_CODE); // 打开相册
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
//        if (DocumentsContract.isDocumentUri(this, uri)) {
//            // 如果是document类型的Uri，则通过document id处理
//            String docId = DocumentsContract.getDocumentId(uri);
//            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
//                String id = docId.split(":")[1];
//                // 解析出数字格式的id
//                String selection = MediaStore.Images.Media._ID + "=" + id;
//                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
//            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
//                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
//                imagePath = getImagePath(contentUri, null);
//            }
//        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            // 如果是content类型的Uri，则使用普通方式处理
//            imagePath = getImagePath(uri, null);
//        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            // 如果是file类型的Uri，直接获取图片路径即可
//            imagePath = uri.getPath();
//        }
        // 根据图片路径显示图片
        displayImage(uri);
    }

    /**
     * android 4.4以前的处理方式
     * @param data
     */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
//        String imagePath = getImagePath(uri, null);
        displayImage(uri);
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

    private void displayImage(Uri imagePath) {
        if (imagePath != null) {
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            String imgFile = ImageRarActivity.getimage(imagePath.getPath().split(":")[1]).getPath();
            filepath = imagePath.getPath().split(":")[1];
            Bitmap bitmap2= BitmapFactory.decodeFile(imgFile);
            imageBtn.setImageBitmap(bitmap2);
            fileUrlBtn.setText(imagePath.getPath().split(":")[1]);
        } else {
            Toast.makeText(this, "获取相册图片失败", Toast.LENGTH_SHORT).show();
        }
    }

}
