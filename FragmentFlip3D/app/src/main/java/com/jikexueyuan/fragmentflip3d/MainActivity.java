package com.jikexueyuan.fragmentflip3d;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 任务九作业三：用3D翻转的效果切换Fragment
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //载入FragmentOne
        getFragmentManager().beginTransaction().add(R.id.root_view,new FragmentOne()).commit();
    }
}