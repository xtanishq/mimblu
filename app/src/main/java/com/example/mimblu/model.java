package com.example.mimblu;

public class model {
    String id,title,state_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public model(String id, String title, String state_id) {
        this.id = id;
        this.title = title;
        this.state_id = state_id;
    }
}
