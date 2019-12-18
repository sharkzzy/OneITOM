package org.iplatform.example.service.service;

import org.iplatform.microservices.core.neo4j.DirectionEnum;
import org.iplatform.microservices.core.neo4j.Neo4jGraphService;
import org.iplatform.microservices.core.neo4j.dto.Node;
import org.iplatform.microservices.core.neo4j.dto.Path;
import org.iplatform.microservices.core.neo4j.dto.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zhanglei
 */
@Configuration
@Service
@RestController
@RequestMapping("/api/v1")
public class IndexService {

    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);

    @Autowired
    private Neo4jGraphService neo4jGraphService;

    //清空所有数据
    @RequestMapping(value = "/clear", method = RequestMethod.GET)
    public void clear() {
        neo4jGraphService.deleteAll();
    }

    //初始化数据
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public @ResponseBody Path test() {
        neo4jGraphService.begin();
        try {
            neo4jGraphService.deleteAll();

            Node ie = new Node();
            ie.setName("IE");
            neo4jGraphService.addNode(ie);

            Node ui = new Node();
            ui.setName("UI");
            neo4jGraphService.addNode(ui);


            Node svr = new Node();
            svr.setName("SVR");
            neo4jGraphService.addNode(svr);

            Node auth = new Node();
            auth.setName("AUTH");
            auth.set("serviceName","AUTH-SERVER");
            neo4jGraphService.addNode(auth);

            Relationship rela = new Relationship();
            rela.setStartId(ie.getId());
            rela.setEndId(ui.getId());
            neo4jGraphService.addRelationship(rela);

            rela = new Relationship();
            rela.setStartId(ui.getId());
            rela.setEndId(svr.getId());
            neo4jGraphService.addRelationship(rela);

            rela = new Relationship();
            rela.setStartId(ui.getId());
            rela.setEndId(auth.getId());
            neo4jGraphService.addRelationship(rela);

            rela = new Relationship();
            rela.setStartId(svr.getId());
            rela.setEndId(auth.getId());
            neo4jGraphService.addRelationship(rela);neo4jGraphService.commit();

            //查询最短路径
            //Path path = neo4jGraphService.queryShortPath(ie,auth, DirectionEnum.OUTGOING, 10);

            //查询和ui节点有关系的点
            //List<Node> nodes = neo4jGraphService.queryRelationshipNode(ui,DirectionEnum.UNDIRECTED);

            //删除svr和auth之间的所有关系
            //neo4jGraphService.deleteRelationship(svr.getId(),auth.getId(),DirectionEnum.UNDIRECTED);

            //删除和ui相关的所有关系
            //neo4jGraphService.deleteRelationship(ui.getId(),DirectionEnum.OUTGOING);

            //查询ie点的所有关系数据，深度10
            Path path = neo4jGraphService.queryRelationshipPathNode(ie, DirectionEnum.OUTGOING, 10);
            return path;
        } catch (Exception e) {
            neo4jGraphService.rollback();
            throw e;
        }
    }

    //使用原生接口
    @RequestMapping(value = "/test2", method = RequestMethod.GET)
    public void run(){
        neo4jGraphService.getSession().run("CREATE (zhanglei:User {name:'zhangeli',age:40})");
    }

    //从文件导入关系和点
    @RequestMapping(value = "/importData", method = RequestMethod.GET)
    public void importData() throws Exception {
        Resource resource = new ClassPathResource("nodes.csv");
        neo4jGraphService.importNodeCSV(resource.getFile(),"id","name","type");
        resource = new ClassPathResource("relations.csv");
        neo4jGraphService.importRelationshipCSV(resource.getFile(),"id","type","start_id","end_id");
    }
}
