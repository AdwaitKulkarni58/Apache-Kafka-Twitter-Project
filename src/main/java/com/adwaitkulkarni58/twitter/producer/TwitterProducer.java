package com.adwaitkulkarni58.twitter.producer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
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
		// create a message queue
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);

		// create twitter client
		logger.info("Creating a new client");
		Client client = createTwitterClient(msgQueue);
		logger.info("***Client created successfully***");

		// make connection to client
		logger.info("Making connection to client");
		client.connect();
		logger.info("***Connection made***");

		// create a producer
		KafkaProducer<String, String> kafkaProducer = createProducer();

		// shutting down the producer and stopping the application
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.info("Stopping the application");
			client.stop();
			logger.info("Client stopped");
			logger.info("Closing the producer");
			kafkaProducer.flush();
			kafkaProducer.close();
			logger.info("Producer closed");
		}));

		// loop for sending tweets to kafka
		while (!client.isDone()) {
			String msg = null;
			try {
				logger.info("Polling for tweets");
				msg = msgQueue.poll(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				client.stop();
				logger.info("Error occured");
			}
			if (msg != null) {
				logger.info(msg);
				logger.info("Sending new message");
				kafkaProducer.send(new ProducerRecord<String, String>("tweets", null, msg), new Callback() {

					@Override
					public void onCompletion(RecordMetadata metadata, Exception exception) {
						if (exception != null) {
							logger.error("An error occured", exception);
						}
					}
				});
				logger.info("***Successfully saved message to Kafka topic***");
			}
		}
	}

	// create a twitter client for the producer to receive messages from
	@Bean
	public Client createTwitterClient(BlockingQueue<String> msgQueue) {
		// get config values from secrets file
		String apiKey = twitterConfig.getTwitterApiKey();
		String apiKeySecret = twitterConfig.getTwitterApiKeySecret();
		String accessToken = twitterConfig.getTwitterAccessToken();
		String accessTokenSecret = twitterConfig.getTwitterAccessTokenSecret();

		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		List<String> terms = Lists.newArrayList("Messi", "football", "world cup", "Argentina");
		hosebirdEndpoint.trackTerms(terms);

		Authentication hosebirdAuth = new OAuth1(apiKey, apiKeySecret, accessToken, accessTokenSecret);

		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01").hosts(hosebirdHosts)
				.authentication(hosebirdAuth).endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue));
		Client hosebirdClient = builder.build();
		return hosebirdClient;
	}

	// create a new kafka producer
	public KafkaProducer<String, String> createProducer() {

		String BOOTSTRAP_SERVERS = "127.0.0.1:9092";

		// set producer properties
		Properties properties = new Properties();

		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		// create the producer
		KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
		return kafkaProducer;
	}

}
