package com.adwaitkulkarni58.twitter.consumer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class TwitterConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ElasticSearchConfig elasticSearchConfig;

	public ElasticsearchClient createClient() {

		String hostname = elasticSearchConfig.getHostname();
		String username = elasticSearchConfig.getUsername();
		String password = elasticSearchConfig.getPassword();

		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		logger.info("Setting credentials for rest client");
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
		logger.info("Credentials set successfully");

		RestClientBuilder builder = RestClient.builder(new HttpHost(hostname, 9200))
				.setHttpClientConfigCallback(new HttpClientConfigCallback() {
					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
					}
				});

		// Create the low-level client
		logger.info("Creating low level client");
		RestClient restClient = RestClient.builder(new HttpHost(hostname, 443)).build();
		logger.info("Low level client initialized successfully");

		// Create the transport with a Jackson mapper
		logger.info("Creating transport for the client");
		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
		logger.info("Transport created successfully");

		// And create the API client
		logger.info("Creating actual elastic client");
		ElasticsearchClient client = new ElasticsearchClient(transport);
		logger.info("Elastic client created successfully");

		return client;

	}

	// search for an actual index using elastic's documentation
	public void searchForIndex() {
		String index = "twitter";
		ElasticsearchClient client = createClient();

		Reader input = new StringReader(
				"{'@timestamp': '2022-04-08T13:55:32Z', 'level': 'warn', 'message': 'Some log message'}".replace('\'',
						'"'));

		KafkaConsumer<String, String> consumer = createConsumer("tweets");

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

			for (ConsumerRecord<String, String> record : records) {
				IndexRequest<JsonData> request = IndexRequest.of(i -> i.index(index).withJson(input));

				IndexResponse response = null;
				try {
					response = client.index(request);
				} catch (ElasticsearchException e) {
					logger.error(e.toString());
				} catch (IOException e) {
					logger.error(e.toString());
				}

				logger.info("Result of indexing " + response);
				logger.info("Indexed with id " + response.id());
				logger.info("Key:" + record.key() + ", value:" + record.value());
			}
		}
	}

	// create a consumer
	public KafkaConsumer<String, String> createConsumer(String topic) {
		String BOOTSTRAP_SERVERS = "127.0.0.1:9092";
		topic = "tweets"; // topic name, change for different topics
		String groupId = "kafka-twitter-elasticsearch";

		// set properties
		Properties properties = new Properties();

		logger.info("Setting consumer properties");
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		logger.info("Properties set successfully");

		logger.info("Creating a kafka consumer");
		@SuppressWarnings("resource")
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
		logger.info("Consumer created successfully");

		consumer.subscribe(Arrays.asList(topic));
		logger.info("Subscribed to a topic");

		return consumer;

	}

}
