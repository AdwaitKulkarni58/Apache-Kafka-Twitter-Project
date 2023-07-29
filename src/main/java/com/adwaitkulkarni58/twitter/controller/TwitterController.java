package com.adwaitkulkarni58.twitter.controller;

import com.adwaitkulkarni58.twitter.kafka.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tweets")
public class TwitterController {

    private final KafkaTwitterProducer kafkaTwitterProducer;

    @Autowired
    public TwitterController(KafkaTwitterProducer kafkaTwitterProducer) {
        this.kafkaTwitterProducer = kafkaTwitterProducer;
    }

    @GetMapping("/fetchAndAnalyze")
    public String fetchAndAnalyzeTweets() {
        kafkaTwitterProducer.fetchTweetsAndSendToKafka();
        return "Fetching and analyzing tweets has been initiated!";
    }
}
