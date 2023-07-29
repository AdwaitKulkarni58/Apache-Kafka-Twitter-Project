package com.adwaitkulkarni58.twitter.service.elastic;

import org.apache.http.*;
import org.apache.http.entity.*;
import org.elasticsearch.client.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;

@Service
public class ElasticSearchService {

    private RestClient elasticsearchClient;

    @Value("${elasticsearch.host}")
    private String elasticsearchHost;

    @Value("${elasticsearch.port}")
    private int elasticsearchPort;

    @Value("${elasticsearch.scheme}")
    private String elasticsearchScheme;

    @Value("${elasticsearch.index}")
    private String elasticsearchIndex;

    public ElasticSearchService(RestClient elasticsearchClient) {
        assert false;
        elasticsearchClient = RestClient.builder(
                        new HttpHost(elasticsearchHost, elasticsearchPort, elasticsearchScheme))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                        .setConnectTimeout(5000)
                        .setSocketTimeout(60000))
                .build();
    }

    public void indexTweetWithSentiment(String tweet, String sentiment) {
        try {
            String jsonDocument = "{\"tweet\":\"" + tweet + "\", \"sentiment\":\"" + sentiment + "\"}";

            Request request = new Request("PUT", "/" + elasticsearchIndex + "/_doc/");
            request.setEntity(new StringEntity(jsonDocument, ContentType.APPLICATION_JSON));

            Response response = elasticsearchClient.performRequest(request);

            System.out.println("Indexed tweet with ID: " + response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            System.err.println("Error indexing tweet: " + e.getMessage());
        }
    }

    public void closeElasticsearchClient() throws IOException {
        elasticsearchClient.close();
    }
}
