package a1.example.com.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import a1.example.com.myapplication.Util.MyMusicUtils;
import a1.example.com.myapplication.Util.MyWriteUtils;
import a1.example.com.myapplication.Util.RecordUserFootUtil;
import a1.example.com.myapplication.Util.RequestUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameShopMusicUpActivity extends AppCompatActivity {
    @BindView(R.id.btnStart)
    Button startBtn;
    @BindView(R.id.btnStop)
    Button stopBtn;
    @BindView(R.id.btnPlay)
    Button playBtn;
    @BindView(R.id.btnUpLoad)
    Button uploadBtn;
    @BindView(R.id.tv1)
    TextView msg;
    @BindView(R.id.back_btn)
    ImageView backBtn;

    // 多媒体播放器
    private MediaPlayer mediaPlayer;
    // 多媒体录制器
    private MediaRecorder mediaRecorder = new MediaRecorder();
    // 音频文件
    private File audioFile;

    // 传给Socket服务器端的上传和下载标志
    private final int UP_LOAD = 1;
    private final int DOWN_LOAD = 2;

    String USERNAME = "";
    String RESULT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_shop_music_up);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        Intent intent = new Intent();
        intent.setClass(GameShopMusicUpActivity.this, GameShopActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.btnStart,R.id.btnStop, R.id.btnPlay,R.id.btnUpLoad,R.id.back_btn})
    public void onClick(View view){
        switch (view.getId()) {
            case (R.id.btnStart):
                startMusic();
                break;
            case (R.id.btnStop):
                stopMusic();
                break;
            case R.id.btnPlay:
                try {
                    playMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnUpLoad:
                uploadMusic();
                break;
            case R.id.back_btn:
                super.onBackPressed();//注释掉这行,back键不退出activity
                Intent intent = new Intent();
                intent.setClass(GameShopMusicUpActivity.this, GameShopActivity.class);
                intent.setAction("user");
                intent.putExtra("username",USERNAME);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void uploadMusic() {
        // post请求上传
        String audioPath = audioFile.getAbsolutePath();
        String uploadUrl = MyWriteUtils.MyURL+"/uploadMusic?username="+USERNAME;
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
            httpURLConnection.setRequestProperty("Content-Type","video/mpeg");

            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());

            // 取得文件的FileInputStream
            if(audioPath!=""){
            FileInputStream fStream = new FileInputStream(audioPath);
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
                    /*imageBtn.setImageBitmap(bitmap2);
                    fileUrlBtn.setText("上传成功！");*/
                    msg.setText("上传成功!!!");
                    String userFoot = "传了新歌";
                    RecordUserFootUtil.recordUserFoot(USERNAME,userFoot);
                }else{
                    /*imageBtn.setImageBitmap(bitmap2);
                    fileUrlBtn.setText("上传失败,请稍后重新选择!");*/
                    msg.setText("上传失败,请稍后重新选择!!!");
                }
                //该干的都干完了,记得把连接断了
                httpURLConnection.disconnect();
            }
            }else{
                msg.setText("没有文件,请别瞎点!!!");
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            setTitle(e.getMessage());
        }
    }

    private void playMusic() throws IOException {
        if (mediaRecorder != null) {
            // mediaRecorder.stop();
            mediaRecorder.reset();
        }

        if (audioFile != null && audioFile.exists()) {
//            Log.i("", ">>>>>>>>>" + audioFile);
            mediaPlayer = new MediaPlayer();
            // 为播放器设置数据文件
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            // 准备并且启动播放器
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    setTitle("此处应该有掌声...");

                }
            });
            msg.setText("正在播放最帅的人唱的最好听的歌...");
        }
    }

    private void stopMusic() {
        if (audioFile != null && audioFile.exists()) {
            // mediaRecorder.stop();
            mediaRecorder.reset();
        }
        msg.setText("已经停止录制...");
    }

    private void startMusic(){
        if (Build.VERSION.SDK_INT>=23){
            int Req = 101;
            String[] per ={
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            for (String str :per){
                if (this.checkSelfPermission(str)!= PackageManager.PERMISSION_GRANTED){
                    this.requestPermissions(per,Req);
                }
            }
        }
        if (!MyMusicUtils.haveSdCard()) {
            Toast.makeText(this, "SD不存在，不正常录音！！", Toast.LENGTH_LONG).show();
        } else {
            // 设置音频来源(一般为麦克风)
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置音频输出格式（默认的输出格式）
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            // 设置音频编码方式（默认的编码方式）
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            // 创建一个临时的音频输出文件.record_是文件的前缀名 .amr是后缀名
            try {
                audioFile = File.createTempFile("record_", ".amr", MyMusicUtils.getAudioRecordDir());
            } catch (IOException e) {
                e.printStackTrace();
            }
            // audioFile =new
            // File(Environment.getExternalStorageDirectory().getCanonicalPath()+"/sound.amr");
            // 设置录制器的文件保留路径
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());

            // 准备并且开始启动录制器
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaRecorder.start();
            msg.setText("正在录制歌曲...");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();

        }
        if (mediaRecorder != null) {
//			mediaRecorder.reset();

            mediaRecorder.release();
            mediaRecorder=null;
        }
    }

}
