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
    TwitterClient client;
    Context context = this;
    Tweet tweet;
    ImageView ivProfileImage;
    TextView tvBody;
    TextView tvScreenName;
    TextView tvTime;
    TextView tvName;
    TextView favoriteCount;
    TextView retweetCount;
    List<ImageView> imageViews;
    ImageView image1;
    ImageView image2;
    ImageView image3;
    ImageView image4;
    ImageView retweet;
    ImageView like;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        client = TwitterApp.getRestClient(this);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvBody = findViewById(R.id.tvBody);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvName = findViewById(R.id.tvName);
        tvTime = findViewById(R.id.tvTime);
        favoriteCount = findViewById(R.id.favoriteCount);
        retweetCount = findViewById(R.id.retweetCount);
        retweet = findViewById(R.id.retweet);
        like = findViewById(R.id.like);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        imageViews = new ArrayList<>();
        imageViews.add(image1);
        imageViews.add(image2);
        imageViews.add(image3);
        imageViews.add(image4);

        // Set all text information
        bindStats(tweet);
        tvBody.setText(tweet.getBody());
        String username = this.getString(R.string.at) + tweet.getUser().getScreenName();
        tvScreenName.setText(username);
        tvTime.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
        tvName.setText(tweet.getUser().getName());

        // Load profile pic
        Glide.with(this).load(tweet.getUser().getProfileImageUrl()).circleCrop().into(ivProfileImage);

        // Set on click listeners for retweet and favorite buttons
        retweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.retweet(tweet.getId(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "Retweet successful");
                        // Make retweet bold and increase retweet count
                        retweet.setImageResource(R.drawable.ic_vector_retweet);
                        tweet.addOneRetweet();
                        retweetCount.setText(tweet.getRetweet_count() + 1);
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Retweet failed", throwable);
                    }
                });
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.favorite(tweet.getId(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "Favorite successful");
                        // Fill in heart button
                        like.setImageResource(R.drawable.ic_vector_heart);
                        tweet.addOneFavorite();
                        favoriteCount.setText(tweet.getFavorite_count() + 1);
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Favorite failed", throwable);
                    }
                });

            }
        });

        // Check if there are images to be added
        if (tweet.isExtendedEntitiesFlag()) {
            Log.i(TAG, "Tweet has extended entities");
            bindImages(tweet);
            return;
        } else {
            for (int j = 0; j <= 3; j++) {
                // Get rid of un needed imageViews
                ImageView unusedView = imageViews.get(j);
                unusedView.getLayoutParams().height = 1;
                unusedView.getLayoutParams().width = 1;
                unusedView.setVisibility(View.GONE);
            }
            return;
        }


    }

    // Bind the stats for the tweet
    public void bindStats(Tweet tweet) {
        // TODO replies
        retweetCount.setText("" + tweet.getRetweet_count());
        favoriteCount.setText("" + tweet.getFavorite_count());

    }

    public void bindImages(Tweet tweet) {
        Log.i(TAG, "Binding images");
        int numImages = tweet.getExtendedEntities().getMediaList().size();
        int i;
        for (i = 0; i < numImages; i++) {
            ImageView imgView = imageViews.get(i);
            final Media tweetImage = tweet.getExtendedEntities().getMediaList().get(i);
            // TODO: probably should be cropped to thumbnail
            //imgView.getLayoutParams().height = tweetImage.getHeight();
            //imgView.getLayoutParams().width = tweetImage.getWidth();
            imgView.getLayoutParams().height = 225;
            imgView.getLayoutParams().width = 225;
            Glide.with(this).load(tweetImage.getMediaUrlHttps()).circleCrop()
                    .into(imgView);
            // Attach on click listener to thumbnail
            imgView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Image thumbnail clicked");
                    // Launch ImageActivity
                    Intent intent = new Intent(context, ImageActivity.class);
                    // Serialize the movie using parceler
                    intent.putExtra(Media.class.getSimpleName(), Parcels.wrap(tweetImage));
                    context.startActivity(intent);

                }
            });

        }
        Log.d(TAG, "Unused Images " + i);
        for (int j = i; j <= 3; j++) {
            // Get rid of un needed imageViews
            ImageView unusedView = imageViews.get(j);
            unusedView.getLayoutParams().height = 1;
            unusedView.getLayoutParams().width = 1;
            unusedView.setVisibility(View.GONE);
        }
        return;

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