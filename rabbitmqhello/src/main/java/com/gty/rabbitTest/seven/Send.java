package com.gty.rabbitTest.seven;

import com.gty.rabbitTest.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//测试fnaout（广播）方式的交换机
public class Send {
    private static final String EXCHANGE_NAME = "topic_logs";
    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            /**
             * Q1-->绑定的是
             * 中间带 two 带 3 个单词的字符串(*.two.*)
             * Q2-->绑定的是
             * 最后一个单词是 three 的 3 个单词(*.*.three)
             * 第一个单词是 one 的多个单词(one.#)
             *
             */
            Map<String, String> bindingKeyMap = new HashMap<>();
            bindingKeyMap.put("quick.two.three","被队列 Q1Q2 接收到");
            bindingKeyMap.put("one.two.elephant","被队列 Q1Q2 接收到");
            bindingKeyMap.put("quick.two.fox","被队列 Q1 接收到");
            bindingKeyMap.put("one.brown.fox","被队列 Q2 接收到");
            bindingKeyMap.put("one.pink.three","虽然满足两个绑定但只被队列 Q2 接收一次");
            bindingKeyMap.put("quick.brown.fox","不匹配任何绑定不会被任何队列接收到会被丢弃");
            bindingKeyMap.put("quick.two.male.three","是四个单词不匹配任何绑定会被丢弃");
            bindingKeyMap.put("one.two.male.three","是四个单词但匹配 Q2");

            for (Map.Entry<String, String> bindingKeyEntry: bindingKeyMap.entrySet()){
                String bindingKey = bindingKeyEntry.getKey();
                String message = bindingKeyEntry.getValue();

                channel.basicPublish(EXCHANGE_NAME,bindingKey, null, message.getBytes("UTF-8"));

                System.out.println("生产者发出消息" + message);
            }
        }
    }

}
