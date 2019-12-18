package org.iplatform.example.service.component;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

/**
 * @author liangruijia
 */
@Component
@ConfigurationProperties(prefix="myapp")
public class MyAppConfig {
    private String username;
    private String password;
    private Integer counter;
    private Boolean enabled;
    private String[] words;
    private String[] array;
    private Pair[] pairs;
    private Map<String, String> params;

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String[] getWords() {
        return words;
    }

    public void setWords(String[] words) {
        this.words = words;
    }

    public String[] getArray() {
        return array;
    }

    public void setArray(String[] array) {
        this.array = array;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Pair[] getPairs() {
        return pairs;
    }

    public void setPairs(Pair[] pairs) {
        this.pairs = pairs;
    }

    @Override
    public String toString() {
        return "MyAppConfig{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", counter=" + counter +
                ", enabled=" + enabled +
                ", words=" + Arrays.toString(words) +
                ", array=" + Arrays.toString(array) +
                ", pairs=" + Arrays.toString(pairs) +
                ", params=" + params +
                '}';
    }
}
