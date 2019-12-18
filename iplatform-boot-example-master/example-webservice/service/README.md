# webservice样例

### 环境
* JDK1.8

### WS

参见UserService和实现类UserServiceImpl，注解@WebService、@WebMethod以及指定实现类接口命名空间并通过WebServiceConfig类发布服务

#### WS验证(执行命令)

```
curl -X POST -H 'Content-Type:text/xml' -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.webservice.example.iplatform.org/"><soapenv:Header/><soapenv:Body><ser:getUser><userId>411001</userId></ser:getUser></soapenv:Body></soapenv:Envelope>' http://localhost:8081/myprojectservice/test/user

```
### RS

参见HelloService和实现类HelloServiceImpl，注解@Path指定路径、@GET请求类型、@Produces返回类型并通过WebServiceConfig类发布服务

#### WS验证(执行命令)

```
http://localhost:8081/myprojectservice/test/hello/sayHello/411001
```