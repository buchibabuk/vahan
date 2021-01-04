function makeCaps(elementName){
    var element = document.getElementById(elementName);
    var val = element.value;
    val = val.toUpperCase();
    element.value=val;
    return true;
}

function makeLatterCaps(elementName){
    var element = document.getElementById(elementName);
    var val = element.value;
    val = val.toUpperCase();
    element.value=val;
    return true;
}

function formatNubmerPart(noPart){
    var element = document.getElementById("tf_REGN_NO2");
    var fmtPart = element.value;
    var fmtNoPart = fmtPart;
    if(fmtPart != ""){
        for(var i= fmtPart.length; i <= noPart - 1; i++){
            fmtNoPart = "0" + fmtNoPart ;
        }
    }
    element.value =  fmtNoPart;
}
function makeNumberPart1(){
    var vehiclenumber1=  document.getElementById("tf_REGN_NO1");
    var Finaloutput1 = vehiclenumber1.value;
    var len  = Finaloutput1.length;
    if(len == 0){
        alert("Series part can not be empty");
    }
}

function makeNumberPart2(){
    var vehiclenumber2=  document.getElementById("tf_REGN_NO2");
    var Finaloutput2 = vehiclenumber2.value;
    var leng = Finaloutput2.length;
    if(leng == 0){
        alert("Number part can not be empty");
        document.getElementById("tf_REGN_NO1").focus();
    }else{
        vehiclenumber2.value =leadingZero3(Finaloutput2);
    }
}


function checkEntry(){
    var reg1 = document.getElementById("tf_REGN_NO1").value;
    var reg2 = document.getElementById("tf_REGN_NO2").value;
    var chasino = document.getElementById("tf_CHASI_NO").value;
    var rtoName = document.getElementById("tf_rtoList").value;
    var capcha = document.getElementById("tf_CAPCHA_GET_DETAILS").value;



    if(rtoName ==-1 || rtoName==null)
    {
        alert("Please select RTO  ");
        document.getElementById("tf_rtoList").focus();
        return false;

    }
    else if(reg1 == null || reg1 == "" )
    {
        alert(" Please enter Registration number") ;
        document.getElementById("tf_REGN_NO1").focus();
        return false;
    }
    else if( reg2 == null || reg2 == "")
    {
        alert(" Please enter Series part") ;
        document.getElementById("tf_REGN_NO2").focus();
        return false;

    }else if ( chasino == null || chasino == "")
    {

        alert("Please enter Chassis number");
        document.getElementById("tf_CHASI_NO").focus();
        return false;
    }else if (capcha == null || capcha == "")
    {
        alert("Please enter Image shown");
        document.getElementById("tf_CAPCHA_GET_DETAILS").focus();
        return false;
    }
   
    return true;
}

function leadingZero3(x){
    var len=x.length;
    len=4-len;
    for(var i=len;i>0;i--){
        x="0"+x;
    }
    return x;
}

function doUnload(evt){
    var e = (window.event) ? window.event : evt;

    if (e.clientX < 0 && e.clientY < 0){

        alert("window closing....");

    }
}

function doSomething(e) {
    var posx = 0;
    var posy = 0;
    if (!e) var e = window.closed;
    if (e.pageX || e.pageY)     {
        posx = e.pageX;
        posy = e.pageY;
    } else if (e.clientX || e.clientY)     {
        posx = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
        posy = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
    }
    alert(posx + ", " + posy);
}


function confirmMe(){
    alert("Thank you for visiting us!!");
}

function fnUnloadHandler() {
    // Add your code here
    alert("Unload event.. Do something to invalidate users session..");
}

function unLoadFnc() {
    if(window.screenLeft < 10004){
        alert("Refresh Coordinate: "+window.screenLeft);
    } else {
        //this is close
        alert("Close Coordinate: "+window.screenLeft);
    }
}//end of unLoadFnc


function fun() {
    top.consoleRef=window.open('#{facesContext.externalContext.requestContextPath}/faces/user/jsp/login.jsp','myconsole',
        'width=350,height=250'
        +',menubar=0'
        +',toolbar=0'
        +',status=0'
        +',scrollbars=1'
        +',resizable=0');
    top.consoleRef.document.close();
}




