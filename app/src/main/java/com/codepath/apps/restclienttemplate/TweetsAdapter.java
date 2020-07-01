package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.activity.ImageActivity;
import com.codepath.apps.restclienttemplate.activity.TweetDetailsActivity;
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
    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            favoriteCount = itemView.findViewById(R.id.favoriteCount);
            retweetCount = itemView.findViewById(R.id.retweetCount);
            image1 = itemView.findViewById(R.id.image1);
            image2 = itemView.findViewById(R.id.image2);
            image3 = itemView.findViewById(R.id.image3);
            image4 = itemView.findViewById(R.id.image4);
            imageViews = new ArrayList<>();
            imageViews.add(image1);
            imageViews.add(image2);
            imageViews.add(image3);
            imageViews.add(image4);

        }

        public void bind(final Tweet tweet) {
            Log.i(TAG, "In bind");

            // Set on click listener for tweet
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Image thumbnail clicked");
                    // Launch DetailsActivity
                    Intent intent = new Intent(context, TweetDetailsActivity.class);
                    // Serialize the tweet using parceler
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    context.startActivity(intent);

                }
            });
            // Set all text information
            bindStats(tweet);
            tvBody.setText(tweet.getBody());
            String username = context.getString(R.string.at) + tweet.getUser().getScreenName();
            tvScreenName.setText(username);
            tvTime.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
            tvName.setText(tweet.getUser().getName());

            // Load profile pic
            Glide.with(context).load(tweet.getUser().getProfileImageUrl()).circleCrop().into(ivProfileImage);

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
                Glide.with(context).load(tweetImage.getMediaUrlHttps()).circleCrop()
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
}
