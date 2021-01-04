/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function cal() {
    var sum = 0;
    for (var i = 0; ; i++) {
        if (document.getElementById("new_trade_cert_application_subview:application_sections_data_table_renew:" + i + ":lb_no_of_allowed_vehicles_editable") !== null) {
            var value = document.getElementById("new_trade_cert_application_subview:application_sections_data_table_renew:" + i + ":lb_no_of_allowed_vehicles_editable").value;
            if (value == "") {
                value = 0;
                document.getElementById("new_trade_cert_application_subview:application_sections_data_table_renew:" + i + ":lb_no_of_allowed_vehicles_editable").value = 0;
            }
            sum += parseInt(value);
        } else {
            break;
        }
    }
    document.getElementById("new_trade_cert_application_subview:application_sections_data_table_renew:tf_grand_total_renewed").value = sum;
}

function calPenalty() {
    var totalFineAmount = 0;
    var initialGrandTotal = 0;
    var manualRcptAmt = 0;
    for (var i = 0; ; i++) {
        if (document.getElementById("new_trade_cert_application_fee_subview:application_sections_data_table:" + i + ":lb_sr_no") !== null) {

            var srNo = document.getElementById("new_trade_cert_application_fee_subview:application_sections_data_table:" + i + ":lb_sr_no").textContent;

            var fee = 0;
            if (document.getElementById("new_trade_cert_application_fee_subview:application_sections_data_table:" + i + ":lb_total_fee") !== null) {
                fee = document.getElementById("new_trade_cert_application_fee_subview:application_sections_data_table:" + i + ":lb_total_fee").textContent;
            }

            var tax = 0;
            if (document.getElementById("new_trade_cert_application_fee_subview:application_sections_data_table:" + i + ":lb_total_tax") !== null) {
                tax = document.getElementById("new_trade_cert_application_fee_subview:application_sections_data_table:" + i + ":lb_total_tax").textContent;
            }

            var surcharge = 0;
            if (document.getElementById("new_trade_cert_application_fee_subview:application_sections_data_table:" + i + ":lb_total_surcharge") !== null) {
                surcharge = document.getElementById("new_trade_cert_application_fee_subview:application_sections_data_table:" + i + ":lb_total_surcharge").textContent;
            }

            initialGrandTotal += parseInt(fee) + parseInt(tax) + parseInt(surcharge);
        } else {
            break;
        }
    }
//  if(document.getElementById("new_trade_cert_application_fee_subview:lb_misc_fee_value")!= null){
//    initialGrandTotal += parseInt(document.getElementById("new_trade_cert_application_fee_subview:lb_misc_fee_value").textContent); 
//  }

    if (document.getElementById("new_trade_cert_application_fee_subview:lb_service_charge_collected_value") !== null) {
        initialGrandTotal += parseInt(document.getElementById("new_trade_cert_application_fee_subview:lb_service_charge_collected_value").textContent);
    }

    if (document.getElementById("new_trade_cert_application_fee_subview:lb_transaction_charge_collected_value") !== null) {
        initialGrandTotal += parseInt(document.getElementById("new_trade_cert_application_fee_subview:lb_transaction_charge_collected_value").textContent);
    }

    for (var i = 0; ; i++) {

        if (document.getElementById("new_trade_cert_application_fee_subview:application_sections_data_table:" + i + ":lb_sr_no") !== null) {

            var value = document.getElementById("new_trade_cert_application_fee_subview:application_sections_data_table:" + i + ":lb_fine").value;

            if (value === "") {
                value = 0;
                document.getElementById("new_trade_cert_application_fee_subview:application_sections_data_table:" + i + ":lb_fine").value = 0;
            }

            totalFineAmount += parseInt(value);
        } else {
            break;
        }
    }

    var balTax = 0;
    if (document.getElementById("new_trade_cert_application_fee_subview:balTax") !== null) {
        try {
            balTax = parseInt(document.getElementById("new_trade_cert_application_fee_subview:balTax").value);
            if (isNaN(balTax)) {
                balTax = 0;
            }
        } catch (error) {
            balTax = 0;
        }
    }
    document.getElementById("new_trade_cert_application_fee_subview:txt_total_fine_collected").textContent = totalFineAmount;
    manualRcptAmt = document.getElementById("new_trade_cert_application_fee_subview:lb_manual_fee_collected_value").textContent;
    document.getElementById("new_trade_cert_application_fee_subview:lb_fee_collected_value").textContent = (initialGrandTotal + totalFineAmount + balTax) - manualRcptAmt;
}







