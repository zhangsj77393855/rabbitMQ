package com.atguigu.rabbitmq.two;

import com.atguigu.rabbitmq.one.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

public class Task01 {
    // 队列的名称
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {

        final Channel channel = RabbitMqUtils.getChannel();
        // 队列的声明
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        // 从控制台中接收信息
        final Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            final String message = scanner.nextLine();
            // 发送一个消费
            // 1. 发送哪个交换机
            // 2. 路由的key是哪个 本次队列的名称
            // 3. 其它参数信息
            // 4. 发送消息的消息体
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("发送消息完成" + message);
        }

    }
}
