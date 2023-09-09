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

@WebServlet(urlPatterns = {"/item"})
public class ItemServletAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName ("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection ("jdbc:mysql://localhost:3306/AjaxJson", "root", "12345678");
            PreparedStatement pstm = connection.prepareStatement ("select * from item");
            ResultSet rst = pstm.executeQuery ();
            PrintWriter writer = resp.getWriter ();
            resp.addHeader ("Content-Type", "application/json");
            resp.addHeader ("Access-Control-Allow-Origin", "*");

            JsonArrayBuilder allItem = Json.createArrayBuilder ();

            while (rst.next ()) {
                String id = rst.getString (1);
                String des = rst.getString (2);
                double up = rst.getDouble (3);
                int qty = rst.getInt (4);

                JsonObjectBuilder item = Json.createObjectBuilder ();

                item.add ("itemId", id);
                item.add ("itemDes", des);
                item.add ("itemUp", up);
                item.add ("itemQty", qty);

                allItem.add (item.build ());
            }

            writer.print (allItem.build ());

        } catch (ClassNotFoundException e) {
            throw new RuntimeException (e);
        } catch (SQLException e) {
            throw new RuntimeException (e);
        }

    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String itemID = req.getParameter ("itemID");
        String itemDes = req.getParameter ("itemDes");
        Double itemUp = Double.valueOf (req.getParameter ("unitPrice"));
        int itemQty = Integer.parseInt (req.getParameter ("itemQty"));

        PrintWriter writer = resp.getWriter ();

        resp.addHeader ("Access-Control-Allow-Origin", "*");

        try {
            Class.forName ("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection ("jdbc:mysql://localhost:3306/AjaxJson", "root", "12345678");
            PreparedStatement pstm = connection.prepareStatement ("insert into item values(?,?,?,?)");

            pstm.setObject (1, itemID);
            pstm.setObject (2, itemDes);
            pstm.setObject (3, itemUp);
            pstm.setObject (4, itemQty);
            if (pstm.executeUpdate () > 0) {

                resp.addHeader("Content-Type","application/json");

                JsonObjectBuilder cussAdd=Json.createObjectBuilder ();
                cussAdd.add ("state","200");
                cussAdd.add ("massage"," Item Added Succuss");
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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonReader reader = Json.createReader (req.getReader ());
        JsonObject jsonObject = reader.readObject ();

        String itemID=jsonObject.getString ("id");
        String itemDes=jsonObject.getString ("des");
        String itemUp =jsonObject.getString ("up");
        String itemQty=jsonObject.getString ("qty");


        PrintWriter writer = resp.getWriter ();

        resp.addHeader ("Access-Control-Allow-Origin", "*");
        try {

            Class.forName ("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection ("jdbc:mysql://localhost:3306/AjaxJson", "root", "12345678");
            PreparedStatement pstm3 = connection.prepareStatement ("update Item set ItemName=?,UnitPrice=?,ItemQty=? where ItemCode=?");
            pstm3.setObject (4, itemID);
            pstm3.setObject (1, itemDes);
            pstm3.setObject (2, itemUp);
            pstm3.setObject (3, itemQty);
            if (pstm3.executeUpdate () > 0) {
                resp.addHeader("Content-Type","application/json");
                JsonObjectBuilder cussAdd=Json.createObjectBuilder ();
                cussAdd.add ("state","200");
                cussAdd.add ("massage"," Item Updated Succuss");
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

        String cusID=req.getParameter ("id");
        PrintWriter writer = resp.getWriter ();

        resp.addHeader ("Access-Control-Allow-Origin", "*");
        try {
            Class.forName ("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection ("jdbc:mysql://localhost:3306/AjaxJson", "root", "12345678");
            PreparedStatement pstm2 = connection.prepareStatement ("delete from item where ItemCode=?");
            pstm2.setObject (1, cusID);
            if (pstm2.executeUpdate () > 0) {
                resp.addHeader("Content-Type","application/json");
                JsonObjectBuilder cussAdd=Json.createObjectBuilder ();
                cussAdd.add ("state","200");
                cussAdd.add ("massage"," Item Deleted Succuss");
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
