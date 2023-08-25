package lk.ijse.jsp.servlet;

import javax.json.*;
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cusId = req.getParameter ("cID");
        String cusName = req.getParameter ("cName");
        String cusAddress = req.getParameter ("cAddress");
        String salary = req.getParameter ("cSalary");

        PrintWriter writer = resp.getWriter ();

        try {
            Class.forName ("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection ("jdbc:mysql://localhost:3306/AjaxJson", "root", "12345678");
            PreparedStatement pstm = connection.prepareStatement ("insert into customer values(?,?,?,?)");

            pstm.setObject (1, cusId);
            pstm.setObject (2, cusName);
            pstm.setObject (3, cusAddress);
            pstm.setObject (4, salary);
            if (pstm.executeUpdate () > 0) {

                resp.addHeader("Content-Type","application/json");
                resp.addHeader ("Access-Control-Allow-Origin", "*");

                JsonObjectBuilder cussAdd=Json.createObjectBuilder ();
                cussAdd.add ("state","200");
                cussAdd.add ("massage"," Customer Added Succuss");
                cussAdd.add ("data","");
                resp.setStatus (200);
                writer.print(cussAdd.build());

            }
        } catch (SQLException | ClassNotFoundException e) {

            resp.addHeader("Content-Type","application/json");
            JsonObjectBuilder obj=Json.createObjectBuilder ();
            obj.add ("state","400");
            obj.add ("massage",e.getMessage ());
            obj.add ("data","");
            resp.setStatus (400);
            writer.print(obj.build());
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//
//        String cusId = req.getParameter ("id");
//        String cusName = req.getParameter ("name");
//        String cusAddress = req.getParameter ("address");
//        String cusSalary = req.getParameter ("salary");
        resp.addHeader ("Access-Control-Allow-Origin", "*");

        PrintWriter writer = resp.getWriter ();

        JsonReader reader = Json.createReader (req.getReader ());
        JsonObject jsonObject = reader.readObject ();

        String id=jsonObject.getString ("id");
        String name=jsonObject.getString ("name");
        String address =jsonObject.getString ("address");
        String salary=jsonObject.getString ("salary");

        try {

            Class.forName ("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection ("jdbc:mysql://localhost:3306/AjaxJson", "root", "12345678");
            PreparedStatement pstm3 = connection.prepareStatement ("update Customer set name=?,address=?,salary=? where id=?");

            pstm3.setObject (4, id);
            pstm3.setObject (1, name);
            pstm3.setObject (2, address);
            pstm3.setObject (3, salary);

            if (pstm3.executeUpdate () > 0) {

                resp.addHeader("Content-Type","application/json");
                JsonObjectBuilder cussAdd=Json.createObjectBuilder ();
                cussAdd.add ("state","200");
                cussAdd.add ("massage"," Customer Updated Succuss");
                cussAdd.add ("data","");
                resp.setStatus (200);
                writer.print(cussAdd.build());

            } else {
                throw new SQLException ();
            }

        } catch (SQLException | ClassNotFoundException e) {
            resp.addHeader("Content-Type","application/json");
            JsonObjectBuilder obj=Json.createObjectBuilder ();
            obj.add ("state","");
            obj.add ("massage",e.getMessage ());
            obj.add ("data","");
            resp.setStatus (400);
            writer.print(obj.build());
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter writer = resp.getWriter ();

        String cusID=req.getParameter ("id");

        resp.addHeader ("Access-Control-Allow-Origin", "*");


        try {
            Class.forName ("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection ("jdbc:mysql://localhost:3306/AjaxJson", "root", "12345678");
            PreparedStatement pstm2 = connection.prepareStatement ("delete from Customer where id=?");
            pstm2.setObject (1, cusID);
            if (pstm2.executeUpdate () > 0) {
                resp.addHeader("Content-Type","application/json");
                JsonObjectBuilder cussAdd=Json.createObjectBuilder ();
                cussAdd.add ("state","200");
                cussAdd.add ("massage"," Customer Deleted Succuss");
                cussAdd.add ("data","");
                resp.setStatus (200);
                writer.print(cussAdd.build());
            }
        } catch (SQLException | ClassNotFoundException e) {
            resp.addHeader("Content-Type","application/json");
            JsonObjectBuilder obj=Json.createObjectBuilder ();
            obj.add ("state","");
            obj.add ("massage",e.getMessage ());
            obj.add ("data","");
            resp.setStatus (400);
            writer.print(obj.build());
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
