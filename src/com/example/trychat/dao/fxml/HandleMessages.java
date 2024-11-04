package com.example.trychat.dao.fxml;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HandleMessages implements HandleMessage {

    private DataOutputStream out; // 文本消息输出流
    private DataInputStream in; // 文本消息输入流


    public DataOutputStream getOut() {
        return out;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    public DataInputStream getIn() {
        return in;
    }

    public void setIn(DataInputStream in) {
        this.in = in;
    }


    @Override
    public boolean sendMessage(Messages messages) {
        if (messages.isImage()){
            try {
                out.writeInt(1); // 1 for image
                out.writeInt(messages.getContent().length); // 发送图片大小
                out.write(messages.getContent()); // 发送图片数据
                out.flush(); // 确保数据被发送

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (messages.isText()){
            return false;
        }
        return true;
    }

    @Override
    public boolean receiveMessage(Messages messages) {
        return false;
    }
}
