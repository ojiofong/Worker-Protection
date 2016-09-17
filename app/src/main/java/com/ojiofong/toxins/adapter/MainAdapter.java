package com.ojiofong.toxins.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ojiofong.toxins.R;
import com.ojiofong.toxins.model.Data;
import com.ojiofong.toxins.ui.ScrollingActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ojiofong on 9/16/16.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    List<Data> items = new ArrayList<>();
    ScrollingActivity scrollingActivity;


    public MainAdapter(ScrollingActivity scrollingActivity, List<Data> items){
        this.scrollingActivity = scrollingActivity;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View convertView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollingActivity.onclickItemView(view);
            }
        });
        return  new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.iv1.setImageResource(items.get(position).getIconID());
        holder.tv1.setText(items.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.list_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv1;
        public ImageView iv1;

        public ViewHolder(View convertView) {
            super(convertView);

            tv1 = (TextView) convertView.findViewById(R.id.textView1);
            iv1 = (ImageView) convertView.findViewById(R.id.imageView1);
        }
    }
}
