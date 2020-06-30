package com.codepath.apps.restclienttemplate;

import android.content.Context;
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
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    public static String TAG = "TweetsAdapter";
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
            tvTime = itemView.findViewById(R.id.tvTime);
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

        public void bind(Tweet tweet) {
            Log.i(TAG, "In bind");
            tvBody.setText(tweet.getBody());
            tvScreenName.setText(tweet.getUser().getScreenName());
            tvTime.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
            Glide.with(context).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
            if (tweet.isExtendedEntitiesFlag()) {
                Log.i(TAG, "Tweet has extended entities");
                bindImages(tweet);
                return;
            } else {
                //TODO clear out unused imageviews?
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

        public void bindImages(Tweet tweet) {
            Log.i(TAG, "Binding images");
            int numImages = tweet.getExtendedEntities().getMediaList().size();
            int i;
            for (i = 0; i < numImages; i++) {
                ImageView imgView = imageViews.get(i);
                Media tweetImage = tweet.getExtendedEntities().getMediaList().get(i);
                // TODO: probably should be cropped to thumbnail
                //imgView.getLayoutParams().height = tweetImage.getHeight();
                //imgView.getLayoutParams().width = tweetImage.getWidth();
                imgView.getLayoutParams().height = 225;
                imgView.getLayoutParams().width = 225;
                Glide.with(context).load(tweetImage.getMediaUrlHttps()).circleCrop()
                        .into(imgView);

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
