package com.imooc.item.starter;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ItemApplication {

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] {"classpath:spring/spring-context.xml"});
        context.start();
        // press any key to exit
        System.in.read();
	}

}
