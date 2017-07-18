package com.jlouistechnology.magnawave.Fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jlouistechnology.magnawave.Activity.DashboardActivity;
import com.jlouistechnology.magnawave.utils.Pref;
import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.databinding.FragmentAddNewRecordBinding;

/**
 * Created by aipxperts on 9/5/17.
 */

public class AddNewRecordFragment extends Fragment {

    FragmentAddNewRecordBinding mBinding;
    View rootView;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         * Binding all xml views
         */

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_new_record, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();

        mBinding.imgHourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientAddNewPatFragment fragment = new ClientAddNewPatFragment();
                Pref.setValue(context,"selected_animal","horse");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();

            }
        });
        mBinding.imgDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientAddNewPatFragment fragment = new ClientAddNewPatFragment();
                Pref.setValue(context,"selected_animal","dog");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();

            }
        });

        mBinding.imgCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientAddNewPatFragment fragment = new ClientAddNewPatFragment();
                Pref.setValue(context,"selected_animal","cat");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();

            }
        });

        mBinding.imgGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientAddNewPatFragment fragment = new ClientAddNewPatFragment();
                Pref.setValue(context,"selected_animal","general");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();

            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Pref.setValue(context,"last_open_fragment","2");
        DashboardActivity.visible_menu();
    }
}
