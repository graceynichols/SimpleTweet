package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {
    private static String TAG = "Tweet";
    private String body;
    private String createdAt;
    private User user;
    private ExtendedEntities extendedEntities;
    private int favorite_count;
    private int retweet_count;
    private String id;
    //private int reply_count;
    private boolean extendedEntitiesFlag;
    private boolean favorited;
    private boolean retweeted;

    // Empty constructor for Parceler library
    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.favorite_count = jsonObject.getInt("favorite_count");
        tweet.retweet_count = jsonObject.getInt("retweet_count");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.id = jsonObject.getString("id_str");
        //tweet.reply_count = jsonObject.getInt("reply_count");
        if (jsonObject.has("extended_entities")) {
            // Tweet has native images to display
            tweet.extendedEntities = ExtendedEntities.fromJson(jsonObject.getJSONObject("extended_entities"));
            // TODO a flag probably isn't the best way to keep track of this
            tweet.extendedEntitiesFlag = true;
        } else {
            tweet.extendedEntitiesFlag = false;
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public static String getTAG() {
        return TAG;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public ExtendedEntities getExtendedEntities() {
        return extendedEntities;
    }

    public boolean isExtendedEntitiesFlag() {
        return extendedEntitiesFlag;
    }

    public int getFavorite_count() {
        return favorite_count;
    }

    public int getRetweet_count() {
        return retweet_count;
    }

    public String getId() {
        return id;
    }

    public int addOneRetweet() {
        retweet_count = retweet_count + 1;
        return retweet_count;
    }

    public int subOneRetweet() {
        retweet_count = retweet_count - 1;
        return retweet_count;
    }

    public int addOneFavorite() {
        favorite_count = favorite_count + 1;
        return favorite_count;
    }

    public int subOneFavorite() {
        favorite_count = favorite_count - 1;
        return favorite_count;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void toggleFavorited() {
        favorited = !favorited;
    }

    public void toggleRetweeted() {
        retweeted = !retweeted;
    }

    //public int getReply_count() {
        //return reply_count;
    //}
}
