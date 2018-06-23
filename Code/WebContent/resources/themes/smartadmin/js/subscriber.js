/* 
 * Copyright YYYY Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
pageSetUp();
getDateTimePicker();

$("a.btn-stat").click(function () {
    $(this).closest("div.group-button").find("a").removeClass("btn-active");
    $(this).addClass("btn-active");
});

function showUserHistory() {
    $('html, body').animate({
        scrollTop: $('#dataSubscriber').offset().top
    }, 3000);
}

function changeTypeOption(id, optionTime) {
    switch (optionTime) {
        case 2 :
            optionTime = 0;
            break;
        case 4 :
            optionTime = 1;
            break;
        default :
            optionTime = 2;
            break;
    }
    $(id).children().each(function (i) {
        if (i === optionTime) {
            $(this).removeClass("hide");
        } else {
            $(this).addClass("hide");
        }
    });
}
//Validation, json
function isEmptyString(string) {
    string = $.trim(string);
    return (!string || (0 === string.length));
}

//Date time
function getDateTimePicker() {
    $('.dateTimePicker').datepicker({
        todayBtn: "linked",
        todayHighlight: true,
        format: "dd-mm-yyyy",
        autoclose: true
    }).on("show", function (event){
        if(event.date){
            $(this).data("stickyDate", event.date);
        }else{
            $(this).data("stickyDate", null);
        }
    }).on("hide", function (event){
        var stickyDate = $(this).data("stickyDate");
        if(!event.date && stickyDate){
            $(this).datepicker('setDate', stickyDate);
            $(this).data("stickyDate", null);
        }
    });

    $('.monthTimePicker').datepicker({
        format: "mm-yyyy",
        viewMode: "months",
        minViewMode: "months",
        autoclose: true
    }).on("show", function (event){
        if(event.date){
            $(this).data("stickyDate", event.date);
        }else{
            $(this).data("stickyDate", null);
        }
    }).on("hide", function (event){
        var stickyDate = $(this).data("stickyDate");
        if(!event.date && stickyDate){
            $(this).datepicker('setDate', stickyDate);
            $(this).data("stickyDate", null);
        }
    });

    $('.yearTimePicker').datepicker({
        format: "yyyy",
        viewMode: "years",
        minViewMode: "years",
        autoclose: true
    }).on("show", function (event){
        if(event.date){
            $(this).data("stickyDate", event.date);
        }else{
            $(this).data("stickyDate", null);
        }
    }).on("hide", function (event){
        var stickyDate = $(this).data("stickyDate");
        if(!event.date && stickyDate){
            $(this).datepicker('setDate', stickyDate);
            $(this).data("stickyDate", null);
        }
    });
}

function isDateFormat(text) {
    if (isEmptyString(text)) {
        return false;
    }
    var dateRegex = /^(\d{1,2})-(\d{1,2})-(\d{4})$/;
    var value = text.match(dateRegex);
    var day = parseInt(value[1]);
    var month = parseInt(value[2]);
    var year = parseInt(value[3]);
    if (day < 1 || day > 31 || month < 1 || month > 12 || day > dayInMonth(month, year)) {
        return false;
    }
    return true;
}

function dayInMonth(month, year) {
    switch (month) {
        case 2 :
            return ((year % 4 === 0) && (year % 100)) || (year % 400 === 0) ? 29 : 28;
        case 4 :
        case 6 :
        case 9 :
        case 14 :
            return 30;
        default :
            return 31;
    }
}

function getParseJSON(tagId) {
    var value = $("#" + tagId).text();
    return isEmptyString(value) === true ? null : $.parseJSON($.trim(value));
}