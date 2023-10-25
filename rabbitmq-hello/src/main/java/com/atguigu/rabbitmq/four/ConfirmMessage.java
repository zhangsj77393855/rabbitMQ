package com.atguigu.rabbitmq.four;

import com.atguigu.rabbitmq.one.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 发布确认模式:
 * 1. 单个确认模式
 * 2. 批量确认模式
 * 3. 异步发布确认模式
 */
public class ConfirmMessage {
    // 批量发消息的个数
    public static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        // 单个  771ms
//        single();
        // 批量 82ms
//        batch();
        // 异步  37ms
        ConfirmMessage.async();
    }

    // 单个确认
    public static void single() throws Exception {
        final Channel channel = RabbitMqUtils.getChannel();
        final String id = UUID.randomUUID().toString();
        channel.queueDeclare(id, true, false, false, null);
        channel.confirmSelect();
        final long timeMillis = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            final String message = i + "";
            channel.basicPublish("", id, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
            final boolean result = channel.waitForConfirms();
            if (!result) {
                System.out.println("消息发送失败");
            }
        }
        final long millis = System.currentTimeMillis();
        System.out.println("发送" + MESSAGE_COUNT + "单个单独确认消息的耗时是:" + (millis - timeMillis));
    }

    // 批量确认
    public static void batch() throws Exception {
        final Channel channel = RabbitMqUtils.getChannel();
        final String id = UUID.randomUUID().toString();
        channel.queueDeclare(id, true, false, false, null);
        channel.confirmSelect();
        final long timeMillis = System.currentTimeMillis();

        // 批量确认消息的大小
        final int batchSize = 100;
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            final String message = i + "";
            channel.basicPublish("", id, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));

            // 判断达到100条消息的时候,批量确认一次
            if (i != 0 && i % 100 == 0) {
                final boolean result = channel.waitForConfirms();
                System.out.println(i);
                if (!result) {
                    System.out.println("消息发送失败");
                }
            }
        }
        final long millis = System.currentTimeMillis();
        System.out.println("发送" + MESSAGE_COUNT + "批量确认消息的耗时是:" + (millis - timeMillis));
    }
    // 异步发布确认
    // 批量确认
    public static void async() throws Exception {
        final Channel channel = RabbitMqUtils.getChannel();
        final String id = UUID.randomUUID().toString();
        channel.queueDeclare(id, true, false, false, null);
        channel.confirmSelect();
        /**
         * 线程安全有序的一个哈希表 使用于高并发的情况下
         * 1. 轻松的将序号与消息进行关联
         * 2. 轻松批量删除条目  只要给到序号
         * 3. 支持高并发(多线程)
         */
        ConcurrentSkipListMap<Long,String> outstandingConfirms = new ConcurrentSkipListMap<>();
        // 后面一个参数代表是否批量应答
        // 消息确认成功  回调函数
        // 1. 消息的标记
        // 2. 是否为批量确认
        ConfirmCallback ackCallback = (deliverTag,multiple) ->{
            // 如果是批量
            if (multiple) {
                // 2. 删除到已经确认的消息,剩下的就是未确认的消息
                final ConcurrentNavigableMap<Long, String> headMap = outstandingConfirms.headMap(deliverTag);
                headMap.clear();
            }else {
                outstandingConfirms.remove(deliverTag);
            }
            System.out.println("确认的消息标识:" + deliverTag);
        };
        // 消息确认失败  回调函数
        ConfirmCallback nackCallback = (deliverTag,multiple) ->{
            // 3. 打印一下未确认的消息
            final String message = outstandingConfirms.get(deliverTag);
            System.out.println("未确认的消息:" + message);
        };

        // 准备消息的监听器 监听那些消息成功了,哪些消息失败了
        // 1. 监听那些消息成功了
        // 2. 监听哪些消息失败了
        channel.addConfirmListener(ackCallback,nackCallback);
        final long timeMillis = System.currentTimeMillis();
        // 批量确认消息的大小
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            final String message = i + "";
            channel.basicPublish("", id, null , message.getBytes(StandardCharsets.UTF_8));
            // 1.记录所有要发送的消息
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
        }
        final long millis = System.currentTimeMillis();
        System.out.println("发送" + MESSAGE_COUNT + "异步确认消息的耗时是:" + (millis - timeMillis));
    }
}
