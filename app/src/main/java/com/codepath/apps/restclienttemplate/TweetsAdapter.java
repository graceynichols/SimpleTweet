package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.activity.ImageActivity;
import com.codepath.apps.restclienttemplate.activity.TweetDetailsActivity;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    private static String TAG = "TweetsAdapter";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private int IMAGE_SIZE = 225;
    private Context context;
    private List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // For each row, inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemTweetBinding binding = ItemTweetBinding.inflate(inflater);
        return new ViewHolder(binding);
    }

    // Bind values based on position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet at the viewholder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clear all elements in data set
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items to data set
    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {
        List<ImageView> imageViews;
        ItemTweetBinding b;

        public ViewHolder(ItemTweetBinding binding) {
            super(binding.getRoot());
            b = binding;
            imageViews = new ArrayList<>();
            imageViews.add(binding.image1);
            imageViews.add(binding.image2);
            imageViews.add(binding.image3);
            imageViews.add(binding.image4);
        }

        public void bind(final Tweet tweet) {
            Log.i(TAG, "In bind");

            // Set on click listener for tweet detail view
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Image thumbnail clicked");
                    // Launch DetailsActivity
                    Intent intent = new Intent(context, TweetDetailsActivity.class);
                    // Serialize the tweet using parceler
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    //context.startActivity(intent);
                    context.startActivity(intent);
                }
            });

            // Check if retweet/favorite icons need to be filled in or not
            if (tweet.isRetweeted()) {
                b.retweet.setImageResource(R.drawable.ic_vector_retweet);
            } else {
                b.retweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            }
            if (tweet.isFavorited()) {
                b.like.setImageResource(R.drawable.ic_vector_heart);
            } else {
                b.like.setImageResource(R.drawable.ic_vector_heart_stroke);
            }

            // Set all text information
            bindStats(tweet);
            b.tvBody.setText(tweet.getBody());
            String username = context.getString(R.string.at) + tweet.getUser().getScreenName();
            b.tvScreenName.setText(username);
            b.tvTime.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
            b.tvName.setText(tweet.getUser().getName());

            // Load profile pic
            Glide.with(context).load(tweet.getUser().getProfileImageUrl()).circleCrop().into(b.ivProfileImage);

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
            b.retweetCount.setText(retweets);
            b.favoriteCount.setText(likes);
        }

        public void bindImages(Tweet tweet) {
            Log.i(TAG, "Binding images");
            int numImages = tweet.getMediaUrlArray().size();
            int i;
            for (i = 0; i < numImages; i++) {
                ImageView imgView = imageViews.get(i);
                imgView.setVisibility(View.VISIBLE);
                final String tweetImage = tweet.getMediaUrlArray().get(i);
                // Load images in as clickable thumbnails
                imgView.getLayoutParams().height = IMAGE_SIZE;
                imgView.getLayoutParams().width = IMAGE_SIZE;
                Glide.with(context).load(tweetImage).circleCrop()
                        .into(imgView);
                // Attach on click listener to thumbnail
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
            try {
                long time = sf.parse(rawJsonDate).getTime();
                long now = System.currentTimeMillis();

                final long diff = now - time;
                if (diff < MINUTE_MILLIS) {
                    return "just now";
                } else if (diff < 2 * MINUTE_MILLIS) {
                    return "a minute ago";
                } else if (diff < 50 * MINUTE_MILLIS) {
                    return diff / MINUTE_MILLIS + " m";
                } else if (diff < 90 * MINUTE_MILLIS) {
                    return "an hour ago";
                } else if (diff < 24 * HOUR_MILLIS) {
                    return diff / HOUR_MILLIS + " h";
                } else if (diff < 48 * HOUR_MILLIS) {
                    return "yesterday";
                } else {
                    return diff / DAY_MILLIS + " d";
                }
            } catch (ParseException e) {
                Log.i(TAG, "getRelativeTimeAgo failed");
                e.printStackTrace();
            }
            return "";
        }
    }
}
