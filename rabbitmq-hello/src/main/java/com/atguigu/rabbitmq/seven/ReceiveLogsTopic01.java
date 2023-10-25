package com.atguigu.rabbitmq.seven;

import com.atguigu.rabbitmq.one.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * 声明主题交换机 以及相关队列
 * 消费者C1
 */
public class ReceiveLogsTopic01 {
    // 交换机名称
    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        final Channel channel = RabbitMqUtils.getChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        // 声明队列
        final String queueName = "Q1";
        // 队列是否需要持久化
        final boolean durable = true;
        channel.queueDeclare(queueName, durable,false,false,null);
        channel.queueBind(queueName, EXCHANGE_NAME, "*.orange.*");
        System.out.println("等待接收消息");
        // 开启发布确认
        channel.confirmSelect();
        // 开发手动确认(关闭自动确认)
        final boolean autoAck = false;
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("ReceiveLogsTopic01控制台打印接收到的消息:" + new String(message.getBody(),"UTF-8"));
            System.out.println("接收队列:" + queueName + "绑定键" + message.getEnvelope().getRoutingKey());
            // 手动应答
            /**
             * 1. 消息的标记 tag
             * 2. 是否批量应答 false表示不批量应答信道中的消息
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };
        CancelCallback callback = consumerTag -> {};
        channel.basicConsume(queueName,autoAck,deliverCallback,callback);

    }
}
