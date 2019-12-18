package org.iplatform.example.service.service;

import org.iplatform.example.service.domain.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanglei
 */
@Service
@CacheConfig(cacheNames = "personCache")
public class IndexService {
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);

    /**
     * 模拟数据库存取数据
     * */
    private final Map<String, Person> database = new HashMap<>();

    @CachePut(key = "#p0.id")
    public Person addPerson(Person person){
        database.putIfAbsent(person.getId(), person);
        LOG.info("新增用户:{}", person.getName());
        return person;
    }

    @Cacheable(key = "#p0", unless="#result == null")
    public Person getPerson(String id){
        Person person = database.get(id);

        if (person != null) {
            LOG.info("查询用户:{}", person.getName());
        }else {
            LOG.info("未查询到用户!");
        }

        return person;
    }

    @CachePut(key = "#p0.id")
    public Person modifyPerson(Person person){
        database.put(person.getId(), person);

        LOG.info("修改用户:{}", person.getName());

        return person;
    }

    @CacheEvict(key = "#p0")
    public void removePerson(String id){
        Person person = database.get(id);
        if (person != null){
            database.remove(id);
            LOG.info("删除用户:{}", person.getName());
        }

    }

}
