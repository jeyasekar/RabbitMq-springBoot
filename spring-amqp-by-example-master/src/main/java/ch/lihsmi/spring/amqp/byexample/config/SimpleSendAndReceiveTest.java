package ch.lihsmi.spring.amqp.byexample.config;

import org.springframework.amqp.core.Binding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import ch.lihsmi.spring.amqp.byexample.config.SimpleRabbitServerConfiguration;

@Component
public class SimpleSendAndReceiveTest {

	@Autowired
	private RabbitTemplate template;
	@Autowired
	private TestMessageListener listener;
	private static final String FANOUT_EXCHANGE = "ExchangeTestsJey.FanoutExchange";

	public void messageCanBeSentAndConsumedAsExpected() throws InterruptedException {
		try {
			template.convertAndSend("Direct-Ex", "Orange", "message meena");
			Object foo = template.receiveAndConvert("Direct-Q");
			System.out.println("val--> " + foo);
			// admin.deleteQueue("Direct-Q");
			// admin.deleteExchange(DIRECT_EXCHANGE);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void fanoutExchangeRoutesMessagesAsExpected() throws InterruptedException {
		
		try {
			for(int i=0;i<10;i++) {
				String st="fanout message"+i;
				byte[] messageBody = st.getBytes();
				Message message = MessageBuilder.withBody(messageBody).build();
			template.setExchange(FANOUT_EXCHANGE);
			//template.convertAndSend(message);
			listener.latch.await();
			}
			
			for(int i=0;i<10;i++) {
				listener.latch.await();
				//System.out.println(template.getMessageConverter().fromMessage(listener.receivedMessages.get(i)));
				System.out.println("aa"+new String(listener.receivedMessages.get(i).getBody()));
			}
			//assertThat(listener.receivedMessages.get(0).getBody(), is(messageBody));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendAndReceiveMessageObject() throws InterruptedException  {
		Message message = MessageBuilder.withBody("message jey".getBytes()).setContentEncoding("text")
				.setHeader("key1", "value1").build();

		template.convertAndSend("Header-Q1","", message);
		Message received = template.receive("Header-Q1");
		System.out.println(new String(received.getBody()));
	}

	public static class TestMessageListener implements MessageListener {
		@Autowired
		private RabbitTemplate template;
		@Autowired
		private TestMessageListener listener;
		private final List<Message> receivedMessages = new ArrayList<>();

		private final CountDownLatch latch = new CountDownLatch(1);

		@Override
		public void onMessage(Message message) {
			this.receivedMessages.add(message);
			System.out.println(new String(listener.receivedMessages.get(receivedMessages.size()-1).getBody()));
			//System.out.println(new String(template.getMessageConverter().fromMessage(message).toString()));
			//listener.receivedMessages.forEach(mes->
			//System.out.println("lis "+new String(mes.getBody())));
			
			latch.countDown();
		}
		
		

	}

	/*
	 * @RabbitListener(queues = "Direct-Q") public void
	 * consumeFirstQueueMessage(String message) {
	 * 
	 * System.out.println("First Queue Message *****************" + message);
	 * 
	 * }
	 */

	@Configuration
	@Import(SimpleRabbitServerConfiguration.class)
	public static class TestConfiguration {

		private static final String FANOUT_EXCHANGE = "ExchangeTestsJey.FanoutExchange";

		@Autowired
		private RabbitAdmin rabbitAdmin;

		@Autowired
		private ConnectionFactory connectionFactory;

		@Bean
		public FanoutExchange fanoutExchange() {
			FanoutExchange exchange = new FanoutExchange(FANOUT_EXCHANGE);
			rabbitAdmin.declareExchange(exchange);
			return exchange;
		}
		
		@Bean
		public Queue queue(FanoutExchange exchange) {
			Queue queue = new Queue("fanout-declareQueue", false, false, true);
			rabbitAdmin.declareQueue(queue);
			rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange));
			
			return queue;
		}

		@Bean
		public TestMessageListener listener(Queue queue) {
			TestMessageListener listener = new TestMessageListener();
			SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
			container.setMessageListener(listener);
			container.setQueues(queue);
			container.start();
			return listener;
		}

		/*
		 * @Bean public RabbitTemplate rabbitTemplate() { RabbitTemplate template = new
		 * RabbitTemplate(connectionFactory); template.setExchange(FANOUT_EXCHANGE);
		 * return template; }
		 */

	}

}
