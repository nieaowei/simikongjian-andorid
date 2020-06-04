package a1.example.com.myapplication;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameShopActivity extends AppCompatActivity {
    @BindView(R.id.my_music_btn)
    RelativeLayout myMusicBtn;
    @BindView(R.id.upload_music_btn)
    RelativeLayout uploadMusicBtn;
    @BindView(R.id.my_study_btn)
    RelativeLayout myStudyBtn;
    @BindView(R.id.my_text_btn)
    RelativeLayout myTextBtn;
    @BindView(R.id.back_btn)
    ImageView backBtn;

    String USERNAME = "";
    String RESULT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_shop);
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
        intent.setClass(GameShopActivity.this, HomeActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.upload_music_btn,R.id.my_music_btn, R.id.my_study_btn,R.id.my_text_btn,R.id.back_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.my_music_btn):
                Intent intent = new Intent();
                intent.setClass(GameShopActivity.this, GameShopMusicActivity.class);
                intent.setAction("user");
                intent.putExtra("username",USERNAME);
                startActivity(intent);
                finish();
                break;
            case (R.id.upload_music_btn):
                Intent intent3 = new Intent();
                intent3.setClass(GameShopActivity.this, GameShopMusicUpActivity.class);
                intent3.setAction("user");
                intent3.putExtra("username",USERNAME);
                startActivity(intent3);
                finish();
                break;
            case R.id.my_study_btn:
                Intent intent5 = new Intent();
                intent5.setClass(GameShopActivity.this, GameShopStudyActivity.class);
                intent5.setAction("user");
                intent5.putExtra("username",USERNAME);
                startActivity(intent5);
                finish();
                break;
            case R.id.my_text_btn:
                Intent intent4 = new Intent();
                intent4.setClass(GameShopActivity.this, GameShopStoryActivity.class);
                intent4.setAction("user");
                intent4.putExtra("username",USERNAME);
                startActivity(intent4);
                finish();
                break;
            case R.id.back_btn:
                Intent intent2 = new Intent();
                intent2.setClass(GameShopActivity.this, HomeActivity.class);
                intent2.setAction("user");
                intent2.putExtra("username",USERNAME);
                startActivity(intent2);
                finish();
                break;
        }
    }
}
