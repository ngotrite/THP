//get width scrollbar
function getScrollbarWidth() {
    var outer = document.createElement("div");
    outer.style.visibility = "hidden";
    outer.style.width = "100px";
    outer.style.msOverflowStyle = "scrollbar"; // needed for WinJS apps

    document.body.appendChild(outer);

    var widthNoScroll = outer.offsetWidth;
    // force scrollbars
    outer.style.overflow = "scroll";

    // add innerdiv
    var inner = document.createElement("div");
    inner.style.width = "100%";
    outer.appendChild(inner);

    var widthWithScroll = inner.offsetWidth;

    // remove divs
    outer.parentNode.removeChild(outer);

    return widthNoScroll - widthWithScroll;
}
//end get width scrollbar
//check scrollbar
(function ($) {
    $.fn.hasScrollBar = function () {
        return this.get(0).scrollHeight > this.height();
    };
})(jQuery);
//end check scrollbar
//check scrollbar
(function ($) {
    $.fn.hasVerticalScrollbar = function () {
        // This will return true, when the div has vertical scrollbar
        return this.get(0).scrollHeight > this.height();
    };
    $.fn.hasHorizontalScrollbar = function () {
        // This will return true, when the div has horizontal scrollbar
        return this.get(0).scrollWidth > this.width();
    };
})(jQuery);
function checkScrollbar() {
    var bodyForm = $("#bodyForm");
//    console.log("Vertical Scrollbar : " + bodyForm.hasVerticalScrollbar());
//    console.log("Horizontal Scrollbar : " + bodyForm.hasHorizontalScrollbar());
    return bodyForm.hasVerticalScrollbar();

}

function scrollTo(id) {
    var el = document.getElementById(id);
    var xPos = 0;
    var yPos = 0;
    while (el) {
//        console.log("id : " + el.id);
        if (el.id === "content") {
            break;
        }
        xPos += el.offsetLeft;
        yPos += el.offsetTop;
        el = el.offsetParent;
    }
    xPos -= 14;//Vi tri tuong doi voi content
    yPos -= 134;//Vi tri tuong doi voi content
//    console.log("xPos : " + xPos);
//    console.log("yPos : " + yPos);
    //hieu chinh trong qua trinh view
    xPos -= 210;
    yPos -= 30;
    $("#panelRuleMap").animate({scrollTop: yPos + "px"}, 100);
    $("#panelRuleMap").animate({scrollLeft: xPos + "px"}, 100);
}

function switchClass(id, preStyle, tmpStyle) {
//    console.log("id : " + id);
//    console.log("preStyle : " + preStyle);
//    console.log("tmpStyle : " + tmpStyle);
    $(id).switchClass(preStyle, tmpStyle, 500);
    $(id).switchClass(tmpStyle, preStyle, 500);
}
//check scrollbar
function showUncolapseButton() {
    jQuery("#right-arrow").removeClass("display-none");
    jQuery("#right-arrow-splitter").removeClass("display-none");
    resizeRightBody(true);
}
function hideUncolapseButton() {
    jQuery("#right-arrow").addClass("display-none");
    jQuery("#right-arrow-splitter").addClass("display-none");
    resizeRightBody(false);
}
function reloadRightBody() {
//    var hHeader = jQuery("#vt-header").height() + jQuery("#vt-header").offset().top;
//    var hBody = jQuery(window).height() - hHeader - 5;
//    var hLeftBody = jQuery("#left-body").height();
//    jQuery("#left-nano").css("min-height", hBody);
//    jQuery("#right-nano").css("min-height", hBody);
//
//    if (hLeftBody > hBody) {
//        jQuery("#menu-container").css("min-height", hLeftBody + 35);
//        jQuery("#sub-system-container").css("height", hLeftBody + 35);
//    }
//    else {
//        jQuery("#menu-container").css("min-height", hBody + 35);
//        jQuery("#sub-system-container").css("height", hBody + 35);
//    }
}
function resizeRightBody(larger) {
    if (larger) {
        var wRightBody = Math.floor(jQuery("#right-body").width());
        var wRightNano = Math.floor(jQuery("#right-nano").width());
        var wLeftBody = Math.floor(jQuery("#left-body").width());
        var wLoss = wRightBody - wRightNano - wLeftBody - 1;
        jQuery("#right-nano").animate({width: wRightNano + wLoss + "px"}, 200);
        jQuery("#bodyContent").animate({width: wRightNano + wLoss - 5 + "px"}, 200);
    }
    else {
        var wRightBody = Math.floor(jQuery("#right-body").width());
        var wLeftNano = Math.floor(jQuery("#left-nano").width());//+ Math.floor(jQuery("#left-nano").offset().left)
        var wRightNano = wRightBody - wLeftNano - 1;
        jQuery("#right-nano").animate({width: wRightNano + "px"}, 200);
        jQuery("#bodyContent").animate({width: wRightNano - 5 + "px"}, 200);
    }
    reloadRightBody();
}
//onResize
//var resizeTimer = 100;
//$(window).resize(function () {
//    clearTimeout(resizeTimer);
//    jQuery("#sub-system-container").css("height", "100%");
//    resizeTimer = setTimeout(reloadRightBody, 100);
//    var wRightBody = Math.floor(jQuery("#right-body").width());
//    var wLeftNano = Math.floor(jQuery("#left-nano").width());//+ Math.floor(jQuery("#left-nano").offset().left);
//    var wRightNano = Math.floor(jQuery("#right-nano").width());
//    if (wRightNano < (wRightBody - wLeftNano)) {
//        resizeRightBody(false);
//    }
//    else {
//        jQuery("#right-nano").animate({width: wRightBody - wLeftNano + 14 + "px"}, 200);
//        jQuery("#bodyContent").animate({width: wRightBody - wLeftNano + 14 + "px"}, 200);
//    }
//});

//jQuery(document).ready(function () {
//    reloadRightBody();
//    jQuery(".nano").nanoScroller();
//    //remove ui-state-default in text box
//    jQuery(".ui-inputfield").css({background: '#ffffff',
//        color: '#3C3F41',
//        fontWeight: 'normal'});
//
//    //execute menu
//    jQuery("#left-arrow").mouseup(function () {
//        jQuery("#main-menu").animate({width: "0"}, 200, function () {
//            jQuery("#left-nano").width("21px");
//            showUncolapseButton();
//        });
//    });
//    jQuery("#right-arrow").mouseup(function () {
//        jQuery("#main-menu").animate({width: "180"}, 200, function () {
//            jQuery("#left-nano").width("202px");
//            hideUncolapseButton();
//        });
//    });
//    //resize right body
//    resizeRightBody(false);
//});

var reFocusCalendar = function () {
    if ($('.iconInline .ui-inputfield').length > 0) {
        $('.iconInline .ui-inputfield').inputmask("mask", {"mask": "99/99/9999"});
    }
};

//Execute Popup
function checkOpenDlg(id) {
    if (PF(id)) {
//        setTimeout(function () {
        PF(id).show();
//        }, 1500);
    }
}
function checkCloseDlg(id) {
    if (PF(id)) {
//        setTimeout(function () {
        PF(id).hide();
//        }, 1500);
    }
//    $('.txtTreeSearchCustom').keyup();
}
// validate
function checkLength(object) {
    if (object) {
        var content = object.getJQ().val();
        if (content.length > 0) {
            return true;
        }
        else {
            return false;
        }
    }
}

function excecuteValueField(object) {
    if (object) {
        if (checkLength(object)) {
            hideValueField();
        }
        else {
            showValueField();
        }
    }
}

function excecuteValueFromTo(object) {
    if (object) {
        if (checkLength(object)) {
            hideValueFromTo();
        }
        else {
            showValueFromTo();
        }
    }
}
//tab change value
function onKeyUpFrom(fromId, toId) {
    var fromNumber = document.getElementById(fromId).value;
    document.getElementById(toId).value = fromNumber;
//    jQuery(document).keyup(function(e) {
//        if (e.keyCode === 9) {
//            if (!fromNumber || fromNumber === "") {
//                alert("from number not null");
//                return;
//            }
//        }
//        document.getElementById(toId).value = fromNumber;
//    });
}
//clear space 2 dau gia tri
function trimAllInputText() {
    var arrInput = document.getElementsByTagName("input");
    try {
        var count = 0;
        for (var i = 0; i < arrInput.length; i++) {
            if (arrInput[i].getAttribute("type") === "text" || arrInput[i].getAttribute("type") === "number" || arrInput[i].getAttribute("role") === "textbox") {
                arrInput[i].value = arrInput[i].value.trim();
                count++;
            }
        }
    } catch (e) {
        console.log(e);
    }
}