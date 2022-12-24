package com.adwaitkulkarni58.twitter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:secrets.properties")
public class TwitterConfig {

	@Value("${twitter.api.key}")
	private String twitterApiKey;

	@Value("${twitter.api.key.secret}")
	private String twitterApiKeySecret;

	@Value("${twitter.access.token}")
	private String twitterAccessToken;

	@Value("${twitter.access.token.secret}")
	private String twitterAccessTokenSecret;

	public String getTwitterApiKey() {
		return twitterApiKey;
	}

	public String getTwitterApiKeySecret() {
		return twitterApiKeySecret;
	}

	public String getTwitterAccessToken() {
		return twitterAccessToken;
	}

	public String getTwitterAccessTokenSecret() {
		return twitterAccessTokenSecret;
	}

}
