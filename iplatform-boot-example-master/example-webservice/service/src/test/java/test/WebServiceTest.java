package test;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.iplatform.example.webservice.service.UserService;
import org.junit.Assert;
import org.junit.Test;

public class WebServiceTest{

    @Test
    public void testDynamic() throws Exception {
        JaxWsDynamicClientFactory jaxWsDynamicClientFactory = JaxWsDynamicClientFactory.newInstance();
        Client client = jaxWsDynamicClientFactory.createClient("http://localhost:8081/myprojectservice/test/user?wsdl");
        Object[] objects=client.invoke("getUser","411001");
        Assert.assertEquals("zhansan", objects[0]);
    }

    @Test
    public void testProxy() {
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setAddress("http://localhost:8081/myprojectservice/test/user?wsdl");
        jaxWsProxyFactoryBean.setServiceClass(UserService.class);

        UserService userService = (UserService) jaxWsProxyFactoryBean.create();

        /**
         * 设置超时时间
         * */
        Client client = ClientProxy.getClient(userService);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(1000);
        httpClientPolicy.setReceiveTimeout(1000);
        httpConduit.setClient(httpClientPolicy);

        String userName = userService.getName("411001");

        Assert.assertEquals("zhansan", userName);
    }

}
