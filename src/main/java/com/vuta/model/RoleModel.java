package com.vuta.model;

import java.util.List;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */
public class RoleModel {

    private int id;
    private String name;
    private List<String> permissions;

    public RoleModel() {
    }

    public RoleModel(int id, String name, List<String> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
