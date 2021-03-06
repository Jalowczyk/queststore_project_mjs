package com.codecool_mjs.dataaccess.dao;

import com.codecool_mjs.model.*;

import java.sql.*;

public class LogInDao {

    private Connection connection;

    private static final String URL = "jdbc:sqlite:src/main/resources/queststore.db";
    private static final String DRIVER_CLASS = "org.sqlite.JDBC";


    public void setConnection() throws DaoException {
        try {
            Class.forName(DRIVER_CLASS);
            this.connection = DriverManager.getConnection(URL);
        } catch (ClassNotFoundException | SQLException e) {
            throw new DaoException("Exception in setConnection in logInDao");
        }
    }

    public User checkLogin(String email, String password) throws DaoException {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (PreparedStatement preStatement = connection.prepareStatement(query)){
            preStatement.setString(1, email);
            preStatement.setString(2, password);
            ResultSet rs = preStatement.executeQuery();

            User user;

            if (rs.isClosed()) {
                return null;
            }

            user = createUser(rs);
            return user;
        } catch (SQLException e) {
            throw new DaoException("Exception in loginDao", e);
        }
    }

    public User logInBySession(String sessionId) throws DaoException {
        String query = "SELECT * from users JOIN sessions ON sessions.user_id = users.id " +
                "WHERE sessions.session_id = ?";

        try(PreparedStatement preStatement = connection.prepareStatement(query)) {
            preStatement.setString(1, sessionId);
            ResultSet rs = preStatement.executeQuery();
            return createUser(rs);
        } catch (SQLException e) {
            throw new DaoException("Exception in logInBySession", e);
        }
    }

    private User createUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String surname = rs.getString("surname");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String profession = rs.getString("profession");

        User user = null;

        if (profession.equals("codecooler")) {
            Wallet wallet = getCodecoolerWallet(id);
            user = new Codecooler(id, name, surname, email, password, wallet);
        } else if (profession.equals("mentor")) {
            user = new Mentor(id, name, surname, email, password);
        } else if (profession.equals("admin")) {
            user = new Admin(id, name, surname, email, password);
        }
        return user;
    }

    public boolean addSession(String uuid, int userId) throws DaoException {
        String query = "INSERT INTO sessions (session_id, user_id) VALUES(? , ?)";

        try (PreparedStatement preStatement = connection.prepareStatement(query)){

            preStatement.setString(1, uuid);
            preStatement.setInt(2, userId);
            int result = preStatement.executeUpdate();
            if (result == 1) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            throw new DaoException("Exception in addSession", e);
        }
    }

    public boolean checkSessionStatus(String session_id) throws DaoException {
        String query = "SELECT * FROM sessions WHERE session_id = ?";

        try (PreparedStatement preStatement = connection.prepareStatement(query)){

            preStatement.setString(1,  session_id);
            ResultSet rs = preStatement.executeQuery();
            if(rs.isClosed()){
                return false;
            } else {
                return true;
            }

        } catch (SQLException e) {
            throw new DaoException("exception in checkSessionStatus", e);
        }
    }

    public boolean remove(String sessionId) throws DaoException {
        String query = "DELETE FROM sessions WHERE session_id = ?";

        try(PreparedStatement preStatement = connection.prepareStatement(query)) {

            preStatement.setString(1, sessionId);
            int result = preStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            throw new DaoException("Exception in session delete", e);
        }
    }

    private Wallet getCodecoolerWallet(int userId) throws SQLException {
        String query = "SELECT * FROM wallets WHERE user_id = ?;";

        try(PreparedStatement preStatement = connection.prepareStatement(query)) {
            preStatement.setInt(1, userId);
            ResultSet rs = preStatement.executeQuery();
            Wallet wallet = createWallet(rs);
            return wallet;
        }
    }

    private Wallet createWallet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        int totalEarned = rs.getInt("total_earned_coins");
        int availableCoins = rs.getInt("available_coins");

        Wallet wallet = new Wallet(id, userId, totalEarned, availableCoins);
        return wallet;
    }
}