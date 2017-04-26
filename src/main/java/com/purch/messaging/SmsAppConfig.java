package com.purch.messaging;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@MapperScan("com.purch.messaging.dao.mapper")
public class SmsAppConfig {

}