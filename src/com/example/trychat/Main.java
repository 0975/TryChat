package com.example.trychat;

import com.example.trychat.SockerNet.ConnectServer;
import com.example.trychat.control.ChatController;
import com.example.trychat.control.Sound;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;

/**
 * 这是启动程序的类
 * @author lihongjie
 * @version 1.0
 * */
public class Main extends Application {
    private double xOffset = 0;
    private double yOffset = 0;
    private ConnectServer connectServer = new ConnectServer();
    private static Main instance;
    private Stage primaryStage;
    private Scene signUp;
    public Sound se = new Sound();
    public ConnectServer getConnectServer() {
        return connectServer;
    }

    @FXML
    ImageView myImageView;
    @FXML
    Label myLabel;



    public void setConnectServer(ConnectServer connectServer) {
        this.connectServer = connectServer;
    }
/*
        单例模式
        私有构造函数：将类的构造函数定义为私有，以防止外部创建多个实例。
        静态变量：在类内部维护一个静态变量，用于存储唯一的实例。
        静态方法：提供一个静态方法，返回该静态变量的实例。如果实例尚未创建，则创建它并返回；如果已创建，则直接返回现有实例。
        **/

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }



    public static Main getInstance() {
        System.out.println("返回实例");
        return instance;
    }
    //返回首页的方法
    public void returnToFirstPage() {
        primaryStage.setScene(signUp);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{


        // 圣诞节
        LocalDate playDate = LocalDate.of(2024, 12, 25); // 设定播放日期
        // 春节
        LocalDate playDate2 = LocalDate.of(2025, 1, 29); // 设定播放日期

        LocalDate currentDate = LocalDate.now();

        // 播放圣诞节
        if (currentDate.equals(playDate)) {
           playSE(0);
           se.loop();
        }
        // 播放春节
        if (currentDate.equals(playDate2)) {
            playSE(1);
            se.loop();
        }




        this.primaryStage = primaryStage;
        instance = this;
        firstPage(primaryStage);

    }

    //加载首页的方法
    public void firstPage(Stage primaryStage) throws IOException {


        javafx.scene.text.Font customFont = Font.loadFont(getClass().getResourceAsStream("/com/example/trychat/font/ShigurEinglish.ttf"), 15);
        // 检查字体是否加载成功
        if (customFont == null) {
            System.out.println("字体加载失败，请检查路径和文件格式。");
            return;
        }

        Pane root = FXMLLoader.load(getClass().getResource("/com/example/trychat/control/sample.fxml"));
        root.setStyle("-fx-background-color: transparent;");

        signUp = new Scene(root, Color.TRANSPARENT);
        String css = this.getClass().getResource("/com/example/trychat/control/application.css").toExternalForm();
        signUp.getStylesheets().add(css);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        // 添加鼠标事件以支持拖动
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
// 设置窗口为始终在最前
        primaryStage.setAlwaysOnTop(true);

        primaryStage.setScene(signUp);
        primaryStage.show();
        connectServer.connectToServer();
        System.out.println("链接服务器成功");

        // 添加关闭事件处理
        primaryStage.setOnCloseRequest(event -> {
            connectServer.disconnectFromServer(); // 停止服务器连接
            System.out.println("链接服务器已关闭");
        });
    }

    public void playSE(int i){
        se.setFile(i);
        se.play();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
