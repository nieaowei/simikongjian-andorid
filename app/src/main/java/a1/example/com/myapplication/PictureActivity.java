package a1.example.com.myapplication;

import android.content.Intent;
import android.os.StrictMode;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PictureActivity extends AppCompatActivity {

    @BindView(R.id.upload_btn)
    RelativeLayout uploadBtn;
    @BindView(R.id.look_btn)
    RelativeLayout lookBtn;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    String USERNAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
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

    @OnClick({R.id.upload_btn, R.id.look_btn,R.id.back_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.upload_btn):
                upload();
                break;
            case R.id.look_btn:
                pictureView();
                break;
            case R.id.back_btn:
                Intent intent = new Intent();
                intent.setClass(PictureActivity.this, HomeActivity.class);
                intent.setAction("user");
                intent.putExtra("username",USERNAME);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void pictureView() {
        Intent intent3 = new Intent();
        intent3.setClass(PictureActivity.this, PictureViewActivity.class);
        intent3.setAction("user");
        intent3.putExtra("username",USERNAME);
        startActivity(intent3);
        finish();
    }

    private void upload() {
        Intent intent = new Intent();
        intent.setClass(PictureActivity.this, UploadActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();//注释掉这行,back键不退出activity
        Intent intent = new Intent();
        intent.setClass(PictureActivity.this, HomeActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }
}
