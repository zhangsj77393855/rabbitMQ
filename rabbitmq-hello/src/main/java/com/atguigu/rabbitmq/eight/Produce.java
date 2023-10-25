package com.atguigu.rabbitmq.eight;

import com.atguigu.rabbitmq.one.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.nio.charset.StandardCharsets;

public class Produce {
    // 交换机名称  普通交换机
    public static final String EXCHANGE_NAME1 = "normal_exchange";

    public static void main(String[] args) throws Exception {
        final Channel channel = RabbitMqUtils.getChannel();
        // 死信信息  设置TTL时间
//        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").deliveryMode(2).build();
        for (int i = 0; i < 10; i++) {
            final String message = "info" + i;
//            channel.basicPublish(EXCHANGE_NAME1, "zhangsan", properties,message.getBytes(StandardCharsets.UTF_8));
            channel.basicPublish(EXCHANGE_NAME1, "zhangsan", null,
                    message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
