package com.jlouistechnology.magnawave.Adapter;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.daimajia.swipe.SwipeLayout;
import com.jlouistechnology.magnawave.Activity.MainActivity;
import com.jlouistechnology.magnawave.Fonts.TextView_Regular;
import com.jlouistechnology.magnawave.Fonts.TextView_bld;
import com.jlouistechnology.magnawave.Fragment.ClientdetailFragment;
import com.jlouistechnology.magnawave.Fragment.ReportFragment;
import com.jlouistechnology.magnawave.Interface.OnClickDeleteListener;
import com.jlouistechnology.magnawave.Interface.OnClickFilterListener;
import com.jlouistechnology.magnawave.Model.Client_details;
import com.jlouistechnology.magnawave.utils.Pref;
import com.jlouistechnology.magnawave.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by aipxperts on 28/1/16.
 */

public class PatDetailsAdapter extends BaseAdapter  {

    public  ArrayList<Client_details> clientlist;
    Context context;
    OnClickFilterListener onClickFilterListener;
    int rec_size;
    int height_desc=0;
    int height_details=0;
    OnClickDeleteListener onClickDeleteListener;

    public PatDetailsAdapter(Context context, ArrayList<Client_details> clientlist) {
       // super(context,textViewResourceId,clientlist);
        this.context=context;
        this.clientlist = clientlist;

    }
    private   class ViewHolder {
        TextView_bld txtdelete;
        TextView_bld tv_time_info;
        TextView_bld tvtitle;
        TextView_Regular tv_time;
        TextView_bld tv_desc;
        TextView_Regular tv_details;
        LinearLayout layout;
        View view_top;
        SwipeLayout swipeLayout;


    }

    @Override
    public int getCount() {
        if (clientlist != null) {
            return clientlist.size();
        } else {
            return 0;
        }
    }
    @Override
    public String getItem(int position) {
        if (clientlist != null) {
            return clientlist.get(position).getPet_id();
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position)+"--"+clientlist.size()+" "+ Pref.getValue(context,"rec_size",0)+" "+position);
     //   onClickFilterListener.OnClickFilterListener(Pref.getValue(context,"rec_size",0));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_client_animals_details, null);

            holder = new ViewHolder();
            holder.tv_time_info=(TextView_bld) convertView.findViewById(R.id.tv_time_info);
            holder.tvtitle=(TextView_bld)convertView.findViewById(R.id.tvtitle);
            holder.tv_time = (TextView_Regular) convertView.findViewById(R.id.tv_time);
            holder.tv_desc = (TextView_bld) convertView.findViewById(R.id.tv_desc);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.layout);
            holder.tv_details = (TextView_Regular) convertView.findViewById(R.id.tv_details);
            holder.view_top=(View)convertView.findViewById(R.id.view_top);
            holder.swipeLayout=(SwipeLayout)convertView.findViewById(R.id.swipe);
            holder.txtdelete=(TextView_bld)convertView.findViewById(R.id.txtdelete);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Drag From Right
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wrapper));


        holder.tv_time_info.setText(clientlist.get(position).getTime_info());

        holder.tvtitle.setText(clientlist.get(position).getCat_name());
        holder.tv_desc.setText(MainActivity.capitalize(clientlist.get(position).getPet_name()));
        Log.e("size",clientlist.get(position).getPat_client_detailsArrayList().size()+"---");
        if(clientlist.get(position).getPat_client_detailsArrayList().size()>0)
        {
            holder.tv_details.setText(MainActivity.capitalize(clientlist.get(position).getPat_client_detailsArrayList().get(0).getBp_desc()));
            holder.tv_details.setVisibility(View.VISIBLE);

        }
        else {

            holder.tv_details.setVisibility(View.INVISIBLE);

        }
        if(clientlist.get(position).getTime_info().equalsIgnoreCase("Today") || clientlist.get(position).getTime_info().equalsIgnoreCase("Yesterday"))
        {
            holder.tv_time.setText(gethour(Long.parseLong(clientlist.get(position).getCreated()))+":"+getminute(Long.parseLong(clientlist.get(position).getCreated())));
        }
        else {
            holder.tv_time.setText(clientlist.get(position).getDates());
        }

        Log.e("height_value",(height_desc+height_details)+"");

        if(clientlist.get(position).getTime_info_visible().equals("1"))
        {
            holder.tv_time_info.setVisibility(View.VISIBLE);

        }
        else {
            holder.tv_time_info.setVisibility(View.GONE);
        }

        holder.txtdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setMessage("Are you sure you want to remove this item?");
                // set positive button: Yes message
                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {

                        Pref.setValue(context,"sub_pet_id",clientlist.get(position).getPet_id());
                        onClickDeleteListener.OnClickDeleteListener(position);

                    }
                });
                // set negative button: No message
                alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the alert box and put a Toast to the user
                        dialog.cancel();

                    }
                });
                // set neutral button: Exit the app message

                AlertDialog alertDialog = alertDialogBuilder.create();
                // show alert
                alertDialog.show();


            }
        });
       
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("SwipeImage","adapter1 " + clientlist.get(position).getPet_img());
                Log.e("SwipeImage","getPet_swip_img " + clientlist.get(position).getPet_swip_img());
                Pref.setValue(context,"pat_id",clientlist.get(position).getPet_id());
                Pref.setValue(context,"pat_name",clientlist.get(position).getPet_name());
                Pref.setValue(context,"created",clientlist.get(position).getCreated());
                Pref.setValue(context,"pat_image",clientlist.get(position).getPet_img());
                Pref.setValue(context,"pat_swipe_image",clientlist.get(position).getPet_swip_img());
                Pref.setValue(context,"pat_type_flip",clientlist.get(position).getCat_name());
                if(!clientlist.get(position).getScreen_width().equals(""))
                {
                    Pref.setValue(context,"screen_width",clientlist.get(position).getScreen_width());
                    Pref.setValue(context,"screen_width_old",clientlist.get(position).getScreen_width());
                }
                if(!clientlist.get(position).getScreen_height().equals(""))
                {
                    Pref.setValue(context,"screen_height",clientlist.get(position).getScreen_height());
                    Pref.setValue(context,"screen_height_old",clientlist.get(position).getScreen_height());
                }

                Pref.setValue(context,"from_edit","0");
                ReportFragment fragment = new ReportFragment();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();

            }
        });

        return convertView;
}
    public int gethour(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis((timestamp*1000));
        Date d = c.getTime();
        return d.getHours();

    }

    public int getminute(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis((timestamp*1000));
        Date d = c.getTime();
        return d.getMinutes();

    }

    public void onClickDeleteListener(OnClickDeleteListener onClickDeleteListener) {
        this.onClickDeleteListener = onClickDeleteListener;
    }

}
