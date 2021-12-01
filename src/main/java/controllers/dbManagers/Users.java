package controllers.dbManagers;

import controllers.DataSource;
import models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Users {

    public static boolean insertUser(String id, String email, String name) {
        boolean isInserted = false;
        try (Connection con = DataSource.getConnection()) {
            String query = "INSERT INTO users (id, email, name) VALUES (?, ?, ?);";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, id);
            statement.setString(2, email);
            statement.setString(3, name);
            statement.executeUpdate();

            isInserted = true;
        } catch (SQLException sqlException) {
            System.err.printf("Error while inserting new user %s to table. %s.\n", email, sqlException.getMessage());
        }

        return isInserted;
    }

    public static String getUserId(String email) {
        String userId = null;

        try (Connection con = DataSource.getConnection()) {
            String query = "SELECT id FROM users WHERE email = ?;";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userId = resultSet.getString("id");
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting user %s information. %s.\n", email, sqlException.getMessage());
        }

        return userId;
    }

    public static String getUserName(String userId) {
        String name = null;

        try (Connection con = DataSource.getConnection()) {
            String query = "SELECT name FROM users WHERE id = ?;";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                name = resultSet.getString("name");
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting user %s information. %s.\n", userId, sqlException.getMessage());
        }

        return name;
    }

    public static User getUserIdAndName(String userId) {
        User user = new User();

        try (Connection con = DataSource.getConnection()) {
            String query = "SELECT id, name FROM users WHERE id = ?;";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                user.setName(resultSet.getString("name"));
                user.setId(resultSet.getString("id"));
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting user %s information. %s.\n", userId, sqlException.getMessage());
            user = null;
        }

        return user;
    }
}
