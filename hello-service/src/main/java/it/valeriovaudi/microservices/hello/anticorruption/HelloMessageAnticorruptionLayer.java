package it.valeriovaudi.microservices.hello.anticorruption;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.valeriovaudi.microservices.hello.model.HelloMessage;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by mrflick72 on 19/10/17.
 */


@Component
public class HelloMessageAnticorruptionLayer {

    private final ObjectMapper objectMapper;

    public HelloMessageAnticorruptionLayer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public HelloMessage newHelloMessage(String body) throws IOException {
        ObjectNode node = (ObjectNode) objectMapper.readTree(body);
        HelloMessage helloMessage = new HelloMessage();

        helloMessage.setUserName(node.get("userName").asText());
        helloMessage.setUserFirstName(node.get("firstName").asText());
        helloMessage.setUserLastName(node.get("lastName").asText());

        return helloMessage;
    }
}
