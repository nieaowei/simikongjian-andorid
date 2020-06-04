package a1.example.com.myapplication;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import a1.example.com.myapplication.Util.MyWriteUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 注册页面
 */
public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.return_btn)
    Button returnBtn;
    @BindView(R.id.register_btn)
    Button registerBtn;
    @BindView(R.id.user_name)
    EditText userName;
    @BindView(R.id.user_pass)
    EditText userPass;

    String RESULT = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        registerBtn.setOnClickListener(new View.OnClickListener()      //3、键入此行代码自动嵌入内部类方法
        {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener()      //3、键入此行代码自动嵌入内部类方法
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void register() {
        if(userName.getText().toString().trim().equals("")){
            Toast.makeText(RegisterActivity.this, "账号不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }else if(userPass.getText().toString().trim().equals("")){
            Toast.makeText(RegisterActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = MyWriteUtils.MyURL+"/register?username="+userName.getText().toString().trim()+"&password="+userPass.getText().toString().trim();
        RESULT = MyHttpRequest.getServiceInfo(url);
        if (RESULT.equals("1")){
            Toast.makeText(RegisterActivity.this, "注册成功，请返回主界面登录账号！", Toast.LENGTH_SHORT).show();
        }else if(RESULT.equals("3")){
            Toast.makeText(RegisterActivity.this, "注册失败，用户名已存在，请更换！", Toast.LENGTH_SHORT).show();
        }else if(RESULT.equals("")){
            Toast.makeText(RegisterActivity.this, "服务器异常！", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(RegisterActivity.this, "注册失败，请重新注册！", Toast.LENGTH_SHORT).show();
        }
    }


}
