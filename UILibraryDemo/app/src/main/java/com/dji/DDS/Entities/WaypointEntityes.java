package com.dji.DDS.Entities;

/**
 * Created by JORGE on 28/09/2017.
 */

public class WaypointEntityes {

    public  String id;
    public  String latitude;
    public  String longitude;
    public  String droneRouteId;
    public  String createAt;
    public  String cupdateAt;


    public WaypointEntityes(String id, String latitude, String longitude, String droneRouteId, String createAt, String cupdateAt) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.droneRouteId = droneRouteId;
        this.createAt = createAt;
        this.cupdateAt = cupdateAt;
    }

    @Override
    public String toString() {
        return "WaypointEntityes{" +
                "id='" + id + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", droneRouteId='" + droneRouteId + '\'' +
                ", createAt='" + createAt + '\'' +
                ", cupdateAt='" + cupdateAt + '\'' +
                '}';
    }
}
