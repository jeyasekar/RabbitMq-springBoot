package ch.lihsmi.spring.amqp.byexample.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MqApp {
	
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context =SpringApplication.run(MqApp.class, args);
       try {
    	   context.getBean(SimpleSendAndReceiveTest.class).messageCanBeSentAndConsumedAsExpected();
		//context.getBean(SimpleSendAndReceiveTest.class).fanoutExchangeRoutesMessagesAsExpected();
		//context.getBean(SimpleSendAndReceiveTest.class).sendAndReceiveMessageObject();
	} catch (BeansException | InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }

    }
