package sample;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class SSHExample {
    private String host = "1.94.30.181"; // 远程主机
    private String user = "root";       // SSH 用户名
    private String password = "Xj5200127@";    // SSH 密码
    private Session session;
    private Timer heartbeatTimer;
    public void executeCommands() {
        String command = "cd ./java-server; java -cp out SimpleChatServer"; // 用分号分隔命令

        // 尝试连接并执行命令
        if (connect()) {
            startHeartbeat(); // 启动心跳机制
            executeCommand(command);
            stopHeartbeat(); // 停止心跳
        } else {
            System.err.println("Failed to connect to the SSH server.");
        }

        // 断开连接
        disconnect();
    }

    public boolean connect() {
        int maxRetries = 5; // 最大重连次数
        int retryCount = 0;
        long retryInterval = 2000; // 初始重连间隔（毫秒）

        while (retryCount < maxRetries) {
            try {
                JSch jsch = new JSch();
                session = jsch.getSession(user, host, 22);
                session.setPassword(password);

                // 避免第一次连接时提示确认主机密钥
                session.setConfig("StrictHostKeyChecking", "no");
                session.setConfig("ServerAliveInterval", "60"); // 设置心跳间隔
                session.setConfig("ServerAliveCountMax", "5"); // 最大未响应心跳次数

                // 打开连接
                session.connect();
                System.out.println("Connected to " + host);
                return true; // 成功连接，返回 true
            } catch (Exception e) {
                retryCount++;
                System.err.println("Connection failed: " + e.getMessage());
                if (retryCount >= maxRetries) {
                    System.err.println("Max retries reached. Exiting.");
                    return false; // 达到最大重连次数，返回 false
                }
                System.out.println("Retrying in " + retryInterval + " ms...");
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                retryInterval *= 2; // 指数退避
            }
        }
        return false; // 连接失败
    }

    public void startHeartbeat() {
        heartbeatTimer = new Timer();
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (session != null && session.isConnected()) {
                    try {
                        // 发送心跳消息
                        executeCommand("HEARTBEAT");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, 30000); // 每30秒发送一次心跳
    }

    public void stopHeartbeat() {
        if (heartbeatTimer != null) {
            heartbeatTimer.cancel();
            heartbeatTimer = null;
        }
    }

    public void executeCommand(String command) {
        try {
            // 检查 SSH 连接状态
            if (session == null || !session.isConnected()) {
                System.err.println("SSH session is disconnected. Attempting to reconnect...");
                if (!connect()) { // 尝试重新连接
                    System.err.println("Failed to reconnect. Command execution aborted.");
                    return; // 如果连接失败，则不执行命令
                }
            }else {
                System.out.println("连接断开");
            }

            // 创建 ChannelExec 对象
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(command);

            // 获取命令的输入流
            InputStream in = channelExec.getInputStream();
            channelExec.connect();

            // 读取命令输出
            byte[] buffer = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(buffer, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(buffer, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("Exit status: " + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            channelExec.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            System.out.println("Disconnected from " + host);
        }
    }
}
