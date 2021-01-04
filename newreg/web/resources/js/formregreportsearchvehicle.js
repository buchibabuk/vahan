/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function vehicleClass(comp){
    comp1=document.getElementById("form1:tf_CHASI_NO");
    comp2=document.getElementById("form1:tf_ENG_NO");
    comp3=document.getElementById("form1:tf_O_NAME");
    if(comp.value == ""){
        alert("Please Enter atleast one report parameter");
    }else if(comp1.value == ""){
        alert("Please Enter atleast one report parameter");
    }else if(comp2.value == ""){
        alert("Please Enter atleast one report parameter");
    }else if(comp3.value == ""){
        alert("Please Enter atleast one report parameter");
    }
}