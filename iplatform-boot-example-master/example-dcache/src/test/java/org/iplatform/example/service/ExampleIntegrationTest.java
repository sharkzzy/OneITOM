package org.iplatform.example.service;

import com.hazelcast.core.Message;
import lombok.SneakyThrows;
import org.iplatform.microservices.dcache.EmbedDistributedService;
import org.iplatform.microservices.dcache.EmbedDistributedTopicListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.awaitility.Awaitility.await;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = IPlatformApplicationContextLoader.class, classes = ServiceApplication.class)
@IntegrationTest({
        "eureka.client.enabled=false",
        "iplatform.dcache.cluster=${spring.application.name}",
        "iplatform.dcache.port=0",
        "iplatform.dcache.host=${spring.cloud.client.hostname}",
        "iplatform.dcache.members=${server.host}",
        /* queue */
        "iplatform.dcache.queue.my_queue.maxSize=10",
        "iplatform.dcache.queue.my_queue.backupCount=1",
        "iplatform.dcache.queue.my_queue.asyncBackupCount=0",
        /* list */
        "iplatform.dcache.list.my_list.maxSize=10",
        "iplatform.dcache.list.my_list.backupCount=1",
        "iplatform.dcache.list.my_list.asyncBackupCount=0",
        /* map */
        "iplatform.dcache.map.my_map.evictionPolicy=LRU",
        "iplatform.dcache.map.my_map.timeToLiveSeconds=5",
        "iplatform.dcache.map.my_map.maxIdleSeconds=10",
        "iplatform.dcache.map.my_map.maxSize=10",
        /* map */
        "iplatform.dcache.map.my_map2.evictionPolicy=LRU",
        "iplatform.dcache.map.my_map2.timeToLiveSeconds=10",
        "iplatform.dcache.map.my_map2.maxIdleSeconds=5",
        "iplatform.dcache.map.my_map2.maxSize=10"
})
public class ExampleIntegrationTest {

  @Autowired
  private EmbedDistributedService embedDistributedService;

  String queue_name = "my_queue";
  String map_name = "my_map";
  String map2_name = "my_map2";
  String list_name = "my_list";

  @Before
  public void befor(){
    embedDistributedService.queue(queue_name).clear();
    embedDistributedService.map(map_name).clear();
  }

  @Test
  public void testQueue(){
    for(int i=0;i<10;i++){
      embedDistributedService.queue(queue_name).put(i,2);
    }
    for(int i=0;i<10;i++){
      assertThat(embedDistributedService.queue(queue_name).poll(), is(i));
    }
    assertThat(embedDistributedService.queue(queue_name).poll(), is(nullValue()));
  }

  @Test(expected = java.util.concurrent.TimeoutException.class)
  public void testQueueMaxSize(){
    for(int i=0;i<11;i++){
      embedDistributedService.queue(queue_name).put(i,2);
    }
  }


  @Test
  public void testMap(){
    String k = "key";
    String v = "value";
    embedDistributedService.map(map_name).set(k,v);
    assertThat(embedDistributedService.map(map_name).get(k), is(v));
  }

  @Test
  public void testMapTimeToLiveSeconds(){
    String k = "key";
    String v = "value";
    embedDistributedService.map(map_name).set(k,v);
    assertThat(embedDistributedService.map(map_name).get(k), is(v));
    await().atMost(6,SECONDS).until(() ->  embedDistributedService.map(map_name).get("key") == null);
  }

  @Test
  @SneakyThrows
  public void testMapMaxIdleSeconds(){
    String k = "key";
    String v = "value";
    embedDistributedService.map(map2_name).set(k,v);
    assertThat(embedDistributedService.map(map2_name).get(k), is(v));
    Thread.sleep(2000);
    assertThat(embedDistributedService.map(map2_name).get(k), is(v));
    Thread.sleep(2000);
    assertThat(embedDistributedService.map(map2_name).get(k), is(v));
    Thread.sleep(2000);
    assertThat(embedDistributedService.map(map2_name).get(k), is(v));
    await().atMost(10,SECONDS).until(() ->  embedDistributedService.map(map2_name).get("key") == null);
  }

  @Test
  public void testList(){
    embedDistributedService.list(list_name).add(1);
    assertThat(embedDistributedService.list(list_name).size(),is(1));
    assertThat(embedDistributedService.list(list_name).get(0),is(1));
    embedDistributedService.list(list_name).remove(0);
    assertThat(embedDistributedService.list(list_name).size(),is(0));
  }

  @Test
  public void testListMaxSize(){
    for(int i=0;i<11;i++){
      embedDistributedService.list(list_name).add(i);
    }
    assertThat(embedDistributedService.list(list_name).size(),is(10));
  }

  @Test
  public void testTopic(){
    String topic_name = "my_topic";
    String message = "topic_value";
    TopicListener listener = new TopicListener();
    embedDistributedService.topic(topic_name).addListener(listener);
    embedDistributedService.topic(topic_name).publish(message);
    await().atMost(1,SECONDS).until(() -> listener.getValue().equals(message));

    embedDistributedService.topic(topic_name).removeAllListener();
    listener.setValue(null);
    embedDistributedService.topic(topic_name).publish(message);
    assertThat(listener.getValue(),is(nullValue()));
  }

  class TopicListener implements EmbedDistributedTopicListener {
    Object value;

    public Object getValue() {
      return value;
    }

    public void setValue(Object value) {
      this.value = value;
    }

    @Override
    public void onMessage(Message message) {
      message.getMessageObject();
      value = message.getMessageObject();
    }
  }
}
