package com.atguigu.rabbitmq.one;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 生产者: 发消息
 */
public class Producer {
    // 队列
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.194.129");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("123456");
        try {
            // 获取连接
            final Connection connection = connectionFactory.newConnection();
            // 获取信道
            final Channel channel = connection.createChannel();
            /**
             * 生成一个队列
             * 1.队列名称
             * 2. 队列里面的消息是否持久化(磁盘),默认情况消息存储在内存中
             * 3.该队列是否只供一个消费者进行消费 是否进行消息共享 true可以多个消费者消费 false:只能一个消费者进行消费
             * 4. 是否自动删除  最后一个消费者断开链接以后  该队列是否自动删除  true自动删除 false不自动删除
             * 5.其它参数
             */
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            final String message = "hello,world";
            // 发送一个消费
            // 1. 发送哪个交换机
            // 2. 路由的key是哪个 本次队列的名称
            // 3. 其它参数信息
            // 4. 发送消息的消息体
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            System.out.println("消息发送完毕");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
