package com.dji.DDS;

/**
 * Created by JORGE on 31/08/2017.
 */

public class TratamentoWaypoint {

    private int numeroWaypoint;
    private double latitude;
    private double longitude;
    private float altitude;
    private float speed;


    public TratamentoWaypoint(int numeroWaypoint,double lat, double longi,float altitude, float speed ) {
        this.numeroWaypoint =numeroWaypoint;
        setLatitude(lat);
        setLongitude(longi);
        this.setAltitude(altitude);
        this.setSpeed(speed);

    }







    @Override
    public String toString() {
        return "Ponto: "+numeroWaypoint+"\nLatitude: " + latitude + "\nLongitude: " + longitude+"\nAltitude: "+altitude+"    Speed: "+speed;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}

