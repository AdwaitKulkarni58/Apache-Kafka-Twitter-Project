package com.adwaitkulkarni58.twitter.kafka.project.kafka.producer;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

	@Autowired
	private TwitterConfig twitterConfig;

	// run the producer
	public void run() {
		hosebirdClient.connect();
	}

	// create twitter client
	@Bean
	public Client createTwitterClient() {
		String apiKey = twitterConfig.getTwitterApiKey();
		String apiKeySecret = twitterConfig.getTwitterApiKeySecret();
		String accessToken = twitterConfig.getTwitterAccessToken();
		String accessTokenSecret = twitterConfig.getTwitterAccessTokenSecret();

		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);

		/**
		 * Declare the host you want to connect to, the end point, and authentication
		 * (basic auth or oauth)
		 */
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		List<String> terms = Lists.newArrayList("kafka");
		hosebirdEndpoint.trackTerms(terms);

		Authentication hosebirdAuth = new OAuth1(apiKey, apiKeySecret, accessToken, accessTokenSecret);

		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01") // optional: mainly for the logs
				.hosts(hosebirdHosts).authentication(hosebirdAuth).endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue));
		Client hosebirdClient = builder.build();
		return hosebirdClient;
	}

}
