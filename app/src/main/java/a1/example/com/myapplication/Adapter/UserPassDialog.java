package a1.example.com.myapplication.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import a1.example.com.myapplication.R;


public class UserPassDialog extends Dialog {

    /**
     * 上下文对象 *
     */
    Activity context;

    public Button ok_btn;

    public Button return_btn;

    public EditText user_old_password;

    public EditText user_name_ok;

    public EditText username_pass;

    public EditText username_pass_ok;


    public View.OnClickListener mClickListener;

    public UserPassDialog(Activity context) {
        super(context);
        this.context = context;
    }
    //View.OnClickListener clickListener
    public UserPassDialog(Activity context, int theme,View.OnClickListener clickListener) {
        super(context, theme);
        this.context = context;
        this.mClickListener = clickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 指定布局
        this.setContentView(R.layout.dialog_user);

        user_old_password = (EditText) findViewById(R.id.et_old_password);
        user_name_ok = (EditText) findViewById(R.id.user_name_ok);
        username_pass = (EditText) findViewById(R.id.username_pass);
        username_pass_ok = (EditText) findViewById(R.id.username_pass_ok);

        /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = this.getWindow();

        WindowManager m = context.getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        m.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (height* 0.9); // 高度设置为屏幕的0.6
        p.width = (int) (width * 0.9); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);

        // 根据id在布局中找到控件对象
        ok_btn = (Button) findViewById(R.id.ok_btn);
        return_btn = (Button) findViewById(R.id.return_btn);

        // 为按钮绑定点击事件监听器
        ok_btn.setOnClickListener(mClickListener);
        return_btn.setOnClickListener(mClickListener);

        this.setCancelable(true);
    }

}