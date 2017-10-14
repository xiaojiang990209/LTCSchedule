package com.roger.ltcschedule;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/14.
 */

public class RouteStopModel {

    private String stopId;
    private String stopName;
    private String routeNumber;
    private String direction;
    private List<String> arrivalTime;
    private String destination;

    public RouteStopModel() {
        this.arrivalTime = new LinkedList<>();
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getStopName() {
        return stopName;
    }

    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }

    public String getRouteNumber() { return routeNumber; }

    public void setDirection(String direction) { this.direction = direction; }

    public String getDirection() { return direction; }

    public void addArrivalTime(String arrivalTime) { this.arrivalTime.add(arrivalTime); }

    public List<String> getArrivalTime() {
        return arrivalTime;
    }

    public boolean isArrivalTimeEmpty() {
        return arrivalTime.isEmpty();
    }

    public void setDestination(String destination) { this.destination = destination; }

    public String getDestination() { return destination; }

    private String getDirectionString() {
        switch(direction) {
            case "1": return "EASTBOUND";
            case "4": return "WESTBOUND";
            case "2": return "NORTHBOUND";
            case "3": return "SOUTHBOUND";
            default: return "ERROR";
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(stopName + " , ");
        sb.append(routeNumber + " , ");
        sb.append(getDirectionString() + " , ");
        sb.append(destination + "\n");
        sb.append(getArrivalTime());
        return sb.toString();
    }

}
