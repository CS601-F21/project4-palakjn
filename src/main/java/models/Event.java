package models;

import utilities.Strings;

import java.time.Duration;

public class Event {
    private String id;
    private String name;
    private String venue;
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
    private String host;
    private String shortDescription;
    private String hostId;
    private String language;
    private String genre;

    public Event(String id, String name, String place, String date, String from, int duration) {
        this.id = id;
        this.name = name;
        this.place = place;
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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
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
        if(!Strings.isNullOrEmpty(place)) {
            return place;
        } else {
            return String.format("%s, %s, %s, %s, %s", venue, city, state, country, zip);
        }
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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
