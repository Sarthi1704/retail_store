package com.electronicstore.servlet_admin;

import com.electronicstore.dao.CategoryDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteCategoryServlet extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Read the category ID
        String idParam = request.getParameter("id");
        try {
            int categoryId = Integer.parseInt(idParam);
            categoryDAO.deleteCategory(categoryId);
        } catch (NumberFormatException e) {
            // ignore invalid id
        }

        // Redirect back to the category list
        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }
}
