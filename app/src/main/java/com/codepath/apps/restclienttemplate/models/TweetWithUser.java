package com.codepath.apps.restclienttemplate.models;

import androidx.room.Embedded;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TweetWithUser {

    // Embeded notation flattens the properties of the User object into the object
    @Embedded
    User user;

    @Embedded(prefix = "tweet_")
    Tweet tweet;


    public static List<Tweet> getTweetList(List<TweetWithUser> tweetWithUsers) {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < tweetWithUsers.size(); i++) {
            Tweet tweet = tweetWithUsers.get(i).tweet;
            tweet.setUser(tweetWithUsers.get(i).user);
            tweets.add(tweet);
        }
        return tweets;
    }
}
