package com.jlouistechnology.magnawave.Adapter;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.jlouistechnology.magnawave.Activity.LoginActivity;
import com.jlouistechnology.magnawave.Activity.MainActivity;
import com.jlouistechnology.magnawave.Fonts.TextView_Regular;
import com.jlouistechnology.magnawave.Fragment.ClientFragment;
import com.jlouistechnology.magnawave.Fragment.ClientFragment_Search;
import com.jlouistechnology.magnawave.Fragment.ClientRecentRecordFragment;
import com.jlouistechnology.magnawave.Fragment.ClientdetailFragment;
import com.jlouistechnology.magnawave.Interface.OnClickFilterListener;
import com.jlouistechnology.magnawave.Model.Client;
import com.jlouistechnology.magnawave.Model.Client_details;
import com.jlouistechnology.magnawave.Model.Pat_client_details;
import com.jlouistechnology.magnawave.Webservice_list.WebService;
import com.jlouistechnology.magnawave.utils.ConnectionDetector;
import com.jlouistechnology.magnawave.utils.Pref;
import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.utils.TimeAgo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aipxperts on 28/1/16.
 */

public class ClientAdapter extends BaseSwipeAdapter implements Filterable {

    public  ArrayList<Client> clientlist;
    public  ArrayList<Client> clientlist_filter;
    Context context;
    OnClickFilterListener onClickFilterListener;
    ConnectionDetector cd;
    int rec_size;

    public ClientAdapter(Context context,ArrayList<Client> clientlist) {
        // super(context,textViewResourceId,clientlist);
        this.context=context;
        this.clientlist = clientlist;
        cd = new ConnectionDetector(context);
    }


    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_client, null);
        SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));


        v.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(context, "click delete", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setMessage("Are you sure you want delete client?");
                // set positive button: Yes message
                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {

                        Pref.setValue(context,"client_id_delete",clientlist.get(position).getClient_id());

                        call_api_to_delete_client(position);

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


        return v;
    }

    @Override
    public void fillValues(final int position, View v) {
        final ImageView img_client=(ImageView) v.findViewById(R.id.img_client);
        TextView_Regular txt_client_name = (TextView_Regular) v.findViewById(R.id.txt_client_name);
        TextView_Regular txt_company_name = (TextView_Regular) v.findViewById(R.id.txt_company_name);
        RelativeLayout main_layout = (RelativeLayout) v.findViewById(R.id.main_layout);

        txt_client_name.setText(MainActivity.capitalize(clientlist.get(position).getFirst_name()+" "+clientlist.get(position).getLast_name()));
        if(MainActivity.capitalize(clientlist.get(position).getCompany()).equals("null"))
        {
            txt_company_name.setText("");
        }
        else {
            txt_company_name.setText(MainActivity.capitalize(clientlist.get(position).getCompany()));
        }

        Glide.with(context).load(clientlist.get(position).getImage()).asBitmap().centerCrop().into(new BitmapImageViewTarget(img_client) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                img_client.setImageDrawable(circularBitmapDrawable);
            }
        });
        main_layout.setOnClickListener(new View.OnClickListener() {
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
                Pref.setValue(context,"client_profile",clientlist.get(position).getImage());
                Log.e("client_profile", Pref.getValue(context,"client_profile","")+" "+clientlist.get(position).getImage());
                ClientdetailFragment fragment = new ClientdetailFragment();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();


            }
        });
    }


    @Override
    public int getCount() {
        return clientlist.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
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

                Log.e("clientlist_filter",clientlist_filter.size()+"--");
                int rec_size=0;
                if (constraint != null) {
                    if (clientlist_filter != null && clientlist_filter.size() > 0) {
                        for (final Client g : clientlist_filter) {
                            if (g.getFirst_name().toLowerCase().contains(constraint.toString())) {
                                // .contains(constraint.toString()))
                                results.add(g);
                                rec_size=rec_size+1;
                                // Pref.setValue(context,"rec_size",rec_size);
                                Log.e("rec_size",rec_size+"---");
                            }

                        }
                        Log.e("clientlist_filter",clientlist_filter.size()+"--"+rec_size);
                        if(rec_size==0)
                        {
                            //Pref.setValue(context, "rec_size", 0);
                        }

                        //onClickFilterListener.OnClickFilterListener(results.size());
                    }

                    //onClickFilterListener.OnClickFilterListener(results.size());
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
                //Log.v("clientlist",clientlist.size()+"");
                notifyDataSetChanged();
            }
        };
    }
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
    public void call_api_to_delete_client(int Position)
    {
        if(cd.isConnectingToInternet()) {

            new ExecuteTask_client_delete(Position).execute();
        }else
        {
            cd.showToast(context, R.string.NO_INTERNET_CONNECTION);

        }
    }




    public void setOnClickFilterListener(OnClickFilterListener onClickFilterListener) {
        this.onClickFilterListener = onClickFilterListener;
    }
    class ExecuteTask_client_delete extends AsyncTask<String, Integer, String> {
        int position1;
        ExecuteTask_client_delete(int position)
        {
            position1=position;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(context);
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.GetData(WebService.CLIENT_DELETE+ Pref.getValue(context,"client_id_delete",""), Pref.getValue(context,"token",""));
            Log.e("res....", "" + res);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                Log.e("result",result+"");
                JSONObject json;
                json = new JSONObject(result);
                JSONObject json2=json.getJSONObject("success");
                if(json2.getString("status").equals("true"))
                {
                    if(json2.getString("response_code").equals("200")) {
                        Toast.makeText(context,json2.getString("message"),Toast.LENGTH_LONG).show();
                        clientlist.remove(position1);
                        notifyDataSetChanged();
                        ((FragmentActivity)context).getSupportFragmentManager().popBackStack();
                        if(Pref.getValue(context,"fragment_to_adapter","").equalsIgnoreCase("search"))
                        {
                            ClientFragment_Search fragment = new ClientFragment_Search();
                            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();


                        }else if(Pref.getValue(context,"fragment_to_adapter","").equalsIgnoreCase("main"))
                        {
                            ClientFragment fragment = new ClientFragment();
                            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();

                        }else if(Pref.getValue(context,"fragment_to_adapter","").equalsIgnoreCase("Recentrecord"))
                        {
                            Pref.setValue(context,"search_filter","client");
                            ClientRecentRecordFragment fragment = new ClientRecentRecordFragment();
                            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();

                        }


                    }
                }
                else {

                    Toast.makeText(context,json2.getString("message"),Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
