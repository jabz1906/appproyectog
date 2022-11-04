package com.abogados.proyectog;

public class FormObject {
    protected Integer id;
    protected Integer parent_id;
    protected String color;
    protected String name;
    protected String description;
    protected String image;

    public FormObject(Integer id, Integer parent_id, String color, String name, String description, String image) {
        this.id = id;
        this.parent_id = parent_id;
        this.color = color;
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
