package com.jikexueyuan.taxibookingserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

//数据库操作
public class OrderDatabase  {

    private Connection c = null;
    private Statement s = null;

    public OrderDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:order.db");
            s = c.createStatement();
        } catch (ClassNotFoundException e) {
            System.err.println("Class error!");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createDatabase(String tablename) {
        String clear = "drop table if exists "+tablename;
        String sql = "create table "+tablename+" (" +
                "_id integer primary key autoincrement not null," +
                "driver text    not null," +
                "user   text    not null," +
                "state  text    not null)";
        try {
            s.executeUpdate(clear);
            s.executeUpdate(sql);
            System.out.println("创建数据表:"+tablename);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(String tablename, String key, String value) {
        String sql = "insert into "+tablename+
                "("+key+") " +
                "values " +
                "('"+value+"');";
        try {
            s.executeUpdate(sql);
            System.out.println("sql = " + sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(String tablename, String [] keys, String [] values) {
        String sql = "insert into "+tablename+ "(";
        for (int i = 0;i<keys.length;i++) {
            sql += keys[i];
            if (i==keys.length-1) {
                sql += ")";
            } else {
                sql += ",";
            }
        }

        sql += " values (";

        for (int i = 0;i<values.length;i++) {
            sql += "'"+values[i]+"'";
            if (i==values.length-1) {
                sql += ");";
            } else {
                sql += ",";
            }
        }


        try {
            System.out.println("sql = " + sql);
            s.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String tablename, String key, String value, String conditionKey, String conditionValue) {
        String sql = "update "+tablename+" set "+key+"='"+value+"' where "+conditionKey+"='"+conditionValue+"';";
        System.out.println("sql = " + sql);
        try {
            s.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String tablename, String conditionKey, String conditionValue) {
        String sql = "delete from "+tablename+" where "+conditionKey+"="+conditionValue+";";
        try {
            System.out.println("sql = " + sql);
            s.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
