package com.jlouistechnology.magnawave.Fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jlouistechnology.magnawave.Activity.DashboardActivity;
import com.jlouistechnology.magnawave.Activity.LoginActivity;
import com.jlouistechnology.magnawave.utils.Pref;
import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.databinding.FragmentHomeBinding;

/**
 * Created by aipxperts on 9/5/17.
 */

public class HomeFragment extends Fragment {

    FragmentHomeBinding mBinding;
    View rootView;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         * Binding all xml views
         */
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_home, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        mBinding.txtHome.setText("GOOD MORNING "+ Pref.getValue(context,"name","").toUpperCase()+" !");

        mBinding.imgAddNewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewRecordFragment fragment = new AddNewRecordFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();
            }
        });

        mBinding.imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment fragment = new SearchFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();
            }
        });

        mBinding.imgClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientFragment fragment = new ClientFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();
            }
        });

        mBinding.imgViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientFragment fragment = new ClientFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Pref.setValue(context,"last_open_fragment","1");
        DashboardActivity.visible_menu();

    }

    public static void find_x_y_according_to_the_screen(final Context context, final View rootView, final int x, final int width_other_screen, final ImageView img, final int y, final int height_other_screen, final FrameLayout frameLayout, final String count_disp, final String is_from_add_screen)
    {

        ViewTreeObserver vto =img.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
              img.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                // img.getViewTreeObserver().removeGlobalOnLayoutListener((ViewTreeObserver.OnGlobalLayoutListener) context);

                int  x_new=0;
                int x1=0;
                if(rootView.getWidth()!=0 && width_other_screen!=0) {
                x_new = ((rootView.getWidth()) * x) / width_other_screen;
                    x1 = (((int) img.getRight() - (int) img.getLeft()) * x_new) / (rootView.getWidth());
                    Log.e("x1", ((int) img.getRight() + " " + (int) img.getLeft()) + " " + x_new + " " + rootView.getWidth() + " x1:" + x1 + " " + rootView.getHeight() + " " + x + " " + y);

                }
                int y_new = 0;
                int y1=0;
                if(rootView.getHeight()!=0 && height_other_screen!=0) {

                    y_new = ((rootView.getHeight()) * y) / height_other_screen;

                    y1 = (((int) img.getBottom()) * y_new) / (rootView.getHeight());
                }
                float inPixels=0f;
                if(is_from_add_screen.equals("true")) {

                    if(rootView.getWidth()>1500)
                    {
                        inPixels = context.getResources().getDimension(R.dimen.count_size_very_large);
                    }
                    else {
                        inPixels = context.getResources().getDimension(R.dimen.count_size);
                    }


                }
                else {
                   inPixels = context.getResources().getDimension(R.dimen.count_size_small);
                }
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int)inPixels,(int)inPixels);
                TextView iv = new TextView(context);
                if(is_from_add_screen.equals("true"))
                {
                    lp.setMargins(x_new, y_new, 0, 0);
                }
                else {
                    lp.setMargins(x1, y1, 0, 0);
                }
                iv.setLayoutParams(lp);
                iv.setText(count_disp);
                iv.setGravity(Gravity.CENTER);
                iv.setTextColor(Color.parseColor("#ffffff"));
                iv.setBackground(context.getResources().getDrawable(
                        R.drawable.circle_white_border));
                if(iv.getParent()!=null)
                    ((ViewGroup)iv.getParent()).removeView(iv); // <- fix
                frameLayout.addView(iv);

            }
        });


    }





}
