function trimAllInputText() {
	var arrInput = document.getElementsByTagName("input");
	try {
		var count = 0;
		for (var i = arrInput.length - 1; i > 0; i--) {
			if (arrInput[i].getAttribute("type") === "text"
					|| arrInput[i].getAttribute("type") === "number"
					|| arrInput[i].getAttribute("role") === "textbox") {
				arrInput[i].value = arrInput[i].value.trim();
				count++;
			}
		}
	} catch (e) {
		console.log(e);
	}
}
// Search menu
function hideLeftMenu(search) {
	$("#left-panel .categoty").hide();
	$('#left-panel .categoty > ul > li').hide();
	$('#left-panel .categoty > ul > li a').each(function() {
		var title = $(this).attr("title");
		if (typeof title !== typeof undefined) {
			if (title.toLowerCase().indexOf(search.toLowerCase()) !== -1) {
				$(this).parent().parent().parent().show();
				$(this).parent().show();
				$(this).parent().parent().parent().addClass("open");
				$(this).parent().parent().show();
			}
		}
	});
}
function showLeftMenu() {
	$("#left-panel .categoty").show();
	$('#left-panel .categoty > ul > li').show();
	$('#left-panel .categoty > ul > li a').parent().parent().parent()
			.removeClass("open");
	$('#left-panel .categoty > ul > li a').parent().parent().hide();
	$('#left-panel ul').find(".hightline").addClass('open');
	$('#left-panel ul').find(".open ul").show();
}
function keyUpSearch() {
	$("#keyword_menu").keyup(function() {
		var search = $.trim(this.value);
		if (search === "") {
			showLeftMenu();
		} else {
			hideLeftMenu(search);
		}
	});
}
function changeBreacum() {
	var nameCat = $('#left-panel .categoty > ul > li[class="active"] a').text();
	var nameParentCat = $('#left-panel .open a span').text();
	if (nameCat) {
		$('.bread_crumb .home span').html(nameParentCat);
		$('.bread_crumb .current-cat').html(nameCat);
	} else {
		$('.bread_crumb .home span').html("Home");
		$('.bread_crumb .current-cat').html("Welcome to vOCS");
	}
}

$(document).ready(function() {
	$(".heightTree").height($(".heightContentTree").height() - 32);

	if ($("#left-panel").height() < $(".heightTree").height() + 72) {
		$("#left-panel").height($(".heightContentTree").height() + 72);
	}
	
	 $('.box-link').click(function(e){
		$('.box-link').removeClass('active');
		$(this).addClass('active'); 
	 });
	 
	 $.mask.definitions['9']= "[0-9*#]";
});

$("html").mouseover(function() {
	$(".heightTree").height($(".heightContentTree").height() - 32);

	if ($("#left-panel").height() < $(".heightTree").height() + 72) {
		$("#left-panel").height($(".heightContentTree").height() + 72);
	}
});

function clickBtnHiddenUpdatePreField() {
	$(".btnUpdateHidden").click();
}

// Context menu - Thanhnd - begin
var siteFunctions = {
	// patch to fix a problem that the context menu disappears after update
	// delay the show to occure after the update
	patchContextMenuShow : function() {
		'use strict';
		var protShow = PrimeFaces.widget.ContextMenu.prototype.show;
		siteFunctions.patchContextMenuShow.lastEvent = null;
		PrimeFaces.widget.ContextMenu.prototype.show = function(e) {
			var ret;
			if (e) {

				// console.log('saving last event');

				siteFunctions.patchContextMenuShow.lastEvent = e;
				siteFunctions.patchContextMenuShow.lastEventArg = arguments;
				siteFunctions.patchContextMenuShow.lastEventContext = this;
			} else if (siteFunctions.patchContextMenuShow.lastEvent) {

				// console.log('executing last event');

				ret = protShow.apply(
						siteFunctions.patchContextMenuShow.lastEventContext,
						siteFunctions.patchContextMenuShow.lastEventArg);

				// console.log('clearing last event');
				siteFunctions.patchContextMenuShow.lastEvent = null;
			}
			
			//huannn
//			$('.txtTreeSearchCustom').keyup();
			
			return ret;
		};
	}
};
$(document).ready(function() {
	'use strict';
	try {
		siteFunctions.patchContextMenuShow();
	} catch (e) {
		console.error(e);
	}
});

function clearFilter(item) {
	if (PF(item) != undefined) {
		PF(item).clearFilters();
	}
}

function onOffRedirectSetting(mode) {
	var items = $(
			PrimeFaces
					.escapeClientId('form_offer_detail:tabId:dtRedirectAddress'))
			.find('input');
	for (var i = 0; i < items.size(); i++) {
		if (mode) {
			$(items[i]).attr("readonly", "readonly");
		} else {
			$(items[i]).removeAttr("readonly");
		}
	}
}


function selectedRowOnTable(btn) {
	var selectedRow = $(PrimeFaces.escapeClientId(btn.source)).parent().parent();
	var otherRows = $(selectedRow).parent().find('tr');
	for (var i = 0; i < otherRows.length; i++) {
		$(otherRows[i]).removeClass('table-selected-row');
	}
	$(selectedRow).addClass('table-selected-row');
}