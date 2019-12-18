package org.iplatform.example.service;

import org.iplatform.microservices.core.IPlatformApplication;
import org.springframework.boot.test.SpringApplicationContextLoader;

public class IPlatformApplicationContextLoader extends SpringApplicationContextLoader {

  protected IPlatformApplication getSpringApplication() {
    return new ServiceApplication();
  }
}
