package com.atguigu.rabbitmq.five;

import com.atguigu.rabbitmq.one.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * 消息的接收
 */
public class ReceiveLogs02 {
    // 交换机的名称
    public static final String EXCHANGE_NAME = "logs";
    public static void main(String[] args) throws Exception {
        final Channel channel = RabbitMqUtils.getChannel();
        // 声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        // 声明一个临时队列
        /**
         * 生成一个临时队列  队列的名称是随机的
         * 当消费者断开与队列的链接的时候  队列就自动删除
         */
        final String queue = channel.queueDeclare().getQueue();
        /**
         * 绑定交换机与队列
         */
        channel.queueBind(queue, EXCHANGE_NAME, "2222");
        System.out.println("等待接收消息,将收到的消息打印在屏幕上...");
        // 接收消息
        final boolean autoAck = false;
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("ReceiveLogs02控制台打印接收到的消息:" + new String(message.getBody(),"UTF-8"));
            // 手动应答
            /**
             * 1. 消息的标记 tag
             * 2. 是否批量应答 false表示不批量应答信道中的消息
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };
        CancelCallback callback = consumerTag -> {};
        channel.basicConsume(queue, autoAck, deliverCallback,callback);
    }
}
