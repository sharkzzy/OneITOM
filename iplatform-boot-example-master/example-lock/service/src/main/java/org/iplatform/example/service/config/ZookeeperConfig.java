package org.iplatform.example.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.zookeeper") 
public class ZookeeperConfig {
    private String adderss;
    private int conntimeout=10000;
    private int sessionTimeOut=10000;
    public String getAdderss() {
        return adderss;
    }
    public void setAdderss(String adderss) {
        this.adderss = adderss;
    }
    public int getConntimeout() {
        return conntimeout;
    }
    public void setConntimeout(int conntimeout) {
        this.conntimeout = conntimeout;
    }
    public int getSessionTimeOut() {
        return sessionTimeOut;
    }
    public void setSessionTimeOut(int sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }
    public ZookeeperConfig(String adderss, int conntimeout, int sessionTimeOut) {
        super();
        this.adderss = adderss;
        this.conntimeout = conntimeout;
        this.sessionTimeOut = sessionTimeOut;
    }     
}