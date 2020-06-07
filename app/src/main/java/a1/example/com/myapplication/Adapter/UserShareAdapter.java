package a1.example.com.myapplication.Adapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.List;

import a1.example.com.myapplication.Model.UserShareModel;
import a1.example.com.myapplication.R;

public class UserShareAdapter extends ArrayAdapter<UserShareModel> {
    private int resourceId;

    public UserShareAdapter(Context context, int resource, List<UserShareModel> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserShareModel userShare = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

            viewHolder = new ViewHolder();

            viewHolder.sharename = (TextView)view.findViewById(R.id.my_friends_shares_name);
            viewHolder.sharenr = (TextView)view.findViewById(R.id.my_friends_shares_nr);
            viewHolder.sharedt = (TextView)view.findViewById(R.id.my_friends_shares_dt);
            viewHolder.img = (ImageView)view.findViewById(R.id.share_img);

            view.setTag(viewHolder);//将viewHolder存储在view中
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag(); //重新获取viewHolder
        }

        viewHolder.sharename.setText(userShare.getUsername());
        viewHolder.sharenr.setText(userShare.getShares());
        viewHolder.sharedt.setText(userShare.getSharesdt());
        Glide.with(this.getContext()).load(userShare.getSharesurl()).into(viewHolder.img);

        return view;
    }

    class ViewHolder{
        TextView sharename;
        TextView sharedt;
        TextView sharenr;
        ImageView img;
    }
}
