package a1.example.com.myapplication.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import a1.example.com.myapplication.R;


public class SendShareDialog extends Dialog {

    /**
     * 上下文对象 *
     */
    Activity context;

    public EditText my_friends_shares_message;

    public Button send_myfriends;

    public Button canel_send_btn;

    public View.OnClickListener mClickListener;

    public SendShareDialog(Activity context) {
        super(context);
        this.context = context;
    }

    public SendShareDialog(Activity context, int theme,View.OnClickListener clickListener) {
        super(context, theme);
        this.context = context;
        this.mClickListener = clickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 指定布局
        this.setContentView(R.layout.dialog_user_share);

        send_myfriends = (Button) findViewById(R.id.send_myfriends);
        canel_send_btn = (Button) findViewById(R.id.canel_send_btn);
        my_friends_shares_message = (EditText) findViewById(R.id.my_friends_shares_message);

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
        p.height = (int) (height* 0.4); // 高度设置为屏幕的0.6
        p.width = (int) (width * 0.9); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);

        // 为按钮绑定点击事件监听器
        send_myfriends.setOnClickListener(mClickListener);
        canel_send_btn.setOnClickListener(mClickListener);

        this.setCancelable(true);
    }
}
