package org.iplatform.example.service.domain;

import java.io.Serializable;

/**
 * @author liangruijia
 */
public class Person implements Serializable {
    private String id;
    private String name;
    private Integer age;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public Person(String id, String name, Integer age) {
        super();
        this.id = id;
        this.name = name;
        this.age = age;
    }
    public Person() {
        super();
    }
}
