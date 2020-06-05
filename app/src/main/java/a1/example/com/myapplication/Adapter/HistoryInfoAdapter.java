package a1.example.com.myapplication.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import a1.example.com.myapplication.Model.UserFriendModel;
import a1.example.com.myapplication.Model.UserInfoModel;
import a1.example.com.myapplication.R;

public class HistoryInfoAdapter extends ArrayAdapter<UserInfoModel> {
    private int resourceId;

    public HistoryInfoAdapter(Context context, int resource, List<UserInfoModel> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserInfoModel userInfoModel = getItem(position);
        View view;
        HistoryInfoAdapter.ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

            viewHolder = new HistoryInfoAdapter.ViewHolder();

            viewHolder.name = (TextView)view.findViewById(R.id.history_time);
            viewHolder.name2 = (TextView)view.findViewById(R.id.history_content);

            view.setTag(viewHolder);//将viewHolder存储在view中
        }else{
            view = convertView;
            viewHolder = (HistoryInfoAdapter.ViewHolder)view.getTag(); //重新获取viewHolder
        }

//        viewHolder.name.setText(book.getFriendname());
        viewHolder.name.setText(userInfoModel.getUpdatedt());
        viewHolder.name2.setText("体重："+userInfoModel.getUserweight()+"kg    身高："+userInfoModel.getUserheight()+"cm    爱好："+userInfoModel.getUserfavorite());

        return view;
    }

    class ViewHolder{
        TextView name;
        TextView name2;
    }
}
