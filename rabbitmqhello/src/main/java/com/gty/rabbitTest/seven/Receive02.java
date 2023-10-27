package com.gty.rabbitTest.seven;

import com.gty.rabbitTest.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

//
public class Receive02 {
    private static final String EXCHANGE_NAME = "topic_logs";
    public static void main(String[] argv) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //设置topic类型
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        //声明 Q2 队列与绑定关系
        String queueName="Q2";
        channel.queueDeclare(queueName, false, false, false, null);

        //绑定匹配规则
        channel.queueBind(queueName, EXCHANGE_NAME, "*.*.three");
        channel.queueBind(queueName, EXCHANGE_NAME, "one.#");


        System.out.println("等待接收消息.....");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("队列名字"+queueName+"，routing："+delivery.getEnvelope().getRoutingKey()+",消息内容:"+message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }

}
