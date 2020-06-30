package com.codepath.apps.restclienttemplate.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Media;

import org.parceler.Parcels;

public class ImageActivity extends AppCompatActivity {
    private static String TAG = "ImageActivity";
    Media tweetImage;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imgView = findViewById(R.id.imageView);
        Log.i(TAG, "Activity started");
        tweetImage = (Media) Parcels.unwrap(getIntent().getParcelableExtra(Media.class.getSimpleName()));
        Glide.with(this).load(tweetImage.getMediaUrlHttps())
                .into(imgView);

    }
}