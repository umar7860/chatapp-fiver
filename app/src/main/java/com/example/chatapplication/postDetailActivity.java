package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class postDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        TextView textView = findViewById(R.id.tv_Message);
        ImageView imageView = findViewById(R.id.tv_image);
        Bundle bundle = getIntent().getExtras();
        textView.append(bundle.getString("message"));
        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("Image");
        imageView.setImageBitmap(bitmap);
    }
}