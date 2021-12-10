package models;

import utilities.Strings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;

/**
 * Class holding all the information regarding event.
 *
 * @author Palak Jain
 */
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

    /**
     * @return the event Id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the event Id
     * @param id Unique Id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Get the event name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of an event
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Get the address
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
     * Combine address, city, state, country and zip and return the
     */
    public String getPlace() {
         return String.format("%s, %s, %s, %s, %s", address, city, state, country, zip);
    }

    /**
     * Set the event date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Get the event date
     *
     * @param withFormat if true then will return date in MM-dd-yyyy format else will return as it
     */
    public String getDate(boolean withFormat) {
        if(withFormat) {
            return getDate();
        }
        else {
            return date;
        }
    }

    /**
     * Get the event date in MM-DD-YYYY format
     */
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

    /**
     * Get the start time of event
     */
    public String getFrom() {
        return from;
    }

    /**
     * Set the start time of event
     */
    public void setFrom(String time) {
        this.from = time;
    }

    /**
     * Get the duration of event
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Set the duration of event in days, hours, minutes format
     */
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

    /**
     * Set the description having 100 words
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Get the duration of event in days, hours, minutes format
     */
    public String getDurationString() {
        return this.durationString;
    }

    /**
     * Set the duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Get the image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Set the image url
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Get the number of available seats
     */
    public int getAvailability() {
        return availability;
    }

    /**
     * Set the number of available seats
     */
    public void setAvailability(int availability) {
        this.availability = availability;
    }

    /**
     * Get the number of total seats
     */
    public int getTotal() {
        return total;
    }

    /**
     * Set the number of total seats
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Get the full description of an event
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the full description of an event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Truncates the long description to the string with 100 letters
     */
    public String getShortDescription() {
        if(!Strings.isNullOrEmpty(description)) {
            if(description.length() > 100) {
                shortDescription = description.substring(0, 99);
            }
        }

        return shortDescription;
    }

    /**
     * Get the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Set the genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Get the host id
     */
    public String getHostId() {
        return hostId;
    }

    /**
     * Set the host id
     */
    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    /**
     * Get the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set the language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Returns true if the event is an upcoming event else false
     */
    public boolean isUpcomingEvent() {
        boolean isUpcoming = false;

        try {
            if(!Strings.isNullOrEmpty(this.date)) {
                Date eventDate = new SimpleDateFormat("yyyy-MM-dd").parse(this.date);
                Date currentDate = new Date(System.currentTimeMillis());
                if(eventDate.after(currentDate) || eventDate.equals(currentDate)) {
                    isUpcoming = true;
                }
            }
        } catch (java.text.ParseException exception) {
            exception.printStackTrace();
        }

        return isUpcoming;
    }

    /**
     * Returns true if the event is past event else false
     */
    public boolean isPastEvent() {
        boolean isPast = false;

        try {
            if(!Strings.isNullOrEmpty(this.date)) {
                Date eventDate = new SimpleDateFormat("yyyy-MM-dd").parse(this.date);
                Date currentDate = new Date(System.currentTimeMillis());
                if(eventDate.before(currentDate)) {
                    isPast = true;
                }
            }
        } catch (java.text.ParseException exception) {
            exception.printStackTrace();
        }

        return isPast;
    }
}
