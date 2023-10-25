package com.atguigu.rabbitmq.three;

import com.atguigu.rabbitmq.one.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 消息在手动应答是不丢失的,放回队列中重新消费
 */
public class Task2 {
    // 队列名称
    public static final String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        final Channel channel = RabbitMqUtils.getChannel();
        // 开启发布确认
        channel.confirmSelect();
        /**
         * 生成一个队列
         * 1.队列名称
         * 2. 队列里面的消息是否持久化(磁盘),默认情况消息存储在内存中
         *  todo 如果队列没有持久化 当mq服务宕机的时候 再次重启 队列就没了
         *  todo 这里的持久化是队列的持久化  队列持久化还不够,还要消息持久化
         *  todo 否则队列持久化了  但是消息没有持久化,在mq服务宕机的时候 消息就丢失了
         * 3.该队列是否只供一个消费者进行消费 是否进行消息共享 true可以多个消费者消费 false:只能一个消费者进行消费
         * 4. 是否自动删除  最后一个消费者断开链接以后  该队列是否自动删除  true自动删除 false不自动删除
         * 5.其它参数
         */
        final boolean durable = true;
        channel.queueDeclare(QUEUE_NAME,durable,false,false,null);
        // 从控制台中接收信息
        final Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            final String message = scanner.nextLine();
            // 发送一个消费
            // 1. 发送哪个交换机
            // 2. 路由的key是哪个 本次队列的名称
            // 3. 其它参数信息
            // todo 需要把消息持久化(要求保存在磁盘上) 默认是保存在内存中
            // 4. 发送消息的消息体
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("发送消息完成" + message);
        }
    }
}
