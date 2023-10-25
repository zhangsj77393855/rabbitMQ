package com.atguigu.rabbitmq.two;

import com.atguigu.rabbitmq.one.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Worker01 {
    // 队列的名称
    public static final String QUEUE_NAME = "hello";

    // 接受信息
    public static void main(String[] args) throws Exception {
        final Channel channel = RabbitMqUtils.getChannel();
        // 消息的接收
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println("接收的消息:" + new String(message.getBody()));
        };
        // 消息取消的回调
        CancelCallback cancelCallback = consumerTag ->{
            System.out.println(consumerTag + "消费者消息取消接口的回调逻辑");
        };
        System.out.println("c2等待接收消息......");
        final boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME,autoAck,deliverCallback,cancelCallback);
    }

}
