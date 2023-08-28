
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

    for (let i = 0; i <=itemCartRow.length; i++) {
        subTotal=subTotal+itemCartRow[4];
        console.log(itemCartRow[4]);
        $('#txtTotal').val(parseInt(subTotal));
    }

    $('#txtQty').val("");
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
    let oID = $('#txtOrderID').val();
    let oDate= $('#txtDate').val();
    let oCusID= $('#selectCusID').val();
    let oItemID= $('#selectItemCode').val();
    let oItemName= $('#txtItemDescription').val();
    let oUnitPrice= $('#txtItemPrice').val();
    let oQty= $('#txtQty').val();
    let qtyOnHand=$('#txtQTYOnHand').val();
    let oCartItems=cartItems;

    console.log(oID,oDate,oCusID,oItemID,oItemName,oUnitPrice,oQty);
    console.log(oCartItems);

    let order = {
        "oID": oID,
        "oDate": oDate,
        "oCusID": oCusID,
        "oItemID": oItemID,
        "oItemName": oItemName,
        "oUnitPrice": oUnitPrice,
        "oQty": oQty,
        "oQtyOnHand":qtyOnHand,
        "oCartItems": oCartItems
    }

    $.ajax({
        url: 'http://localhost:8080/javaEE_Pos/SPA/placeOrder',
        method: "post",
        setRequestHeader:"Access-Control-Allow-Origin",
        origin:"*",
        contentType: "application/json",
        data: JSON.stringify(order),
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

    $('#txtTotal').val("");
    $('#txtSubTotal').val("");
    $('#txtCash').val("");
    $('#txtDiscount').val("");
    $('#txtBalance').val("");

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
    let itemID =$("#selectItemCode").val()
    let oldQty=$("#txtQTYOnHand").val();
    let buyingQty=$("#txtQty").val();
    let newQty=(oldQty-buyingQty).toString();

    let updateItem = {
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
        data: JSON.stringify(updateItem),
        success: function (resp) {
            alert(resp.message);
        },
        error: function (error) {
            alert(error.responseJSON.message);
        }
    });
}

// //search order
// $(document).ready(function(){
//     $("#searchbar").on("keyup", function() {
//         var value = $(this).val().toLowerCase();
//         $("#tblOrders tr").filter(function() {
//             $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
//         });
//     });
// });