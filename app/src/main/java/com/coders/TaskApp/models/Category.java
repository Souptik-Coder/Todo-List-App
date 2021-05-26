package com.coders.TaskApp.models;

import java.util.UUID;

public class Category {
    String id;
    String name;


   public Category(){
        id= UUID.randomUUID().toString();
        name="";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
