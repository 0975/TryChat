package com.example.trychat.control;

import com.example.trychat.SockerNet.ConnectServer;
import com.example.trychat.Main;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SignInController {

    ChatController chatController;
    private double xOffset = 0;
    private double yOffset = 0;
    String selectedCity;
    @FXML
    private Circle circleIcon;
    Image image;
    Image image2;
    Image tomImage;
    Image tom2Image;
    static Stage stage;
    private Scene scene;
    private Pane root;
    @FXML
    TextField userIdTextField;
    @FXML
    PasswordField passwordField;
    private String[] cities = {
            "Beijing", "Shanghai", "Guangzhou", "Shenzhen", "Tianjin", "Chongqing", "Hangzhou", "Nanjing", "Wuhan", "Chengdu",
            "Xi'an", "Shenyang", "Qingdao", "Zhengzhou", "Changsha", "Harbin", "Hefei", "Fuzhou", "Xiamen", "Jinan",
            "Dalian", "Kunming", "Ningbo", "Nanchang", "Wenzhou", "Liuzhou", "Guiyang", "Tangshan", "Nanning", "Shijiazhuang",
            "Changchun", "Hohhot", "Lanzhou", "Urumqi", "Xining", "Haikou", "Yinchuan", "Zhongshan", "Maoming",
            "Zhuhai", "Foshan", "Jiangmen", "Zhaoqing", "Dongguan", "Huizhou", "Shantou", "Zhanjiang", "Guilin", "Nantong",
            "Yangzhou", "Changzhou", "Shaoxing", "Taizhou", "Jinhua", "Quzhou", "Wenzhou", "Lishui", "Handan", "Zibo"
    };

    public SignInController() throws Exception {
    }


    private void getCity(ActionEvent event) {
        String myFood = chatController.myChoiceBox.getValue();
        System.out.println(myFood);
    }

    public void initialize() {

        // 加载图片
        Image image = new Image("com/example/trychat/images/chiikawa.jpg");
        // 将图片填充到圆形
        circleIcon.setFill(new ImagePattern(image));


    }

    public void signIn(ActionEvent event) {
        Main.getInstance().getConnectServer().signAccount(userIdTextField.getText(), passwordField.getText(), success -> {
            Platform.runLater(() -> {
                if (success) {
                    // 登录成功的逻辑
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/trychat/control/chatController.fxml"));
                        root = loader.load();
                        // 获取聊天界面的控制器
                        root.setStyle("-fx-background-color: transparent;");

                        chatController = loader.getController();
                        updateChatTime();
                        // 创建一个 Timeline 每秒更新一次
                        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event1 -> {
                            updateChatTime();
                        }));
                        timeline.setCycleCount(Timeline.INDEFINITE); // 使其无限循环
                        timeline.play(); // 开始动画

                        //加载天气的方法
                        climateLoad();

                        //加载好友列表
                        friendsLoad();

                        //ConnectServer connectServer = new ConnectServer(chatController);
                        // 使用带参数的构造方法
                        // 设置 ChatController 到已经连接的 ConnectServer 实例
                        Main.getInstance().getConnectServer().setChatController(chatController);

                        System.out.println("聊天窗连接服务器成功");
                        chatController.setLabel(userIdTextField.getText(), 0); // 设置用户名
                        if (chatController.getDrawing() == false) {
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


                        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                        scene = new Scene(root, Color.TRANSPARENT);
                        String css = this.getClass().getResource("/com/example/trychat/control/application.css").toExternalForm();
                        scene.getStylesheets().add(css);
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    // 登录失败的逻辑
                    Main.getInstance().getPrimaryStage().setAlwaysOnTop(false);
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
            });
        });

    }

    private void friendsLoad() {
        chatController.allFriendsGroup.setTranslateY(14+11+77+10);
        chatController.allFriendsGroup.setPrefHeight(160);
        chatController.friendsFamily.setPrefWidth(180);
        chatController.friendsFamily.setTranslateX((250-180)/2);
    }

    //加载天气的办法
    private void climateLoad(){

        chatController.cityChoiceComponent.setTranslateY(14 + 77);
        chatController.cityChoiceComponent.setPrefHeight(12);

        chatController.myChoiceBox.setPrefHeight(11);
        chatController.myChoiceBox.setPrefWidth(80);
        chatController.myChoiceBox.setTranslateX(170 / 2);
        chatController.myChoiceBox.getItems().addAll(cities);

        // 创建 Tooltip鼠标悬浮提示工具
        Tooltip tooltip = new Tooltip("选择城市，出现对应天气");
        Tooltip.install(chatController.myChoiceBox, tooltip); // 将 Tooltip 安装到 ChoiceBox 上
        // 设置 Tooltip 的显示和隐藏延迟
        tooltip.setShowDelay(new Duration(100)); // 设置显示延迟为100毫秒
        tooltip.setHideDelay(new Duration(50)); // 设置隐藏延迟为50毫秒
        // 加载字体
        Font customFont = Font.loadFont(getClass().getResourceAsStream("/com/example/trychat/font/ShigurEinglish.ttf"), 16);

        // 检查字体是否加载成功
        if (customFont == null) {
            System.out.println("字体加载失败，请检查路径和文件格式。");
            return;
        }

        // 使用 CSS 设置 ChoiceBox 字体
        chatController.myChoiceBox.setStyle("-fx-font-family: 'ShigurEinglish'; -fx-font-size: 13px;"); // 修改字体名称和大小
        chatController.myChoiceBox.setOnAction(events -> {
            selectedCity = chatController.myChoiceBox.getValue();
            WeatherFetcher weatherFetcher = new WeatherFetcher();
            ArrayList<String> weather = weatherFetcher.getWeather(selectedCity);
            image2 = new Image("com/example/trychat/images/today.png");
            chatController.today.setImage(image2);
            //获得天气集合遍历，然后把每个天气放到对应的组件里
            for (int i = 0; i < weather.size(); i++) {
                String weathers = weather.get(i);
                System.out.println("第" + i + "天，天气是：" + weathers);
                switch (i) {
                    case 0:

                        switch (weathers) {
                            case "晴":
                                // 加载图片
                                image = new Image("com/example/trychat/images/qing.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "多云":
                                // 加载图片
                                image = new Image("com/example/trychat/images/duoyun.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "阴":
                                // 加载图片
                                image = new Image("com/example/trychat/images/yintian.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "小雨":
                                // 加载图片
                                image = new Image("com/example/trychat/images/xiaoyu.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "中雨":
                                // 加载图片
                                image = new Image("com/example/trychat/images/zhongyu.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "大雨":
                                // 加载图片
                                image = new Image("com/example/trychat/images/dayu.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "雷阵雨":
                                // 加载图片
                                image = new Image("com/example/trychat/images/leizhenyu.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "雪":
                                image = new Image("com/example/trychat/images/xue.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "小雪":
                                image = new Image("com/example/trychat/images/xiaoxue.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "大雪":
                                image = new Image("com/example/trychat/images/daxue.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "雾":
                                image = new Image("com/example/trychat/images/wu.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "霜":
                                image = new Image("com/example/trychat/images/shuang.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "冰雹":
                                image = new Image("com/example/trychat/images/bingbao.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "沙尘暴":
                                image = new Image("com/example/trychat/images/shabao.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "阵雨":
                                image = new Image("com/example/trychat/images/zhenyu.png");
                                chatController.todayWeather.setImage(image);
                                break;
                            case "有风":
                                break;
                            case "霾":
                                break;
                            case "冷":
                                break;
                            case "热":
                                break;
                            default:
                                System.out.println("其他");
                                break;
                        }
                        break;
                    case 1:
                        switch (weathers) {
                            case "晴":
                                // 加载图片
                                tomImage = new Image("com/example/trychat/images/qing2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "多云":
                                // 加载图片
                                tomImage = new Image("com/example/trychat/images/duoyun2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "阴":
                                // 加载图片
                                tomImage = new Image("com/example/trychat/images/yintian2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "小雨":
                                // 加载图片
                                tomImage = new Image("com/example/trychat/images/xiaoyu2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "中雨":
                                // 加载图片
                                tomImage = new Image("com/example/trychat/images/zhongyu2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "大雨":
                                // 加载图片
                                tomImage = new Image("com/example/trychat/images/dayu2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "雷阵雨":
                                // 加载图片
                                tomImage = new Image("com/example/trychat/images/leizhenyu2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "雪":
                                tomImage = new Image("com/example/trychat/images/xue2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "小雪":
                                tomImage = new Image("com/example/trychat/images/xiaoxue2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "大雪":
                                tomImage = new Image("com/example/trychat/images/daxue2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "雾":
                                tomImage = new Image("com/example/trychat/images/wu2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "霜":
                                tomImage = new Image("com/example/trychat/images/shuang2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "冰雹":
                                tomImage = new Image("com/example/trychat/images/bingbao2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "沙尘暴":
                                tomImage = new Image("com/example/trychat/images/shabao2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "阵雨":
                                tomImage = new Image("com/example/trychat/images/zhenyu2.png");
                                chatController.tom.setImage(tomImage);
                                break;
                            case "有风":
                                break;
                            case "霾":
                                break;
                            case "冷":
                                break;
                            case "热":
                                break;
                            default:
                                System.out.println("其他");
                                break;
                        }
                        break;
                    case 2:
                        switch (weathers) {
                            case "晴":
                                // 加载图片
                                // 加载图片
                                tom2Image = new Image("com/example/trychat/images/qing2.png");
                                chatController.tom1.setImage(tom2Image);

                                break;
                            case "多云":
                                tom2Image = new Image("com/example/trychat/images/duoyun2.png");
                                chatController.tom1.setImage(tom2Image);
                                break;
                            case "阴":
                                tom2Image = new Image("com/example/trychat/images/yintian2.png");
                                chatController.tom1.setImage(tom2Image);
                                break;
                            case "小雨":
                                tom2Image = new Image("com/example/trychat/images/xiaoyu2.png");
                                chatController.tom1.setImage(tom2Image);
                                break;
                            case "中雨":
                                tom2Image = new Image("com/example/trychat/images/zhongyu2.png");
                                chatController.tom1.setImage(tom2Image);
                                break;
                            case "大雨":
                                tom2Image = new Image("com/example/trychat/images/dayu2.png");
                                chatController.tom1.setImage(tom2Image);
                                break;
                            case "雷阵雨":
                                tom2Image = new Image("com/example/trychat/images/leizhenyu2.png");
                                chatController.tom1.setImage(tom2Image);
                                break;
                            case "雪":
                                tom2Image = new Image("com/example/trychat/images/xue2.png");
                                chatController.tom1.setImage(tom2Image);
                                break;
                            case "小雪":
                                tom2Image = new Image("com/example/trychat/images/xiaoxue2.png");
                                chatController.tom1.setImage(tom2Image);
                                break;
                            case "大雪":
                                tom2Image = new Image("com/example/trychat/images/daxue2.png");
                                chatController.tom1.setImage(tom2Image);
                                break;
                            case "雾":
                                tom2Image = new Image("com/example/trychat/images/wu2.png");
                                chatController.tom1.setImage(tom2Image);
                                break;
                            case "霜":
                                tom2Image = new Image("com/example/trychat/images/shuang2.png");
                                chatController.tom1.setImage(tom2Image);
                                break;
                            case "冰雹":
                                tom2Image = new Image("com/example/trychat/images/bingbao2.png");
                                chatController.tom1.setImage(tom2Image);
                                break;
                            case "沙尘暴":
                                tom2Image = new Image("com/example/trychat/images/shabao2.png");
                                chatController.tom1.setImage(tom2Image);
                                break;
                            case "阵雨":
                                tom2Image = new Image("com/example/trychat/images/zhenyu2.png");
                                chatController.tom1.setImage(tom2Image);
                                break;
                            case "有风":
                                break;
                            case "霾":
                                break;
                            case "冷":
                                break;
                            case "热":
                                break;
                            default:
                                System.out.println("其他");
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }


        });

        chatController.weatherComponent.setTranslateY(14 + 77 + 11);
        chatController.weatherComponent.setPrefHeight(48);
        chatController.todayWeatherComponent.setPrefHeight(48);
        chatController.todayWeatherComponent.setPrefWidth(48);
        chatController.todayWeatherComponent.setTranslateX(80);
        chatController.allWeatherComponent.setPrefHeight(48);
        chatController.allWeatherComponent.setPrefWidth(16);
        chatController.allFriendsGroup.setTranslateY(14 + 77 + 11 + 48);
        chatController.todayC.setPrefWidth(16);
        chatController.tomC.setPrefWidth(16);
        chatController.tomC1.setPrefWidth(16);
        chatController.allWeatherComponent.setTranslateX(80 + 16);
        chatController.todayWeatherComponent.setTranslateX((250 - 64) / 2);
    }
    //更新时间的方法
    private void updateChatTime() {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        Font customFont = Font.loadFont(getClass().getResourceAsStream("/com/example/trychat/font/ShigurEinglish.ttf"), 15);
        // 检查字体是否加载成功
        if (customFont == null) {
            System.out.println("字体加载失败，请检查路径和文件格式。");
            return;
        }

        chatController.myTime.setText(currentTimestamp.toString());
        chatController.myTimeComponent.setPrefHeight(14);
        chatController.myTimeComponent.setTranslateY(77);
        chatController.myTime.setTranslateX(84);
        // 获取文本的宽度并居中
        String text = chatController.myTime.getText();
        Text text1 = new Text(text);
        double textWidth = text1.getLayoutBounds().getWidth();
        chatController.myTime.setFont(customFont);
        chatController.myTime.setTranslateX((250 - textWidth) / 2);
    }

    public void signUpQuit(ActionEvent event) {

        Main.getInstance().getConnectServer().disconnectFromServer();
        Platform.exit();
    }

    //注册页面返回首页的方法
    public void back(ActionEvent event) {
        //从Main创建实例，调用Main中返回首页的方法
        Main.getInstance().returnToFirstPage();
    }

}
