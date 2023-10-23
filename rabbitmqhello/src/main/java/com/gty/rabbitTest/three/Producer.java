package com.gty.rabbitTest.three;

import com.gty.rabbitTest.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

//测试队列持久化
public class Producer {
    //
    public static final String QUEUE_NAME = "hello";
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        /**
         * 生成一个队列
         * @param queue the name of the queue    队列的名称
         * @param durable true if we are declaring a durable queue (the queue will survive a server restart)
         *                如果声明持久队列(队列在服务器重启后仍然有效)，则为true。
         * @param exclusive true if we are declaring an exclusive queue (restricted to this connection)
         *                  如果声明独占队列（只供一个消费者进行消费）(仅限于此连接)，则为True。
         * @param autoDelete true if we are declaring an autodelete queue (server will delete it when no longer in use)
         *                   如果我们声明一个自动删除队列(服务器将在不再使用时删除它)，则为True。
         * @param arguments other properties (construction arguments) for the queue
         *                  队列的其他属性(构造参数)
         */
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);


        /**
         * 发送一个消息
         * @param exchange the exchange to publish the message to
         *                 将消息发布到的交换器
         * @param routingKey the routing key    路由键
         * @param props other properties for the message - routing headers etc
         *              消息的其他属性-路由头等
         * @param body the message body 消息体
         */
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message=scanner.next();
            channel.basicPublish("",QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            System.out.println("生产者发出消息");
        }

    }
}
