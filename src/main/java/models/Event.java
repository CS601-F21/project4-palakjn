package models;

import utilities.Strings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class Event {
    private String id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String place;
    private String date;
    private String from;
    private int duration;
    private String durationString;
    private String imageUrl;
    private int availability;
    private int total;
    private String description;
    private String shortDescription;
    private String hostId;
    private String language;
    private String genre;

    public Event(String id, String name, String date, String from, int duration) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.from = from;
        this.duration = duration;
        setDurationString();
    }

    public Event() {}

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

    public String getPlace() {
         return String.format("%s, %s, %s, %s, %s", address, city, state, country, zip);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate(boolean withFormat) {
        if(withFormat) {
            return getDate();
        }
        else {
            return date;
        }
    }

    public String getDate() {
        try {
            if(!Strings.isNullOrEmpty(this.date)) {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(this.date);
                DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

                return dateFormat.format(date);
            }
        } catch (java.text.ParseException exception) {
            exception.printStackTrace();
        }

        return this.date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String time) {
        this.from = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDurationString() {
        Duration d = Duration.ofMinutes(duration);
        long days = d.toDaysPart();
        long hours = d.toHoursPart();
        long minutes = d.toMinutesPart();
        if(days > 0) {
            durationString = String.format("%d days, %d hour and %d minutes", days, hours, minutes);
        }
        else if(hours > 0) {
            if(minutes > 0) {
                durationString = String.format("%d hour and %d minutes", hours, minutes);
            }
            else {
                durationString = String.format("%d hour", hours);
            }
        }
        else if(minutes > 0) {
            durationString = String.format("%s minutes", minutes);
        }
        else {
            durationString = "";
        }
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDurationString() {
        return this.durationString;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getImageUrl() {
        if(Strings.isNullOrEmpty(imageUrl)) {
            imageUrl = "/images/default.png";
        }

        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        if(!Strings.isNullOrEmpty(description)) {
            if(description.length() > 100) {
                shortDescription = description.substring(0, 99);
            }
        }

        return shortDescription;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
