package com.atguigu.rabbitmq.five;

import com.atguigu.rabbitmq.one.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * 消息的接收
 */
// todo
//  Fanout 类型的交换机能够实现广播的效果，其中一个原因是所有绑定到该交换机的队列都会接收到相同的消息。
//  Fanout 交换机会忽略消息中的 Routing Key，它会直接将消息广播到所有与之绑定的队列。
//  无论队列的 Routing Key 是什么，它们都会收到同一条消息，并且消息会以相同的方式传递给每个队列。
//  因此，使用 Fanout 交换机时，多个队列可以绑定到同一个交换机，并且它们可以拥有不同的 Routing Key，
//  但最终它们都会收到相同的消息。
//  这种广播的特性使得 Fanout 类型的交换机在一些需要将消息发送给所有消费者的场景中非常有用，
//  例如日志广播和实时通知等。
public class ReceiveLogs01 {
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
        channel.queueBind(queue, EXCHANGE_NAME, "1111");
        System.out.println("等待接收消息,将收到的消息打印在屏幕上...");
        // 接收消息
        final boolean autoAck = false;
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("ReceiveLogs01控制台打印接收到的消息:" + new String(message.getBody(),"UTF-8"));
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
