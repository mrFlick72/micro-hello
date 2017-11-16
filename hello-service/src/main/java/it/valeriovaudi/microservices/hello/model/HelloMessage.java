package it.valeriovaudi.microservices.hello.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by mrflick72 on 19/10/17.
 */
@Data
@ToString
public class HelloMessage {
    private String instance;
    private String userName;
    private String userFirstName;
    private String userLastName;

    private List<Tweet> tweetList;
}
