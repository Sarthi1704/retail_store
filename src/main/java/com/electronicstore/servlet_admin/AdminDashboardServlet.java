package com.electronicstore.servlet_admin;

import java.io.IOException;
import com.electronicstore.dao.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // fetch all counts
        int userCount     = adminDAO.getUserCount();
        int productCount  = adminDAO.getProductCount();
        int categoryCount = adminDAO.getCategoryCount();
        int orderCount    = adminDAO.getOrderCount();

        // push into request
        request.setAttribute("userCount",     userCount);
        request.setAttribute("productCount",  productCount);
        request.setAttribute("categoryCount", categoryCount);
        request.setAttribute("orderCount",    orderCount);

        // forward to JSP
        request.getRequestDispatcher("/WEB-INF/admin/adminDashboard.jsp")
               .forward(request, response);
    }
}
