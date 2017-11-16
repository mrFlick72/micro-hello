package it.valeriovaudi.microservices.hello.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by mrflick72 on 19/10/17.
 */
@Data
@ConfigurationProperties(prefix = "service.configuration")
public class ServiceConfiguration {
    private String peopleServiceServiceTemplate;
    private String tweetSocialServiceServiceTemplate;
    private String linkedInSocialServiceServiceTemplate;
}
