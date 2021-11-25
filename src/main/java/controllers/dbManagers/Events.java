package controllers.dbManagers;

import controllers.DataSource;
import models.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Events {

    public static boolean insert(Event event) {
        boolean isInserted = false;

        try(Connection con = DataSource.getConnection()) {
            String query = "INSERT INTO events VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, event.getId());
            statement.setString(2, event.getName());
            statement.setString(3, event.getPlace());
            statement.setTime(4, event.getTime());
            statement.setDate(5, event.getDate());
            statement.setString(6, event.getImageUrl());
            statement.setInt(7, event.getAvailability());
            statement.setInt(8, event.getTotal());
            statement.setString(9, event.getStatus());
            statement.setString(10,event.getDescription());
            statement.setString(11, event.getHost());
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
            String query = "SELECT eventId, name, image, place, time, date, status FROM events;";
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Event event = new Event(resultSet.getString("eventId"),
                                        resultSet.getString("name"),
                                        resultSet.getString("place"),
                                        resultSet.getDate("date"),
                                        resultSet.getTime("time"),
                                        resultSet.getString("status"));
                event.setImageUrl(resultSet.getString("image"));
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
            String query = "SELECT * FROM events WHERE eventId = ?;";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                event = new Event(resultSet.getString("eventId"),
                        resultSet.getString("name"),
                        resultSet.getString("place"),
                        resultSet.getDate("date"),
                        resultSet.getTime("time"),
                        resultSet.getString("status"));
                event.setImageUrl(resultSet.getString("image"));
                event.setAvailability(resultSet.getInt("availability"));
                event.setTotal(resultSet.getInt("total"));
                event.setDescription(resultSet.getString("description"));
                event.setHost(resultSet.getString("host"));
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting all events from the table. %s\n", sqlException.getMessage());
            event = null;
        }

        return event;
    }
}
