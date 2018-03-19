package com.udgrp.utils;

import com.udgrp.bean.VehicleFlow;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author kejw
 * @version V1.0
 * @Project ud-spring-flow-predict
 * @Description: TODO
 * @date 2018/1/29
 */
public class DBUtil {
    public static SqlSessionFactory sessionFactory;

    static {
        try {
            //使用MyBatis提供的Resources类加载mybatis的配置文件
            Reader reader = Resources.getResourceAsReader("mybatis/mybatis-config.xml");
            //构建sqlSession的工厂
            sessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //创建能执行映射文件中sql的sqlSession
    public static SqlSession getSession() {
        return sessionFactory.openSession();
    }

    /**
     * 读取数据库数据
     *
     * @param params
     * @param type   train为训练数据，test为测试数据，predict为预测数据
     * @return
     */
    public static List<VehicleFlow> readData(HashMap<String, String> params, String type) {
        List<VehicleFlow> vehicleFlows;
        if (Constant.TRAIN.equals(type)) {
            vehicleFlows = getSession().selectList("gettraindata", params);
        } else if (Constant.TEST.equals(type)) {
            vehicleFlows = getSession().selectList("gettestdata", params);
        } else {
            vehicleFlows = getSession().selectList("getlatelyday", params);
        }
        return vehicleFlows;
    }

    /**
     * 批量保存预测后的数据
     *
     * @param vehicleFlows
     */
    public static void batchInsert(List<VehicleFlow> vehicleFlows) {
        try {
            SqlSession session = getSession();
            session.insert("batchInsert", vehicleFlows);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //程序写死便于测试
    public static List<String> getHighWay() {
        /**
         * 数据不全的高速
         *
         * 包茂高速
         梅大东延
         渝湛高速
         */
        List<String> list = new ArrayList<>();
        String[] arr = {/*"汕汾高速",*/ "大广高速", "平兴高速", "潮漳高速", "京珠北", "清连高速", "粤赣高速", "韶赣高速", "天汕高速", "云梧高速", "二广高速"};

       // String[] arr = {"汕汾高速"};
        for (String a : arr) {
            list.add(a);
        }
        return list;
    }
}
