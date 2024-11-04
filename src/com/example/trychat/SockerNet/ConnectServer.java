package com.example.trychat.SockerNet;

import com.example.trychat.control.controller.ChatController;
import com.example.trychat.dao.fxml.HandleMessages;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectServer {

    HandleMessages messages = new HandleMessages();
    private Socket socket;
    ChatController chatController = new ChatController();
    //连接服务器的方法
    public void connectToServer() {

        try {
            // 尝试连接到远程主机
          /*if (sshExample.connect()) {
              // 执行命令
              sshExample.executeCommands();
              sshExample.startHeartbeat();
              // 断开连接
             // sshExample.disconnect();
          } else {
              System.err.println("Failed to connect to the SSH server.");
          }*/
            socket = new Socket("1.94.30.181", 12347); // 连接到服务器
            // 创建两个流
            messages.setOut(new DataOutputStream(socket.getOutputStream()));
            // imageOut = new DataOutputStream(socket.getOutputStream()); // 这里可以使用相同的输出流
            messages.setIn(new DataInputStream(socket.getInputStream()));
            //imageIn = new DataInputStream(socket.getInputStream()); // 这里也可以使用相同的输入流
            // 启动心跳线程
            new Thread(() -> {
                try {
                    while (true) {
                        messages.getOut().writeInt(2); // 发送心跳消息类型
                        messages.getOut().flush();
                        Thread.sleep(5000); // 每 5 秒发送一次心跳
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            // 启动接收消息的线程
            new Thread(new IncomingMessageHandler()).start();

            // 启动心跳机制
            //startHeartbeat();
        }  catch (Exception e) {
            e.printStackTrace();
        }

    }
    //接收消息
    private class IncomingMessageHandler implements Runnable {
        public void run() {
            try {
                while (true) {
                    int messageType = messages.getIn().readInt(); // 先读取消息类型


                    if (messageType == 0) { // 文本消息
                        String message = messages.getIn().readUTF(); // 读取文本消息
                        Platform.runLater(() -> chatController.addOtherMessage(message)); // 更新 UI
                    }
                    if (messageType == 1) { // 图片消息
                        int imageSize = messages.getIn().readInt(); // 读取图片大小
                        byte[] imageBytes = new byte[imageSize];
                        messages.getIn().readFully(imageBytes); // 读取完整的图片数据

                        System.out.println("接收图片");
                        // 将字节数组转换为 BufferedImage
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
                        Platform.runLater(() -> chatController.addOtherChatDrawMessage(SwingFXUtils.toFXImage(img, null))); // 显示图像
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}
