package com.jlouistechnology.magnawave.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.tool.util.L;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.jlouistechnology.magnawave.Webservice_list.WebService;
import com.jlouistechnology.magnawave.utils.ConnectionDetector;
import com.jlouistechnology.magnawave.utils.Constants;
import com.jlouistechnology.magnawave.utils.Pref;
import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.databinding.FragmentLoginBinding;

import org.json.JSONObject;


/**
 * Created by aipxperts on 9/5/17.
 */

public class LoginActivity extends Activity {

    FragmentLoginBinding mBinding;
    ConnectionDetector cd;
    View rootView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.fragment_login);
        cd=new ConnectionDetector(LoginActivity.this);
        StatusBar();
        set_material_design();
         mBinding.txtlogin.setOnClickListener(new View.OnClickListener() {
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

                if(mBinding.edtPassword.getText().toString().trim().isEmpty())
                {
                    mBinding.edtPassword.setError(getString(R.string.password_is_required));

                    err_cnt++;
                }

                if(err_cnt==0) {
                    if(cd.isConnectingToInternet()) {

                        new ExecuteTask().execute(mBinding.edtEmail.getText().toString(), mBinding.edtPassword.getText().toString());
                    }else
                    {
                        cd.showToast(LoginActivity.this, R.string.NO_INTERNET_CONNECTION);

                    }
                }

            }
        });
        mBinding.txtforgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void set_material_design()
    {
        Constants.setTextWatcher(LoginActivity.this, mBinding.edtEmail,mBinding.txtEmail);
        Constants.setTextWatcher(LoginActivity.this,mBinding.edtPassword,mBinding.txtPassword);

    }
    public  void StatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

    }


    class ExecuteTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(LoginActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.Login_param, WebService.LOGIN);
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
                        JSONObject jsonObject_data=json2.getJSONObject("data");
                        String name=jsonObject_data.getString("name");
                        Log.e("name",name+"");
                        Pref.setValue(LoginActivity.this,"name",name);
                        Pref.setValue(LoginActivity.this,"email",mBinding.edtEmail.getText().toString());
                        String token=json2.getString("token");
                        Log.e("token",token+"");
                        Pref.setValue(LoginActivity.this,"token",token);
                        Toast.makeText(LoginActivity.this,json2.getString("message"),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }


                }
                else {
                    Toast.makeText(LoginActivity.this,json2.getString("message"),Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();

    }
}
