package com.atguigu.rabbitmq.six;

import com.atguigu.rabbitmq.one.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class ReceiveLogsDirect01 {
    public static final String EXCHANGE_NAME = "direct_logs";
    public static void main(String[] args) throws Exception {
        final Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        final boolean durable = true;
        channel.queueDeclare("console", durable, false, false, null);
        channel.queueBind("console", EXCHANGE_NAME,"info" );
        channel.queueBind("console", EXCHANGE_NAME,"warning" );
        System.out.println("等待接收消息,将收到的消息打印在屏幕上...");
        // 接收消息
        final boolean autoAck = false;
        // 开启发布确认
        channel.confirmSelect();
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("ReceiveLogsDirect01控制台打印接收到的消息:" + new String(message.getBody(),"UTF-8"));
            // 手动应答
            /**
             * 1. 消息的标记 tag
             * 2. 是否批量应答 false表示不批量应答信道中的消息
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };
        CancelCallback callback = consumerTag -> {};
        channel.basicConsume("console", autoAck, deliverCallback,callback);
    }
}
