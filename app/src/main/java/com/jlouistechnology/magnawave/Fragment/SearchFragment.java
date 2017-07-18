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
import com.jlouistechnology.magnawave.databinding.FragmentSearchBinding;

/**
 * Created by aipxperts on 9/5/17.
 */

public class SearchFragment extends Fragment {

    FragmentSearchBinding mBinding;
    View rootView;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         * Binding all xml views
         */

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_search, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        mBinding.txtRecentClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pref.setValue(context,"search_filter","client");
                ClientRecentRecordFragment fragment = new ClientRecentRecordFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();

            }
        });
        mBinding.txtRecentRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pref.setValue(context,"search_filter","record");
                ClientRecentRecordFragment fragment = new ClientRecentRecordFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();

            }
        });
        mBinding.txtAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientFragment_Search fragment = new ClientFragment_Search();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Pref.setValue(context,"last_open_fragment","3");
        DashboardActivity.visible_menu();
    }
}
