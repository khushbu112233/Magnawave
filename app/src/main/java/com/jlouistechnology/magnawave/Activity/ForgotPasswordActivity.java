package com.jlouistechnology.magnawave.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.Webservice_list.WebService;
import com.jlouistechnology.magnawave.databinding.ForgotPasswordLayoutBinding;
import com.jlouistechnology.magnawave.utils.ConnectionDetector;
import com.jlouistechnology.magnawave.utils.Constants;
import com.jlouistechnology.magnawave.utils.Pref;

import org.json.JSONObject;

/**
 * Created by aipxperts-ubuntu-01 on 5/6/17.
 */

public class ForgotPasswordActivity extends Activity {
    ConnectionDetector cd;
    ForgotPasswordLayoutBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.forgot_password_layout);
        cd = new ConnectionDetector(ForgotPasswordActivity.this);
        StatusBar();

        set_material_design();
        mBinding.txtdone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                int err_cnt=0;

                if(mBinding.edtEmail.getText().toString().trim().isEmpty())
                {
                    mBinding.edtEmail.setError(getString(R.string.email_is_required));
                    err_cnt++;
                }
                else {

                    if (!Patterns.EMAIL_ADDRESS.matcher(
                            mBinding.edtEmail.getText().toString()).matches()) {
                        mBinding.edtEmail.setError(getString(R.string.invalid_email));
                        err_cnt++;
                    }
                }
                if(err_cnt==0) {
                    if(cd.isConnectingToInternet()) {

                        new ExecuteTask().execute(mBinding.edtEmail.getText().toString());
                    }else
                    {
                        cd.showToast(ForgotPasswordActivity.this, R.string.NO_INTERNET_CONNECTION);

                    }
                }

            }
        });
    }
    public  void StatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

    }
    public void set_material_design()
    {
        Constants.setTextWatcher(ForgotPasswordActivity.this, mBinding.edtEmail,mBinding.txtEmail);
    }
    class ExecuteTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(ForgotPasswordActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.Forgot_param, WebService.FORGOT);
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
                        Toast.makeText(ForgotPasswordActivity.this,json2.getString("message"),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }


                }
                else {
                    Toast.makeText(ForgotPasswordActivity.this,json2.getString("message"),Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
