package com.jlouistechnology.magnawave.Fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
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
import com.jlouistechnology.magnawave.Webservice_list.WebService;
import com.jlouistechnology.magnawave.utils.ConnectionDetector;
import com.jlouistechnology.magnawave.utils.Pref;
import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.databinding.FragmentSettingsBinding;

import org.json.JSONObject;

/**
 * Created by aipxperts on 9/5/17.
 */

public class SettingsFragment extends Fragment {

    FragmentSettingsBinding mBinding;
    View rootView;
    Context context;
    ConnectionDetector cd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         * Binding all xml views
         */
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_settings, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        cd=new ConnectionDetector(context);
        mBinding.txtAccount.setText(Pref.getValue(context,"email",""));
        mBinding.txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setMessage("Are you sure you want to logout?");
                // set positive button: Yes message
                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {

                        call_api_to_logout();

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
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Pref.setValue(context,"last_open_fragment","6");
        DashboardActivity.visible_menu();
    }


    public void call_api_to_logout()
    {
        if(cd.isConnectingToInternet()) {

            new ExecuteTask().execute(WebService.LOGOUT);
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

            String res = WebService.PostData(WebService.LOGOUT, Pref.getValue(context,"token",""));
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

                        ((FragmentActivity)context).finish();
                        Pref.deleteAll(context);
                        Intent intent=new Intent(context,LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(context,json2.getString("message"),Toast.LENGTH_LONG).show();
                    }


                }
                else {
                    if(json2.getString("response_code").equals("1000")) {

                        ((FragmentActivity)context).finish();
                        Intent intent=new Intent(context,LoginActivity.class);
                        startActivity(intent);
                    }
                    Toast.makeText(context,json2.getString("message"),Toast.LENGTH_LONG).show();
                    ((FragmentActivity)context).finish();
                    Intent intent=new Intent(context,LoginActivity.class);
                    startActivity(intent);
                    Pref.deleteAll(context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
