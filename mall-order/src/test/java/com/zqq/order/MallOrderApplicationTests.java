package com.zqq.order;

import org.junit.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MallOrderApplicationTests {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Test
    public void contextLoads() {

//        amqpAdmin.declareExchange();

    }

}
