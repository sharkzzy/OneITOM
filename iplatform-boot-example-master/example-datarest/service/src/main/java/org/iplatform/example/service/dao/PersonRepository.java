package org.iplatform.example.service.dao;

import java.util.List;

import org.iplatform.example.service.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 新增人员 {"name":"wls","age":12}
 * post http://localhost:8081/myprojectservice/api/persons
 * 
 * 人员列表
 * get http://localhost:8081/myprojectservice/api/persons
 * 
 * 人员搜索
 * get http://localhost:8081/myprojectservice/api/persons/search/findByName?name=wls
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{
	
	Person findByName(@Param("name") String name);
	
	Person findByNameAndAge(@Param("name") String name,@Param("age") Integer age);

	List<Person> findByAge(@Param("age") Integer age);
	
}
