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
import a1.example.com.myapplication.R;

public class UserFriendAdapter extends ArrayAdapter<UserFriendModel> {
    private int resourceId;

    public UserFriendAdapter(Context context, int resource, List<UserFriendModel> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserFriendModel book = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

            viewHolder = new ViewHolder();

            viewHolder.name = (TextView)view.findViewById(R.id.item_my_friends_username);

            view.setTag(viewHolder);//将viewHolder存储在view中
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag(); //重新获取viewHolder
        }

        viewHolder.name.setText(book.getFriendname());

        return view;
    }

    class ViewHolder{
        TextView name;
    }
}
