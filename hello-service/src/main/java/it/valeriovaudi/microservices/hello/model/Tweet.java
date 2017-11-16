package it.valeriovaudi.microservices.hello.model;

import lombok.Data;
import lombok.ToString;

/**
 * Created by mrflick72 on 19/10/17.
 */

@Data
@ToString
public class Tweet {
    private String text;
    private long creationDate;
}