package com.jlouistechnology.magnawave.Activity;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.jlouistechnology.magnawave.databinding.ActivityMainBinding;
import com.jlouistechnology.magnawave.utils.Pref;
import com.jlouistechnology.magnawave.R;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    ActivityMainBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        StatusBar();

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                mBinding.loginSignup.setVisibility(View.GONE);
           /*     if(Pref.getValue(MainActivity.this,"token","").equals("")) {
                    mBinding.txtlogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            // close this activity
                            finish();
                        }
                    });
                    mBinding.txtSignUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                            startActivity(intent);
                            // close this activity
                            finish();
                        }
                    });
                }else
                {
                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    // close this activity
                    finish();
                }*/
                // This method will be executed once the timer is over
                // Start your app main activity


                 if(Pref.getValue(MainActivity.this,"token","").equals("")) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    // close this activity
                    finish();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    // close this activity
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }

    public  void StatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

    }
    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
