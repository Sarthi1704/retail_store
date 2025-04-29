package com.electronicstore.servlet_admin;

import com.electronicstore.dao.OrderDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MarkOrderDoneServlet extends HttpServlet {
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        try {
            int orderId = Integer.parseInt(idParam);
            orderDAO.markAsDone(orderId);
        } catch (NumberFormatException ignored) { }
        resp.sendRedirect(req.getContextPath() + "/admin/orders");
    }
}
