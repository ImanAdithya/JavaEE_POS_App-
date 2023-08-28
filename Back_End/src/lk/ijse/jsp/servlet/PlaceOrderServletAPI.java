package lk.ijse.jsp.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(urlPatterns = "/placeOrder")
public class PurchaseOrderServletAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaee_pos_app", "root", "12345678");
            PreparedStatement pstm = connection.prepareStatement("select * from orders");
//            PreparedStatement pstm2 = connection.prepareStatement("select * from order_detail");
            ResultSet rst = pstm.executeQuery();
//            ResultSet rst2 = pstm2.executeQuery();
            PrintWriter writer = resp.getWriter();
            resp.addHeader("Access-Control-Allow-Origin","*");
            resp.addHeader("Content-Type","application/json");

            JsonArrayBuilder allCustomers = Json.createArrayBuilder();


            while (rst.next()) {
                String orderID = rst.getString(1);
                String orderCusID = rst.getString(2);
                String orderDate = rst.getString(3);
//                String orderTotal = String.valueOf(rst.getInt(4));

                JsonObjectBuilder customer = Json.createObjectBuilder();

                customer.add("orderID",orderID);
                customer.add("orderCusID",orderCusID);
                customer.add("orderDate",orderDate);
//                customer.add("contact",contact);

                allCustomers.add(customer.build());
            }


            writer.print(allCustomers.build());


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.addHeader("Access-Control-Allow-Origin","*");
        System.out.println("dddddd");
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
//        JsonArray jsonArray=reader.readArray();
        String oID = jsonObject.getString("oID");
        String oDate = jsonObject.getString("oDate");
        String oCusID = jsonObject.getString("oCusID");
        String oItemID = jsonObject.getString("oItemID");
        String oItemName = jsonObject.getString("oItemName");
        String oUnitPrice = jsonObject.getString("oUnitPrice");
        String oQty = jsonObject.getString("oQty");
        JsonArray oCartItems = jsonObject.getJsonArray("oCartItems");


        System.out.println(oID);
        System.out.println(oDate);
        System.out.println(oCusID);
        System.out.println(oItemID);
        System.out.println(oItemName);
        System.out.println(oUnitPrice);
        System.out.println(oQty);
        System.out.println(oCartItems);

        for (int i = 0; i < oCartItems.size(); i++) {

            System.out.println(oCartItems.getJsonArray(i).getString(0));
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaee_pos_app", "root", "12345678");
            connection.setAutoCommit(false);
            PreparedStatement pstm = connection.prepareStatement("insert into orders values(?,?,?)");
            pstm.setObject(1, oID);
            pstm.setObject(2, oDate);
            pstm.setObject(3, oCusID);
            resp.addHeader("Content-Type", "application/json");

            if (pstm.executeUpdate() > 0) {

                for (int i = 0; i < oCartItems.size(); i++) {
                    PreparedStatement pstm2 = connection.prepareStatement("insert into order_detail values(?,?,?,?)");
                    pstm2.setObject(1, oCartItems.getJsonArray(i).getString(0));
                    pstm2.setObject(2, oID);
                    pstm2.setObject(3, oCartItems.getJsonArray(i).getString(3));
                    pstm2.setObject(4, oCartItems.getJsonArray(i).getString(2));

                    if (pstm2.executeUpdate()>0){
                        connection.commit();
                        resp.addHeader("Content-Type", "application/json");
                        JsonObjectBuilder response = Json.createObjectBuilder();
                        response.add("state", "Ok");
                        response.add("message", "Successfully Added.!");
                        response.add("data", "");
                        resp.getWriter().print(response.build());
                    }else {
                        connection.rollback();
                    }

                }
            }
            else {
                connection.rollback();
            }

        } catch (ClassNotFoundException e) {
            resp.addHeader("Content-Type", "application/json");
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("state", "Error");
            response.add("message", e.getMessage());
            response.add("data", "");
            resp.setStatus(400);
            resp.getWriter().print(response.build());

        } catch (SQLException e) {
            resp.addHeader("Content-Type", "application/json");
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("state", "Error");
            response.add("message", e.getMessage());
            response.add("data", "");
            resp.setStatus(400);
            resp.getWriter().print(response.build());

        }

    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.addHeader("Access-Control-Allow-Headers","content-type");
        resp.addHeader("Access-Control-Allow-Methods","PUT");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin","*");
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        String itemID = jsonObject.getString("itemID");
        int newQty = Integer.parseInt(jsonObject.getString("newQty"));

        try {
            Class.forName("com.mysql.jdbc.Driver");

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaee_pos_app", "root", "12345678");

            PreparedStatement pstm3 = connection.prepareStatement("update item set qty_on_hnd=? where item_ID=?");
            pstm3.setObject(2, itemID);
            pstm3.setObject(1, newQty);
            if (pstm3.executeUpdate() > 0) {
                resp.addHeader("Content-Type", "application/json");
                JsonObjectBuilder response = Json.createObjectBuilder();
                response.add("state", "Ok");
                response.add("message", "Successfully Updated.!");
                response.add("data", "");
                resp.getWriter().print(response.build());
            }
        } catch (ClassNotFoundException e) {
            resp.addHeader("Content-Type", "application/json");
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("state", "Error");
            response.add("message", e.getMessage());
            response.add("data", "");
            resp.setStatus(400);
            resp.getWriter().print(response.build());
        } catch (SQLException e) {
            resp.addHeader("Content-Type", "application/json");
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("state", "Error");
            response.add("message", e.getMessage());
            response.add("data", "");
            resp.setStatus(400);
            resp.getWriter().print(response.build());
        }
    }
}
