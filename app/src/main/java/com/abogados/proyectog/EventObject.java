package com.abogados.proyectog;

public class EventObject {
    protected Integer id;
    protected String color;
    protected String formName;
    protected String userName;
    protected String registered;

    public EventObject(Integer id, String color, String formName, String userName, String registered) {
        this.id = id;
        this.color = color;
        this.formName = formName;
        this.userName = userName;
        this.registered = registered;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }
}
