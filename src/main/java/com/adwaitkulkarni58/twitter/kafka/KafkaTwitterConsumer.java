package com.adwaitkulkarni58.twitter.kafka;

import com.adwaitkulkarni58.twitter.service.elastic.*;
import com.adwaitkulkarni58.twitter.service.sentiment.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;

@Component
public class KafkaTwitterConsumer {

    @Value("${kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${kafka.tweets-topic}")
    private String topicName;

    private final SentimentAnalysisService sentimentAnalysisService;

    private ElasticSearchService elasticSearchService;

    private final KafkaConsumer<String, String> consumer;

    @Autowired
    public KafkaTwitterConsumer(SentimentAnalysisService sentimentAnalysisService) {
        this.sentimentAnalysisService = sentimentAnalysisService;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "tweets-consumer-group");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topicName));
    }

    public void consumeTweets() {
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                String tweet = record.value();
                System.out.println("Received tweet: " + tweet);

                String sentiment = sentimentAnalysisService.analyzeSentiment(tweet);
                System.out.println("Sentiment: " + sentiment);

                elasticSearchService.indexTweetWithSentiment(tweet, sentiment);
            }
        }
    }
}
