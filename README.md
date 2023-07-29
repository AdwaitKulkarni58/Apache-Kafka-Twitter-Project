## Title:  
  
Tweet-Vibes
  
## Description:  
  
- This is a Spring Boot project built with Maven and uses external tools like Apache Kafka for reading, storing, and writing data and Elasticsearch as the distributed search and analytics engine for performing sentiment analysis.  
  
## UML Diagram:  
  
![Kafka_UML_Final](https://user-images.githubusercontent.com/65598707/209486490-5144c03f-b7ba-47e6-be3d-c142afa43383.png)  
  
## Technologies used and why:  
  
- For the Twitter client, I used Twitter4j, Elasticsearch for indexing data, Apache Kafka as a distributed messaging system, Stanford NLP Library for sentiment analysis, Spring MVC as the underlying architecture, and Maven as the build system.  
  
## Functionality:  
  
- The Twitter client at the start of the pipeline is responsible for the authentication of the user and for making a connection to Twitter's API. There are numerous Twitter clients online that do this job, I earlier used HBC which was the most popular but it was deprecated after building the project. I then had to migrate the project to use Twitter4j.  
- Once the developer is authenticated and logged in, Apache Kafka comes into play.  
- A single producer uses the client to gather tweets in real-time based on topic names already created in the Kafka cluster and publishes these tweets to the respective brokers. Currently, there is only a single topic with 3 partitions with a replication factor of 2, so all the tweets related to different subjects go to the same topic. However, this can be customized to create multiple topics in the future.  
- Once the data is inside Kafka, the consumer and Elasticsearch come into the picture.  
- The Kafka consumer reads data stored inside the partitions within the cluster and then uses Elasticsearch to index that data. Elasticsearch has an index with the same name as the topic name and this index provides REST endpoints that you can hit with the unique Id each tweet generates inside the consumer and see the tweet as a JSON response. Once the tweet has been indexed, it is then sentimentally analyzed using the Stanford NLP Library which provides it a value of either "very negative, negative, neutral, positive, or very positive". The index within Elasticsearch and the topic inside Kafka have to be created beforehand.
  
## Challenges:  
  
- There were several challenges with this project, more bugs than code.  
- The first challenge was getting the application to start running, despite having all the necessary classes, the application wouldn't run and after debugging online, I realized that the Main class has to be outside a particular directory for a Spring application to run and making this change fixed this bug.  
- The next challenge was with Kafka. Since I am working on a Windows machine, running Kafka binaries with the native Windows CLI is already not recommended and I ran into problems doing this almost immediately. My topic wouldn't be created, Zookeeper would automatically shut down for no reason and it was just a difficult time. Downloading WSL2 solved this problem instantly.
- After that, I encountered my most significant challenge of the project, to make the whole thing work. The Twitter client, HBC is deprecated for developers with basic authorization access after Twitter announced the V2 version of its API and so I had to migrate the entire project to using a different Twitter client, Twitter4j.
- I also wanted to do some sentiment analysis of the tweets to see if Twitter really was as negative as it is made to be and the emotional sentiment does seem to be leaning a bit on the negative side. In order to do this, I had to figure out how sentiment analysis works and what options there are to integrate sentiment analysis in a Spring project. I came across the Stanford NLP Library and after a bit of reading about how to use it, I decided to go ahead with it.  
  
## Contributing:  
  
- Feel free to raise issues should they arise and make suggestions directly using PRs. I don't have a contributing guide created right now but if the repository receives sufficient traffic, I will create it.  
  
## Future Plans:  
  
- I plan to work more with Kafka and use it in different projects as it is quite efficient for handling data streams and managing large amounts of data in an organized fashion.  
- I want to create a UI where people can see the "Twitter mood for the day". Something like this would likely involve starting analyzing tweets from a certain time in the morning till the evening and throughout the day would display Twitter's mood at different periods.
