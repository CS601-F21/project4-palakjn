package models;

import utilities.Strings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Holds the information of User
 *
 * @author Palak Jain
 */
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

    /**
     * Get the user Id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the user Id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the location
     */
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

    /**
     * Set the location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Get the phone
     */
    public String getPhone() {
        if(Strings.isNullOrEmpty(phone)&&
                !Strings.isNullOrEmpty(countryCode) &&
                !Strings.isNullOrEmpty(areaCode) &&
                !Strings.isNullOrEmpty(exchangeCode) &&
                !Strings.isNullOrEmpty(lineNumber)) {
            return String.format("%s-%s-%s-%s", countryCode, areaCode, exchangeCode, lineNumber);
        }

        return phone;
    }

    /**
     * Set the phone
     */
    public void setPhone(String phone) {
        if(!Strings.isNullOrEmpty(phone)) {
            String[] parts = phone.split("-");
            if (parts.length == 4) {
                countryCode = parts[0];
                areaCode = parts[1];
                exchangeCode = parts[2];
                lineNumber = parts[3];
                phone = String.format("%s-%s-%s-%s", countryCode, areaCode, exchangeCode, lineNumber);
            }
        }

        this.phone = phone;
    }

    /**
     * Get the image
     */
    public String getImage() {
        return image;
    }

    /**
     * Set the image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Get the date of birth
     * @param withFormat if true then will return in MM-DD-YYYY format else in YYYY-MM-DD
     */
    public String getDob(boolean withFormat) {
        if(withFormat) {
            return getDob();
        }
        else {
            return dob;
        }
    }

    /**
     * Get the date of birth in MM-DD-YYYY format
     */
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

    /**
     * Set the date of birth
     */
    public void setDob(String dob) {
        this.dob = dob;
    }

    /**
     * Get the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Set the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Get the state
     */
    public String getState() {
        return state;
    }

    /**
     * Set the state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Get the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Set the country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Get the zip
     */
    public String getZip() {
        return zip;
    }

    /**
     * Set the zip
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     * Get the country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Set the country code
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Get the area code
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * Set the area code
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    /**
     * Get exchange code
     */
    public String getExchangeCode() {
        return exchangeCode;
    }

    /**
     * Set the exchange order
     */
    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    /**
     * Get the line number
     */
    public String getLineNumber() {
        return lineNumber;
    }

    /**
     * Set the line number
     */
    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }
}
