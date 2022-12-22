package com.adwaitkulkarni58.twitter.kafka.project.kafka.producer;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.adwaitkulkarni58.twitter.config.TwitterConfig;
import com.google.common.collect.Lists;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

@Configuration
public class TwitterProducer {

	@Autowired
	private TwitterConfig twitterConfig;

	// run the producer
	public void run() {

	}

	// create twitter client
	@Bean
	public void createTwitterClient() {
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
		// Optional: set up some followings and track terms
		List<String> terms = Lists.newArrayList("kafka");
		hosebirdEndpoint.trackTerms(terms);

		// These secrets should be read from a config file
		Authentication hosebirdAuth = new OAuth1(apiKey, apiKeySecret, accessToken, accessTokenSecret);
	}

}
