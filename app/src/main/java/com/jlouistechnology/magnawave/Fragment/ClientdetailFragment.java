package com.jlouistechnology.magnawave.Fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jlouistechnology.magnawave.Activity.DashboardActivity;
import com.jlouistechnology.magnawave.Adapter.EditPatDetailsInfoRecyclerViewAdapter;
import com.jlouistechnology.magnawave.Adapter.PatDetailsAdapter;
import com.jlouistechnology.magnawave.Interface.OnClickDeleteListener;
import com.jlouistechnology.magnawave.Model.Client_details;
import com.jlouistechnology.magnawave.Model.Pat_client_details;
import com.jlouistechnology.magnawave.Webservice_list.WebService;
import com.jlouistechnology.magnawave.utils.ConnectionDetector;
import com.jlouistechnology.magnawave.utils.Pref;
import com.jlouistechnology.magnawave.utils.TimeAgo;
import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.databinding.FragmentClientDetailsBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by aipxperts on 9/5/17.
 */

public class ClientdetailFragment extends Fragment {

    FragmentClientDetailsBinding mBinding;
    View rootView;
    Context context;
    public static ArrayList<Client_details> client_detailsArrayList = new ArrayList<>();
    ConnectionDetector cd;
    public static com.jlouistechnology.magnawave.Fonts.TextView_bld tv_time_info_new;
    int position;
    PatDetailsAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         * Binding all xml views
         */
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_client_details, container, false);
        rootView = mBinding.getRoot();
        tv_time_info_new = (com.jlouistechnology.magnawave.Fonts.TextView_bld) rootView.findViewById(R.id.tv_time_info_new);
        context = getActivity();
        cd = new ConnectionDetector(context);
        setdata();
        call_api_to_get_client_list();

        mBinding.clientInfo.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("first_vible_position", mBinding.clientInfo.getFirstVisiblePosition() + "");
                tv_time_info_new.setText(client_detailsArrayList.get(mBinding.clientInfo.getFirstVisiblePosition()).getTime_info());
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mBinding.txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditClientFragment fragment = new EditClientFragment();
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();
            }
        });
        return rootView;
    }

    public void setdata() {
        mBinding.txtCompanyName.setText(Pref.getValue(context, "client_company_name", ""));
        mBinding.txtEmail.setText("E-mail: " + Pref.getValue(context, "client_email", ""));
        mBinding.txtPhone.setText("Phone: " + Pref.getValue(context, "client_pno", ""));
        mBinding.txtName.setText(Pref.getValue(context, "client_fname", "").toUpperCase() + " " + Pref.getValue(context, "client_lname", "").toUpperCase());
        Log.e("client_profile", Pref.getValue(context, "client_profile", "") + " ");
        if (!Pref.getValue(context, "client_profile", "").equals("")) {
            Glide.with(context).load(Pref.getValue(context, "client_profile", "")).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(new BitmapImageViewTarget(mBinding.imgClient) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    mBinding.imgClient.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
    }

    public void call_api_to_get_client_list() {
        if (cd.isConnectingToInternet()) {

            new ExecuteTask_account_type().execute(WebService.LISTPATDETAILS);
        } else {
            cd.showToast(context, R.string.NO_INTERNET_CONNECTION);

        }
    }

    class ExecuteTask_account_type extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.GetData(WebService.LISTPATDETAILS + Pref.getValue(context, "client_id", ""), Pref.getValue(context, "token", ""));
            Log.e("res....", "" + res);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                Log.e("result", result + "");
                JSONObject json;
                json = new JSONObject(result);
                JSONObject json2 = json.getJSONObject("success");
                if (json2.getString("status").equals("true")) {
                    if (json2.getString("response_code").equals("200")) {
                        client_detailsArrayList.clear();
                        JSONArray jsonArray_data = json2.getJSONArray("data");
                        Client_details[] clients = new Client_details[jsonArray_data.length()];
                        for (int i = 0; i < jsonArray_data.length(); i++) {
                            clients[i] = new Client_details();
                            clients[i].setClient_id(jsonArray_data.getJSONObject(i).getString("client_id"));
                            clients[i].setPet_id(jsonArray_data.getJSONObject(i).getString("pet_id"));
                            clients[i].setUser_id(jsonArray_data.getJSONObject(i).getString("user_id"));
                            clients[i].setCat_name(jsonArray_data.getJSONObject(i).getString("cat_name"));
                            clients[i].setPet_name(jsonArray_data.getJSONObject(i).getString("pet_name"));
                            clients[i].setPet_img(jsonArray_data.getJSONObject(i).getString("pet_img"));
                            clients[i].setPet_swip_img(jsonArray_data.getJSONObject(i).getString("pet_swip_img"));
                            clients[i].setCreated(jsonArray_data.getJSONObject(i).getString("created"));
                            clients[i].setDates(jsonArray_data.getJSONObject(i).getString("dates"));
                            clients[i].setScreen_width(jsonArray_data.getJSONObject(i).optString("width"));
                            clients[i].setScreen_height(jsonArray_data.getJSONObject(i).optString("height"));
                            TimeAgo timeAgo = new TimeAgo(context);
                            timeAgo.timeAgo(Long.parseLong(jsonArray_data.getJSONObject(i).getString("created")));
                            clients[i].setTime_info(timeAgo.timeAgo(Long.parseLong(jsonArray_data.getJSONObject(i).getString("created"))));
                            ArrayList<Pat_client_details> pat_detailsArrayList = new ArrayList<>();
                            JSONArray jsonArray_pat_detail = jsonArray_data.getJSONObject(i).getJSONArray("pets");
                            Pat_client_details[] pat_detailses = new Pat_client_details[jsonArray_pat_detail.length()];
                            pat_detailsArrayList.clear();
                            for (int j = 0; j < jsonArray_pat_detail.length(); j++) {
                                pat_detailses[j] = new Pat_client_details();
                                pat_detailses[j].setPat_detail_id(jsonArray_pat_detail.getJSONObject(j).getString("pat_detail_id"));
                                pat_detailses[j].setBp_title(jsonArray_pat_detail.getJSONObject(j).getString("bp_title"));
                                pat_detailses[j].setBp_desc(jsonArray_pat_detail.getJSONObject(j).getString("bp_desc"));
                                pat_detailses[j].setBp_img(jsonArray_pat_detail.getJSONObject(j).getString("bp_img"));
                                pat_detailses[j].setPat_main_id(jsonArray_pat_detail.getJSONObject(j).getString("pat_detail_id"));
                                pat_detailses[j].setX(jsonArray_pat_detail.getJSONObject(j).optInt("x"));
                                pat_detailses[j].setY(jsonArray_pat_detail.getJSONObject(j).optInt("y"));
                                pat_detailses[j].setIs_flip_image(jsonArray_pat_detail.getJSONObject(j).optString("is_flip"));
                                pat_detailsArrayList.add(pat_detailses[j]);
                            }
                            clients[i].setPat_client_detailsArrayList(pat_detailsArrayList);
                            client_detailsArrayList.add(clients[i]);

                        }

                        set_adapter();
                    }
                } else {

                    Toast.makeText(context, json2.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void set_adapter() {
        int first_time_today = 0;
        int first_time_yesterday = 0;
        int first_time_week = 0;
        int first_time_month = 0;
        int first_time_year = 0;
        int first_before_year = 0;
        for (int i = 0; i < client_detailsArrayList.size(); i++) {
            if (client_detailsArrayList.get(i).getTime_info().equalsIgnoreCase("Today")) {
                if (first_time_today == 0) {
                    client_detailsArrayList.get(i).setTime_info_visible("1");
                    first_time_today = 1;
                } else {
                    client_detailsArrayList.get(i).setTime_info_visible("0");
                }
            }

            if (client_detailsArrayList.get(i).getTime_info().equalsIgnoreCase("yesterday")) {
                if (first_time_yesterday == 0) {
                    client_detailsArrayList.get(i).setTime_info_visible("1");
                    first_time_yesterday = 1;
                } else {
                    client_detailsArrayList.get(i).setTime_info_visible("0");
                }
            }

            if (client_detailsArrayList.get(i).getTime_info().equalsIgnoreCase("this week")) {
                if (first_time_week == 0) {
                    client_detailsArrayList.get(i).setTime_info_visible("1");
                    first_time_week = 1;
                } else {
                    client_detailsArrayList.get(i).setTime_info_visible("0");
                }
            }
            if (client_detailsArrayList.get(i).getTime_info().equalsIgnoreCase("this month")) {
                if (first_time_month == 0) {
                    client_detailsArrayList.get(i).setTime_info_visible("1");
                    first_time_month = 1;
                } else {
                    client_detailsArrayList.get(i).setTime_info_visible("0");
                }
            }
            if (client_detailsArrayList.get(i).getTime_info().equalsIgnoreCase("this year")) {
                if (first_time_year == 0) {
                    client_detailsArrayList.get(i).setTime_info_visible("1");
                    first_time_year = 1;
                } else {
                    client_detailsArrayList.get(i).setTime_info_visible("0");
                }
            }
            if (client_detailsArrayList.get(i).getTime_info().equalsIgnoreCase("Before this Year")) {
                if (first_before_year == 0) {
                    client_detailsArrayList.get(i).setTime_info_visible("1");
                    first_before_year = 1;
                } else {
                    client_detailsArrayList.get(i).setTime_info_visible("0");
                }
            }

        }

        adapter = new PatDetailsAdapter(context, client_detailsArrayList);
        mBinding.clientInfo.setAdapter(adapter);
        adapter.onClickDeleteListener(onClickDeleteListener);
        if (client_detailsArrayList.size() > 0) {
            tv_time_info_new.setText(client_detailsArrayList.get(0).getTime_info());
        }

    }

    public static Date createDate(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        Date d = c.getTime();
        return d;
    }

    @Override
    public void onResume() {
        super.onResume();
        setdata();
        DashboardActivity.visible_back();

    }

    OnClickDeleteListener onClickDeleteListener=new OnClickDeleteListener() {
        @Override
        public void OnClickDeleteListener(int pos) {
            if (cd.isConnectingToInternet()) {
                Log.e("Delete","11111 " +pos);
                new ExecuteTask_ForDeletePet(pos).execute();
            } else {
                cd.showToast(context, R.string.NO_INTERNET_CONNECTION);

            }
        }
    };


    class ExecuteTask_ForDeletePet extends AsyncTask<String, Integer, String> {

       private ExecuteTask_ForDeletePet(int Position){
           Log.e("Delete","^^^ " +Position);
            position=Position;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.GetData(WebService.CLIENT_PET_DELETE + Pref.getValue(context, "sub_pet_id", ""), Pref.getValue(context, "token", ""));
            Log.e("res....", "" + res);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                Log.e("CLientPet", result + "");
                JSONObject json;
                json = new JSONObject(result);
                JSONObject json2 = json.getJSONObject("success");
                if (json2.getString("status").equals("true")) {
                    if (json2.getString("response_code").equals("200")) {
                        client_detailsArrayList.remove(position);
                        adapter.notifyDataSetChanged();
                        set_adapter();
                        Toast.makeText(context, json2.getString("message"), Toast.LENGTH_LONG).show();

                    }
                } else {

                    Toast.makeText(context, json2.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
