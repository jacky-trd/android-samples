package com.jikexueyuan.simpleimagebrowser;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageBrowserActivity extends AppCompatActivity {

    //用于显示image的ImageView
    private ImageView ivShowImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browser);

        //获取Uri
        Uri imageUri = getIntent().getData();
        //显示图片
        ivShowImage = (ImageView) findViewById(R.id.ivDownloadImage);
        ivShowImage.setImageURI(imageUri);
    }
}