package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/*
 * 连接数据库的类
 * */
public class DatabaseConnection {
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // 直接使用 DriverManager 获取连接
            connection = DriverManager.getConnection(
                    "jdbc:mysql://1.94.30.181:3306/mydb",
                    "root",
                    "Xj5200127@"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
