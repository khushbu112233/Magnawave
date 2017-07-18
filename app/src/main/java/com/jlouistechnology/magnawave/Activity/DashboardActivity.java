package com.jlouistechnology.magnawave.Activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.jlouistechnology.magnawave.Fragment.AddNewRecordFragment;
import com.jlouistechnology.magnawave.Fragment.ClientFragment;
import com.jlouistechnology.magnawave.Fragment.GuideLineFragment;
import com.jlouistechnology.magnawave.Fragment.HomeFragment;
import com.jlouistechnology.magnawave.Fragment.SearchFragment;
import com.jlouistechnology.magnawave.Fragment.SettingsFragment;
import com.jlouistechnology.magnawave.utils.Pref;
import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.databinding.ActivityDashboardNewBinding;

public class DashboardActivity extends FragmentActivity {

    ActivityDashboardNewBinding mBinding;
    int is_slider_open=0;
    public static ImageView img_back;
    public static ImageView img_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard_new);
        img_back=(ImageView)findViewById(R.id.img_back);
        img_menu=(ImageView)findViewById(R.id.img_menu);
        StatusBar();
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_slider_open==0) {
                    img_menu.setImageResource(R.mipmap.cancel);
                    mBinding.swipeLayout.setVisibility(View.VISIBLE);
                    expand(mBinding.swipeLayout);
                    is_slider_open=1;
                }
                else {
                    img_menu.setImageResource(R.mipmap.menu);
                   // mBinding.swipeLayout.setVisibility(View.GONE);
                    collapse(mBinding.swipeLayout);
                    is_slider_open=0;
                }
            }
        });

       img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().popBackStack();
            }
        });

        mBinding.txtHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidemenu();
                if(!Pref.getValue(DashboardActivity.this,"last_open_fragment","").equals("1")) {
                    HomeFragment fragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();
                }

            }
        });

        mBinding.txtAddRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidemenu();
                if(!Pref.getValue(DashboardActivity.this,"last_open_fragment","").equals("2")) {
                    AddNewRecordFragment fragment = new AddNewRecordFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();
                }

            }
        });

        mBinding.txtViewReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidemenu();
                if(!Pref.getValue(DashboardActivity.this,"last_open_fragment","").equals("4")) {
                    ClientFragment fragment = new ClientFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();
                }
            }
        });


        mBinding.txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidemenu();
                if(!Pref.getValue(DashboardActivity.this,"last_open_fragment","").equals("3")) {
                    SearchFragment fragment = new SearchFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();
                }

            }
        });

        mBinding.txtSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidemenu();
                if(!Pref.getValue(DashboardActivity.this,"last_open_fragment","").equals("6")) {
                    SettingsFragment fragment = new SettingsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();
                }

            }
        });

        mBinding.txtGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidemenu();
                if(!Pref.getValue(DashboardActivity.this,"last_open_fragment","").equals("7")) {
                    GuideLineFragment fragment = new GuideLineFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();
                }

            }
        });
        mBinding.txtClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidemenu();
                if(!Pref.getValue(DashboardActivity.this,"last_open_fragment","").equals("4")) {
                    ClientFragment fragment = new ClientFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();
                }

            }
        });
        mBinding.txtTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidemenu();
            }
        });
        HomeFragment fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();

    }
    public static void expand(final View v) {
        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? WindowManager.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void hidemenu()
    {
        img_menu.setImageResource(R.mipmap.menu);
        // mBinding.swipeLayout.setVisibility(View.GONE);
        collapse(mBinding.swipeLayout);
        is_slider_open=0;
    }
    public  void StatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    public static void visible_menu()
    {
        img_back.setVisibility(View.GONE);
        img_menu.setVisibility(View.VISIBLE);
    }

    public static void visible_back()
    {
       img_back.setVisibility(View.VISIBLE);
        img_menu.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hidemenu();
    }
}
