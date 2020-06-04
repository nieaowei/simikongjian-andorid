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


public class UserDeleteDialog extends Dialog {

    /**
     * 上下文对象 *
     */
    Activity context;

    public Button delete_user_friend_btn;

    public Button canel_delete_btn;

    public View.OnClickListener mClickListener;

    public UserDeleteDialog(Activity context) {
        super(context);
        this.context = context;
    }
    //View.OnClickListener clickListener
    public UserDeleteDialog(Activity context, int theme,View.OnClickListener clickListener) {
        super(context, theme);
        this.context = context;
        this.mClickListener = clickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 指定布局
        this.setContentView(R.layout.dialog_user_delete);

        delete_user_friend_btn = (Button) findViewById(R.id.delete_user_friend_btn);
        canel_delete_btn = (Button) findViewById(R.id.canel_delete_btn);

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
        p.height = (int) (height* 0.3); // 高度设置为屏幕的0.6
        p.width = (int) (width * 0.9); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);

        // 为按钮绑定点击事件监听器
        delete_user_friend_btn.setOnClickListener(mClickListener);
        canel_delete_btn.setOnClickListener(mClickListener);

        this.setCancelable(true);
    }
}
