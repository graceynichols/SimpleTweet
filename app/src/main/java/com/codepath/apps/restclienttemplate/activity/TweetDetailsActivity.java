package com.codepath.apps.restclienttemplate.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {
    private static String TAG = "TweetDetailsActivity";
    private static ActivityTweetDetailsBinding binding;
    private int IMAGE_SIZE = 225;
    TwitterClient client;
    Context context = this;
    Tweet tweet;
    List<ImageView> imageViews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        client = TwitterApp.getRestClient(this);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        imageViews = new ArrayList<>();
        imageViews.add(binding.image1);
        imageViews.add(binding.image2);
        imageViews.add(binding.image3);
        imageViews.add(binding.image4);

        // Set all text information
        bindStats(tweet);
        binding.tvBody.setText(tweet.getBody());
        String username = this.getString(R.string.at) + tweet.getUser().getScreenName();
        binding.tvScreenName.setText(username);
        binding.tvTime.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
        binding.tvName.setText(tweet.getUser().getName());

        // Check if retweet/favorite icons need to be filled in
        if (tweet.isRetweeted()) {
            binding.retweet.setImageResource(R.drawable.ic_vector_retweet);
        }
        if (tweet.isFavorited()) {
            binding.like.setImageResource(R.drawable.ic_vector_heart);
        }

        // Load profile pic
        Glide.with(this).load(tweet.getUser().getProfileImageUrl()).circleCrop().into(binding.ivProfileImage);

        // Set on click listeners for retweet and favorite buttons
        binding.retweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tweet.isRetweeted()) {
                    client.unRetweet(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Unretweet successful");
                            // Make retweet bold and increase retweet count
                            // TODO externalize repeated code
                            binding.retweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                            tweet.subOneRetweet();
                            binding.retweetCount.setText("" + tweet.getRetweet_count());
                            tweet.toggleRetweeted();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "Unretweet failed", throwable);
                        }
                    });
                } else {
                    client.retweet(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Retweet successful");
                            // Make retweet bold and increase retweet count
                            binding.retweet.setImageResource(R.drawable.ic_vector_retweet);
                            tweet.addOneRetweet();
                            binding.retweetCount.setText("" + tweet.getRetweet_count());
                            tweet.toggleRetweeted();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "Retweet failed", throwable);
                        }
                    });
                }

            }
        });

        binding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tweet.isFavorited()) {
                    client.unFavorite(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "UnFavorite successful");
                            // Fill in heart button
                            binding.like.setImageResource(R.drawable.ic_vector_heart_stroke);
                            tweet.subOneFavorite();
                            binding.favoriteCount.setText("" + tweet.getFavorite_count());
                            tweet.toggleFavorited();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "UnFavorite failed", throwable);
                        }
                    });
                } else {
                    client.favorite(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Favorite successful");
                            // Fill in heart button
                            binding.like.setImageResource(R.drawable.ic_vector_heart);
                            tweet.addOneFavorite();
                            binding.favoriteCount.setText("" + tweet.getFavorite_count());
                            tweet.toggleFavorited();

                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "Favorite failed", throwable);
                        }
                    });

                }

            }
        });

        // Check if there are images to be added
        if (tweet.isExtendedEntitiesFlag()) {
            Log.i(TAG, "Tweet has extended entities");
            bindImages(tweet);
        } else {
            for (int j = 0; j <= 3; j++) {
                // Get rid of un needed imageViews
                ImageView unusedView = imageViews.get(j);
                unusedView.setVisibility(View.GONE);
            }
        }
    }

    // Bind the stats for the tweet
    public void bindStats(Tweet tweet) {
        String retweets = "" + tweet.getRetweet_count();
        String likes = "" + tweet.getFavorite_count();
        binding.retweetCount.setText(retweets);
        binding.favoriteCount.setText(likes);

    }

    public void bindImages(Tweet tweet) {
        Log.i(TAG, "Binding images");
        int numImages = tweet.getExtendedEntities().getMediaList().size();
        int i;
        for (i = 0; i < numImages; i++) {
            ImageView imgView = imageViews.get(i);
            final Media tweetImage = tweet.getExtendedEntities().getMediaList().get(i);
            imgView.setVisibility(View.VISIBLE);
            imgView.getLayoutParams().height = IMAGE_SIZE;
            imgView.getLayoutParams().width = IMAGE_SIZE;
            Glide.with(this).load(tweetImage.getMediaUrlHttps()).circleCrop()
                    .into(imgView);
            // Attach on click listener to thumbnail
            imgView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Image thumbnail clicked");
                    // Launch ImageActivity
                    Intent intent = new Intent(context, ImageActivity.class);
                    // Serialize the media using parceler
                    intent.putExtra(Media.class.getSimpleName(), Parcels.wrap(tweetImage));
                    context.startActivity(intent);

                }
            });

        }
    }

    // Convert created_at time to relative time
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            // TODO change min to just m
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}