package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.*;

public class SignUpController {
    @FXML
    private Label myLabel;
    @FXML
    TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField userNameField;
    @FXML
    private TextField passwordTextField;



    private Stage stage;
    private Scene scene;
    private Pane root;

    String firstName;
    String lastName;
    String email;
    int age;
    String userName;
    String password;
    String user_name;
    @FXML
    private Circle circleIcon;//头像框
    @FXML
    Button signUpQuit;//退出按钮
    @FXML
    Button back;//返回按钮
    //修改头像的方法
    public void initialize() {
        // 加载图片
        Image image = new Image("sample/images/chiikawa.jpg");
        // 将图片填充到圆形
        circleIcon.setFill(new ImagePattern(image));


    }
    //注册页面返回首页的方法
    public void back(ActionEvent event){
        //从Main创建实例，调用Main中返回首页的方法
        Main.getInstance().returnToFirstPage();
    }
    //退出程序的方法
    public void signUpQuit(ActionEvent event){
        Platform.exit();
    }

    public void submit(ActionEvent event){

        try {
            userName = userNameField.getText();
            password = passwordTextField.getText();
            age = Integer.parseInt(ageField.getText());
            firstName = firstNameField.getText();
            lastName = lastNameField.getText();
            email = emailField.getText();
            password = passwordTextField.getText();
            if (age <18){
                myLabel.setText("你是未成年，不许注册");
                return;
            }
            System.out.println(age);
            FXMLLoader loader =new FXMLLoader(getClass().getResource("fxml/userMessage.fxml"));
            root = loader.load();
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            root.setStyle("-fx-background-color: transparent;");
            scene = new Scene(root,Color.TRANSPARENT);

            String css = this.getClass().getResource("application.css").toExternalForm();
            scene.getStylesheets().add(css);
            UserMessageController userMessageController = loader.getController();
            userMessageController.display(userName);
            user_name = insertUser(userName,firstName,lastName,age,email,password);


            //stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        }catch (NumberFormatException n){
            myLabel.setText("请输入数字");
        }
        catch (IOException e) {
            System.out.println(e);
        }


    }

    public String insertUser(String userName, String firstName, String lastName, int age, String email, String password) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = null;
            statement = connection.prepareStatement(
                    "INSERT INTO users (user_name, first_name, last_name, age, email, password) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, userName);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setInt(4, age);
            statement.setString(5, email);
            statement.setString(6, password);
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    String newUserName = generatedKeys.getString(1);
                    return newUserName;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return "注册失败,请稍后重试。"; // 在 catch 块中返回错误信息
        }
        return "注册失败,没有影响到任何行。";
    }
}
