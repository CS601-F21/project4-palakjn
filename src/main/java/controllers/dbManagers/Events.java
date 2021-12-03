package controllers.dbManagers;

import controllers.DataSource;
import models.Event;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Events {

    public static boolean insert(Event event) {
        boolean isInserted = false;
        Time from = getTime(event.getFrom());
        if(from == null) {
            return false;
        }

        try(Connection con = DataSource.getConnection()) {
            String query = "INSERT INTO events VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, event.getId());
            statement.setString(2, event.getName());
            statement.setString(3, event.getAddress());
            statement.setString(4, event.getCity());
            statement.setString(5, event.getState());
            statement.setString(6, event.getCountry());
            statement.setString(7, event.getZip());
            statement.setDate(8, Date.valueOf(event.getDate(false)));
            statement.setTime(9, from);
            statement.setInt(10, event.getDuration());
            statement.setString(11, event.getImageUrl());
            statement.setInt(12, event.getAvailability());
            statement.setInt(13, event.getTotal());
            statement.setString(14, event.getDescription());
            statement.setString(15, event.getHostId());
            statement.setString(16, event.getLanguage());
            statement.setString(17, event.getGenre());
            statement.executeUpdate();

            isInserted = true;
        } catch (SQLException sqlException) {
            System.err.printf("Error while inserting event %s to the table. %s\n", event.getName(), sqlException.getMessage());
        }

        return isInserted;
    }

    public static List<Event> getEvents() {
        List<Event> allEvents = new ArrayList<>();

        try(Connection con = DataSource.getConnection()) {
            String query = "SELECT id, name, imageUrl, address, city, state, country, zip, fromTime, duration, date, description, hostId FROM events;";
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Event event = new Event(resultSet.getString("id"),
                                        resultSet.getString("name"),
                                        resultSet.getDate("date").toString(),
                                        resultSet.getTime("fromTime").toString(),
                                        resultSet.getInt("duration"));
                event.setImageUrl(resultSet.getString("imageUrl"));
                event.setDescription(resultSet.getString("description"));
                event.setHostId(resultSet.getString("hostId"));
                event.setAddress(resultSet.getString("address"));
                event.setCity(resultSet.getString("city"));
                event.setState(resultSet.getString("state"));
                event.setCountry(resultSet.getString("country"));
                event.setZip(resultSet.getString("zip"));

                allEvents.add(event);
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting all events from the table. %s\n", sqlException.getMessage());
            allEvents = null;
        }

        return allEvents;
    }

    public static Event getEvent(String id) {
        Event event = null;

        try(Connection con = DataSource.getConnection()) {
            String query = "SELECT * FROM events WHERE id = ?;";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                event = new Event(resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getDate("date").toString(),
                        resultSet.getTime("fromTime").toString(),
                        resultSet.getInt("duration"));
                event.setImageUrl(resultSet.getString("imageUrl"));
                event.setAvailability(resultSet.getInt("availability"));
                event.setTotal(resultSet.getInt("total"));
                event.setDescription(resultSet.getString("description"));
                event.setHostId(resultSet.getString("hostId"));
                event.setLanguage(resultSet.getString("language"));
                event.setGenre(resultSet.getString("genre"));
                event.setAddress(resultSet.getString("address"));
                event.setCity(resultSet.getString("city"));
                event.setState(resultSet.getString("state"));
                event.setCountry(resultSet.getString("country"));
                event.setZip(resultSet.getString("zip"));
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting %s event information from the table. %s\n", id, sqlException.getMessage());
            event = null;
        }

        return event;
    }

    public static Event getEventSeats(String id) {
        Event event = null;

        try(Connection con = DataSource.getConnection()) {
            String query = "SELECT availability, total FROM events WHERE id = ?;";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                event = new Event();
                event.setAvailability(resultSet.getInt("availability"));
                event.setTotal(resultSet.getInt("total"));
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting %s event information from the table. %s\n", id, sqlException.getMessage());
            event = null;
        }

        return event;
    }

    public static boolean updateEvent(Event event, boolean withImage) {
        boolean isUpdated;
        Time from = getTime(event.getFrom());
        if(from == null) {
            return false;
        }

        try(Connection con = DataSource.getConnection()) {
            String query;

            if(withImage) {
                query = "UPDATE events SET name = ?, date = ?, fromTime = ?, duration = ?, availability = ?, total = ?, description = ?, hostId = ?, language = ?, genre = ?, address = ?, city = ?, state = ?, country = ?, zip = ?, imageUrl = ? WHERE id = ?";
            } else {
                query = "UPDATE events SET name = ?, date = ?, fromTime = ?, duration = ?, availability = ?, total = ?, description = ?, hostId = ?, language = ?, genre = ?, address = ?, city = ?, state = ?, country = ?, zip = ? WHERE id = ?";
            }
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, event.getName());
            statement.setDate(2, Date.valueOf(event.getDate(false)));
            statement.setTime(3, from);
            statement.setInt(4, event.getDuration());
            statement.setInt(5, event.getAvailability());
            statement.setInt(6, event.getTotal());
            statement.setString(7, event.getDescription());
            statement.setString(8, event.getHostId());
            statement.setString(9, event.getLanguage());
            statement.setString(10, event.getGenre());
            statement.setString(11, event.getAddress());
            statement.setString(12, event.getCity());
            statement.setString(13, event.getState());
            statement.setString(14, event.getCountry());
            statement.setString(15, event.getZip());
            if(withImage) {
                statement.setString(16, event.getImageUrl());
                statement.setString(17, event.getId());
            } else {
                statement.setString(16, event.getId());
            }
            statement.executeUpdate();

            isUpdated = true;
        } catch (SQLException sqlException) {
            System.err.printf("Error while updating event with the name as %s. %s\n", event.getName(), sqlException.getMessage());
            isUpdated = false;
        }

        return isUpdated;
    }

    private static Time getTime(String time) {
        Time from = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            long ms = sdf.parse(time).getTime();
            from = new Time(ms);
        } catch (ParseException exception) {
            exception.printStackTrace();
        }

        return from;
    }
}
