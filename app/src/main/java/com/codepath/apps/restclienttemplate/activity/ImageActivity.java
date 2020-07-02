package com.codepath.apps.restclienttemplate.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityImageBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Media;

import org.parceler.Parcels;

public class ImageActivity extends AppCompatActivity {
    private static String TAG = "ImageActivity";
    private static ActivityImageBinding binding;
    String tweetImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Log.i(TAG, "Activity started");
        // Receive image URL from timeline
        tweetImage = Parcels.unwrap(getIntent().getParcelableExtra(String.class.getSimpleName()));
        Glide.with(this).load(tweetImage)
                .into(binding.imageView);
    }
}