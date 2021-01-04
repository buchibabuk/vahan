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

function authority(comp){
    if(comp.value == ""){
        alert("Please Enter Authority");
    }
}

function stateCode(comp){
    if(comp.value == "-1"){
        alert("Please Select StateTo");
    }
}

function ncrtClearance(comp){
    if(comp.value == ""){
        alert("Please Enter N.C.R.B Number");
    }
}

function purpose(comp){
    if(comp.value == "-1"){
        alert("Please Enter Purpose");
    }
}