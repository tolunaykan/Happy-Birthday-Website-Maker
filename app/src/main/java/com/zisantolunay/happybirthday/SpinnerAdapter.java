package com.zisantolunay.happybirthday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter{

    private List<String> list;
    private Context context;
    private LayoutInflater layoutInflater;



    public SpinnerAdapter(Context context, List<String> list){
        this.list = list;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }


    class viewHolder{
        TextView textView;
        ImageView imageView;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        viewHolder holder;
        if(convertView == null){
            holder = new viewHolder();
            convertView = layoutInflater.inflate(R.layout.spinner_row_layout, parent ,false);
            holder.textView = convertView.findViewById(R.id.textView2);
            holder.imageView = convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        }else {
            holder = (viewHolder) convertView.getTag();
        }

        holder.textView.setText(list.get(position));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                Options.getInstance().removeMessage(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }


}
