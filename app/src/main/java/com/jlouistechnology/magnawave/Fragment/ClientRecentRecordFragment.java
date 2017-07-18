package com.jlouistechnology.magnawave.Fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.jlouistechnology.magnawave.Activity.DashboardActivity;
import com.jlouistechnology.magnawave.Activity.LoginActivity;
import com.jlouistechnology.magnawave.Adapter.ClientAdapter;
import com.jlouistechnology.magnawave.Interface.OnClickFilterListener;
import com.jlouistechnology.magnawave.Model.Client;
import com.jlouistechnology.magnawave.Webservice_list.WebService;
import com.jlouistechnology.magnawave.utils.ConnectionDetector;
import com.jlouistechnology.magnawave.utils.Pref;
import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.databinding.FragmentClientsBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aipxperts on 9/5/17.
 */

public class ClientRecentRecordFragment extends Fragment {

    FragmentClientsBinding mBinding;
    View rootView;
    Context context;
    Filter filter;
    ConnectionDetector cd;
    public  ArrayList<Client> clientArrayList=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         * Binding all xml views
         */
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_clients, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        mBinding.txtnew.setVisibility(View.GONE);
        cd=new ConnectionDetector(context);
        clientArrayList=new ArrayList<>();
        clientArrayList.clear();
        int id = mBinding.search.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView)mBinding.search.findViewById(id);
        textView.setTextColor(Color.parseColor("#262626"));
        textView.setHintTextColor(Color.parseColor("#b3b3b3"));

        Pref.setValue(context,"fragment_to_adapter","Recentrecord");
        int searchPlateId = mBinding.search.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = mBinding.search.findViewById(searchPlateId);
        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(Color.parseColor("#ebebeb"));
        }
        SearchManager searchManager = (SearchManager)context.getSystemService(Context.SEARCH_SERVICE);
        mBinding.search.setIconifiedByDefault(false);
        call_api_to_get_client_list();
        mBinding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (TextUtils.isEmpty(newText)) {
                        if(filter!=null) {
                            filter.filter("");
                        }
                    } else {
                        if (filter != null) {
                            filter.filter(newText);
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return true;
            }
        });
        mBinding.search.setSubmitButtonEnabled(true);
        mBinding.txtnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewClientFragment fragment = new AddNewClientFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();

            }
        });
        return rootView;
    }


    public void set_adapter()
    {
        Pref.setValue(context,"rec_size",clientArrayList.size());
        if(clientArrayList.size()==0 || clientArrayList.size()==1) {
            mBinding.txtNoOfClient.setText(clientArrayList.size() + " CLIENT FOUND");
        }
        else {
            mBinding.txtNoOfClient.setText(clientArrayList.size() + " CLIENTS FOUND");
        }
        ClientAdapter adapter=new ClientAdapter(context,clientArrayList);
        mBinding.clientList.setAdapter(adapter);

        adapter.setOnClickFilterListener(onClickFilterListener);
        filter = adapter.getFilter();
    }

    @Override
    public void onResume() {
        super.onResume();
        Pref.setValue(context,"last_open_fragment","4");
        mBinding.search.setQuery("",false);
        DashboardActivity.visible_back();
    }
    OnClickFilterListener onClickFilterListener=new OnClickFilterListener() {
        @Override
        public void OnClickFilterListener(int pos) {
            if(pos==0 || pos==1) {
                mBinding.txtNoOfClient.setText(pos + " CLIENT FOUND");
            }
            else {
                mBinding.txtNoOfClient.setText(pos + " CLIENTS FOUND");
            }
        }
    };


    public void call_api_to_get_client_list()
    {
        if(cd.isConnectingToInternet()) {

            new ExecuteTask_account_type().execute(WebService.LISTRECENTRECORD);
        }else
        {
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

            String res = WebService.GetData(WebService.LISTRECENTRECORD+ Pref.getValue(context,"search_filter",""), Pref.getValue(context,"token",""));
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
                        JSONArray jsonArray_data=json2.getJSONArray("data");
                        Client[] clients=new Client[jsonArray_data.length()];
                        for(int i=0;i<jsonArray_data.length();i++)
                        {
                            clients[i]=new Client();
                            clients[i].setClient_id(jsonArray_data.getJSONObject(i).getString("client_id"));
                            clients[i].setFirst_name(jsonArray_data.getJSONObject(i).getString("fname"));
                            clients[i].setLast_name(jsonArray_data.getJSONObject(i).getString("lname"));
                            clients[i].setCompany(jsonArray_data.getJSONObject(i).getString("company"));
                            clients[i].setEmail(jsonArray_data.getJSONObject(i).getString("email"));
                            clients[i].setPhone_no(jsonArray_data.getJSONObject(i).getString("contact"));
                            clients[i].setImage(jsonArray_data.getJSONObject(i).getString("profile"));
                            clientArrayList.add(clients[i]);
                        }
                        set_adapter();

                    }


                }
                else {
                    if(json2.getString("response_code").equals("1000")) {

                        ((FragmentActivity)context).finish();
                        Intent intent=new Intent(context,LoginActivity.class);
                        startActivity(intent);
                    }
                    Toast.makeText(context,json2.getString("message"),Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
