package com.example.trychat.dao;

/**
 *这是一个客户端接口
 *@author lihongjie
* @version 1.0
 */
public interface HandleMessage {

    /**处理消息的方法
     * @param  messages 要处理的信息
     * @return 返回消息是否发送成功*/
    public abstract boolean sendMessage(Messages messages);
    /**处理消息的方法
     * @param  messages 要处理的信息
     * @return 返回消息是否接收*/
    public abstract boolean receiveMessage(Messages messages);

}
