package com.jlouistechnology.magnawave.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jlouistechnology.magnawave.Activity.MainActivity;
import com.jlouistechnology.magnawave.Fonts.TextView_Regular;
import com.jlouistechnology.magnawave.Fragment.AddNewPatFragment;
import com.jlouistechnology.magnawave.Interface.OnClickFilterListener;
import com.jlouistechnology.magnawave.Model.Client;
import com.jlouistechnology.magnawave.utils.Pref;
import com.jlouistechnology.magnawave.R;

import java.util.ArrayList;

/**
 * Created by aipxperts on 28/1/16.
 */

public class ClientAddnewpatAdapter extends BaseAdapter implements Filterable {

    public  ArrayList<Client> clientlist;
    public  ArrayList<Client> clientlist_filter;
    Context context;
    OnClickFilterListener onClickFilterListener;
    int rec_size_new=0;

    public ClientAddnewpatAdapter(Context context, ArrayList<Client> clientlist) {
       // super(context,textViewResourceId,clientlist);
        this.context=context;
        this.clientlist = clientlist;

    }
    private   class ViewHolder {
        ImageView img_client;
        TextView_Regular txt_client_name;
        TextView_Regular txt_company_name;
        RelativeLayout main_layout;
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
            return clientlist.get(position).getFirst_name();
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
        Log.v("ConvertView", String.valueOf(position));
       // onClickFilterListener.OnClickFilterListener(Pref.getValue(context,"rec_size_new",0));
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_client_add_new_pet, null);

            holder = new ViewHolder();
            holder.img_client=(ImageView) convertView.findViewById(R.id.img_client);
            holder.txt_client_name = (TextView_Regular) convertView.findViewById(R.id.txt_client_name);
            holder.txt_company_name = (TextView_Regular) convertView.findViewById(R.id.txt_company_name);
            holder.main_layout = (RelativeLayout) convertView.findViewById(R.id.main_layout);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_client_name.setText(MainActivity.capitalize(clientlist.get(position).getFirst_name()+" "+clientlist.get(position).getLast_name()));
        if(MainActivity.capitalize(clientlist.get(position).getCompany()).equals("null"))
        {
            holder.txt_company_name.setText("");
        }
        else {
            holder.txt_company_name.setText(MainActivity.capitalize(clientlist.get(position).getCompany()));
        }       final ViewHolder finalHolder = holder;
        Glide.with(context).load(clientlist.get(position).getImage()).asBitmap().centerCrop().into(new BitmapImageViewTarget(finalHolder.img_client) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);

                finalHolder.img_client.setImageDrawable(circularBitmapDrawable);
            }
        });

        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pref.setValue(context,"client_fname",clientlist.get(position).getFirst_name());
                Pref.setValue(context,"client_lname",clientlist.get(position).getLast_name());
                if(MainActivity.capitalize(clientlist.get(position).getCompany()).equals("null"))
                {
                    Pref.setValue(context,"client_company_name","");
                }
                else {
                    Pref.setValue(context,"client_company_name",clientlist.get(position).getCompany());
                }

                Pref.setValue(context,"client_email",clientlist.get(position).getEmail());
                Pref.setValue(context,"client_pno",clientlist.get(position).getPhone_no());
                Pref.setValue(context,"client_id",clientlist.get(position).getClient_id());
                Pref.setValue(context,"client_address",clientlist.get(position).getAddress());
                AddNewPatFragment fragment = new AddNewPatFragment();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();


            }
        });

        return convertView;
}

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Client> results = new ArrayList<Client>();
                if(clientlist_filter != null && clientlist_filter.size()<clientlist.size() )
                {
                    clientlist_filter = clientlist;
                }
                if (clientlist_filter == null)
                    clientlist_filter = clientlist;
                int rec_size_new=0;
                Log.v("clientlist_filter",clientlist_filter.size()+"");
                if (constraint != null) {
                    if (clientlist_filter != null && clientlist_filter.size() > 0) {
                        for (final Client g : clientlist_filter) {
                            if (g.getFirst_name().toLowerCase().contains(constraint.toString())) {
                                // .contains(constraint.toString()))
                                results.add(g);
                                rec_size_new = rec_size_new + 1;

                            }
                        }
                    }

                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                clientlist = (ArrayList<Client>) results.values;
                if(results!=null)
                {
                    Pref.setValue(context,"rec_size",clientlist.size());
                    Log.e("rinal",clientlist.size()+"");
                    onClickFilterListener.OnClickFilterListener(clientlist.size());

                }
                else {
                    Pref.setValue(context,"rec_size",0);
                }
                notifyDataSetChanged();
            }
        };
    }
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void setOnClickFilterListener(OnClickFilterListener onClickFilterListener) {
        this.onClickFilterListener = onClickFilterListener;
    }


}
