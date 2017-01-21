package com.jikexueyuan.fragmentflip3d;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentOne extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_one,null);

        //监听按钮点击事件
        root.findViewById(R.id.btnPositiveSide).setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View view) {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.animator_two_enter,R.animator.animator_one_exit)
                .replace(R.id.root_view,new FragmentTwo())
                .commit();
    }
}
