package com.jlouistechnology.magnawave.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jlouistechnology.magnawave.Activity.MainActivity;
import com.jlouistechnology.magnawave.Fonts.TextView_Regular;
import com.jlouistechnology.magnawave.Fonts.TextView_bld;
import com.jlouistechnology.magnawave.Fragment.ClientdetailFragment;
import com.jlouistechnology.magnawave.Fragment.GuideLineDetailFragment;
import com.jlouistechnology.magnawave.Interface.OnClickFilterListener;
import com.jlouistechnology.magnawave.Model.GuideLine;
import com.jlouistechnology.magnawave.Model.GuideLine;
import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.utils.Pref;

import java.util.ArrayList;

/**
 * Created by aipxperts on 28/1/16.
 */

public class GuidelineAdapter extends BaseAdapter {

    public  ArrayList<GuideLine> guideLinelist;
  
    Context context;
    OnClickFilterListener onClickFilterListener;
    int rec_size;

    public GuidelineAdapter(Context context, ArrayList<GuideLine> guideLinelist) {
       // super(context,textViewResourceId,guideLinelist);
        this.context=context;
        this.guideLinelist = guideLinelist;

    }
    private   class ViewHolder {

        TextView_bld txt_id;
        TextView_Regular txt_desc;
        LinearLayout main_layout;
        View view;
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
            return guideLinelist.get(position).getId();
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
        Log.v("ConvertView", String.valueOf(position)+"--"+guideLinelist.size()+" "+ Pref.getValue(context,"rec_size",0));
     //   onClickFilterListener.OnClickFilterListener(Pref.getValue(context,"rec_size",0));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_guideline, null);
            holder = new ViewHolder();
            holder.txt_id = (TextView_bld) convertView.findViewById(R.id.txt_id);
            holder.txt_desc = (TextView_Regular) convertView.findViewById(R.id.txt_desc);
            holder.main_layout = (LinearLayout) convertView.findViewById(R.id.main_layout);
            holder.view = (View) convertView.findViewById(R.id.view);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txt_id.setText((position+1)+"");
        holder.txt_desc.setText(guideLinelist.get(position).getName());
        if(position==0)
        {
            holder.txt_id.setBackgroundColor(context.getColor(R.color.colorAccent));
        }
        else if(position%2==0)
        {
            holder.txt_id.setBackgroundColor(context.getColor(R.color.colorAccent));
        }
        else {
            holder.txt_id.setBackgroundColor(context.getColor(R.color.blue));
        }



        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pref.setValue(context,"title_value",guideLinelist.get(position).getName());
                Pref.setValue(context,"guide_id",guideLinelist.get(position).getId());
                GuideLineDetailFragment fragment = new GuideLineDetailFragment();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();


            }
        });


        return convertView;
}


}
