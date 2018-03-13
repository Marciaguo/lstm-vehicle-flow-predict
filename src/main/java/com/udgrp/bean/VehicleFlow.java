package com.udgrp.bean;

import org.jetbrains.annotations.NotNull;

/**
 * @author kejw
 * @version V1.0
 * @Project ud-spring-flow-predict
 * @Description: TODO
 * @date 2018/1/29
 */
public class VehicleFlow implements Comparable<VehicleFlow> {

    private String stationId;
    private String date;
    private String inOutType;
    private int hour;
    private double flow;

    public VehicleFlow() {
    }

    public VehicleFlow(double flow) {
        this.flow = flow;
    }

    public VehicleFlow(String inOutType,String stationId, String date, int hour, double flow) {
        this.inOutType = inOutType;
        this.stationId = stationId;
        this.date = date;
        this.hour = hour;
        this.flow = flow;
    }

    public String getInOutType() {
        return inOutType;
    }

    public void setInOutType(String inOutType) {
        this.inOutType = inOutType;
    }

    public double getFlow() {
        return flow;
    }

    public void setFlow(double flow) {
        this.flow = flow;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
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
