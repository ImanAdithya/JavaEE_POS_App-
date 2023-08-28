
loadCustomerIDs();
loadItemIds();
function loadCustomerIDs() {
    var cusOption='';

    $.ajax({
       url:'http://localhost:8080/javaEE_Pos/SPA/cus',
       method:'get',
       success:function (customers) {
           for (i in customers) {
               let cus=customers[i];
               let id=cus.id;
               cusOption += '<option value="' +id + '">' + id + '</option>';
           }
           $("#selectCusID").append(cusOption);
       },
        error:function (res) {
            console.log(res);
        }
    });
}

$("#selectCusID").change(function () {
    let val=$(this).val();

    $.ajax({
        url:'http://localhost:8080/javaEE_Pos/SPA/cus',
        method:'get',
        success:function (customers) {
            for(i in customers){
                let id=customers[i].id;
                let name=customers[i].name;
                let salary=customers[i].salary;
                let adderss=customers[i].address;

                if(val==customers[i].id){
                    $('#orderCustomerID').val(id);
                    $('#orderCustomerName').val(name);
                    $('#orderCustomerSalary').val(salary);
                    $('#orderCustomerAddress').val(adderss);
                    break;
                }
            }
        },error:function (res) {
            console.log(res);
        }
    });
});

function loadItemIds() {
    var itemOption='';
    $.ajax({
        url:'http://localhost:8080/javaEE_Pos/SPA/item',
        method:'get',
        success:function (items) {
            for(i in items){
                let id=items[i].itemId;
                itemOption += '<option value="' +id + '">' + id + '</option>';
            }
            $('#selectItemCode').append(itemOption);
        }
    });
}

$('#selectItemCode').change(function () {
    var val=$(this).val();

    $.ajax({
        url:'http://localhost:8080/javaEE_Pos/SPA/item',
        method:'get',
        success:function (items) {
            for(i in items){

                if (val==items[i].itemId){
                    $('#txtItemCode').val(items[i].itemId);
                    $('#txtItemDescription').val(items[i].itemDes);
                    $('#txtItemPrice').val(items[i].itemUp);
                    $('#txtQTYOnHand').val(items[i].itemQty);
                    break;
                }
            }
        }
    });
});

let cartItems=[];
let subTotal=0;
let discount=0;
let finalTotal=0;

$('#btnAddToTable').click(function () {



    let itemCode=$('#txtItemCode').val();
    let itemName=$('#txtItemDescription').val();
    let price=$('#txtItemPrice').val();
    let qty=$('#txtQty').val();
    let total=price*qty;



    let itemCartRow=[];
    itemCartRow.push(itemCode,itemName,price,qty,total);



    let row=`<tr><td>${itemCode}</td><td>${itemName}</td><td>${price}</td><td>${qty}</td><td>${total}</td></tr>`;
    $("#orderTable").append(row);

    cartItems.push(itemCartRow);

    for (let i = 0; i <=cartItems.length; i++) {
        subTotal=subTotal+cartItems[i][4];
        console.log(cartItems[i][4]);
        $('#txtTotal').val(parseInt(subTotal));
    }
});

$('#txtDiscount').keydown(function (event) {

    if (event.key==="Enter"){
       if ($('#txtDiscount').val()!=="0"){
           finalTotal=subTotal-Number($('#txtDiscount').val());

           $('#txtSubTotal').val(finalTotal);
       }
    }

})

$('#txtCash').keydown(function (event) {
    if(event.key==="Enter"){
       let balance= $('#txtCash').val()-finalTotal;

       $('#txtBalance').val(parseInt(balance));
    }
})

// ========================================================
$('#btnSubmitOrder').click(function () {
    let oID = $('#InputOID').val();
    let oDate= $('#InputDate').val();
    let oCusID= $('#selectCusId').val();
    let oItemID= $('#selectItemId').val();
    let oItemName= $('#ItemName').val();
    let oUnitPrice= $('#UnitPrice').val();
    let oQty= $('#Qty').val();
    let oCartItems=cartItems;

    console.log(oID,oDate,oCusID,oItemID,oItemName,oUnitPrice,oQty);
    console.log(oCartItems);

    let a = {
        "oID": oID,
        "oDate": oDate,
        "oCusID": oCusID,
        "oItemID": oItemID,
        "oItemName": oItemName,
        "oUnitPrice": oUnitPrice,
        "oQty": oQty,
        "oCartItems": oCartItems
    }

    $.ajax({
        url: 'http://localhost:8080/JavaEE_Pos/placeOrder',
        method: "post",
        setRequestHeader:"Access-Control-Allow-Origin",
        origin:"*",
        contentType: "application/json",
        data: JSON.stringify(a),
        success: function (resp) {
            alert(resp.message);
            updateItemQty();
            console.log(resp.message)
        },
        error: function (error) {
            alert(error.responseJSON.message);
            console.log(error.message)
        }
    });

})

function getAllOrders(){
    $("#tblOrders").empty();
    <!--send ajax request to the item servlet using jQuery-->
    $.ajax({
        url: 'http://localhost:8080/JavaEE_Pos/placeOrder',
        dataType: "json",
        success: function (orders) {
            for (let i in orders) {
                let order = orders[i];
                let orderID = order.orderID;
                let orderCusName = order.orderCusID;
                let orderDate = order.orderDate;
                let row = `<tr><td>${orderID}</td><td>${orderCusName}</td><td>${orderDate}</td></tr>`;
                $("#tblOrders").append(row);
                // bindTrEventsItem();
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function updateItemQty() {
    let itemID =$("#selectItemId").val()
    let oldQty=$("#QtyOnHnd").val();
    let buyingQty=$("#Qty").val();
    let newQty=(oldQty-buyingQty).toString();

    let b = {
        "itemID": itemID,
        "newQty": newQty,
    }

    $.ajax({
        url: 'http://localhost:8080/JavaEE_Pos/placeOrder',
        method: 'put',
        origin: "*",
        // header: "Access-Control-Allow-Origin",
        setRequestHeader: "Access-Control-Allow-Origin",
        contentType: "application/json",
        data: JSON.stringify(b),
        success: function (resp) {
            alert(resp.message);
        },
        error: function (error) {
            alert(error.responseJSON.message);
        }
    });


}

//search order
$(document).ready(function(){
    $("#searchbar").on("keyup", function() {
        var value = $(this).val().toLowerCase();
        $("#tblOrders tr").filter(function() {
            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
        });
    });
});