package com.electronicstore.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.RequestDispatcher;
import com.electronicstore.model.Cart;
import com.electronicstore.dao.CartDAO;

public class CartViewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        List<Cart> cartItems = cartDAO.getCartItemsByUserId(userId);

        if (cartItems == null || cartItems.isEmpty()) {
            // Forward to JSP with an error message
            request.setAttribute("errorMsg", "Your cart is empty.");
            request.setAttribute("cartItems", new ArrayList<Cart>());
            RequestDispatcher dispatcher = 
                request.getRequestDispatcher("/views/cart.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Normal flow: show items
        request.setAttribute("cartItems", cartItems);
        RequestDispatcher dispatcher = 
            request.getRequestDispatcher("/WEB-INF/views/cart.jsp");
        dispatcher.forward(request, response);
    }
}
