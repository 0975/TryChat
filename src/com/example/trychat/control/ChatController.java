package com.example.trychat.control;

import com.example.trychat.Main;
import com.example.trychat.SockerNet.ConnectServer;
import com.example.trychat.dao.*;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;


import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;

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
    StackPane privateInformation;
    @FXML
    VBox privateGroup;
    @FXML
    HBox myTimeComponent;
    @FXML
    Label myTime;
    @FXML
    HBox cityChoiceComponent;
    @FXML
    ChoiceBox<String> myChoiceBox;
    @FXML
    HBox weatherComponent;
    @FXML
    VBox todayWeatherComponent;
    @FXML
    ImageView todayWeather;
    @FXML
    VBox allWeatherComponent;
    @FXML
    Label cityChoiceB;
    @FXML
    Label climateChoiceB;
    @FXML
    HBox todayC;
    @FXML
    ImageView today;
    @FXML
    HBox tomC;
    @FXML
    ImageView tom;
    @FXML
    HBox tomC1;
    @FXML
    ImageView tom1;
    @FXML
    HBox allFriendsGroup;
    @FXML
    ScrollPane friendsFamily;
    @FXML
    Pane ChatUi;
    private String[] cities = {
            "Beijing", "Shanghai", "Guangzhou", "Shenzhen", "Tianjin", "Chongqing", "Hangzhou", "Nanjing", "Wuhan", "Chengdu",
            "Xi'an", "Shenyang", "Qingdao", "Zhengzhou", "Changsha", "Harbin", "Hefei", "Fuzhou", "Xiamen", "Jinan",
            "Dalian", "Kunming", "Ningbo", "Nanchang", "Wenzhou", "Liuzhou", "Guiyang", "Tangshan", "Nanning", "Shijiazhuang",
            "Changchun", "Hohhot", "Lanzhou", "Urumqi", "Xining", "Haikou", "Yinchuan", "Zhongshan", "Maoming",
            "Zhuhai", "Foshan", "Jiangmen", "Zhaoqing", "Dongguan", "Huizhou", "Shantou", "Zhanjiang", "Guilin", "Nantong",
            "Yangzhou", "Changzhou", "Shaoxing", "Taizhou", "Jinhua", "Quzhou", "Wenzhou", "Lishui", "Handan", "Zibo"
    };

    private Stage stage;

    Timestamp currentTimestamp;

    private double lastX, lastY; // 记录上一个鼠标位置

    public ChatController() throws Exception {
    }

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
    private ScrollPane chatContentMy;
    @FXML
    private ScrollPane chatContentOther;
    @FXML
    private ImageView myImage;
    @FXML
    private ImageView otherImage;
    private Boolean canvasDraw = false;
    private ImageMessages imageMessages;
    public Boolean getDrawing() {
        return isDrawing;
    }

    public void setDrawing(Boolean drawing) {
        isDrawing = drawing;
    }
    private boolean isSendingMessage = false; // 新增标志
    //SSHExample sshExample = new SSHExample();

    private boolean isDragging = false;
    private double startX;
    private double startY;


    @FXML
    public void initialize() {

       // 创建 Tooltip
        Tooltip tooltip = new Tooltip("单击显示时间&天气，右击更改头像");
        Tooltip.install(circleIcon, tooltip); // 将 Tooltip 安装到 ChoiceBox 上
        // 设置 Tooltip 的显示和隐藏延迟
        tooltip.setShowDelay(new Duration(100)); // 设置显示延迟为100毫秒
        tooltip.setHideDelay(new Duration(50)); // 设置隐藏延迟为50毫秒
        privateInformation.setVisible(false);
        // 加载图片
        Image image = new Image("com/example/trychat/images/chiikawa.jpg");
        // 将图片填充到圆形
        circleIcon.setFill(new ImagePattern(image));
        loadAvatar();
// Set up mouse events
        circleIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                isDragging = false;
            }
        });
        circleIcon.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            isDragging = true;
            // Handle circle dragging logic here
        });
        circleIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY && !isDragging) {
                if (event.getClickCount() == 1) {
                    privateInformation.setVisible(true);
                } else if (event.getClickCount() == 2) {
                    privateInformation.setVisible(false);
                }
            }
        });

        // 添加右键点击事件
        circleIcon.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) { // 检查是否为右键点击
                showContextMenu(stage, circleIcon);
            }
        });
        circleIcon.setOnMouseDragged(event -> privateInformation.setVisible(false));
        circleIcon.setOnMouseDragExited(event -> privateInformation.setVisible(false));
        // 在这里可以安全地使用 otherMessage


        myCanvas = new Canvas(INITIAL_WIDTH, INITIAL_HEIGHT);

        // 设置鼠标悬浮事件
        myCanvas.setOnMouseEntered(event -> {
            myCanvas.setHeight(150);
            myCanvas.setWidth(200);
        });

       readMessage();
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


        if (otherMessage != null) {
            System.out.println("otherMessage has been initialized!");
        } else {
            System.out.println("otherMessage is null!");
        }

    }

    //选择文件的方法
    private void showContextMenu(Stage owner,Circle circle) {
        //不让界面一直在最上面
        Main.getInstance().getPrimaryStage().setAlwaysOnTop(false);
        // 打开文件选择框
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(owner);

        // 如果选择了文件，则替换头像
        if (selectedFile != null) {
            Image newImage = new Image(selectedFile.toURI().toString());
            circle.setFill(new ImagePattern(newImage)); // 用新图片填充圆形
            saveAvatarPath(selectedFile.getAbsolutePath()); // 保存路径
        } else {
            // 可以选择添加提示，表明未选择文件
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "未选择任何文件！", ButtonType.OK);
            alert.setTitle("提示");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }
    private void saveAvatarPath(String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("avatar.txt"))) {
            writer.write(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAvatar() {
        try (BufferedReader reader = new BufferedReader(new FileReader("avatar.txt"))) {
            String avatarPath = reader.readLine();
            if (avatarPath != null) {
                Image image = new Image(new File(avatarPath).toURI().toString());
                circleIcon.setFill(new ImagePattern(image)); // 用新图片填充圆形
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //阅读消息的方法
    private void readMessage() {
        chatContentMy.setOnMouseEntered(event -> {
            // 记录当前鼠标位置
            startX = event.getX();
            startY = event.getY();

        });
        chatContentMy.addEventHandler(MouseEvent.MOUSE_ENTERED,event -> {
            // 计算移动的方向
            double deltaX = event.getX() - startX;
            double deltaY = event.getY() - startY;

            // 根据方向滚动
            if (Math.abs(deltaY) > Math.abs(deltaX) && deltaY > 0) {
                // 向下滚动
                chatContentMy.setVvalue(chatContentMy.getVvalue() + 0.1);
            } else if (Math.abs(deltaY) > Math.abs(deltaX) && deltaY < 0) {
                // 向上滚动
                chatContentMy.setVvalue(chatContentMy.getVvalue() - 0.1);
            } else if (Math.abs(deltaX) > Math.abs(deltaY) && deltaX > 0) {
                // 向右滚动
                chatContentMy.setHvalue(chatContentMy.getHvalue() + 0.1);
            } else if (Math.abs(deltaX) > Math.abs(deltaY) && deltaX < 0) {
                // 向左滚动
                chatContentMy.setHvalue(chatContentMy.getHvalue() - 0.1);
            }

            // 更新开始位置
            startX = event.getX();
            startY = event.getY();
        });
        chatContentOther.setOnMouseEntered(event -> {
            // 记录当前鼠标位置
            startX = event.getX();
            startY = event.getY();

        });
        chatContentOther.addEventHandler(MouseEvent.MOUSE_ENTERED,event -> {
            // 计算移动的方向
            double deltaX = event.getX() - startX;
            double deltaY = event.getY() - startY;

            // 根据方向滚动
            if (Math.abs(deltaY) > Math.abs(deltaX) && deltaY > 0) {
                // 向下滚动
                chatContentOther.setVvalue( chatContentOther.getVvalue() + 0.2);
            } else if (Math.abs(deltaY) > Math.abs(deltaX) && deltaY < 0) {
                // 向上滚动
                chatContentOther.setVvalue( chatContentOther.getVvalue() - 0.2);
            } else if (Math.abs(deltaX) > Math.abs(deltaY) && deltaX > 0) {
                // 向右滚动
                chatContentOther.setHvalue( chatContentOther.getHvalue() + 0.2);
            } else if (Math.abs(deltaX) > Math.abs(deltaY) && deltaX < 0) {
                // 向左滚动
                chatContentOther.setHvalue( chatContentOther.getHvalue() - 0.2);
            }

            // 更新开始位置
            startX = event.getX();
            startY = event.getY();
        });
    }


    //按下回车的方法用KeyEvent事件监听
    public void pressEnter(KeyEvent event) throws IOException {

        javafx.scene.input.KeyCode code = event.getCode();
        isSendingMessage = true; // 设置标志为 true，表明正在发送消息
        if (code == KeyCode.valueOf("ENTER")){
            String message = messageTextField.getText().trim();
            //更精准的时间类
            currentTimestamp = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
            System.out.println(currentTimestamp);
            String sender_id = usernameLabel.getText();
            if (!message.isEmpty()) {
                addChatMessage(message);
                HandleMessages.getInstance().getOut().writeInt(0); // 0 for text
                HandleMessages.getInstance().getOut().writeUTF(sender_id+"-"+currentTimestamp+"-"+message); // 发送文本消息
                HandleMessages.getInstance().getOut().flush(); // 确保数据被发送
                messageTextField.clear();
            }





            //insertUser(sender_id,"gouzi",message,currentTimestamp,(byte) 0);
            isSendingMessage = false; // 新增标志

        }
    }
    public void sendMessage(ActionEvent event) throws IOException {

        isSendingMessage = true; // 设置标志为 true，表明正在发送消息
        String message = messageTextField.getText().trim();
        String senderId = usernameLabel.getText();
        // 更新数据库
        currentTimestamp = new Timestamp(System.currentTimeMillis());
        if (!message.isEmpty()) {
            addChatMessage(message);
            HandleMessages.getInstance().getOut().writeInt(0); // 0 for text
            HandleMessages.getInstance().getOut().writeUTF(senderId+" "+currentTimestamp+" "+message); // 发送文本消息
            HandleMessages.getInstance().getOut().flush(); // 确保数据被发送
            messageTextField.clear();


            //insertUser(senderId, "gouzi", message, currentTimestamp, (byte) 0);

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
                    //建立图片消息
                    imageMessages = new ImageMessages(saveCanvasToImage(myCanvas));
                    System.out.println(imageMessages.getImage());
                    myCanvas.setHeight(INITIAL_HEIGHT);
                    myCanvas.setWidth(INITIAL_WIDTH);
                    //发送图片消息
                    HandleMessages.getInstance().sendMessage(imageMessages);
                }


            });
        });





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



    //提示按钮
    public void showConfirmationDialog(Stage stage) {

        Main.getInstance().getPrimaryStage().setAlwaysOnTop(false);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("你真的要退出吗");
        alert.setContentText("点击OK关闭, 或 Cancel 停留.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {

               /* try {

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
                }*/
                System.exit(0);
                Platform.exit();
            }if (response == ButtonType.CANCEL){
                System.out.println("方法退出");
            }
        });

    }
   /* //将聊天记录数据传入数据库方法
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
    }*/
    //设置用户id和状态的方法
    public void setLabel(String username,int num){

        Font customFont = Font.loadFont(getClass().getResourceAsStream("/com/example/trychat/font/ShigurEinglish.ttf"), 15);
        // 检查字体是否加载成功
        if (customFont == null) {
            System.out.println("字体加载失败，请检查路径和文件格式。");
            return;
        }
        usernameLabel.setFont(customFont);
        usernameLabel.setText(username);
        status.setFont(customFont);
        status.setText(Integer.toString(num)+"online");
    }
    public void addChatMessage(String message) {
        // 创建聊天记录 Label
        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true); // 启用换行
        msgLabel.setStyle("-fx-background-color: #0000; -fx-padding: 5 20; -fx-font-size: 14px; -fx-letter-spacing: 8px;"); // 设置字间距
        // 设置 Label 的最大宽度，可以根据需要调整
        msgLabel.setMaxWidth(250); // 设置最大宽度
        // 添加到容器并执行淡出动画
        myMessage.getChildren().add(msgLabel);
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(30), msgLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> myMessage.getChildren().remove(msgLabel));
        fadeOut.play();

    }
    public void addOtherMessage(String message){
        System.out.println("Adding message to otherMessage: " + message);
        // 创建聊天记录 Label
        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true); // 启用换行
        msgLabel.setStyle("-fx-background-color: #0000; -fx-padding: 5 20; -fx-font-size: 14px; -fx-letter-spacing: 2px;"); // 设置字间距

        // 设置 Label 的最大宽度，可以根据需要调整
        msgLabel.setMaxWidth(250); // 设置最大宽度
        otherMessage.getChildren().add(msgLabel);

        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(30), msgLabel);
        fadeOut2.setFromValue(1.0);
        fadeOut2.setToValue(0.0);
        fadeOut2.setOnFinished(event -> otherMessage.getChildren().remove(msgLabel));
        fadeOut2.play();
    }
     private void addChatDrawMessage(WritableImage mydraw){
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
     }
    public void addOtherChatDrawMessage(WritableImage mydraw){
        System.out.println("绘画方法运行了");
        System.out.println(otherImageMessage);
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


    public void quitButton(ActionEvent event){
        showConfirmationDialog(stage);
    }


}
