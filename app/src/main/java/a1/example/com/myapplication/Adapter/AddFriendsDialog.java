package a1.example.com.myapplication.Adapter;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import a1.example.com.myapplication.R;


public class AddFriendsDialog extends Dialog {
    /**
     * 上下文对象 *
     */
    AppCompatActivity context;

    public EditText inputFriendText;

    public Button addFriendDialogBtn;

    public View.OnClickListener mClickListener;

    public AddFriendsDialog(AppCompatActivity context) {
        super(context);
        this.context = context;
    }
    //View.OnClickListener clickListener
    public AddFriendsDialog(AppCompatActivity context, int theme, View.OnClickListener clickListener) {
        super(context, theme);
        this.context = context;
        this.mClickListener = clickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 指定布局
        this.setContentView(R.layout.dialog_add_friends);

        inputFriendText = (EditText) findViewById(R.id.input_friend_text);
        addFriendDialogBtn = (Button) findViewById(R.id.add_friend_dialog_btn);

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

        // 根据id在布局中找到控件对象
       /* select_head_btn = (Button) findViewById(R.id.select_head_btn);
        upload_head_btn = (Button) findViewById(R.id.upload_head_btn);

        // 为按钮绑定点击事件监听器
        select_head_btn.setOnClickListener(mClickListener);*/
        addFriendDialogBtn.setOnClickListener(mClickListener);

        this.setCancelable(true);
    }
}
