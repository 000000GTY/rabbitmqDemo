package com.gty.rabbitTest.three;

import com.gty.rabbitTest.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
//测试手动应答，不公平分发，预期值
public class Consumer {
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("消费快的");
//        System.out.println("消费慢的");
        //推送的消息如何进行消费的接口回调
        DeliverCallback deliverCallback=(consumerTag, message)->{
            try {
                //沉睡
                Thread.sleep(1*1000);
                //Thread.sleep(30*1000);
                String messages= new String(message.getBody());
                System.out.println("接收到："+ messages);
                //手动应答
                //@param deliveryTag the tag from the received 收到的标签
                //@param multiple true to acknowledge all messages up to and; True表示确认所有到和的信息
                //                (应答全部收到的消息就算之前的消息没处理完)
                channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        };
        //取消消费的一个回调接口 如在消费的时候队列被删除掉了
        CancelCallback cancelCallback=(consumerTag)->{
            System.out.println("消息消费被中断");
        };

        //设置不公平分发
//        channel.basicQos(1);
        //设置预期值为2
        channel.basicQos(2);
        //启动手动应答
        channel.basicConsume(QUEUE_NAME,false,deliverCallback,cancelCallback);


    }
}
