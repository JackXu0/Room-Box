/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.Group.WebApplication1;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Administrator
 */
public class ReturnStory extends HttpServlet {

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String room = request.getParameter("room").trim();
        String record = "";
        for(int i =0;i<UserDAO.story(room).size();i++){
            String temp = UserDAO.story(room).get(i);
            int num = UserDAO.story(room).size() - 1;
            if (i != num) {
                record = record + temp+ "!@!@";
            } else {
                record = record + temp;
            }
        }
        try (PrintWriter out = response.getWriter()) {
            out.write(record);
        }
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request,response);
    }

    
}
