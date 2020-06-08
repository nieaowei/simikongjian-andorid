package a1.example.com.myapplication.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import a1.example.com.myapplication.R;

public class NoteItemDialog extends Dialog {
    Activity context;

    public Button ok_btn;

    public Button note_return;

    public TextView note_address;

    public TextView note_title;

    public TextView note_weather;

    public TextView note_content;

    public TextView note_date;


    public View.OnClickListener mClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(
                R.layout.dialog_note);

        note_title = (TextView) findViewById(R.id.note_title);
        note_address = (TextView) findViewById(R.id.note_address);
        note_content = (TextView) findViewById(R.id.note_content);
        note_weather = (TextView) findViewById(R.id.note_weather);
        note_date = (TextView) findViewById(R.id.note_date);
        Window dialogWindow = this.getWindow();

        WindowManager m = context.getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        m.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (height* 0.9); // 高度设置为屏幕的0.6
        p.width = (int) (width * 1); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);

        note_return = (Button) findViewById(R.id.note_return);

        note_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    NoteItemDialog.this.dismiss();

            }
        });

        this.setCancelable(true);
    }


    public NoteItemDialog(@NonNull Activity context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }
}
