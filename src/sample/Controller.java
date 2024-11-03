package sample;
import sample.SSHExample;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private Circle circleIcon;

    private Stage stage;
    private Scene scene;
    private Pane root;
    SSHExample sshExample = new SSHExample();

    public void initialize() {
        // 加载图片
        Image image = new Image("sample/images/chiikawa.jpg");
        // 将图片填充到圆形
        circleIcon.setFill(new ImagePattern(image));


    }
    public void signUp(ActionEvent event){

        try {


            sshExample.executeCommands();
            FXMLLoader loader =new FXMLLoader(getClass().getResource("fxml/signUp.fxml"));
            root = loader.load();
            root.setStyle("-fx-background-color: transparent;");
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root, Color.TRANSPARENT);
            String css = this.getClass().getResource("application.css").toExternalForm();
            scene.getStylesheets().add(css);

            root.setOnMousePressed(events -> {
                xOffset = events.getSceneX();
                yOffset = events.getSceneY();
            });

            root.setOnMouseDragged(events -> {
                stage.setX(events.getScreenX() - xOffset);
                stage.setY(events.getScreenY() - yOffset);
            });
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void signIn(ActionEvent event){
        try {
            FXMLLoader loader =new FXMLLoader(getClass().getResource("fxml/signInController.fxml"));
            root = loader.load();
            root.setStyle("-fx-background-color: transparent;");
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root, Color.TRANSPARENT);
            String css = this.getClass().getResource("application.css").toExternalForm();
            scene.getStylesheets().add(css);

            root.setOnMousePressed(events -> {
                xOffset = events.getSceneX();
                yOffset = events.getSceneY();
            });

            root.setOnMouseDragged(events -> {
                stage.setX(events.getScreenX() - xOffset);
                stage.setY(events.getScreenY() - yOffset);
            });
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void quit(ActionEvent event){
       Platform.exit();
    }
}
