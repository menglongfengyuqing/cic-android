package com.ztmg.cicmorgan.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by bcq on 2018/5/8.
 * If they throw stones at you, don’t throw back, use them to build your own foundation instead.
 */

public class BaseFragment extends Fragment {
    protected void onFragmentVisibleChange(boolean isVisible) {

    }

    public Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }
}
