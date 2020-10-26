package no.hvl.dat250.h2020.group5;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@SpringBootApplication
public class Main {

  static final String topicExchangeName = "pollapp-exchange";

  static final String queueName = "polls";

  static final String routingKey = "poll";

  @Bean
  Queue queue() {
    return new Queue(queueName, false);
  }

  @Bean
  TopicExchange exchange() {
    return new TopicExchange(topicExchangeName);
  }

  @Bean
  Binding binding(Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(routingKey);
  }

  @Bean
  public TaskExecutor taskExecutor() {
    return new SimpleAsyncTaskExecutor();
  }

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }
}
