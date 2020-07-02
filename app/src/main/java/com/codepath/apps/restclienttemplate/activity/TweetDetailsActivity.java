package com.codepath.apps.restclienttemplate.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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
    private Context context = this;
    private List<ImageView> imageViews;

    TwitterClient client;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        client = TwitterApp.getRestClient(this);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
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
                    client.unRetweet(tweet.getIdLong(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Unretweet successful");
                            // Make retweet not bold and decrease retweet count
                            updateCount(true, false, tweet);
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "Unretweet failed", throwable);
                        }
                    });
                } else {
                    client.retweet(tweet.getIdLong(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Retweet successful");
                            // Make retweet bold and increase retweet count
                            updateCount(true, true, tweet);
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
                    client.unFavorite(tweet.getIdLong(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "UnFavorite successful");
                            // Unfill heart button and decrease count
                            updateCount(false, false, tweet);
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "UnFavorite failed", throwable);
                        }
                    });
                } else {
                    client.favorite(tweet.getIdLong(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Favorite successful");
                            // Fill in heart button and increase count
                            updateCount(false, true, tweet);
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
        }
    }

    // Bind the stats for the tweet
    public void bindStats(Tweet tweet) {
        String retweets = "" + tweet.getRetweet_count();
        String likes = "" + tweet.getFavorite_count();
        binding.retweetCount.setText(retweets);
        binding.favoriteCount.setText(likes);
    }

    public void updateCount(boolean isRetweet, boolean pos, Tweet tweet) {
        if (isRetweet) {
            if (pos) {
                // Tweet was retweeted
                binding.retweet.setImageResource(R.drawable.ic_vector_retweet);
                tweet.addOneRetweet();
                binding.retweetCount.setText("" + tweet.getRetweet_count());
                tweet.setRetweeted(true);
            } else {
                // Tweet was unretweeted
                binding.retweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                tweet.subOneRetweet();
                binding.retweetCount.setText("" + tweet.getRetweet_count());
                tweet.setRetweeted(false);
            }
        } else {
            if (pos) {
                // Tweet was favorited
                binding.like.setImageResource(R.drawable.ic_vector_heart);
                tweet.addOneFavorite();
                binding.favoriteCount.setText("" + tweet.getFavorite_count());
                tweet.setFavorited(true);

            } else {
                // Tweet was unfavorited

                binding.like.setImageResource(R.drawable.ic_vector_heart_stroke);
                tweet.subOneFavorite();
                binding.favoriteCount.setText("" + tweet.getFavorite_count());
                tweet.setFavorited(false);
            }
        }
    }

    public void bindImages(Tweet tweet) {
        Log.i(TAG, "Binding images");
        int numImages = tweet.getMediaUrlArray().size();
        int i;
        for (i = 0; i < numImages; i++) {
            ImageView imgView = imageViews.get(i);
            final String tweetImage = tweet.getMediaUrlArray().get(i);
            // Make image view visible
            imgView.setVisibility(View.VISIBLE);
            imgView.getLayoutParams().height = IMAGE_SIZE;
            imgView.getLayoutParams().width = IMAGE_SIZE;
            Glide.with(this).load(tweetImage).circleCrop()
                    .into(imgView);
            // Attach on click listener to thumbnail for ImageActivity
            imgView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Image thumbnail clicked");
                    // Launch ImageActivity
                    Intent intent = new Intent(context, ImageActivity.class);
                    // Serialize the media using parceler
                    intent.putExtra(String.class.getSimpleName(), Parcels.wrap(tweetImage));
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
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }
}