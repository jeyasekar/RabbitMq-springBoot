package ch.lihsmi.spring.amqp.byexample.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
abstract public class RabbitConfiguration {
	static final String EX = "ExchangeTestsJey.FanoutExchange";
    static final String Q1 = "fanout-declareQueue";
  //  @Autowired
	//private RabbitAdmin rabbitAdmin;
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("10.244.200.242");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }
	/*
	 * @Bean Binding binding1() { return
	 * BindingBuilder.bind(queue1()).to(exchange()).with("jey"); }
	 * 
	 * @Bean Queue queue1() { Queue queue = new Queue("fanout-declareQueue", false,
	 * false, true); rabbitAdmin.declareQueue(queue);
	 * rabbitAdmin.declareBinding(binding1()); return queue; }
	 * 
	 * @Bean TopicExchange exchange() { FanoutExchange exchange = new
	 * FanoutExchange(EX); rabbitAdmin.declareExchange(exchange); return exchange; }
	 */

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

}
