package com.atguigu.rabbitmq.springbootrabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TTL队列配置文件类代码
 */
@Configuration
public class TtlQueueConfig {
     // 普通交换机的名称
    public static final String X_EXCHANGE = "X";

    // 死信交换机的名称
    public static final String Y_DEAD_EXCHANGE = "Y";

    // 普通队列的名称
    public static final String QUEUE_A = "QA";

    public static final String QUEUE_B = "QB";

    // 自动延时队列的名称
    public static final String QUEUE_TTL_TIME = "QC";

    // 死信队列的名称
    public static final String QUEUE_DEAD = "QD";

    // 声明xExchange 别名
    @Bean("xExchange")
    public DirectExchange xExchange() {
        return new DirectExchange(X_EXCHANGE);
    }

    // 声明yExchange
    @Bean("yExchange")
    public DirectExchange yExchange() {
        return new DirectExchange(Y_DEAD_EXCHANGE);
    }

    // 声明队列
    @Bean("queueA")
    public Queue queueA() {
//        HashMap<String, Object> map = new HashMap<>(4);
//        /**设置死信队列的交换机*/
//        map.put("x-dead-letter-exchange", Y_DEAD_EXCHANGE);
//        /**设置死信队列的routingKey*/
//        map.put("x-dead-letter-routing-key", "YD");
//        /**设置过期时间*/
//        map.put("x-message-ttl", 10000);
        return QueueBuilder.durable(QUEUE_A).ttl(10000).deadLetterExchange(Y_DEAD_EXCHANGE).deadLetterRoutingKey("YD").build();
    }
    // 声明队列
    @Bean("queueB")
    public Queue queueB() {
        return QueueBuilder.durable(QUEUE_B).ttl(40000).deadLetterExchange(Y_DEAD_EXCHANGE).deadLetterRoutingKey("YD").build();
    }

    @Bean("queueC")
    public Queue queueC() {
        return QueueBuilder.durable(QUEUE_TTL_TIME).deadLetterExchange(Y_DEAD_EXCHANGE).deadLetterRoutingKey("YD").build();
    }

    // 声明死信队列
    @Bean("queueD")
    public Queue queueD() {
        return QueueBuilder.durable(QUEUE_DEAD).build();
    }

    /**
     * 队列和交换机绑定
     * @param queueA
     * @param xExchange
     * @return
     */
    @Bean
    public Binding queueABindX(@Qualifier("queueA") final Queue queueA,@Qualifier("xExchange") final Exchange xExchange) {
        return BindingBuilder.bind(queueA).to(xExchange).with("XA").noargs();
    }

    @Bean
    public Binding queueBBindX(@Qualifier("queueB") final Queue queueB,@Qualifier("xExchange") final Exchange xExchange) {
        return BindingBuilder.bind(queueB).to(xExchange).with("XB").noargs();
    }

    @Bean
    public Binding queueCBindX(@Qualifier("queueC")final Queue queueC,@Qualifier("xExchange")final Exchange xExchange) {
        return BindingBuilder.bind(queueC).to(xExchange).with("XC").noargs();
    }

    @Bean
    public Binding queueDBindY(@Qualifier("queueD") final Queue queueD,@Qualifier("yExchange") final Exchange yExchange) {
        return BindingBuilder.bind(queueD).to(yExchange).with("YD").noargs();
    }
}
