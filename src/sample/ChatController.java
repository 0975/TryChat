package sample;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatController extends Thread{
    private static final double INITIAL_WIDTH = 200;  // 初始宽度
    private static final double INITIAL_HEIGHT = 5;   // 初始高度
    @FXML
    private Circle circleIcon;
    @FXML
    private TextField messageTextField;
    @FXML
    private VBox myMessage;
    @FXML
    private VBox imageMessage;

    @FXML
    private VBox otherMessage;
    @FXML
    private VBox otherImageMessage;

    @FXML
    private Label usernameLabel;
    @FXML
    Label status;

    @FXML
    StackPane drawWindow;

    @FXML
    Pane ChatUi;


    private Stage stage;

    Timestamp currentTimestamp;
    Connection connection = DatabaseConnection.getConnection();
    private Socket socket;
    private DataOutputStream out; // 文本消息输出流
    private DataOutputStream imageOut; // 图像消息输出流
    private DataInputStream in; // 文本消息输入流
    private DataInputStream imageIn; // 图像消息输入流
    private double lastX, lastY; // 记录上一个鼠标位置
    public Stage getStage() {
        return stage;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @FXML
    Canvas myCanvas;
    private GraphicsContext gc;
    private Boolean isDrawing =false;

    @FXML
    private ImageView myImage;
    @FXML
    private ImageView otherImage;
    private Boolean canvasDraw = false;
    private WritableImage writableImage;
    public Boolean getDrawing() {
        return isDrawing;
    }

    public void setDrawing(Boolean drawing) {
        isDrawing = drawing;
    }
    private boolean isSendingMessage = false; // 新增标志

    SSHExample sshExample = new SSHExample();
    @FXML
    public void initialize() {
// 获取当前窗口并设置 stage
// 获取当前窗口并赋值给成员变量

        // 加载图片
        Image image = new Image("sample/images/chiikawa.jpg");
        // 将图片填充到圆形
        circleIcon.setFill(new ImagePattern(image));

        myCanvas = new Canvas(INITIAL_WIDTH, INITIAL_HEIGHT);

        // 设置鼠标悬浮事件
        myCanvas.setOnMouseEntered(event -> {
            myCanvas.setHeight(150);
            myCanvas.setWidth(200);
        });

       /* myCanvas.setOnMouseExited(event -> {
            myCanvas.setHeight(INITIAL_HEIGHT);
            myCanvas.setWidth(INITIAL_WIDTH);


        });*/
        gc = myCanvas.getGraphicsContext2D();
        drawPicture(myCanvas,gc);



        // 禁止窗口拖动


        drawWindow=new StackPane();
        drawWindow.getChildren().add(myCanvas);

        drawWindow.setStyle("-fx-background-color: #e0fffb;"); // 设置背景颜色
        drawWindow.setLayoutX(44); // X 坐标
        drawWindow.setLayoutY(430); // Y 坐标
        // 设置鼠标悬浮事件
        drawWindow.setOnMouseEntered(event -> {
            drawWindow.setPrefSize(200, 150);
        });

        drawWindow.setOnMouseExited(event -> {
            drawWindow.setPrefSize(INITIAL_WIDTH, INITIAL_HEIGHT);


        });
        // 设置 StackPane 的位置，使其重叠在长方形上
        drawWindow.setPrefSize(INITIAL_WIDTH, INITIAL_HEIGHT); // 设置 StackPane 的大小




    }


    //按下回车的方法用KeyEvent事件监听
    public void pressEnter(KeyEvent event) throws IOException {

        javafx.scene.input.KeyCode code = event.getCode();
        isSendingMessage = true; // 设置标志为 true，表明正在发送消息
        if (code == KeyCode.valueOf("ENTER")){
            String message = messageTextField.getText().trim();
            if (!message.isEmpty()) {
                addChatMessage(message);
                out.writeInt(0); // 0 for text
                out.writeUTF(message); // 发送文本消息
                out.flush(); // 确保数据被发送
                messageTextField.clear();
            }

            //更精准的时间类
            currentTimestamp = new Timestamp(System.currentTimeMillis() / 1000 * 1000);

            System.out.println(currentTimestamp);
            String sender_id = usernameLabel.getText();

            insertUser(sender_id,"gouzi",message,currentTimestamp,(byte) 0);
            isSendingMessage = false; // 新增标志

        }
    }
    public void sendMessage(ActionEvent event) throws IOException {

            // 尝试连接到远程主机
        /*    if (sshExample.connect()) {
                // 执行命令
                sshExample.executeCommands();
                sshExample.startHeartbeat();
                // 断开连接
                // sshExample.disconnect();
            } else {
                System.err.println("Failed to connect to the SSH server.");
            }*/
        isSendingMessage = true; // 设置标志为 true，表明正在发送消息
        String message = messageTextField.getText().trim();
        if (!message.isEmpty()) {
            addChatMessage(message);
            out.writeInt(0); // 0 for text
            out.writeUTF(message); // 发送文本消息
            out.flush(); // 确保数据被发送
            messageTextField.clear();

            // 更新数据库
            currentTimestamp = new Timestamp(System.currentTimeMillis());
            String senderId = usernameLabel.getText();
            insertUser(senderId, "gouzi", message, currentTimestamp, (byte) 0);

          isSendingMessage = false; // 新增标志
        }

    }
    public void drawPicture(Canvas myCanvas,GraphicsContext gc){
        // 添加鼠标事件处理


        // 添加鼠标事件处理
        myCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
              System.out.println(isDrawing);

                // 开始新的路径
                gc.beginPath();
                gc.moveTo(event.getX(), event.getY());


        });

        myCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {

            // 绘制线条
            gc.setStroke(Color.web("#6ed0d7"));
            gc.setLineWidth(2);
            gc.lineTo(event.getX(), event.getY());
            gc.stroke();
            event.consume(); // 消耗事件，防止窗口拖动
        });

        myCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            // 结束当前路径
            gc.lineTo(event.getX(), event.getY());
            gc.stroke();
            gc.closePath(); // 结束路径
            this.isDrawing = true;
            myCanvas.setOnMouseExited(even -> {
               if(!isSendingMessage){
                   writableImage = saveCanvasToImage(myCanvas);
                   // 获取 Canvas 的图像数据
                   BufferedImage bufferedImage = SwingFXUtils.fromFXImage(myCanvas.snapshot(null, null), null);

                   myCanvas.setHeight(INITIAL_HEIGHT);
                   myCanvas.setWidth(INITIAL_WIDTH);
                   sendImageToServer(bufferedImage);
               }


            });
        });





    }
    private void sendImageToServer(BufferedImage bufferedImage){

        try {
            byte[] imageBytes = writableImageToByteArray(bufferedImage);
            out.writeInt(1); // 1 for image
            out.writeInt(imageBytes.length); // 发送图片大小
            out.write(imageBytes); // 发送图片数据
            out.flush(); // 确保数据被发送
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private byte[] writableImageToByteArray(BufferedImage bufferedImage) {
        // 使用 ByteArrayOutputStream 存储字节数据
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            ImageIO.write(bufferedImage, "png", baos); // 可以选择其他格式，如 "jpg"
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void saveCanvasAsImage(BufferedImage bufferedImage) {

        // 定义保存路径和文件名
        String savePath = "E:\\javaSe\\TryChat\\src\\sample\\images\\output_image.png"; // 修改为你希望保存的路径和文件名

        // 创建文件对象
        File file = new File(savePath);

        try {
            // 将图像保存为文件
            ImageIO.write(bufferedImage, "png", file); // 可以根据需要调整格式
            System.out.println("图像已保存到：" + file.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }



    }
    private WritableImage saveCanvasToImage(Canvas canvas) {
        System.out.println(canvas+"转图片了");
        return canvas.snapshot(null, null);
    }
    public void display(Image image){
        myImage.setImage(image);
        System.out.println("图片显示了");
    }
    //接收消息
    private class IncomingMessageHandler implements Runnable {
        public void run() {
            try {
                while (true) {
                    int messageType = in.readInt(); // 先读取消息类型


                    if (messageType == 0) { // 文本消息
                        String message = in.readUTF(); // 读取文本消息
                        Platform.runLater(() -> addOtherMessage(message)); // 更新 UI
                    }
                    if (messageType == 1) { // 图片消息
                        int imageSize = in.readInt(); // 读取图片大小
                        byte[] imageBytes = new byte[imageSize];
                        in.readFully(imageBytes); // 读取完整的图片数据

                        System.out.println("接收图片");
                        // 将字节数组转换为 BufferedImage
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
                        Platform.runLater(() -> addOtherChatDrawMessage(SwingFXUtils.toFXImage(img, null))); // 显示图像
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


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
          out = new DataOutputStream(socket.getOutputStream());
          // imageOut = new DataOutputStream(socket.getOutputStream()); // 这里可以使用相同的输出流
          in= new DataInputStream(socket.getInputStream());
          //imageIn = new DataInputStream(socket.getInputStream()); // 这里也可以使用相同的输入流
          // 启动心跳线程
          new Thread(() -> {
              try {
                  while (true) {
                      out.writeInt(2); // 发送心跳消息类型
                      out.flush();
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


    // 启动心跳机制
    private void startHeartbeat() {
        ScheduledExecutorService heartbeatScheduler = Executors.newScheduledThreadPool(1);
        heartbeatScheduler.scheduleAtFixedRate(() -> sendHeartbeat(), 0, 60, TimeUnit.SECONDS); // 每30秒发送一次心跳
    }

    private void sendHeartbeat() {
        try {
            String heartbeatMessage = "HEARTBEAT"; // 根据协议定义心跳消息
            out.writeInt(2);
            out.writeUTF(heartbeatMessage); // 发送心跳消息
            out.flush();
            System.out.println("心跳消息已发送");
        } catch (IOException e) {
            System.err.println("心跳发送失败: " + e.getMessage());
            // 可以在这里考虑处理连接丢失的情况
        }
    }
    // 关闭连接和停止心跳
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //暂停项目
    private static void pauseProgram() {
        try {
            System.out.println("Pausing program...");

            Thread.sleep(10000); // 每隔10秒重试
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Pause interrupted: " + e.getMessage());
        }
    }
    //提示按钮
    public void showConfirmationDialog(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("你真的要退出吗");
        alert.setContentText("点击OK关闭, 或 Cancel 停留.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {


                try {
                    PreparedStatement statement = connection.prepareStatement(
                            "UPDATE messages SET status = ? WHERE sender_id = ? AND timestamp = ?"
                    );
                    statement.setInt(1,1); // 设置 status 为 newStatu
                    statement.setString(2,usernameLabel.getText());
                    statement.setTimestamp(3, currentTimestamp); // 确保 currentTimestamp 是正确的时间戳

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("状态更新成功。");
                        status.setText("1"); // 更新 UI 状态
                    } else {
                        System.out.println("没有找到匹配的记录。");
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }finally{
                    System.exit(0);
                    Platform.exit();
                }
            }if (response == ButtonType.CANCEL){
                System.out.println("方法退出");
            }
        });

    }
    //将数据传入数据库方法
    public void insertUser(String senderId, String receiverId, String message, Timestamp timestamp, byte status) {
        try (
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO messages(sender_id,receiver_id, message, timestamp , status) VALUES (?, ?, ?, ?, ?)"
                )) {
            statement.setString(1,senderId);
            statement.setString(2, receiverId);
            statement.setString(3, message);
            statement.setTimestamp(4, timestamp);
            statement.setByte(5, status);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //设置用户id和状态的方法
    public void setLabel(String username,int num){
        usernameLabel.setText(username);
        status.setText(Integer.toString(num)+"online");
    }
    private void addChatMessage(String message) {
        // 创建聊天记录 Label
        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-background-color: #0000; -fx-padding: 0 20;");

        // 添加到容器并执行淡出动画
        myMessage.getChildren().add(msgLabel);
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(10), msgLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> myMessage.getChildren().remove(msgLabel));
        fadeOut.play();

    }

   /* private void addChatDrawMessage(WritableImage mydraw){
        System.out.println("绘画方法运行了");
        // 创建ImageView
        if (!imageMessage.getChildren().isEmpty()) {
            imageMessage.getChildren().clear(); // 移除旧的图像
        }

        // 检查 mydraw 是否有效
        if (mydraw != null) {
            myImage.setImage(mydraw);
            myImage.setFitWidth(125); // 设置缩放后的宽度
            myImage.setFitHeight(100); // 设置缩放后的高度
            myImage.setPreserveRatio(true); // 保持图像比例
            System.out.println(myImage);

            // 添加到容器中
            imageMessage.getChildren().add(myImage);

            // 创建并播放淡出动画
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(10), myImage);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> imageMessage.getChildren().remove(myImage));
            fadeOut.play();
        } else {
            System.out.println("mydraw 是 null，无法设置图像");
        }
    }*/
    private void addOtherChatDrawMessage(WritableImage mydraw){
        System.out.println("绘画方法运行了");
        // 创建ImageView
        if (!otherImageMessage.getChildren().isEmpty()) {
            otherImageMessage.getChildren().clear(); // 移除旧的图像
        }

        // 检查 mydraw 是否有效
        if (mydraw != null) {
            otherImage.setImage(mydraw);
            otherImage.setFitWidth(125); // 设置缩放后的宽度
            otherImage.setFitHeight(100); // 设置缩放后的高度
            otherImage.setPreserveRatio(true); // 保持图像比例
            System.out.println(otherImage);

            // 添加到容器中
            otherImageMessage.getChildren().add(otherImage);

            // 创建并播放淡出动画
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(10), otherImage);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> otherImageMessage.getChildren().remove(otherImage));
            fadeOut.play();
        } else {
            System.out.println("mydraw 是 null，无法设置图像");
        }
    }
    private void addOtherMessage(String message){
        // 创建聊天记录 Label
        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-background-color: #0000; -fx-padding: 5 20;");
        otherMessage.getChildren().add(msgLabel);

        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(10), msgLabel);
        fadeOut2.setFromValue(1.0);
        fadeOut2.setToValue(0.0);
        fadeOut2.setOnFinished(event -> otherMessage.getChildren().remove(msgLabel));
        fadeOut2.play();
    }

    public void quitButton(ActionEvent event){
       showConfirmationDialog(stage);
    }


}
