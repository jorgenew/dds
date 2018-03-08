package com.dji.DDS.Entities;

/**
 * Created by JORGE on 28/09/2017.
 */

public class ActionsEntityes {

    public String id;
    public String device;
    public String action;
    public String droneWaypointId;
    public String createAt;
    public String updateeAt;


    public ActionsEntityes(String id, String device, String action, String droneWaypointId, String createAt, String updateeAt) {
        this.id = id;
        this.device = device;
        this.action = action;
        this.droneWaypointId = droneWaypointId;
        this.createAt = createAt;
        this.updateeAt = updateeAt;
    }

    @Override
    public String toString() {
        return "ActionsEntityes{" +
                "id='" + id + '\'' +
                ", device='" + device + '\'' +
                ", action='" + action + '\'' +
                ", droneWaypointId='" + droneWaypointId + '\'' +
                ", createAt='" + createAt + '\'' +
                ", updateeAt='" + updateeAt + '\'' +
                '}';
    }
}
