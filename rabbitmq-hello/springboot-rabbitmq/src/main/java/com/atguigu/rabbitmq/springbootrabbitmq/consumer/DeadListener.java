package com.atguigu.rabbitmq.springbootrabbitmq.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class DeadListener {
    // 接收死信队列的消息
    @RabbitListener(queues = "QD")
    public void receiveD(Message msg, Channel channel) {
        log.info("当前的channel信息为;{}", channel);
        final String message = new String(msg.getBody());
        log.info("当前时间:{},收到的消息为:{}", LocalDateTime.now(), message);
    }
}
