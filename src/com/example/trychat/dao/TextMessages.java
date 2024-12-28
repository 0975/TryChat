package com.example.trychat.dao;

public class TextMessages extends Messages{
    private String textContent;

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public TextMessages(String textContent) {
        this.textContent = textContent;
    }
    @Override
    public byte[] getContent() {
        return textContent.getBytes(); // 将文本转换为字节数组
    }

    @Override
    public boolean isText() {
        return true;
    }

    @Override
    public boolean isImage() {
        return false;
    }
}
