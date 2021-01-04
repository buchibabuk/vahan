/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

function regnNum1(comp){
    if(comp.value == ""){
        alert("Please Enter Registration Series Part");
    }
}

function regnNum2(comp){
    if(comp.value == ""){
        alert("Please Enter Registration Numeric Part ");
    }
}


function chasiNum(comp){
    var error = ""
    if(comp.value == ""){
        error = "Please Enter Chassis Number\n"
        //alert("Please Enter Chasis Number");
    }
    comp = document.getElementById("tf_REG_NO1")
    if(comp.value == ""){
        error = error + "Please Enter Registration Series Part\n"
        comp.focus()
    }
    comp = document.getElementById("tf_REG_NO2")
    if(comp.value == ""){
        error = error + "Please Enter Registration Numeric Part"
    }
    if(error != ""){
        alert(error);
    }else{
       document.forms[0].submit()
    }
}

function purpose(comp){
    if(comp.value == "-1"){
        alert("Please Select the Purpose");
    }
}

function ownersName(comp){
    if(comp.value == ""){
        alert("Please Enter the Owner Name");
    }
}

function fathersName(comp){
    if(comp.value == ""){
        alert("Please Enter the Father Name");
    }
}

function add1(comp){
    if(comp.value == ""){
        alert("Please Enter the Address");
    }
}

function city(comp){
    if(comp.value == ""){
        alert("Please Enter the City");
    }
}

function pincode(comp){
    if(comp.value == ""){
        alert("Please Enter the Pincode");
    }
}

function remarks(comp){
    if(comp.value == ""){
        alert("Please Enter the Remarks");
    }
}

function panNum(comp){
    if(comp.value == ""){
        alert("Please Enter the Pan Number");
    }
}

function serial(comp){
    if(comp.value == ""){
        alert("Please Enter the Serial Number");
    }
}

function ownerCode(comp){
    if(comp.value == "-1"){
        alert("Please Select the Owner Code");
    }
}

function saleAmt(comp){
    if(comp.value == ""){
        alert("Please Enter the Sale Amount");
    }
}

function padd1(comp){
    if(comp.value == ""){
        alert("Please Enter the Permanent Address");
    }
}

function pcity(comp){
    if(comp.value == ""){
        alert("Please Enter the Permanent City");
    }
}

function ppincode(comp){
    if(comp.value == ""){
        alert("Please Enter the Permanent Pincode");
    }
}