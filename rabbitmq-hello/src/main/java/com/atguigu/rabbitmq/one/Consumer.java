package com.atguigu.rabbitmq.one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者 接收消息的
 */
public class Consumer {
    // 队列的名称
    public static final String QUEUE_NAME = "hello";
    // 接收消息
    public static void main(String[] args) {
        // 创建链接工厂
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.194.129");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("123456");
        try {
            final Connection connection = connectionFactory.newConnection();
            final Channel channel = connection.createChannel();
            // 声明 接收消息

            DeliverCallback deliveredCallback = (consumerTag,message) -> {
                System.out.println("当前的deliveredCallback:" + consumerTag);
                System.out.println(new String(message.getBody()));
            };
            // 声明 取消消息时的回调
            CancelCallback caller = consumerTag -> {
                System.out.println("caller:" + consumerTag);
                System.out.println("消息消费被中断");
            };
            /**
             * 消费者消费消息
             * 1. 消费哪个队列
             * 2. 消费成功后是否自动应答 true 代表的自动应答
             * 3. 消费者成功消费的回调
             * 4. 消费者取消消费的回调
             */
            channel.basicConsume(QUEUE_NAME,true,deliveredCallback,caller);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
