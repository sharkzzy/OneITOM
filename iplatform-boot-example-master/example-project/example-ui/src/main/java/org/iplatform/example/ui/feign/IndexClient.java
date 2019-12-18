package org.iplatform.example.ui.feign;

import org.iplatform.microservices.core.http.RestResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("example-service")
public interface IndexClient {

    @RequestMapping(value = "exampleservice/api/v1/hello", method = RequestMethod.GET)
    public ResponseEntity<RestResponse<String>> hello();
}
