package com.gty.rabbitTest.one;

import com.gty.rabbitTest.utils.RabbitMqUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        //推送的消息如何进行消费的接口回调
        DeliverCallback deliverCallback=(consumerTag, delivery)->{
            String message= new String(delivery.getBody());
            System.out.println(message);
        };
        //取消消费的一个回调接口 如在消费的时候队列被删除掉了
        CancelCallback cancelCallback=(consumerTag)->{
            System.out.println("消息消费被中断");
        };
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
