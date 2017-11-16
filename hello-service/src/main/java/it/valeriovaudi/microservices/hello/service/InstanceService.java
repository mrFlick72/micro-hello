package it.valeriovaudi.microservices.hello.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by mrflick72 on 19/10/17.
 */

@Service
public class InstanceService {

    @Getter
    @Value("#{T(java.util.UUID).randomUUID().toString()}")
    private String instance;


}