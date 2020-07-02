package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity=User.class, parentColumns="id", childColumns="userId"))
public class Tweet {

    private static String TAG = "Tweet";

    @ColumnInfo
    @PrimaryKey
    private long idLong;
    @ColumnInfo
    private String body;
    @ColumnInfo
    private String createdAt;
    @Ignore
    private User user;
    @ColumnInfo
    private long userId;
    @ColumnInfo
    private int favorite_count;
    @ColumnInfo
    private int retweet_count;
    @ColumnInfo
    private String mediaUrlString;
    @ColumnInfo
    private boolean extendedEntitiesFlag;
    @ColumnInfo
    private boolean favorited;
    @ColumnInfo
    private boolean retweeted;

    // Empty constructor for Parceler library
    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        // Populate tweet fields
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.favorite_count = jsonObject.getInt("favorite_count");
        tweet.retweet_count = jsonObject.getInt("retweet_count");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.idLong = jsonObject.getLong("id");
        tweet.userId = tweet.user.getId();

        if (jsonObject.has("extended_entities")) {
            // Tweet has native images to display
            ExtendedEntities extendedEntities = ExtendedEntities.fromJson(jsonObject.getJSONObject("extended_entities"));
            tweet.extendedEntitiesFlag = true;
            // Save media urls as string for easy saving to database
            tweet.mediaUrlString = extendedEntities.getMediaUrlsString();
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

    public void setUserId(long userId) {
        this.userId = userId;
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

    public String getMediaUrlString() {
        return mediaUrlString;
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


    public long getIdLong() {
        return idLong;
    }

    public long getUserId() {
        return userId;
    }

    public void setIdLong(long idLong) {
        this.idLong = idLong;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFavorite_count(int favorite_count) {
        this.favorite_count = favorite_count;
    }

    public void setRetweet_count(int retweet_count) {
        this.retweet_count = retweet_count;
    }

    public void setExtendedEntitiesFlag(boolean extendedEntitiesFlag) {
        this.extendedEntitiesFlag = extendedEntitiesFlag;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public List<String> getMediaUrlArray() {
        return Arrays.asList(mediaUrlString.split(","));
    }

    public void setMediaUrlString(String mediaUrlString) {
        this.mediaUrlString = mediaUrlString;
    }
}
