package a1.example.com.myapplication.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.List;

import a1.example.com.myapplication.Model.HappyNoteModel;
import a1.example.com.myapplication.R;

public class HappyNoteAdapter extends ArrayAdapter<HappyNoteModel> {
    private int resourceId;

    public HappyNoteAdapter(Context context, int resource, List<HappyNoteModel> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HappyNoteModel book = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

            viewHolder = new ViewHolder();

            viewHolder.name = (TextView)view.findViewById(R.id.item_happy_note_name);
            viewHolder.author = (TextView)view.findViewById(R.id.item_happy_note_time);

            view.setTag(viewHolder);//将viewHolder存储在view中
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag(); //重新获取viewHolder
        }

        viewHolder.name.setText(book.getNotetitle());
        viewHolder.author.setText(book.getWritetime());

        return view;
    }

    class ViewHolder{
        TextView name;
        TextView author;
    }

}
