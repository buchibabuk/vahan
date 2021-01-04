
//for only numbers
function checkIt(evt) {
    evt = (evt) ? evt : window.event
    var charCode = (evt.which) ? evt.which : evt.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        status = "This field accepts numbers only."
        return false
    }
    status = ""
    return true
}


//for only characters
function onlyCharNoSpace1(evt) {
    evt = (evt) ? evt : window.event
    var charCode = (evt.which) ? evt.which : evt.keyCode
    //alert(charCode)
    if ((charCode > 31 & charCode < 65) | (charCode > 90 & charCode < 97) | (charCode > 122 & charCode < 127)) {
        status = "This field accepts charecters only."
        return false
    }
    status = ""
    return true
}

//for only numbers
function onlyNumbers(evt) {

    evt = (evt) ? evt : window.event
    var charCode = (evt.which) ? evt.which : evt.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        status = "This field accepts numbers only."
        return false
    }
    status = ""
    return true
}

//for NoSpecialCharacter
function NoSpecialCharacter(evt) {
    evt = (evt) ? evt : window.event
    var charCode = (evt.which) ? evt.which : evt.keyCode
    //alert(charCode)
    if ((charCode >= 32 & charCode < 48) | charCode == 64 | charCode == 94 | charCode == 95 | charCode == 61 | charCode == 126 | charCode == 96) {
        status = "This field accepts No Special Character."
        return false
    }
    status = ""
    return true
}
function chassis_no_validation(evt) {

    evt = (evt) ? evt : window.event
    var charCode = (evt.which) ? evt.which : evt.keyCode
    if((charCode >= 32 & charCode <= 34)|(charCode >= 36 & charCode <= 41)|(charCode >= 43 & charCode <= 47) | charCode ==64 | charCode ==94 | charCode ==95 | charCode ==96 | charCode ==59 | charCode ==58 | charCode ==63 | charCode ==92 | charCode ==124 | charCode ==91 | charCode ==93 | charCode ==123 | charCode ==125 | charCode ==60 | charCode ==62 | charCode ==61){
        status = "This field accepts No Special Character."
        return false
    }
    status = ""
    return true
}
function onlyAlphNumericAllowed(evt)
{
    evt = (evt) ? evt : (window.event);
    var charCode = (evt.which) ? evt.which : (evt.keyCode);


    if ((charCode >= 32 & charCode <= 47) | (charCode >= 58 & charCode <= 64) | (charCode >= 91 & charCode <= 96) | (charCode >= 123 & charCode <= 126))
    {

        staturs = "Only Alpha numeric values are allowed";
        alert(staturs);
        return false;
    }
    staturs = ""
    return true;


}

function showSeatList()
{
    //alert("hello");

    var ab = document.getElementById('app_disapp_form:bt_status:2');
     //alert("afterb ab");
    if (ab.checked == true) {
          //alert("in if");
        document.getElementById('app_disapp_form:prevAction').style.display = 'block';
    }
    else {
        //alert("in else");
        document.getElementById('app_disapp_form:prevAction').style.display = 'none';

    }
}


function noBack() {
    //alert("this is noBack");      
    window.history.forward(1);

}

function AlphaWithSpaceOnly(e, strExtraChar)
{
    var key;
    var keychar;
    var strCheckString;

    strCheckString = "abcdefghijklmnopqrstuvwxyzb " + strExtraChar;

    if (window.event)
        key = window.event.keyCode;
    else if (e)
        key = e.which;
    else
        return true;
    keychar = String.fromCharCode(key);
    keychar = keychar.toLowerCase();

    // control keys
    if ((key==null) || (key==0) || (key==8) ||
        (key==9) || (key==13) || (key==27) )
        return true;

    // alphas and numbers
    else if ((strCheckString.toString().indexOf(keychar) > -1))
        return true;
    else
        return false;
}


