package com.example.trychat.dao;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageMessages extends Messages{
    private WritableImage image;

    public ImageMessages(WritableImage image) {
        this.image = image;
    }

    public WritableImage getImage() {
        return image;
    }

    public void setImage(WritableImage image) {
        this.image = image;
    }

    @Override
    public byte[] getContent() {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        // 使用 ByteArrayOutputStream 存储字节数据
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            ImageIO.write(bufferedImage, "png", baos); // 可以选择其他格式，如 "jpg"
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isText() {
        return false;
    }

    @Override
    public boolean isImage() {
        return true;
    }

}
