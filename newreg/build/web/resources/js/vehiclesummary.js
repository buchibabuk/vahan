/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function vehicleClass(comp){
    comp1=document.getElementById("form1:vehicletype");
    comp2=document.getElementById("form1:bodetype");
    comp3=document.getElementById("form1:fuel");
    comp4=document.getElementById("form1:ownershipcode");
    comp5=document.getElementById("form1:manufacture");
    comp6=document.getElementById("form1:models");
    if(comp.value == "-1"){
        alert("Please Select atleast one parameter");
    }else if(comp1.value == "-1"){
        alert("Please Select atleast one parameter");
    }else if(comp2.value == "-1"){
        alert("Please Select atleast one parameter");
    }else if(comp3.value == "-1"){
        alert("Please Select atleast one parameter");
    }else if(comp4.value == "-1"){
        alert("Please Select atleast one parameter");
    }else if(comp5.value == "-1"){
        alert("Please Select atleast one parameter");
    }else if(comp6.value == "-1"){
        alert("Please Select atleast one parameter");
    }
}
                    
