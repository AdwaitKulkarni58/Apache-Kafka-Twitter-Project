package com.adwaitkulkarni58.twitter.kafka;

import org.apache.kafka.clients.producer.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import twitter4j.*;
import twitter4j.conf.*;

@Component
public class KafkaTwitterProducer {

    @Value("${twitter.api.consumer-key=}")
    private String consumerKey;

    @Value("${twitter.api.consumer-secret}")
    private String consumerSecret;

    @Value("${twitter.api.access-token}")
    private String accessToken;

    @Value("${twitter.api.access-token-secret}")
    private String accessTokenSecret;

    @Value("${kafka.tweets-topic}")
    private String kafkaTopic;

    private final KafkaProducer<String, String> kafkaProducer;

    @Autowired
    public KafkaTwitterProducer(KafkaProducer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void fetchTweetsAndSendToKafka() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        try {
            Query query = new Query("example");  // this can be anything you wish to search Twitter for
            query.setCount(10);
            QueryResult result = twitter.search(query);

            for (Status status : result.getTweets()) {
                String tweet = status.getText();
                System.out.println("Fetched tweet: " + tweet);

                kafkaProducer.send(new ProducerRecord<>(kafkaTopic, tweet));
            }

        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
}
