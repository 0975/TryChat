package com.example.trychat.SockerNet;

import com.example.trychat.control.ChatController;
import com.example.trychat.dao.HandleMessages;
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
    private Socket socket;
    ChatController chatController;
    private boolean countExit;
    private LoginCallback loginCallback; // 新增字段


    public synchronized boolean isCountExit() {
        return countExit;
    }

    public synchronized void setCountExit(boolean countExit) {
        this.countExit = countExit;
    }

    public ConnectServer() {
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }
    //连接服务器的方法
    public void connectToServer() {

        try {

            socket = new Socket("1.94.30.181", 12347); // 连接到服务器"1.94.30.181", 12347

            // 创建两个流
            HandleMessages.getInstance().setOut(new DataOutputStream(socket.getOutputStream()));
            HandleMessages.getInstance().setIn(new DataInputStream(socket.getInputStream()));

            // 启动接收消息的线程
            new Thread(new IncomingMessageHandler()).start();
            // 启动心跳线程
            new Thread(() -> {
                try {
                    while (true) {
                        HandleMessages.getInstance().getOut().writeInt(2); // 发送心跳消息类型
                        HandleMessages.getInstance().getOut().flush();
                        Thread.sleep(5000); // 每 5 秒发送一次心跳
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();


            // 启动心跳机制
            //startHeartbeat();
        }  catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 添加一个发送注册信息的方法
    public void sendRegistrationInfo(String userName,String firstName,String lastName,int age,String email,String password) {

        try {
            // 发送消息类型 3，表示注册信息
            HandleMessages.getInstance().getOut().writeInt(3);
            // 发送用户名
            HandleMessages.getInstance().getOut().writeUTF(userName+" "+firstName+" "+lastName+" "+age+" "+email+" "+password);
            HandleMessages.getInstance().getOut().flush(); // 刷新输出流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //登录的方法
    public void signAccount(String username,String password,LoginCallback callback){
        this.loginCallback = callback; // 保存回调
        try {
            // 发送消息类型 3，表示注册信息
            HandleMessages.getInstance().getOut().writeInt(4);
            // 发送用户名
            HandleMessages.getInstance().getOut().writeUTF(username+" "+password);
            HandleMessages.getInstance().getOut().flush(); // 刷新输出流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //修改状态的方法
    public void accountQuit(int num){
        try {
            // 发送消息类型 3，表示注册信息
            HandleMessages.getInstance().getOut().writeInt(5);
            // 发送用户名
            HandleMessages.getInstance().getOut().writeUTF(Integer.toString(num));
            HandleMessages.getInstance().getOut().flush(); // 刷新输出流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //接收消息
    private class IncomingMessageHandler implements Runnable {
        public void run() {
            try {
                while (true) {
                    int messageType = HandleMessages.getInstance().getIn().readInt(); // 先读取消息类型

                    if (messageType == 0) { // 文本消息
                        String message = HandleMessages.getInstance().getIn().readUTF(); // 读取文本消息
                        String[] s = message.split(" ");
                        System.out.println("收到的"+s[3]);
                        Platform.runLater(() -> chatController.addOtherMessage(s[3])); // 更新 UI
                    }
                    if (messageType == 1) { // 图片消息
                        int imageSize = HandleMessages.getInstance().getIn().readInt(); // 读取图片大小
                        byte[] imageBytes = new byte[imageSize];
                        HandleMessages.getInstance().getIn().readFully(imageBytes); // 读取完整的图片数据

                        System.out.println("接收图片");
                        // 将字节数组转换为 BufferedImage
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
                        Platform.runLater(() -> chatController.addOtherChatDrawMessage(SwingFXUtils.toFXImage(img, null))); // 显示图像
                    }
                    if (messageType == 3) {
                        String message = HandleMessages.getInstance().getIn().readUTF(); // 读取文本消息
                        System.out.println("收到的"+message);
                    }if (messageType == 4) {
                        String message = HandleMessages.getInstance().getIn().readUTF(); // 读取文本消息
                        System.out.println("收到的"+message);
                        boolean success = "true".equals(message);
                        setCountExit(success);
                        // 调用回调
                        if (loginCallback != null) {
                            Platform.runLater(() -> loginCallback.onLoginResult(success)); // 在 UI 线程中调用回调
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 关闭连接的方法
    public void disconnectFromServer() {
        try {
            if (socket != null && !socket.isClosed()) {
                HandleMessages.getInstance().getIn().close(); // 关闭输入流
                HandleMessages.getInstance().getOut().close(); // 关闭输出流
                socket.close(); // 关闭 socket
                System.out.println("连接已关闭");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface LoginCallback {
        void onLoginResult(boolean success);
    }
}
