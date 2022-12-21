package com.adwaitkulkarni58.twitter.kafka.project.kafka.producer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterProducer {

	// constructor
	public TwitterProducer() {

	}

	// run the producer
	public void run() {

	}

	// create twitter client
	public void createTwitterClient() {
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
	}

}
