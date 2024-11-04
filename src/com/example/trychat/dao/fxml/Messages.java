package com.example.trychat.dao.fxml;

import javafx.scene.image.WritableImage;

import java.util.ArrayList;

public abstract class Messages {
    // 抽象方法：获取消息内容
    public abstract byte[] getContent();

    // 判断消息类型
    public abstract boolean isText();
    public abstract boolean isImage();
}
