package org.iplatform.microservices.core.db.dynamic;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import io.shardingsphere.shardingjdbc.api.yaml.YamlMasterSlaveDataSourceFactory;
import io.shardingsphere.shardingjdbc.api.yaml.YamlShardingDataSourceFactory;

/**
 * 动态数据源注册
 */
@Configuration
@Component
@ConditionalOnExpression("${custom.datasource.enable:false} || ${spring.dynamicdatasource.enable:false} || ${sharding.jdbc.enable:false}")
public class DynamicDataSourceAutoConfigure implements EnvironmentAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceAutoConfigure.class);
    private ConversionService conversionService = new DefaultConversionService();
    private PropertyValues dataSourcePropertyValues;

    // 如配置文件中未指定数据源类型，使用该默认值
    private static final Object DATASOURCE_TYPE_DEFAULT = "org.apache.tomcat.jdbc.pool.DataSource";

    // 数据源
    private DataSource defaultDataSource;
    private Map<String, DataSource> customDataSources = new HashMap<>();
    
    // 是否启用分片功能
    @Value("${sharding.jdbc.enable:false}")
    private boolean shardingEnable;
    
    @Value("${sharding.jdbc.masterslave:false}")
    private boolean shardingMasterSlave;

    @Bean(name = "dataSource")
    public DynamicDataSource init() {
        Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
        // 将主数据源添加到更多数据源中
        targetDataSources.put("dataSource", defaultDataSource);
        DynamicDataSourceContextHolder.dataSourceIds.add("dataSource");
        LOGGER.info(String.format("注册主数据源 %s", defaultDataSource));
        // 添加更多数据源
        targetDataSources.putAll(customDataSources);
        for (Map.Entry<String, DataSource> entry : customDataSources.entrySet()) {
            String key = entry.getKey();
            DataSource value = entry.getValue();
            DynamicDataSourceContextHolder.dataSourceIds.add(key);
            LOGGER.info(String.format("注册动态数据源[%s] %s",key, value));
        }
        // 初始化分片数据源
        if(shardingEnable){
        	this.initShardingDataSource(targetDataSources);
        }
        DynamicDataSource dds = new DynamicDataSource();
        dds.setDefaultTargetDataSource(defaultDataSource);
        dds.setTargetDataSources(targetDataSources);
        LOGGER.info("AutoConfigure Dynamic DataSource Registry");
        return dds;
    }

    /**
     * 创建DataSource
     *
     * @param type
     * @param driverClassName
     * @param url
     * @param username
     * @param password
     * @return
     */
    @SuppressWarnings("unchecked")
    public DataSource buildDataSource(Map<String, Object> dsMap) {
        try {
            Object type = dsMap.get("type");
            if (type == null)
                type = DATASOURCE_TYPE_DEFAULT;// 默认DataSource

            Class<? extends DataSource> dataSourceType;
            dataSourceType = (Class<? extends DataSource>) Class.forName((String) type);

            String driverClassName = dsMap.get("dataSourceClassName").toString();
            String url = dsMap.get("url").toString();
            String username = dsMap.get("username").toString();
            String password = dsMap.get("password").toString();

            DataSourceBuilder factory = DataSourceBuilder.create().driverClassName(driverClassName).url(url)
                    .username(username).password(password).type(dataSourceType);
            return factory.build();
        } catch (ClassNotFoundException e) {
            LOGGER.info("", e);
        }
        return null;
    }

    /**
     * 加载多数据源配置
     */
    @Override
    public void setEnvironment(Environment env) {
        initDefaultDataSource(env);
        initCustomDataSources(env);
    }

    /**
     * 初始化主数据源
     */
    private void initDefaultDataSource(Environment env) {
        // 读取主数据源
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
        Map<String, Object> dsMap = new HashMap<>();
        dsMap.put("type", propertyResolver.getProperty("type"));
        dsMap.put("dataSourceClassName", propertyResolver.getProperty("dataSourceClassName"));
        dsMap.put("url", propertyResolver.getProperty("url"));
        dsMap.put("username", propertyResolver.getProperty("username"));
        dsMap.put("password", propertyResolver.getProperty("password"));

        defaultDataSource = buildDataSource(dsMap);

        dataBinder(defaultDataSource, env);
    }

    /**
     * 为DataSource绑定更多数据
     *
     * @param dataSource
     * @param env
     */
    private void dataBinder(DataSource dataSource, Environment env) {
        RelaxedDataBinder dataBinder = new RelaxedDataBinder(dataSource);
        //dataBinder.setValidator(new LocalValidatorFactory().run(this.applicationContext));
        dataBinder.setConversionService(conversionService);
        dataBinder.setIgnoreNestedProperties(false);//false
        dataBinder.setIgnoreInvalidFields(false);//false
        dataBinder.setIgnoreUnknownFields(true);//true
        if (dataSourcePropertyValues == null) {
            Map<String, Object> rpr = new RelaxedPropertyResolver(env, "spring.datasource").getSubProperties(".");
            Map<String, Object> values = new HashMap<>(rpr);
            // 排除已经设置的属性
            values.remove("type");
            values.remove("dataSourceClassName");
            values.remove("url");
            values.remove("username");
            values.remove("password");
            dataSourcePropertyValues = new MutablePropertyValues(values);
        }
        dataBinder.bind(dataSourcePropertyValues);
    }

    /**
     * 初始化更多数据源
     */
    private void initCustomDataSources(Environment env) {
        // 读取配置文件获取更多数据源，也可以通过defaultDataSource读取数据库获取更多数据源
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, "spring.dynamicdatasource.");
        String dsPrefixs = propertyResolver.getProperty("names");
        if (dsPrefixs != null) {
            for (String dsPrefix : dsPrefixs.split(",")) {// 多个数据源
                Map<String, Object> dsMap = propertyResolver.getSubProperties(dsPrefix + ".");
                DataSource ds = buildDataSource(dsMap);
                customDataSources.put(dsPrefix, ds);
                dataBinder(ds, env);
            }
        }

    }

    /**
     * 分片数据源注册
     * @param targetDataSources
     */
	private void initShardingDataSource(Map<Object, Object> targetDataSources){
    	LOGGER.info("initShardingDataSource start...");
    	try {
    		String defaultDataSourceName = "defaultds";
    		String shardingDataSourceKey = "shardingds";

    		Map<String, DataSource> dataSourceMap = new HashMap<>(); //设置分库映射
       	 	dataSourceMap.put(defaultDataSourceName, defaultDataSource);
       	 	for (Map.Entry<String, DataSource> entry : customDataSources.entrySet()) {
                String key = entry.getKey();
                DataSource value = entry.getValue();
                dataSourceMap.put(key, value);
            }
       	 	
       	 	DataSource shardingDataSource = null;
       	 	if(shardingMasterSlave){
       	 		shardingDataSource = YamlMasterSlaveDataSourceFactory.createDataSource(dataSourceMap, ResourceUtils.getFile("classpath:sharding-jdbc.yml"));
       	 	}else{
       	 		shardingDataSource = YamlShardingDataSourceFactory.createDataSource(dataSourceMap, ResourceUtils.getFile("classpath:sharding-jdbc.yml"));
       	 	}
   			targetDataSources.put(shardingDataSourceKey, shardingDataSource);
   			
   			DynamicDataSourceContextHolder.dataSourceIds.add(shardingDataSourceKey);
		} catch (Exception e) {
			LOGGER.info("注册sharding-jdbc数据源失败");
			e.printStackTrace();
		}
    }

}
