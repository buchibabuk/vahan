/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function checkit() {
    var oldpwd = document.getElementById("oldPwd");
    var newpwd = document.getElementById("newPwd");
    var cnfrmpwd = document.getElementById("CnfrmPwd");

    getSHA256(oldpwd);
    getSHA256(newpwd);
    getSHA256(cnfrmpwd);
    return true;
}

function getSHA256(element) {
    var val = element.value;
    if (val !== null || val !== '') {
        var shaval = hex_sha256(val);
        element.value = shaval;
    }
}

function userCreatePasswordStrength(element)
{
    var password = element.value;
    var flag = true;
    if (password !== "" && !CheckPassword(password)) {
        alert("Invalid Password.Provide password as per the instructions");
        element.focus();
        element.value = "";
        flag = false;
    }
    return flag;
}


function checkmerchentKey(element) {
    var password = element.value;
    var flag = true;
    if (password !== "" && !CheckMerchant(password)) {
        alert("Invalid Merchant Key. Key should contain at least one number, contain at least one lowercase letter, contain at least one uppercase letter, contain at least one special character. Minimum Length should be 8 characters ");
        element.focus();
        element.value = "";
        flag = false;
    }
    return flag;
}
function CheckMerchant(inputtxt)
{
    var decimal = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?!.*\s).{8,20}$/;
    return(inputtxt.match(decimal));
}

function CheckPassword(inputtxt)
{
    var decimal = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?!.*\s).{8,15}$/;
    return(inputtxt.match(decimal));
}


function makePassword1() {
    var element = document.getElementById("empform:user_pwd");
    userCreatePasswordStrength(element);
    if (element !== null) {
        var val = element.value;
        if (val !== null || val !== '') {
            var shaval = hex_sha256(val);
            element.value = shaval;
            return true;
        }
    }
}