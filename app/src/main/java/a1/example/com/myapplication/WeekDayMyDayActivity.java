package a1.example.com.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import a1.example.com.myapplication.Model.WeekDayModel;
import a1.example.com.myapplication.Util.MyWriteUtils;
import butterknife.ButterKnife;

public class WeekDayMyDayActivity extends AppCompatActivity {
//    @BindView(R.id.weekDayMyDay)
//    LinearLayout weekDayMyDay;
    private MyBaseAdapt mba;
    private ListView lv;
    String RESULT = "";
    List<WeekDayModel> weekDayModels = new ArrayList<>();
    String USERNAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_day_my_day);

        mba = new MyBaseAdapt((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        lv = findViewById(R.id.list_view);
        ButterKnife.bind(this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals("user")){
            String result = intent.getStringExtra("result");
            USERNAME = intent.getStringExtra("username");
            RESULT = result;
            Map<String, Object> map = jsonToMap(RESULT);
            Iterator iterator = map.keySet().iterator();
            int length = map.size();
            for(int i = 0 ; i<length;i++){
//                        Object aa = map.values().iterator().next();
                Map<String, Object> aaa = (Map<String, Object>)map.values().iterator().next();
                iterator.next();
                iterator.remove();
                WeekDayModel weekDayModel = new WeekDayModel();
                Map<String, Object> map2 = (Map<String, Object>) map.get("3");
                String birthday = (String) aaa.get("birthday");
                String shenfen = (String) aaa.get("shenfen");
                String createby = (String) aaa.get("createby");
                weekDayModel.setBirthday(birthday);
                weekDayModel.setShenfen(shenfen);
                weekDayModel.setCreateby(createby);
                weekDayModels.add(weekDayModel);
            }

            lv.setAdapter(mba);
        }

    }

    Map<String, Object> jsonToMap(String content) {
        content = content.trim();
        Map<String, Object> result = new HashMap<>();
        try {

            if (content.charAt(0) == '[') {
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Object value = jsonArray.get(i);
                    if (value instanceof JSONArray || value instanceof JSONObject) {
                        result.put(i + "", jsonToMap(value.toString().trim()));
                    } else {
                        result.put(i + "", jsonArray.getString(i));
                    }
                }
            } else if (content.charAt(0) == '{'){
                JSONObject jsonObject = new JSONObject(content);
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Object value = jsonObject.get(key);
                    if (value instanceof JSONArray || value instanceof JSONObject) {
                        result.put(key, jsonToMap(value.toString().trim()));
                    } else {
                        result.put(key, value.toString().trim());
                    }
                }
            }else {
                Log.e("异常", "json2Map: 字符串格式错误");
            }
        } catch (JSONException e) {
            Log.e("异常", "json2Map: ", e);
            result = null;
        }
        return result;
    }
    int ii = 0;
    public class MyBaseAdapt extends BaseAdapter {
        public class ViewHolder{
            TextView item_weekady;
            TextView item_weekday_name;
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
            ii++;
            return v;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(WeekDayMyDayActivity.this, WeekDayActivity.class);
        intent.setAction("user");
        intent.putExtra("username",USERNAME);
        startActivity(intent);
        finish();
    }
}

