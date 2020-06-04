package a1.example.com.myapplication;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HappyNoteActivity extends AppCompatActivity {
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.write_note_btn)
    RelativeLayout writeNoteBtn;
    @BindView(R.id.my_note_btn)
    RelativeLayout myNoteBtn;

    String USERNAME = "";
    String RESULT = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy_note);
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
        intent.setClass(HappyNoteActivity.this, HomeActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.back_btn, R.id.write_note_btn,R.id.my_note_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.back_btn):
                Intent intent = new Intent();
                intent.setClass(HappyNoteActivity.this, HomeActivity.class);
                intent.setAction("user");
                intent.putExtra("username",USERNAME);
                startActivity(intent);
                finish();
                //Toast.makeText(HomeActivity.this, "1！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.write_note_btn:
                Intent intent2 = new Intent();
                intent2.setClass(HappyNoteActivity.this, HappyNoteWriteActivity.class);
                intent2.setAction("user");
                intent2.putExtra("username",USERNAME);
                startActivity(intent2);
                finish();
                break;
            case R.id.my_note_btn:
                Intent intent3 = new Intent();
                intent3.setClass(HappyNoteActivity.this, HappyNoteMineActivity.class);
                intent3.setAction("user");
                intent3.putExtra("username",USERNAME);
                startActivity(intent3);
                finish();
                break;
        }
    }

}
