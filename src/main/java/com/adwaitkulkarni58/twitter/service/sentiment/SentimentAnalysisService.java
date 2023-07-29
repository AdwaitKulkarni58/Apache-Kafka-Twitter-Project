package com.adwaitkulkarni58.twitter.service.sentiment;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.sentiment.*;
import edu.stanford.nlp.util.*;
import jakarta.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class SentimentAnalysisService {

    private StanfordCoreNLP pipeline;

    @PostConstruct
    public void initialize() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, split, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    public String analyzeSentiment(String text) {
        String sentiment = "Unknown";
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            break;
        }

        return sentiment;
    }
}
