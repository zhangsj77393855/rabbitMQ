package com.atguigu.rabbitmq.eight;

import com.atguigu.rabbitmq.one.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列  实战
 * 消费者1
 */
public class Consumer02 {

    // 死信队列的名称
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        final Channel channel = RabbitMqUtils.getChannel();

        // 开启发布确认
        channel.confirmSelect();
        System.out.println("等待接收消息");
        CancelCallback callback = consumerTag -> {};
        // 关闭自动确认
        final boolean autoAck = false;
        DeliverCallback deliverCallback = (consumerTag,message) ->{
            System.out.println("Consumer02控制台打印接收到的消息:" + new String(message.getBody(),"UTF-8"));
            System.out.println("接收队列:" + DEAD_QUEUE + "绑定键" + message.getEnvelope().getRoutingKey());
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };
        channel.basicConsume(DEAD_QUEUE, autoAck, deliverCallback, callback);
    }
}
