package com.roger.ltcschedule;

/**
 * Created by Administrator on 2017/9/14.
 */

public class RouteStopModel {

    private String stopId;
    private String stopName;
    private String routeNumber;
    private String direction;
    private String arrivalTime;
    private String destination;

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

    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getArrivalTime() { return arrivalTime; }

    public void setDestination(String destination) { this.destination = destination; }

    public String getDestination() { return destination; }

    @Override
    public String toString() {
        return stopId + " , " + routeNumber + " , " + direction + " , " + destination;
    }

}
