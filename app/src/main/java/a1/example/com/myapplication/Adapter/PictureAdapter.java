package a1.example.com.myapplication.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import a1.example.com.myapplication.Model.PictureModel;
import a1.example.com.myapplication.R;
import a1.example.com.myapplication.Util.MyPictureUtils;

public class PictureAdapter extends ArrayAdapter<PictureModel> {
    private int resourceId;
    protected Context context;
    protected LayoutInflater inflater;
    protected int resource;
    List<PictureModel> list = new ArrayList<>();

    public PictureAdapter(Context context, int resource, List<PictureModel> objects) {
        super(context, resource, objects);
        resourceId = resource;
        if (objects!=null){
            list = objects;
        }
        inflater = LayoutInflater.from(context);
        this.resource = resource;
        this.context = context;
    }

    @Override
    public int getCount() {
        if(list.size()%2>0) {
            return list.size()/2+1;
        } else {
            return list.size()/2;
        }
    }

    @Override
    public PictureModel getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //PictureModel book = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = inflater.inflate(resource, null);
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

            viewHolder = new ViewHolder();

            viewHolder.name = (ImageView)view.findViewById(R.id.image_view);
            viewHolder.name2 = (ImageView)view.findViewById(R.id.image_view3);

            view.setTag(viewHolder);//将viewHolder存储在view中
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag(); //重新获取viewHolder
        }
        int distance =  list.size() - position*2;
        int cellCount = distance >= 2? 2:distance;
        List<PictureModel> itemList = list.subList(position*2,position*2+cellCount);
        if (itemList.size() >0) {
            Glide.with(this.context).load(itemList.get(0).url).into(viewHolder.name);
            Log.d("image",itemList.get(0).url);
//            Bitmap bitmap = MyPictureUtils.stringtoBitmap(itemList.get(0).getKeys());
//            viewHolder.name.setImageBitmap(bitmap);
            if (itemList.size() >1){
//                Bitmap bitmap2 = MyPictureUtils.stringtoBitmap(itemList.get(1).getKeys());
                viewHolder.name2.setVisibility(View.VISIBLE);
                Log.d("image",itemList.get(1).url);

                Glide.with(this.context).load(itemList.get(1).url).into(viewHolder.name2);

//                viewHolder.name2.setImageBitmap(bitmap2);

            }else{
                viewHolder.name2.setVisibility(View.INVISIBLE);
            }
        }
//        Bitmap bitmap = MyPictureUtils.stringtoBitmap(book.getKeys());
//        viewHolder.name.setImageBitmap(bitmap);

        return view;
    }

    class ViewHolder{
        ImageView name;
        ImageView name2;
    }
}
