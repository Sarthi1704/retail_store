package com.electronicstore.servlet_admin;

import com.electronicstore.dao.CategoryDAO;
import com.electronicstore.model.Category;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AdminCategoryListServlet extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Fetch all categories
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        // Forward to JSP for rendering
        request.getRequestDispatcher("/WEB-INF/admin/categoryList.jsp")
               .forward(request, response);
    }
}
