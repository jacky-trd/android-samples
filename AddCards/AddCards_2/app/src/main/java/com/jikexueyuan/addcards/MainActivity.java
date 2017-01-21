package com.jikexueyuan.addcards;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * 本工程对应任务八作业一：用代码分4列5行添加20个数字
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //根布局
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        //设置行布局参数：各行的权重为1
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        lpRow.weight = 1;

        //设置Button布局参数：各个Button的边缘空白为5，权重为1
        final int margin = 5;
        LinearLayout.LayoutParams lpButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        lpButton.setMargins(margin, margin, margin, margin);
        lpButton.weight = 1;

        //四列五行添加红色按钮
        final int rows = 5;
        final int columns = 4;
        LinearLayout layoutRow = null;
        Button button = null;

        for (int i = 0; i < rows; i++) {
            layoutRow = new LinearLayout(this);
            layoutRow.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < columns; j++) {
                //定制Button
                button = makeButton(String.valueOf(i*columns+j+1));
                layoutRow.addView(button,lpButton);
            }
            root.addView(layoutRow,lpRow);
        }

        setContentView(root);
    }

    //定制Button
    private Button makeButton(String text){
        Button button = new Button(this);
        button.setBackgroundColor(Color.RED);
        button.setText(text);
        return button;
    }
}