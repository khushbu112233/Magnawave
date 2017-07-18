package com.jlouistechnology.magnawave.Fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jlouistechnology.magnawave.Activity.DashboardActivity;
import com.jlouistechnology.magnawave.Activity.LoginActivity;
import com.jlouistechnology.magnawave.Adapter.GuidelineAdapter;
import com.jlouistechnology.magnawave.Model.Client;
import com.jlouistechnology.magnawave.Model.GuideLine;
import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.Webservice_list.WebService;
import com.jlouistechnology.magnawave.databinding.FragmentGuidelineBinding;
import com.jlouistechnology.magnawave.databinding.FragmentSearchBinding;
import com.jlouistechnology.magnawave.utils.ConnectionDetector;
import com.jlouistechnology.magnawave.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aipxperts on 9/5/17.
 */

public class GuideLineFragment extends Fragment {

    FragmentGuidelineBinding mBinding;
    View rootView;
    Context context;
    ArrayList<GuideLine> guideLineArrayList;
    ConnectionDetector cd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         * Binding all xml views
         */

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_guideline, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        cd=new ConnectionDetector(context);
        guideLineArrayList=new ArrayList<>();
        call_api();



        return rootView;
    }

    public void setadapter()
    {
        GuidelineAdapter adapter=new GuidelineAdapter(context,guideLineArrayList);
        mBinding.listGuide.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Pref.setValue(getActivity(),"last_open_fragment","7");
        DashboardActivity.visible_menu();
    }

    public void call_api()
    {
        if(cd.isConnectingToInternet()) {

            new ExecuteTask().execute(WebService.GUIDELINECATEGORIES);
        }else
        {
            cd.showToast(context, R.string.NO_INTERNET_CONNECTION);

        }
    }

    class ExecuteTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.GetData(WebService.GUIDELINECATEGORIES, Pref.getValue(context,"token",""));
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
                        GuideLine[] guideLines=new GuideLine[jsonArray_data.length()];
                        guideLineArrayList.clear();
                        for(int i=0;i<jsonArray_data.length();i++)
                        {
                            guideLines[i]=new GuideLine();
                            guideLines[i].setId(jsonArray_data.getJSONObject(i).getString("id"));
                            guideLines[i].setName(jsonArray_data.getJSONObject(i).getString("name"));

                            guideLineArrayList.add(guideLines[i]);
                        }
                        setadapter();

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
