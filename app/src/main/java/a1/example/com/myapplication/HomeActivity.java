package a1.example.com.myapplication;

import android.content.Intent;
import android.os.StrictMode;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.pic_btn)
    ConstraintLayout picBtn;
    @BindView(R.id.weekday_btn)
    ConstraintLayout weekdayBtn;
    @BindView(R.id.happy_btn)
    ConstraintLayout happyBtn;
    @BindView(R.id.myself_btn)
    ConstraintLayout myselfBtn;
    @BindView(R.id.shop_btn)
    ConstraintLayout shopBtn;
    @BindView(R.id.manage_btn)
    ConstraintLayout manageBtn;
    @BindView(R.id.foot_btn)
    ConstraintLayout footBtn;
    @BindView(R.id.friend_btn)
    ConstraintLayout friendBtn;
    @BindView(R.id.setting_btn)
    ConstraintLayout settingBtn;
    String USERNAME = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        intent.setClass(HomeActivity.this, MainActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.pic_btn, R.id.weekday_btn,R.id.happy_btn,R.id.myself_btn,R.id.shop_btn,R.id.manage_btn,R.id.foot_btn,R.id.friend_btn,R.id.setting_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.pic_btn):
                // 照片管理
                picture();
                break;
            case R.id.weekday_btn:
                // 节假日管理
                weekday();
                break;
            case R.id.happy_btn:
                // 心情日记
                happy();
                break;
            case R.id.myself_btn:
                // 个人信息
                personalInfo();
                break;
            case R.id.shop_btn:
                //心情商城
                gameShop();
                break;
            case R.id.manage_btn:
                // 服装搭配
                myclothes();
                break;
            case R.id.foot_btn:
                // 记录痕迹
                Myfoot();
                break;
            case R.id.friend_btn:
                // 朋友圈
                Myfriends();
                break;
            case R.id.setting_btn:
                // 我的设置
                UserSetting();
                break;
        }
    }

    private void UserSetting() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, SettingActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void Myfriends() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, MyFriendsActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void myclothes() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, MyClothesActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void Myfoot() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, MyFootActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void gameShop() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, GameShopActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void personalInfo() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, PersonalInfoActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    private void happy() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, HappyNoteActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }
// 节假日
    private void weekday() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, WeekDayActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    /**
     * 照片管理
     */
    private void picture() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, PictureActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }
}
