package com.gty.rabbitTest.four;

import com.gty.rabbitTest.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;
import java.util.UUID;

//测试消息发布确认

/**生产者 批量发送消息
 *
 */
public class Producer {
    public static String QUEUE_NAME = UUID.randomUUID().toString();
    public static void main(String[] args) throws Exception {
//        danGeQueRen();
piLiangQueRen();
    }

    //单个确认方式的耗时
    public static void danGeQueRen() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);

        //开启发布确认
        channel.confirmSelect();
        //系统当前时间 毫秒
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            String message = "生产者发送："+ i ;
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            //马上发布确认
            boolean b = channel.waitForConfirms();
            if (b){
                System.out.println("消息发送确认");
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("耗时："+ (end -start));
    }

    //批量确认方式的耗时
    public static void piLiangQueRen() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);

        //开启发布确认
        channel.confirmSelect();
        //系统当前时间 毫秒
        long start = System.currentTimeMillis();

        int batchSize = 10;

        for (int i = 0; i < 100; i++) {
            String message = "生产者发送："+ i ;
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());

            if (i%batchSize == 0){
                //发布确认
                boolean b = channel.waitForConfirms();
                System.out.println("消息发送确认");
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("耗时："+ (end -start));
    }
}
