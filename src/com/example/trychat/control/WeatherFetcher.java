package com.example.trychat.control;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WeatherFetcher {

    private static final String TIANQI_DAILY_WEATHER_URL = "https://api.seniverse.com/v3/weather/daily.json";
    private static final String TIANQI_API_KEY = "SZVCXdd_EsG_ln3yN"; // 替换为你的 Seniverse API 密钥
    private String date;
    private String textDay;
    private String high;
    private String low;
    private String windSpeed;
    private String humidity;
    private ArrayList<String> weatherList = new ArrayList<>();

    public ArrayList<String> getWeatherList() {
        return weatherList;
    }

    public static String fetchDailyWeather(String location, int days) throws Exception {
        // 构建请求 URL
        String urlString = String.format("%s?key=%s&location=%s&language=zh-Hans&unit=c&start=0&days=%d",
                TIANQI_DAILY_WEATHER_URL,
                TIANQI_API_KEY,
                location,
                days
        );

        // 创建连接
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("GET");

        // 检查响应码
        if (conn.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString(); // 返回天气数据的 JSON 字符串
        } else {
            throw new Exception("请求失败，响应码: " + conn.getResponseCode());
        }
    }

    public ArrayList<String> getWeather(String city){
        try {
            String weatherData = fetchDailyWeather(city, 3);

            // 解析 JSON
            JSONObject jsonObject = new JSONObject(weatherData);
            JSONArray resultsArray = jsonObject.getJSONArray("results");
            JSONObject locationData = resultsArray.getJSONObject(0).getJSONObject("location");
            JSONArray dailyWeatherArray = resultsArray.getJSONObject(0).getJSONArray("daily");

            // 提取位置信息
            String locationName = locationData.getString("name");
            String country = locationData.getString("country");
            System.out.println("位置: " + locationName + ", " + country);
            // 清空列表以确保只存储最新的天气信息
            weatherList.clear();
            // 提取每日天气信息
            for (int i = 0; i < dailyWeatherArray.length(); i++) {
                JSONObject dailyWeather = dailyWeatherArray.getJSONObject(i);
                date = dailyWeather.getString("date");
                textDay = dailyWeather.getString("text_day");
                high = dailyWeather.getString("high");
                low = dailyWeather.getString("low");
                windSpeed = dailyWeather.getString("wind_speed");
                humidity = dailyWeather.getString("humidity");

                System.out.println("日期: " + date);
                System.out.println("白天气象: " + textDay);
                System.out.println("最高气温: " + high + "°C");
                System.out.println("最低气温: " + low + "°C");
                System.out.println("风速: " + windSpeed + " km/h");
                System.out.println("湿度: " + humidity + "%");
                System.out.println("--------------------------------");

                // 将当天的天气添加到列表的第一位
                if (i == 0) {
                    weatherList.add(0, textDay); // 将当天的天气放在第一位
                } else {
                    weatherList.add(textDay); // 其他天气信息依次添加
                }
            }



        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return weatherList;
    }
}