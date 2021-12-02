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
        Time from;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            long ms = sdf.parse(event.getFrom()).getTime();
            from = new Time(ms);
        } catch (ParseException exception) {
            exception.printStackTrace();
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
}
