package controllers.dbManagers;

import controllers.DataSource;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds all the queries being made to the Users table.
 *
 * @author Palak Jain
 */
public class Users {

    /**
     * Insert the user information
     * @param id User unique identifier
     * @param email User email address
     * @param name User name
     * @return true if successful else false
     */
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

    /**
     * Get the user Unique Identifier by email.
     * Along with user id, email id is also unique per user.
     * @param email User email address
     * @return UniqueID if found else null
     */
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

    /**
     * Get the user display name by id
     * @param userId User unique id
     * @return not-null value if found else null
     */
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

    /**
     * Get few user details like id, name, imageUrl, phone by id
     * @param userId user unique identifier
     * @return user object if found else null
     */
    public static User getUserInfo(String userId) {
        User user = new User();

        try (Connection con = DataSource.getConnection()) {
            String query = "SELECT id, name, imageUrl, phone FROM users WHERE id = ?;";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                user.setName(resultSet.getString("name"));
                user.setId(resultSet.getString("id"));
                user.setImage(resultSet.getString("imageUrl"));
                user.setPhone(resultSet.getString("phone"));
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting user %s information. %s.\n", userId, sqlException.getMessage());
            user = null;
        }

        return user;
    }

    /**
     * Get all the details of one user by Id
     * @param userId User Unique Identifier
     * @return User object if found else null
     */
    public static User getUserProfile(String userId) {
        User user = new User();

        try (Connection con = DataSource.getConnection()) {
            String query = "SELECT * FROM users WHERE id = ?;";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                user.setId(resultSet.getString("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setDob(resultSet.getDate("dob") == null ? null : resultSet.getDate("dob").toString());
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                user.setCity(resultSet.getString("city"));
                user.setState(resultSet.getString("state"));
                user.setCountry(resultSet.getString("country"));
                user.setZip(resultSet.getString("zip"));
                user.setImage(resultSet.getString("imageUrl"));
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting user %s information. %s.\n", userId, sqlException.getMessage());
            user = null;
        }

        return user;
    }

    /**
     * Get the ID and name of all the users
     * @return List of all users' information if found else null
     */
    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();

        try (Connection con = DataSource.getConnection()) {
            String query = "SELECT id, name FROM users;";
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getString("id"));
                user.setName(resultSet.getString("name"));

                users.add(user);
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting all users information %s.\n", sqlException.getMessage());
            users = null;
        }

        return users;
    }

    /**
     * Updating user information
     * @param user User details which wants to ber updated
     * @param withPhoto true if wants to update image else false
     * @return true if successful else false
     */
    public static boolean updateUser(User user, boolean withPhoto) {
        boolean isUpdated = false;

        try (Connection con = DataSource.getConnection()) {
            String query = null;
            if(withPhoto) {
               query = "UPDATE users SET dob = ?, phone = ?, address = ?, city = ?, state = ?, country = ?, zip = ?, imageUrl = ? WHERE id = ?";
            } else {
                query = "UPDATE users SET dob = ?, phone = ?, address = ?, city = ?, state = ?, country = ?, zip = ? WHERE id = ?";
            }
            PreparedStatement statement = con.prepareStatement(query);
            statement.setDate(1, Date.valueOf(user.getDob(false)));
            statement.setString(2, user.getPhone());
            statement.setString(3, user.getAddress());
            statement.setString(4, user.getCity());
            statement.setString(5, user.getState());
            statement.setString(6, user.getCountry());
            statement.setString(7, user.getZip());
            if(withPhoto) {
                statement.setString(8, user.getImage());
                statement.setString(9, user.getId());
            } else {
                statement.setString(8, user.getId());
            }

            statement.executeUpdate();

            isUpdated = true;
        } catch (SQLException sqlException) {
            System.err.printf("Error while updating user %s information. %s.\n", user.getName(), sqlException.getMessage());
            isUpdated = false;
        }

        return isUpdated;
    }
}
