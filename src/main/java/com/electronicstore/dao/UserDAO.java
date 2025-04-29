package com.electronicstore.dao;

import com.electronicstore.model.User;
import com.electronicstore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Fetch every user from the 'users' table
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, full_name, email, phone_number, address, gender, created_at FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setPhoneNumber(rs.getString("phone_number"));
                u.setAddress(rs.getString("address"));
                u.setGender(rs.getString("gender"));
                // (you can add a createdAt field if you wish)
                users.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    // Delete a user by ID; any carts/orders cascade via your FKs
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        int affected = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            affected = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (affected > 0);
    }

    // Get user full name by user ID
    public String getUserNameById(int userId) {
        String sql = "SELECT full_name FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("full_name");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
