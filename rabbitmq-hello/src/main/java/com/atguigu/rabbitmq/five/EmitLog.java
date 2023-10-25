package com.atguigu.rabbitmq.five;

import com.atguigu.rabbitmq.one.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 发消息 交换机(消费者)
 */
public class EmitLog {
    // 交换机的名称
    public static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        final Channel channel = RabbitMqUtils.getChannel();
        final Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            final String message = scanner.nextLine();
            // 发送一个消费
            // 1. 发送哪个交换机
            // 2. 路由的key是哪个 本次队列的名称
            // 3. 其它参数信息
            // 4. 发送消息的消息体
            channel.basicPublish(EXCHANGE_NAME, "33", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("发送消息完成" + message);
        }
    }
}
