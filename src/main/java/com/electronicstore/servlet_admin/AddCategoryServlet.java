package com.electronicstore.servlet_admin;

import com.electronicstore.dao.CategoryDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddCategoryServlet extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Read form fields
        String name = request.getParameter("name");
        String description = request.getParameter("description");

        // Only attempt add if name is present
        if (name != null && !name.trim().isEmpty()) {
            categoryDAO.addCategory(name.trim(), description);
        }

        // Redirect back to the category list
        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }
}
