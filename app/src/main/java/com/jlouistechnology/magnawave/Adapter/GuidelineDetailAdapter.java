package com.jlouistechnology.magnawave.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jlouistechnology.magnawave.Fonts.TextView_Regular;
import com.jlouistechnology.magnawave.Fonts.TextView_bld;
import com.jlouistechnology.magnawave.Interface.OnClickFilterListener;
import com.jlouistechnology.magnawave.Model.GuideLineDetails;
import com.jlouistechnology.magnawave.Model.GuideLineDetails;
import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.utils.Pref;

import java.util.ArrayList;

/**
 * Created by aipxperts on 28/1/16.
 */

public class GuidelineDetailAdapter extends BaseAdapter {

    public  ArrayList<GuideLineDetails> guideLinelist;

    Context context;
    OnClickFilterListener onClickFilterListener;
    int rec_size;

    public GuidelineDetailAdapter(Context context, ArrayList<GuideLineDetails> guideLinelist) {
       // super(context,textViewResourceId,guideLinelist);
        this.context=context;
        this.guideLinelist = guideLinelist;

    }
    private   class ViewHolder {

        TextView_bld tvtitle;
        TextView_Regular txt_desc;
        ImageView img_collapse;
        View view;
        LinearLayout desc_layout;
        RelativeLayout header_layout;
    }

    @Override
    public int getCount() {
        if (guideLinelist != null) {
            return guideLinelist.size();
        } else {
            return 0;
        }
    }
    @Override
    public String getItem(int position) {
        if (guideLinelist != null) {
            return guideLinelist.get(position).getTitle();
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_guideline_details, null);
            holder = new ViewHolder();
            holder.tvtitle = (TextView_bld) convertView.findViewById(R.id.tvtitle);
            holder.txt_desc = (TextView_Regular) convertView.findViewById(R.id.txt_details);
            holder.img_collapse = (ImageView) convertView.findViewById(R.id.img_collapse);
            holder.view = (View) convertView.findViewById(R.id.ver_view);
            holder.desc_layout=(LinearLayout)convertView.findViewById(R.id.desc_layout);
            holder.header_layout=(RelativeLayout)convertView.findViewById(R.id.header_layout);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvtitle.setText(guideLinelist.get(position).getTitle());
        holder.txt_desc.setText(guideLinelist.get(position).getDesc());

        if(guideLinelist.get(position).getIs_expand().equals("1"))
        {
            holder.desc_layout.setVisibility(View.VISIBLE);
            holder.img_collapse.setImageResource(R.mipmap.expand);

        }
        else {
            holder.desc_layout.setVisibility(View.GONE);
            holder.img_collapse.setImageResource(R.mipmap.collapse);
        }

        final ViewHolder finalHolder = holder;
        holder.header_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(guideLinelist.get(position).getIs_expand().equals("0"))
                {
                    guideLinelist.get(position).setIs_expand("1");
                    finalHolder.img_collapse.setImageResource(R.mipmap.expand);

                }
                else {
                    guideLinelist.get(position).setIs_expand("0");
                    finalHolder.img_collapse.setImageResource(R.mipmap.collapse);
                }
                notifyDataSetChanged();

            }
        });



        return convertView;
}


}
