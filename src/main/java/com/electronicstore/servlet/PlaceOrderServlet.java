package com.electronicstore.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date; // Import Date

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.RequestDispatcher;

import com.electronicstore.model.Cart;
import com.electronicstore.model.OrderItem;
import com.electronicstore.model.Order;
import com.electronicstore.dao.CartDAO;
import com.electronicstore.util.DBConnection;

public class PlaceOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CartDAO cartDAO = new CartDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // 🛑 Step 1: Check if the user is logged in
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        List<Cart> cartItems = cartDAO.getCartItemsByUserId(userId);

        // 🛑 Step 2: Check if cart is empty
        if (cartItems == null || cartItems.isEmpty()) {
            response.sendRedirect("cart?error=Cart is empty");
            return;
        }

        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement orderItemStmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Enable transaction

            // 📝 Step 3: Insert Order into `orders` table
            String insertOrderSQL = "INSERT INTO orders (user_id, total_price) VALUES (?, ?)";
            orderStmt = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);

            double totalPrice = cartItems.stream().mapToDouble(c -> c.getProduct().getPrice() * c.getQuantity()).sum();
            orderStmt.setInt(1, userId);
            orderStmt.setDouble(2, totalPrice);
            orderStmt.executeUpdate();

            // Get generated order ID
            ResultSet rs = orderStmt.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            // 📝 Step 4: Insert Order Items into `order_items` table
            String insertOrderItemSQL = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            orderItemStmt = conn.prepareStatement(insertOrderItemSQL);

            List<OrderItem> orderItemList = new ArrayList<>();
            for (Cart cartItem : cartItems) {
                OrderItem orderItem = new OrderItem(
                    0, // ID (auto-increment)
                    orderId,
                    cartItem.getProduct(),
                    cartItem.getQuantity(),
                    cartItem.getProduct().getPrice()
                );
                orderItemList.add(orderItem);

                orderItemStmt.setInt(1, orderId);
                orderItemStmt.setInt(2, cartItem.getProduct().getId());
                orderItemStmt.setInt(3, cartItem.getQuantity());
                orderItemStmt.setDouble(4, cartItem.getProduct().getPrice());
                orderItemStmt.executeUpdate();
            }

            // 🛑 Step 5: Clear Cart
            cartDAO.clearCart(userId);

            conn.commit(); // Commit transaction

            // ✅ Step 6: Redirect to Order Confirmation Page
            com.electronicstore.dao.UserDAO userDAO = new com.electronicstore.dao.UserDAO();
            String userName = userDAO.getUserNameById(userId);
            request.setAttribute("order", new Order(orderId, userId, userName, totalPrice, "Pending", new Date())); // Include current date and userName
            request.setAttribute("orderItems", orderItemList); // Now passing OrderItem list

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/orderConfirmation.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // Rollback transaction if error occurs
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            response.sendRedirect("checkout?error=Failed to place order");
        } finally {
            try {
                if (orderStmt != null) orderStmt.close();
                if (orderItemStmt != null) orderItemStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }
}
