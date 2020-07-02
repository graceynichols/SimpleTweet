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
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    private static String TAG = "TweetsAdapter";
    private static int REQUEST_CODE = 30;
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
                    //TODO store tweets in memory
                    ((Activity) context).startActivityForResult(intent, REQUEST_CODE);

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
                    unusedView.getLayoutParams().height = 1;
                    unusedView.getLayoutParams().width = 1;
                    unusedView.setVisibility(View.GONE);
                }
            }
        }

        // Bind the stats for the tweet
        public void bindStats(Tweet tweet) {
            // TODO replies
            b.retweetCount.setText("" + tweet.getRetweet_count());
            b.favoriteCount.setText("" + tweet.getFavorite_count());

        }

        public void bindImages(Tweet tweet) {
            Log.i(TAG, "Binding images");
            int numImages = tweet.getExtendedEntities().getMediaList().size();
            int i;
            for (i = 0; i < numImages; i++) {
                ImageView imgView = imageViews.get(i);
                imgView.setVisibility(View.VISIBLE);
                final Media tweetImage = tweet.getExtendedEntities().getMediaList().get(i);
                // Load images in as clickable thumbnails
                imgView.getLayoutParams().height = IMAGE_SIZE;
                imgView.getLayoutParams().width = IMAGE_SIZE;
                Glide.with(context).load(tweetImage.getMediaUrlHttps()).circleCrop()
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
}
