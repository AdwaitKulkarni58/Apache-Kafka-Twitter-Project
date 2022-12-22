package com.adwaitkulkarni58.twitter.kafka.project.kafka.producer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.adwaitkulkarni58.twitter.config.TwitterConfig;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

@Configuration
public class TwitterProducer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private TwitterConfig twitterConfig;

	// run the producer
	@Bean
	public void run() {
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);

		// create twitter client
		Client client = createTwitterClient(msgQueue);

		// make connection to client
		logger.info("Making connection to client");
		client.connect();

		// create a producer
		KafkaProducer<String, String> producer = createProducer();

		// loop for sending tweets to kafka
		while (!client.isDone()) {
			String msg = null;
			try {
				logger.info("Polling for tweets");
				msg = msgQueue.poll(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				client.stop();
			}
			if (msg != null) {
				logger.info(msg);
			}
		}
	}

	@Bean
	public Client createTwitterClient(BlockingQueue<String> msgQueue) {
		String apiKey = twitterConfig.getTwitterApiKey();
		String apiKeySecret = twitterConfig.getTwitterApiKeySecret();
		String accessToken = twitterConfig.getTwitterAccessToken();
		String accessTokenSecret = twitterConfig.getTwitterAccessTokenSecret();

		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		List<String> terms = Lists.newArrayList("Messi");
		hosebirdEndpoint.trackTerms(terms);

		Authentication hosebirdAuth = new OAuth1(apiKey, apiKeySecret, accessToken, accessTokenSecret);

		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01").hosts(hosebirdHosts)
				.authentication(hosebirdAuth).endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue));
		Client hosebirdClient = builder.build();
		return hosebirdClient;
	}

	public KafkaProducer<String, String> createProducer() {

		// set producer properties
		Properties properties = new Properties();

		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		return null;

	}

}
