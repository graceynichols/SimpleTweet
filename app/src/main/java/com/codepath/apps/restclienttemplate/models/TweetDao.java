package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TweetDao {

    @Query("SELECT Tweet.body AS tweet_body, Tweet.createdAt AS tweet_createdAt, " +
            "Tweet.idLong AS tweet_idLong, Tweet.extendedEntitiesFlag AS tweet_extendedEntitiesFlag," +
            "Tweet.favorite_count AS tweet_favorite_count, Tweet.favorited AS tweet_favorited," +
            "Tweet.mediaUrlString AS tweet_mediaUrlString, Tweet.retweet_count AS tweet_retweet_count," +
            "Tweet.retweeted AS tweet_retweeted, User.*" +
            " FROM Tweet INNER JOIN User ON Tweet.userId = User.id " +
            "ORDER BY createdAt DESC LIMIT 5")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);
}
