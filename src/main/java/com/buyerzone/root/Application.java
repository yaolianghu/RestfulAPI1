package com.buyerzone.root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.apache.log4j.Logger;

@SpringBootApplication
public class Application extends SpringBootServletInitializer{
	
	/* Used when launching as an executable jar or war */
	public static void main(String[] args) throws Exception{
    	Logger logger = Logger.getLogger(Application.class);
    	logger.info("Application Starting.");
    	
    	org.apache.ibatis.logging.LogFactory.useLog4JLogging();
    	
    	SpringApplication.run(Application.class, args);
    }
	
	/* Used when deployed to a standalone container */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
		return application.sources(Application.class);
	}
	
}
