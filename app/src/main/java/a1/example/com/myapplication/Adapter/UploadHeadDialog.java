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
import android.widget.ImageView;

import a1.example.com.myapplication.R;


public class UploadHeadDialog extends Dialog {
    /**
     * 上下文对象 *
     */
    AppCompatActivity context;

    public ImageView head_image;

    public EditText head_url;

    public Button select_head_btn;

    public Button upload_head_btn;


    public View.OnClickListener mClickListener;

    public UploadHeadDialog(AppCompatActivity context) {
        super(context);
        this.context = context;
    }
    //View.OnClickListener clickListener
    public UploadHeadDialog(AppCompatActivity context, int theme, View.OnClickListener clickListener) {
        super(context, theme);
        this.context = context;
        this.mClickListener = clickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 指定布局
        this.setContentView(R.layout.dialog_upload_head);

        head_image = (ImageView) findViewById(R.id.head_image);
        head_url = (EditText) findViewById(R.id.head_url);

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
        p.height = (int) (height* 0.5); // 高度设置为屏幕的0.6
        p.width = (int) (width * 0.9); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);

        // 根据id在布局中找到控件对象
        select_head_btn = (Button) findViewById(R.id.select_head_btn);
        upload_head_btn = (Button) findViewById(R.id.upload_head_btn);

        // 为按钮绑定点击事件监听器
        select_head_btn.setOnClickListener(mClickListener);
        upload_head_btn.setOnClickListener(mClickListener);

        this.setCancelable(true);
    }
}
