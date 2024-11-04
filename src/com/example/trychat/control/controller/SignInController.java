package com.example.trychat.control.controller;

import com.example.trychat.SockerNet.ConnectServer;
import com.example.trychat.dao.fxml.DatabaseConnection;
import com.example.trychat.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignInController {
    private double xOffset = 0;
    private double yOffset = 0;
    Connection connection = DatabaseConnection.getConnection();
    ConnectServer connectServer = new ConnectServer();
    @FXML
    private Circle circleIcon;

    static Stage stage;
    private Scene scene;
    private Pane root;
    @FXML
    TextField userIdTextField;
    @FXML
    PasswordField passwordField;



    public void initialize() {
        // 加载图片
        Image image = new Image("com/example/trychat/images/chiikawa.jpg");
        // 将图片填充到圆形
        circleIcon.setFill(new ImagePattern(image));


    }
    public void signIn(ActionEvent event){

        try {
         if (true){
             if (checkUserExists(userIdTextField.getText()) && checkPasswordExists(passwordField.getText())) {

                 System.out.println("用户存在，登录成功！"); // 用户名存在，继续登录


                 FXMLLoader loader =new FXMLLoader(getClass().getResource("/com/example/trychat/control/chatController.fxml"));
                 root = loader.load();


                 // 获取聊天界面的控制器
                 root.setStyle("-fx-background-color: transparent;");

                 ChatController chatController = loader.getController();
                 chatController.setLabel(userIdTextField.getText(),0); // 设置用户名
                 if (chatController.getDrawing() == false){
                     System.out.println(chatController.getDrawing());
                     root.setOnMousePressed(events -> {
                         xOffset = events.getSceneX();
                         yOffset = events.getSceneY();
                     });

                     root.setOnMouseDragged(events -> {
                         stage.setX(events.getScreenX() - xOffset);
                         stage.setY(events.getScreenY() - yOffset);
                     });
                 }
                 root.getChildren().add(chatController.drawWindow);



                 stage = (Stage) ((Node)event.getSource()).getScene().getWindow();

                 scene = new Scene(root, Color.TRANSPARENT);
                 String css = this.getClass().getResource("/com/example/trychat/control/application.css").toExternalForm();
                 scene.getStylesheets().add(css);
                 stage.setScene(scene);
                 stage.show();
                 connectServer.connectToServer();
                 System.out.println("链接服务器成功");

             } else {
                 System.out.println("用户不存在，登录失败。"); // 用户名不存在，停止方法
                 //警示框
                 Alert alert = new Alert(Alert.AlertType.ERROR); // 使用 ERROR 类型
                 alert.setTitle("登录错误");
                 alert.setHeaderText("密码错误");
                 alert.setContentText("您输入的密码不正确，请重试。");

                 // 设置样式
                 // 设置样式
                 alert.getDialogPane().setStyle("-fx-background-color: #f8d7da;"); // 背景颜色
                 alert.getDialogPane().setStyle("-fx-border-color: #f5c6cb;-fx-font-size: 10px"); // 边框样式
                 alert.showAndWait(); // 显示对话框并等待用户关闭
                 return; // 停止方法
             }
         }else {
             System.out.println("请等待服务器连接");
             //警示框
             Alert alert = new Alert(Alert.AlertType.ERROR); // 使用 ERROR 类型
             alert.setTitle("服务器请求中");
             alert.setHeaderText("等待服务器连接");
             alert.setContentText("请等待服务器连接。");

             // 设置样式
             // 设置样式
             alert.getDialogPane().setStyle("-fx-background-color: #f8d7da;"); // 背景颜色
             alert.getDialogPane().setStyle("-fx-border-color: #f5c6cb;-fx-font-size: 10px"); // 边框样式
             alert.showAndWait(); // 显示对话框并等待用户关闭
         }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    // 检查用户名是否存在
    public boolean checkUserExists(String userName) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_name = ?"; // 使用占位符
        // String sql = "SELECT COUNT(*) FROM users WHERE user_name = ?"; // 使用占位符

        try ( PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userName); // 设置参数
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1); // 获取计数
                    return count > 0; // 如果计数大于 0，表示用户名存在
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 打印异常信息
        }
        return false; // 默认返回 false
    }
    // 检查密码是否存在
    public boolean checkPasswordExists(String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE password = ?"; // 使用占位符
        // String sql = "SELECT COUNT(*) FROM users WHERE user_name = ?"; // 使用占位符

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, password); // 设置参数
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1); // 获取计数
                    return count > 0; // 如果计数大于 0，表示用户名存在
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 打印异常信息
        }
        return false; // 默认返回 false
    }
    public void signUpQuit(ActionEvent event){
        Platform.exit();
    }
    //注册页面返回首页的方法
    public void back(ActionEvent event){
        //从Main创建实例，调用Main中返回首页的方法
        Main.getInstance().returnToFirstPage();
    }

}
