package a1.example.com.myapplication.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



import java.util.List;

import a1.example.com.myapplication.Model.UserFootModel;
import a1.example.com.myapplication.R;

public class UserFootAdapter extends ArrayAdapter<UserFootModel> {
    private int resourceId;

    public UserFootAdapter(Context context, int resource, List<UserFootModel> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserFootModel book = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

            viewHolder = new ViewHolder();

            viewHolder.item_my_foot_dt = (TextView)view.findViewById(R.id.item_my_foot_dt);
            viewHolder.item_my_foot_nr = (TextView)view.findViewById(R.id.item_my_foot_nr);

            view.setTag(viewHolder);//将viewHolder存储在view中
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag(); //重新获取viewHolder
        }

        viewHolder.item_my_foot_dt.setText(book.getDt());
        viewHolder.item_my_foot_nr.setText(book.getUserfoot());

        return view;
    }

    class ViewHolder{
        TextView item_my_foot_dt;
        TextView item_my_foot_nr;
    }
}
