package com.gty.rabbitTest.four;

import com.gty.rabbitTest.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

//测试消息发布确认

/**生产者 批量发送消息
 *
 */
public class Producer {
    public static String QUEUE_NAME = UUID.randomUUID().toString();
    public static void main(String[] args) throws Exception {
//        danGeQueRen();
//piLiangQueRen();
        yiBuQueRen();
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

    //异步确认方式的耗时
    public static void yiBuQueRen() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);

        //开启发布确认
        channel.confirmSelect();
        //系统当前时间 毫秒
        long start = System.currentTimeMillis();

        //线程安全的Map集合用于记录发送的消息是否成功发送，发送成功就删除Map中对应的数据，
        // 最后留存的是消息发送不成功的。
        ConcurrentSkipListMap<Long,String> concurrentSkipListMap =
                new ConcurrentSkipListMap<>();


        //deliveryTag 消息标签 ； multiple是否为批量确认
        //消息确认成功的回调函数
        ConfirmCallback ackCallback = (deliveryTag,multiple)->{
            System.out.println("消息成功标记："+deliveryTag);
            if (multiple){//如果是批量的
//               //ConcurrentSkipListMap的headMap方法是一个用于返回ConcurrentSkipListMap中小于指定键的所有键值对的子映射的方法。
                ConcurrentNavigableMap<Long, String> headMap =
                        concurrentSkipListMap.headMap(deliveryTag,true);
                //concurrentSkipListMap的headMap方法返回的ConcurrentNavigableMap如果使用clear会删除concurrentSkipListMap的数据。
                //这是因为子映射是原映射的一个视图，对子映射的修改会反映到原映射中，反之亦然。
                headMap.clear();
            }else {
                //删除当前的消息
                concurrentSkipListMap.remove(deliveryTag);
            }

        };
        //消息确认失败的回调函数
        ConfirmCallback nackCallback = (deliveryTag,multiple)->{
            System.out.println("消息失败标记："+deliveryTag);
        };
        //异步消息监听，监听哪些消息成功或失败
        channel.addConfirmListener(ackCallback,nackCallback);

        for (int i = 0; i < 100; i++) {
            String message = "生产者发送："+ i ;
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            //发送完记录消息
            concurrentSkipListMap.put(channel.getNextPublishSeqNo(),message);
        }

        long end = System.currentTimeMillis();
        System.out.println("耗时："+ (end -start));
    }
}
