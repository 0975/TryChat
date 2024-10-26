package sample;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

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
    private VBox otherMessage;


    @FXML
    private Label usernameLabel;
    @FXML
    Label status;

    @FXML
    StackPane drawWindow;

    private Stage stage;

    Timestamp currentTimestamp;
    Connection connection = DatabaseConnection.getConnection();
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    public void initialize() {
        // 加载图片
        Image image = new Image("sample/images/chiikawa.jpg");
        // 将图片填充到圆形
        circleIcon.setFill(new ImagePattern(image));
        drawWindow=new StackPane();
        drawWindow.setStyle("-fx-background-color: #6ED0D7FF;"); // 设置背景颜色
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
    public void pressEnter(KeyEvent event){
        javafx.scene.input.KeyCode code = event.getCode();
        if (code == KeyCode.valueOf("ENTER")){
            String message = messageTextField.getText().trim();
            if (!message.isEmpty()) {
                addChatMessage(message);
                out.println(message); // 发送消息到服务器
                messageTextField.clear();
            }

            //更精准的时间类
            currentTimestamp = new Timestamp(System.currentTimeMillis() / 1000 * 1000);

            System.out.println(currentTimestamp);
            String sender_id = usernameLabel.getText();

            insertUser(sender_id,"gouzi",message,currentTimestamp,(byte) 0);

        }
    }
    public void sendMessage(ActionEvent event){

        String message = messageTextField.getText().trim();
        if (!message.isEmpty()) {
            addChatMessage(message);
            out.println(message); // 发送消息到服务器
            messageTextField.clear();
        }

        //更精准的时间类
        currentTimestamp = new Timestamp(System.currentTimeMillis() / 1000 * 1000);

        System.out.println(currentTimestamp);
        String sender_id = usernameLabel.getText();

        insertUser(sender_id,"gouzi",message,currentTimestamp,(byte) 0);

    }
    public void sendPicture(ActionEvent event){

    }
    //接收消息
    private class IncomingMessageHandler implements Runnable {
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    String finalMessage = message;
                    Platform.runLater(() -> addOtherMessage(finalMessage)); // 调用 addOtherMessage
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //连接服务器的方法
    public void connectToServer() {
        try {
            socket = new Socket("1.94.30.181", 12347); // 连接到服务器
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 启动接收消息的线程
            new Thread(new IncomingMessageHandler()).start();
        } catch (IOException e) {
            e.printStackTrace();
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

    private void addOtherMessage(String message){
        // 创建聊天记录 Label
        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-background-color: #0000; -fx-padding: 5 20;");
        otherMessage.getChildren().add(msgLabel);

        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(10), msgLabel);
        fadeOut2.setFromValue(1.0);
        fadeOut2.setToValue(0.0);
        fadeOut2.setOnFinished(event -> myMessage.getChildren().remove(msgLabel));
        fadeOut2.play();
    }

    public void quitButton(ActionEvent event){
       showConfirmationDialog(stage);
    }


}
