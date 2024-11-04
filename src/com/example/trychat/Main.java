package com.example.trychat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
/**
 * 这是启动程序的类
 * @author lihongjie
 * @version 1.0
 * */
public class Main extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    private static Main instance;
    private Stage primaryStage;
    private Scene signUp;
    /*
        单例模式
        私有构造函数：将类的构造函数定义为私有，以防止外部创建多个实例。
        静态变量：在类内部维护一个静态变量，用于存储唯一的实例。
        静态方法：提供一个静态方法，返回该静态变量的实例。如果实例尚未创建，则创建它并返回；如果已创建，则直接返回现有实例。
        **/

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
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
        this.primaryStage = primaryStage;
        instance = this;
        firstPage(primaryStage);
    }

    //加载首页的方法
    public void firstPage(Stage primaryStage) throws IOException {
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
