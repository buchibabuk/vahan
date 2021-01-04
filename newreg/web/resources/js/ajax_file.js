
function getAjax()
{

    var xmlhttp;
    if (window.XMLHttpRequest)
    {// code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp = new XMLHttpRequest();
    }
    else
    {// code for IE6, IE5
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    return xmlhttp;
}



function showWork()
{
    var xmlhttp = getAjax();

    var bdata = document.getElementById("showWorkPanelTask");
    
    xmlhttp.onreadystatechange = function()
    {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200)
        {
            //alert("response text == " +xmlhttp.responseText);
            bdata.innerHTML = xmlhttp.responseText;
        }
    }
    var url = "http://localhost:8084/Kerla/vahan/ui/authorised/CommonUI/SeatTask.xhtml";

    xmlhttp.open("POST", url, true);
    xmlhttp.send();

}




function showSelectedSeatWork()
{
    var xmlhttp = getAjax();

    var bdata = document.getElementById("showWorkPanelTask");
    
    xmlhttp.onreadystatechange = function()
    {
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200)
        {
            //alert("response text == " +xmlhttp.responseText);
            bdata.innerHTML = xmlhttp.responseText;
        }
    }
    var url = "http://localhost:8084/Kerla/vahan/ui/authorised/CommonUI/NewRegistration.xhtml";

    xmlhttp.open("POST", url, true);
    xmlhttp.send();

}