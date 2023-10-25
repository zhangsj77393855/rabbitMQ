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
public class Consumer01 {
    // 交换机名称  普通交换机
    public static final String EXCHANGE_NAME1 = "normal_exchange";
    // 交换机名称   死信交换机
    public static final String EXCHANGE_NAME2 = "dead_exchange";

    // 普通队列的名称
    public static final String NORMAL_QUEUE = "normal_queue";
    // 死信队列的名称
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        final Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME1, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(EXCHANGE_NAME2, BuiltinExchangeType.DIRECT);
        // 队列是否需要持久化
        final boolean durable = true;
        Map<String, Object> arguments = new HashMap<>();
        // 设置过期时间
        // todo  此处的过期时间可以在队列中声明也可以在生产者的时候声明
        //  但是一般都在生产者声明 因为可以灵活的设置过期时间
//        arguments.put("x-message-ttl",100000);
        // 正常队列设置死信交换机
        arguments.put("x-dead-letter-exchange", EXCHANGE_NAME2);
        // 设置死信的RoutingKey
        arguments.put("x-dead-letter-routing-key", "lisi");
        // 设置正常队列的最大长度
//        arguments.put("x-max-length", 6);

        // 声明普通队列
        channel.queueDeclare(NORMAL_QUEUE,durable,false,false,arguments);
        // 声明死信队列
        channel.queueDeclare(DEAD_QUEUE, durable, false, false, null);
        // 绑定普通的交换机和普通的队列
        channel.queueBind(NORMAL_QUEUE, EXCHANGE_NAME1, "zhangsan");
        // 绑定死信的交换机和死信的队列
        channel.queueBind(DEAD_QUEUE, EXCHANGE_NAME2, "lisi");
        // 开启发布确认
        channel.confirmSelect();
        System.out.println("等待接收消息");
        CancelCallback callback = consumerTag -> {};
        // 关闭自动确认
        final boolean autoAck = false;
        DeliverCallback deliverCallback = (consumerTag,message) ->{
            final String msg = new String(message.getBody(), "UTF-8");
            System.out.println("Consumer01控制台打印接收到的消息:" + msg);
            System.out.println("接收队列:" + NORMAL_QUEUE + "绑定键" + message.getEnvelope().getRoutingKey());
            if (("info5").equals(msg)) {
                System.out.println("info5被拒绝了");
                // 后面的boolean值指的是拒绝后是否需要重新放回队列 建议不放回 放死信队列里面
                channel.basicReject(message.getEnvelope().getDeliveryTag(),false);
            } else {
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }
        };
        channel.basicConsume(NORMAL_QUEUE, autoAck, deliverCallback, callback);
    }
}
