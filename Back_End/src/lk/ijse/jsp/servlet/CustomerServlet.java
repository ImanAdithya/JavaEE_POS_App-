package lk.ijse.jsp.servlet;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(urlPatterns = {"/SPA/cus"})
public class CustomerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName ("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection ("jdbc:mysql://localhost:3306/AjaxJson", "root", "12345678");
            PreparedStatement pstm = connection.prepareStatement ("select * from Customer");
            ResultSet rst = pstm.executeQuery ();
            PrintWriter writer = resp.getWriter ();
            resp.addHeader ("Content-Type", "application/json");
            resp.addHeader ("Access-Control-Allow-Origin", "*");


            JsonArrayBuilder allCustomer = Json.createArrayBuilder ();


            while (rst.next ()) {
                String id = rst.getString (1);
                String name = rst.getString (2);
                String address = rst.getString (3);
                String salary = (rst.getString (4));

                JsonObjectBuilder customer = Json.createObjectBuilder ();

                customer.add ("id", id);
                customer.add ("name", name);
                customer.add ("address", address);
                customer.add ("salary", salary);

                allCustomer.add (customer.build ());
            }
            writer.print (allCustomer.build ());

        } catch (ClassNotFoundException e) {
            throw new RuntimeException (e);
        } catch (SQLException e) {
            throw new RuntimeException (e);
        }
    }
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //preflight
        resp.addHeader ("Access-Control-Allow-Origin", "*");
        resp.addHeader ("Access-Control-Allow-Methods", "PUT");
        resp.addHeader ("Access-Control-Allow-Methods", "DELETE");
        resp.addHeader ("Access-Control-Allow-Headers", "Content-Type");
    }
}
