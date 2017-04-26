package com.buyerzone.root;

import javax.sql.DataSource;

import com.buyerzone.dao.mapper.EConnectMapper;
import com.buyerzone.dao.mapper.EmailOnlyLeadsMapper;
import com.buyerzone.service.impl.DefaultQuoteRequestService;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.buyerzone.com.filters.CorsFilter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@ComponentScan(basePackages = {"com.buyerzone.*", "com.purch.*"})
@MapperScan("com.buyerzone.dao.mapper")
@PropertySource("classpath:application.properties")
public class AppConfig {
	
	Logger logger = Logger.getLogger(AppConfig.class);
	
	//@Value("${MYSQL_URL:${mysql.url}}")
	//private String mysqlUrl;
	
	@Autowired
	private Environment env;

    @Bean
    public DataSource getDataSource() {
       BasicDataSource dataSource = new BasicDataSource();

       dataSource.setDriverClassName(env.getProperty("mysql.driver"));
       dataSource.setUrl(env.getProperty("mysql.url"));
       dataSource.setUsername(env.getProperty("mysql.user"));
       dataSource.setPassword(env.getProperty("mysql.password"));
       dataSource.setMaxActive(Integer.parseInt(env.getProperty("mysql.maxactive")));
       dataSource.setMinIdle(Integer.parseInt(env.getProperty("mysql.minidle")));
       dataSource.setValidationQuery(env.getProperty("mysql.validationquery"));

       return dataSource;
   }
    
    @Bean
    public FilterRegistrationBean commonsRequestLoggingFilter()
    {
      final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
      registrationBean.setFilter(new CorsFilter());
      return registrationBean;
    }
    
   @Bean
   public DataSourceTransactionManager transactionManager() {
       return new DataSourceTransactionManager(getDataSource());
   }
   
   @Bean
   public SqlSessionFactory sqlSessionFactory() throws Exception {
      SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
      sessionFactory.setDataSource(getDataSource());
      return sessionFactory.getObject();
   }

	/*
	@Bean
	public static PropertyPlaceholderConfigurer properties(){
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		Resource[] resources = new ClassPathResource[ ]
				{ new ClassPathResource( "application.properties" ) };
		
		ppc.setLocations(resources);
		
		ppc.setSearchSystemEnvironment(true);
		ppc.setIgnoreResourceNotFound(true);
		//ppc.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
		
		return ppc;
	}*/
   
} 