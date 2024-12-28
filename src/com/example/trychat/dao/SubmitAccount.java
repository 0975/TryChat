package com.example.trychat.dao;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SubmitAccount {
    private DataOutputStream out; // 文本消息输出流
    private DataInputStream in; // 文本消息输入流

    public SubmitAccount() {
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    public DataInputStream getIn() {
        return in;
    }

    public void setIn(DataInputStream in) {
        this.in = in;
    }


}
