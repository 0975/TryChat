package com.example.trychat.control.controller;

import com.example.trychat.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class UserMessageController {
    @FXML
    Label nameLabel;
    @FXML
    Label passwordLabel;
    @FXML
    Button back;
    private Stage stage;
    private Scene scene;
    private Parent root;
    public void display(String username){
        nameLabel.setText("恭喜"+username);
        passwordLabel.setText("注册成功 ");
    }
    public void back(ActionEvent event){

        Main.getInstance().returnToFirstPage();

        System.out.println("方法运行了");
    }
    public void signUpQuit(ActionEvent event){
        Platform.exit();
    }
}
