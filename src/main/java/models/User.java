package models;

import utilities.Strings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    private String id;
    private String name;
    private String email;
    private String location;
    private String phone;
    private String image;
    private String dob;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String countryCode;
    private String areaCode;
    private String exchangeCode;
    private String lineNumber;

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
        if(Strings.isNullOrEmpty(location) &&
            !Strings.isNullOrEmpty(address) &&
            !Strings.isNullOrEmpty(city) &&
            !Strings.isNullOrEmpty(state) &&
            !Strings.isNullOrEmpty(country) &&
            !Strings.isNullOrEmpty(zip)) {
            location = String.format("%s %s %s %s %s", address, city, state, country, zip);
        }

        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if(!Strings.isNullOrEmpty(phone)) {
            String[] parts = phone.split("-");
            if (parts.length == 4) {
                countryCode = parts[0];
                areaCode = parts[1];
                exchangeCode = parts[2];
                lineNumber = parts[3];
                phone = String.format("+%s-%s-%s-%s", countryCode, areaCode, exchangeCode, lineNumber);
            }
        }

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

    public String getDob(boolean withFormat) {
        if(withFormat) {
            return getDob();
        }
        else {
            return dob;
        }
    }

    public String getDob() {
        try {
            if(!Strings.isNullOrEmpty(this.dob)) {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(this.dob);
                DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

                return dateFormat.format(date);
            }
        } catch (java.text.ParseException exception) {
            exception.printStackTrace();
        }

        return this.dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }
}
