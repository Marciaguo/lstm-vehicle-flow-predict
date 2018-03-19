package com.udgrp.bean;

import org.jetbrains.annotations.NotNull;

/**
 * @author kejw
 * @version V1.0
 * @Project ud-spring-count-predict
 * @Description: TODO
 * @date 2018/1/29
 */
public class VehicleFlow implements Comparable<VehicleFlow> {
    //id
    private String id;
    //日期
    private String date;
    //道路
    private String highway;
    //进出口
    private String in_out_type;
    //小时
    private int hour;
    //车流量
    private double count;

    public VehicleFlow() {
    }

    public VehicleFlow(double count) {
        this.count = count;
    }

    public VehicleFlow(String id, String date, String highway, String in_out_type, int hour, double count) {
        this.id = id;
        this.date = date;
        this.highway = highway;
        this.in_out_type = in_out_type;
        this.hour = hour;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIn_out_type() {
        return in_out_type;
    }

    public void setIn_out_type(String in_out_type) {
        this.in_out_type = in_out_type;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public String getHighway() {
        return highway;
    }

    public void setHighway(String highway) {
        this.highway = highway;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @Override
    public int compareTo(@NotNull VehicleFlow o) {
        return this.getHour() - o.getHour();
    }
}
