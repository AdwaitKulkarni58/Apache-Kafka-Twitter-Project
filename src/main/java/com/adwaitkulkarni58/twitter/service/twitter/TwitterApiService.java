package com.adwaitkulkarni58.twitter.service.twitter;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import twitter4j.*;
import twitter4j.conf.*;

import java.util.*;

@Service
public class TwitterApiService {

    @Value("${twitter.api.consumer-key=}")
    private String consumerKey;

    @Value("${twitter.api.consumer-secret}")
    private String consumerSecret;

    @Value("${twitter.api.access-token}")
    private String accessToken;

    @Value("${twitter.api.access-token-secret}")
    private String accessTokenSecret;

    public List<Status> getTweets(String query, int tweetCount) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        try {
            Query q = new Query(query);
            q.setCount(tweetCount);
            QueryResult result = twitter.search(q);
            return result.getTweets();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
