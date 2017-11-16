package it.valeriovaudi.microservices.hello.anticorruption;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import it.valeriovaudi.microservices.hello.model.Tweet;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by mrflick72 on 19/10/17.
 */


@Component
public class TweetAnticorruptionLayer {

    private final ObjectMapper objectMapper;

    public TweetAnticorruptionLayer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Tweet newTweet(JsonNode itemAux){
        Tweet tweet = new Tweet();
        tweet.setCreationDate(itemAux.get("createdAt").asLong());
        tweet.setText(itemAux.get("text").asText());

        return tweet;
    }

    public ArrayNode newTweetList(String body) throws IOException {
        return (ArrayNode) objectMapper.readTree(body);
    }

}
