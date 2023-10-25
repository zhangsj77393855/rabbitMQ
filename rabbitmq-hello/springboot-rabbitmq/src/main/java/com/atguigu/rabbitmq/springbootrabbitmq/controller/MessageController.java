package com.atguigu.rabbitmq.springbootrabbitmq.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/ttl")
@Slf4j
public class MessageController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 开始发送消息
     *
     * @param msg 消息内容
     */
    @GetMapping("/sendMsg/{msg}")
    public void sendMessage(@PathVariable("msg") final String msg) {
        log.info("当前时间:{},发送的信息的内容:{}", LocalDateTime.now(), msg);
        rabbitTemplate.convertAndSend("X", "XA", "消息来自ttl为10s的队列:" + msg, message->{
            final MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
        rabbitTemplate.convertAndSend("X", "XB", "消息来自ttl为40s的队列:" + msg, message->{
            final MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
    }

    /**
     * 自定义延迟时间的发送消息
     */
    @GetMapping("/sendMsg/{ttlTime}/{msg}")
    public void sendMessageWithTtlTime(@PathVariable("ttlTime") final String ttlTime, @PathVariable("msg") final String msg) {
        log.info("自定义延迟时间发送信息,延时时间:{},信息内容:{}", ttlTime, msg);
        rabbitTemplate.convertSendAndReceive("X", "XC", "ttlTime为:" + ttlTime + "发送信息为:" + msg, message ->{
            final MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setExpiration(ttlTime);
            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
    }
}
