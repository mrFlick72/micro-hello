package it.valeriovaudi.microservices.hello.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mrflick72 on 19/10/17.
 */


@Service
public class ParamService {

    public Map<String, String> pram(String userName){
        Map<String, String> param = new HashMap<>();
        param.put("userName", userName);

        return param;
    }
}