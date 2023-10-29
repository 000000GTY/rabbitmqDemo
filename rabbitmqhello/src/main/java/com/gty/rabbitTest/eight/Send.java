package com.gty.rabbitTest.eight;

import com.gty.rabbitTest.utils.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.util.HashMap;
import java.util.Map;

//测试死信
public class Send {
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);

            //设置消息的 TTL 时间
            AMQP.BasicProperties properties = new
                    AMQP.BasicProperties().builder().expiration("10000").build();

            //该信息是用作演示队列个数限制
            for (int i = 1; i <11 ; i++) {
                String message="info"+i;

                channel.basicPublish(NORMAL_EXCHANGE, "zhangsan", properties, message.getBytes());

                System.out.println("生产者发送消息:"+message);
            }
        }
    }

}
