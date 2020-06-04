package a1.example.com.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import a1.example.com.myapplication.Model.WeekDayModel;
import a1.example.com.myapplication.Util.MyWriteUtils;
import butterknife.ButterKnife;

public class WeekDayHolidayActivity extends AppCompatActivity {
    String USERNAME = "";
    private MyBaseAdapt mba;
    private ListView lv;
    String RESULT = "";
    List<WeekDayModel> weekDayModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_day_holiday);
        ButterKnife.bind(this);
        mba = new MyBaseAdapt((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        lv = findViewById(R.id.list_view_holiday);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        addHoliday();

        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals("user")){
            String username = intent.getStringExtra("username");
            USERNAME = username;
        }
        lv.setAdapter(mba);
    }

    private void addHoliday() {
        WeekDayModel weekDayModel = new WeekDayModel();
        weekDayModel.setBirthday("2020-01-01");
        weekDayModel.setShenfen("元旦节");
        weekDayModels.add(weekDayModel);
        WeekDayModel weekDayModel1 = new WeekDayModel();
        weekDayModel1.setBirthday("2020-01-24");
        weekDayModel1.setShenfen("春节");
        weekDayModels.add(weekDayModel1);
        WeekDayModel weekDayModel2 = new WeekDayModel();
        weekDayModel2.setBirthday("2020-04-04");
        weekDayModel2.setShenfen("清明节");
        weekDayModels.add(weekDayModel2);
        WeekDayModel weekDayModel3 = new WeekDayModel();
        weekDayModel3.setBirthday("2020-05-01");
        weekDayModel3.setShenfen("劳动节");
        weekDayModels.add(weekDayModel3);
        WeekDayModel weekDayModel4 = new WeekDayModel();
        weekDayModel4.setBirthday("2020-06-25");
        weekDayModel4.setShenfen("端午节");
        weekDayModels.add(weekDayModel4);
        WeekDayModel weekDayModel5 = new WeekDayModel();
        weekDayModel5.setBirthday("2020-10-01");
        weekDayModel5.setShenfen("中秋节");
        weekDayModels.add(weekDayModel5);
        WeekDayModel weekDayModel6 = new WeekDayModel();
        weekDayModel6.setBirthday("2020-10-01");
        weekDayModel6.setShenfen("国庆节");
        weekDayModels.add(weekDayModel6);
    }

    int ii = 0;
    public class MyBaseAdapt extends BaseAdapter {
        public class ViewHolder{
            TextView item_weekady;
            TextView item_weekday_name;
            TextView haiyou;
        }

        private LayoutInflater inflater;

        public MyBaseAdapt(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return weekDayModels.size();
        }

        @Override
        public Object getItem(int i) {
            return weekDayModels.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            if (v == null) {
                v = inflater.inflate(R.layout.item_weekday_myday, null);
                ViewHolder vh = new ViewHolder();
                vh.item_weekady = v.findViewById(R.id.item_weekady);
                vh.item_weekday_name = v.findViewById(R.id.item_weekady_name);
                vh.haiyou = v.findViewById(R.id.haiyou);
                v.setTag(vh);
            }

            ViewHolder vh = (ViewHolder) v.getTag();
            if (ii==weekDayModels.size()){
                ii=0;
            }
            WeekDayModel book = weekDayModels.get(ii);
            DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = null;
            long day = 00;
            Date now = new Date(System.currentTimeMillis());
            String aa = book.getBirthday();
            aa=aa.substring(4);
            String bir = "2020"+aa;

            String today = sdf.format(now);
            day = MyWriteUtils.differentDayMillisecond(bir,today);
            if(day<0){
                day = 365+day;
            }

            String finalday=String.valueOf(day);
            vh.item_weekady.setText(finalday);
            vh.item_weekday_name.setText(book.getShenfen());
            vh.haiyou.setText("还有");
            ii++;
            return v;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(WeekDayHolidayActivity.this, WeekDayActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }


}
