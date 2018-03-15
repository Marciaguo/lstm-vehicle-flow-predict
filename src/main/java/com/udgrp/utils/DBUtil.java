package com.udgrp.utils;

import com.udgrp.bean.VehicleFlow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kejw
 * @version V1.0
 * @Project ud-spring-flow-predict
 * @Description: TODO
 * @date 2018/1/29
 */
public class DBUtil {
    private static ResultSet retsult = null;
    private static final String url = "jdbc:mysql://127.0.0.1/jtt_new?serverTimezone=UTC&characterEncoding=utf8&useUnicode=true&useSSL=false";
    private static final String name = "com.mysql.jdbc.Driver";
    private static final String user = "root";
    private static final String password = "root";

    private static Connection conn = null;
    private static PreparedStatement pst = null;

    public static List<String[]> readData(String inoutType, String type, String stationId) {
        int paraCount = 4; //读取参数数量
        String sql = "";
        if ("1".equals(inoutType)) {
            if ("train".equals(type)) {
                sql = "select station_id,date,hour,count from t_vehicle_in_out_count_for_predict where station_id = '" + stationId + "' AND in_out_type = '1' GROUP BY date,hour ORDER BY date,`hour`";
            } else if ("test".equals(type)) {
                sql = "select station_id,date,hour,count from t_vehicle_in_out_count_for_predict where station_id = '" + stationId + "' and in_out_type = '1' and date in ('2018-03-08','2018-03-09','2018-03-10','2018-03-11') GROUP BY date,hour ORDER BY date,`hour`";
            } else {
                sql = "select station_id,date,hour,count from t_vehicle_in_out_count_for_predict where station_id = '" + stationId + "' and in_out_type = '1' and date ='2018-03-11' GROUP BY date,hour ORDER BY date,`hour`";
            }
        } else {
            if ("train".equals(type)) {
                sql = "select station_id,date,hour,count from t_vehicle_in_out_count_for_predict where station_id = '" + stationId + "' AND in_out_type = '0' GROUP BY date,hour ORDER BY date,`hour`";
            } else if ("test".equals(type)) {
                sql = "select station_id,date,hour,count from t_vehicle_in_out_count_for_predict where station_id = '" + stationId + "' and in_out_type = '1' and date in ('2018-03-08','2018-03-09','2018-03-10','2018-03-11') GROUP BY date,hour ORDER BY date,`hour`";
            } else {
                sql = "select station_id,date,hour,count from t_vehicle_in_out_count_for_predict where station_id = '" + stationId + "' and in_out_type = '0' and date = '2018-03-11' GROUP BY date,hour ORDER BY date,`hour`";
            }
        }

        try {
            Class.forName(name);//指定连接类型
            conn = DriverManager.getConnection(url, user, password);//获取连接
            pst = conn.prepareStatement(sql);//准备执行语句
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String[]> list = new ArrayList<>();
        try {
            retsult = pst.executeQuery();//执行语句，得到结果集
            while (retsult.next()) {
                String[] paras = new String[paraCount];
                for (int i = 0; i < paraCount; i++) {
                    paras[i] = retsult.getString(i + 1);
                }
                list.add(paras);
            }
            retsult.close();
            conn.close();//关闭连接
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void insert(VehicleFlow vehicleFlow) {
        String sql = "insert into t_vehicle_in_out_count_predict(date,hour,in_out_type,station_id,count) values(?,?,?,?,?)";//SQL语句
        try {
            Class.forName(name);//指定连接类型
            conn = DriverManager.getConnection(url, user, password);//获取连接
            pst = conn.prepareStatement(sql);//准备执行语句
            pst.setString(1, vehicleFlow.getDate());
            pst.setInt(2, vehicleFlow.getHour());
            pst.setString(3, vehicleFlow.getInOutType());
            pst.setString(4, vehicleFlow.getStationId());
            pst.setDouble(5, vehicleFlow.getFlow());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            pst.executeUpdate();//执行语句
            conn.close();//关闭连接
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> getStationIds() {
        int paraCount = 1; //读取参数数量
        String sql = "select siteId from t_hp_bu_toll_border_station order by siteId desc";//SQL语句

        try {
            Class.forName(name);//指定连接类型
            conn = DriverManager.getConnection(url, user, password);//获取连接
            pst = conn.prepareStatement(sql);//准备执行语句
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String[]> list = new ArrayList<>();
        try {
            retsult = pst.executeQuery();//执行语句，得到结果集
            while (retsult.next()) {
                String[] paras = new String[paraCount];
                for (int i = 0; i < paraCount; i++) {
                    paras[i] = retsult.getString(i + 1);
                }
                list.add(paras);
            }
            retsult.close();
            conn.close();//关闭连接
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String[]> getStationIdLists() {
        List<String[]> list = new ArrayList<>();
        String[] a = {"4412-110-3"};
        String[] b = {"4412-114-13"};
        String[] d = {"4412-12-19"};
        String[] e = {"4412-120-1"};
        String[] f = {"4412-121-1"};
        String[] g = {"4412-129-1"};
        String[] h = {"4412-51-3"};
        String[] i = {"4412-64-19"};
        String[] j = {"4412-72-31"};
        String[] k = {"4412-73-5"};
        String[] l = {"4412-74-25"};
        String[] m = {"4412-78-11"};
        String[] n = {"4412-85-11"};
        String[] o = {"4412-85-21"};
        String[] p = {"4412-90-29"};
        String[] q = {"4412-90-3"};
        list.add(a);
        list.add(b);
        list.add(d);
        list.add(e);
        list.add(f);
        list.add(g);
        list.add(h);
        list.add(i);
        list.add(j);
        list.add(k);
        list.add(l);
        list.add(m);
        list.add(n);
        list.add(o);
        list.add(p);
        list.add(q);

        return list;
    }

    public static List<String[]> getLastDayData(String inoutType, String stationId) {
        int paraCount = 4; //读取参数数量
        String sql = "";
        if ("1".equals(inoutType)) {
            sql = "select station_id,date,hour,count from t_vehicle_in_out_count_for_predict where station_id = '" + stationId + "' AND in_out_type = '1' GROUP BY date,hour ORDER BY date,`hour`";//SQL语句
        } else {
            sql = "select station_id,date,hour,count from t_vehicle_in_out_count_for_predict where station_id = '" + stationId + "' AND in_out_type = '0' GROUP BY date,hour ORDER BY date,`hour`";//SQL语句
        }

        try {
            Class.forName(name);//指定连接类型
            conn = DriverManager.getConnection(url, user, password);//获取连接
            pst = conn.prepareStatement(sql);//准备执行语句
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String[]> list = new ArrayList<>();
        try {
            retsult = pst.executeQuery();//执行语句，得到结果集
            while (retsult.next()) {
                String[] paras = new String[paraCount];
                for (int i = 0; i < paraCount; i++) {
                    paras[i] = retsult.getString(i + 1);
                }
                list.add(paras);
            }
            retsult.close();
            conn.close();//关闭连接
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
