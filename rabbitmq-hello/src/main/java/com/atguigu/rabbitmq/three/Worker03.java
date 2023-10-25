package com.atguigu.rabbitmq.three;

import com.atguigu.rabbitmq.one.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Worker03 {
    // 队列名称
    public static final String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        final Channel channel = RabbitMqUtils.getChannel();
        System.out.println("C3消费者等待消费信息");
        // 采用手动应答
        DeliverCallback deliverCallback = (consumerTag, messages) ->{
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("接收到的消息" + new String(messages.getBody(),"UTF-8"));
            // 手动应答
            /**
             * 1. 消息的标记 tag
             * 2. 是否批量应答 false表示不批量应答信道中的消息
             */
            channel.basicAck(messages.getEnvelope().getDeliveryTag(),false);
        };
        CancelCallback cancelCallback = consumerTag ->{
            System.out.println(consumerTag + "消费者取消消费接口回调");
        };
        // 设置不公平分发 用信道
        // final int prefetchCount = 1;
        // 预取值
        final int prefetchCount = 5;
        channel.basicQos(prefetchCount);
        final boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME,autoAck,deliverCallback,cancelCallback);
    }
}
