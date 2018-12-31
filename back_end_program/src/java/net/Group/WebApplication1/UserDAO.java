/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.Group.WebApplication1;

/**
 *
 * @author Administrator
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author Administrator
 */
public class UserDAO {

    
    public static void signIn(String room, String time, String lastTime, String Couple) {
        //获得数据库的连接对象
        Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        //生成SQL代码
        StringBuilder sqlStatement = new StringBuilder();
        sqlStatement.append("insert into signin values(?,?,?)");

        //设置数据库的字段值
        try {
            int hour = Integer.parseInt(time.split(";")[3]);
            int lstTime = Integer.parseInt(lastTime);
            for (int i = 0; i < lstTime; i++) {
                String time1 = time.split(";")[0]+";"+time.split(";")[1]+";"+time.split(";")[2]+";"+(hour+i);
                preparedStatement = connection.prepareStatement(sqlStatement.toString());
                preparedStatement.setString(1, room);
                preparedStatement.setString(2, time1);
                preparedStatement.setString(3, Couple);
                preparedStatement.executeUpdate();
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBManager.closeAll(connection, preparedStatement, resultSet);
        }
    }

    public static ArrayList<String> querySign(String time) {
        //获得数据库的连接对象
        Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ArrayList<String> arr = new ArrayList<String>();
        //生成SQL代码
        StringBuilder sqlStatement = new StringBuilder();
        sqlStatement.append("SELECT * FROM signin where time = ?");

        //设置数据库的字段值
        try {
            preparedStatement = connection.prepareStatement(sqlStatement.toString());
            preparedStatement.setString(1, time);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                String room = resultSet.getString("room");
                String cp = resultSet.getString("Couple");
                String record = room+";"+cp;
                arr.add(record);
            } 
            return arr;
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            DBManager.closeAll(connection, preparedStatement, resultSet);
        }
    }
    
    public static ArrayList<String> query(String time) {
        //获得数据库的连接对象
        Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ArrayList<String> arr = new ArrayList<String>();
        //生成SQL代码
        StringBuilder sqlStatement = new StringBuilder();
        sqlStatement.append("SELECT * FROM room");

        //设置数据库的字段值
        try {
            preparedStatement = connection.prepareStatement(sqlStatement.toString());
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                String room = resultSet.getString("room");
                String roomType = resultSet.getString("roomType");
                String building = resultSet.getString("building");
                String sequence = resultSet.getString("sequence");
                String record = room+";"+roomType+";"+building+";"+sequence;
                arr.add(record);
            } 
            return arr;
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            DBManager.closeAll(connection, preparedStatement, resultSet);
        }
    }
    
    public static void Delete(){
        Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = null;
        StringBuilder sqlStatement = new StringBuilder();
        sqlStatement.append("delete from room");
        try {
            System.out.println("qqqqqqqqqq");
            preparedStatement = connection.prepareStatement(sqlStatement.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("sssssssssss");
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void addStory(String userName,String room,String story,String time,String pic){
        Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = null;
        StringBuilder sqlStatement = new StringBuilder();
        sqlStatement.append("insert into story values(?,?,?,?,?)");
        try {
            System.out.println("qqqqqqqqqq");
            preparedStatement = connection.prepareStatement(sqlStatement.toString());
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, room);
            preparedStatement.setString(3, story);
            preparedStatement.setString(4, time);
            preparedStatement.setString(5, pic);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("sssssssssss");
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static ArrayList<String> story(String room){
        ArrayList<String> arr = new ArrayList<String>();
        Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        StringBuilder sqlStatement = new StringBuilder();
        sqlStatement.append("select * from story where room =?");
        try {
            System.out.println("qqqqqqqqqq");
            preparedStatement = connection.prepareStatement(sqlStatement.toString());
            preparedStatement.setString(1, room);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                String userName = resultSet.getString("userName");
                String story = resultSet.getString("story");
                String time1 = resultSet.getString("time");
                String pic = resultSet.getString("pic");
                String[] t = time1.split(";");
                String time = t[1]+"/"+t[2];
                String record = userName+"!@"+story+"!@"+time+"!@"+pic;
                arr.add(record);
            }
            return arr;
        } catch (SQLException ex) {
            System.out.println("sssssssssss");
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static boolean checkExists(String time){
        Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        StringBuilder sqlStatement = new StringBuilder();
        sqlStatement.append("select * from record where time =?");
        try {
            System.out.println("qqqqqqqqqq");
            preparedStatement = connection.prepareStatement(sqlStatement.toString());
            preparedStatement.setString(1, time);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }else{
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("sssssssssss");
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

}
