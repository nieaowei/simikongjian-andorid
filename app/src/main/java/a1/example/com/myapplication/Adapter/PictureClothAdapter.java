package a1.example.com.myapplication.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;

import a1.example.com.myapplication.Model.PictureModel;
import a1.example.com.myapplication.R;
import a1.example.com.myapplication.Util.MyPictureUtils;

public class PictureClothAdapter extends ArrayAdapter<PictureModel> {
    private int resourceId;
    protected Context context;
    protected LayoutInflater inflater;
    protected int resource;
    List<PictureModel> list = new ArrayList<>();

    public PictureClothAdapter(Context context, int resource, List<PictureModel> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PictureModel book = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

            viewHolder = new ViewHolder();

            viewHolder.name = (ImageView)view.findViewById(R.id.cloth_image_view);
            view.setTag(viewHolder);//将viewHolder存储在view中
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag(); //重新获取viewHolder
        }

            Bitmap bitmap = MyPictureUtils.stringtoBitmap(book.getKeys());
            viewHolder.name.setImageBitmap(bitmap);

//        Bitmap bitmap = MyPictureUtils.stringtoBitmap(book.getKeys());
//        viewHolder.name.setImageBitmap(bitmap);

        return view;
    }

    class ViewHolder{
        ImageView name;
    }
}
