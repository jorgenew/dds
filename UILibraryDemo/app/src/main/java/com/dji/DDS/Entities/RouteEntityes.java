package com.dji.DDS.Entities;

/**
 * Created by JORGE on 28/09/2017.
 */

public class RouteEntityes {

    public String id;
    public String name;
    public String userId;
    public String createdAt;
    public String updateAt;

    public RouteEntityes(String id, String name, String userId, String createdAt, String updateAt) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "RouteEntityes{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updateAt='" + updateAt + '\'' +
                '}';
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName_(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }


}
