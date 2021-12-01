package models;

import utilities.Strings;

public class User {
    private String id;
    private String name;
    private String email;
    private String location;
    private String phone;
    private String image;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        if(Strings.isNullOrEmpty(this.image)) {
            image = "/images/profilePic.png";
        }

        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
