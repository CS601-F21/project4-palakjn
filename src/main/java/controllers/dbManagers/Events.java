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
        Time to;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            long ms = sdf.parse(event.getFrom()).getTime();
            from = new Time(ms);
            ms = sdf.parse(event.getTo()).getTime();
            to = new Time(ms);
        } catch (ParseException exception) {
            exception.printStackTrace();
            return false;
        }

        try(Connection con = DataSource.getConnection()) {
            String query = "INSERT INTO events VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, event.getId());
            statement.setString(2, event.getName());
            statement.setString(3, event.getPlace());
            statement.setDate(4, Date.valueOf(event.getDate()));
            statement.setString(5, event.getImageUrl());
            statement.setInt(6, event.getAvailability());
            statement.setInt(7, event.getTotal());
            statement.setString(8, event.getStatus());
            statement.setString(9,event.getDescription());
            statement.setString(10, event.getHost());
            statement.setTime(11, from);
            statement.setTime(12, to);
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
            String query = "SELECT eventId, name, image, place, fromTime, toTime, date, status FROM events;";
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Event event = new Event(resultSet.getString("eventId"),
                                        resultSet.getString("name"),
                                        resultSet.getString("place"),
                                        resultSet.getDate("date").toString(),
                                        resultSet.getTime("fromTime").toString(),
                                        resultSet.getTime("toTime").toString(),
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
                        resultSet.getDate("date").toString(),
                        resultSet.getTime("fromTime").toString(),
                        resultSet.getTime("toTime").toString(),
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
