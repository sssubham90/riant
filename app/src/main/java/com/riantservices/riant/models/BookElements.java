package com.riantservices.riant.models;

public class BookElements{
    private String pickup, destination, dateTime, distance, driver, contact, fare;
    public BookElements(String pickup, String destination, String dateTime, String distance, String driver, String contact, String fare) {
        this.pickup = pickup;
        this.destination = destination;
        this.dateTime = dateTime;
        this.distance = distance;
        this.driver = driver;
        this.contact = contact;
        this.fare = fare;
    }
    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDistance() {
        return distance;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDriver() {
        return driver;
    }

    public String getContact() {
        return contact;
    }

    public String getFare() { return fare;}
}