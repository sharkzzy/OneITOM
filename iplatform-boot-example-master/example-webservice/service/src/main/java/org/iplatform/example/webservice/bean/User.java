package org.iplatform.example.webservice.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author liangruijia
 */
@Getter
@Setter
@ToString
@XmlRootElement(name="user")
public class User {
    private String userId;
    private String userName;
    private int age;
}
