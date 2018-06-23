/* 
 * Copyright YYYY Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


// DO NOT REMOVE : GLOBAL FUNCTIONS!
pageSetUp();

/*
 * PAGE RELATED SCRIPTS
 */

$(".js-status-update a").click(function () {
    var selText = $(this).text();
    var $this = $(this);
    $this.parents('.btn-group').find('.dropdown-toggle').html(selText + ' <span class="caret"></span>');
    $this.parents('.dropdown-menu').find('li').removeClass('active');
    $this.parent().addClass('active');
});

/*
 * TODO: add a way to add more todo's to list
 */

// initialize sortable
$(function () {
    $("#sortable1, #sortable2").sortable({
        handle: '.handle',
        connectWith: ".todo",
        update: countTasks
    }).disableSelection();
});

// check and uncheck
$('.todo .checkbox > input[type="checkbox"]').click(function () {
    var $this = $(this).parent().parent().parent();

    if ($(this).prop('checked')) {
        $this.addClass("complete");

        // remove this if you want to undo a check list once checked
        //$(this).attr("disabled", true);
        $(this).parent().hide();

        // once clicked - add class, copy to memory then remove and add to sortable3
        $this.slideUp(500, function () {
            $this.clone().prependTo("#sortable3").effect("highlight", {}, 800);
            $this.remove();
            countTasks();
        });
    } else {
        // insert undo code here...
    }

})
// count tasks
function countTasks() {

    $('.todo-group-title').each(function () {
        var $this = $(this);
        $this.find(".num-of-tasks").text($this.next().find("li").size());
    });

}

/*
 * RUN PAGE GRAPHS
 */

// Load FLOAT dependencies (related to page)
//loadScript("js/plugin/flot/jquery.flot.cust.js", loadFlotResize);
//
//function loadFlotResize() {
//    loadScript("js/plugin/flot/jquery.flot.resize.js", loadFlotToolTip);
//}
//
//function loadFlotToolTip() {
//    loadScript("js/plugin/flot/jquery.flot.tooltip.js", generatePageGraphs);
//}
generatePageGraphs();
function generatePageGraphs() {

    /* TAB 1: UPDATING CHART */
    // For the demo we use generated data, but normally it would be coming from the server

    var data = [],
            totalPoints = 200,
            $UpdatingChartColors = $("#updating-chart").css('color');

    function getRandomData() {
        if (data.length > 0)
            data = data.slice(1);

        // do a random walk
        while (data.length < totalPoints) {
            var prev = data.length > 0 ? data[data.length - 1] : 50;
            var y = prev + Math.random() * 10 - 5;
            if (y < 0)
                y = 0;
            if (y > 100)
                y = 100;
            data.push(y);
        }

        // zip the generated y values with the x values
        var res = [];
        for (var i = 0; i < data.length; ++i)
            res.push([i, data[i]])
        return res;
    }

    // setup control widget
    var updateInterval = 1500;
    $("#updating-chart").val(updateInterval).change(function () {

        var v = $(this).val();
        if (v && !isNaN(+v)) {
            updateInterval = +v;
            $(this).val("" + updateInterval);
        }

    });

    // setup plot
    var options = {
        yaxis: {
            min: 0,
            max: 100
        },
        xaxis: {
            min: 0,
            max: 100
        },
        colors: [$UpdatingChartColors],
        series: {
            lines: {
                lineWidth: 1,
                fill: true,
                fillColor: {
                    colors: [{
                            opacity: 0.4
                        }, {
                            opacity: 0
                        }]
                },
                steps: false

            }
        }
    };

    var plot = $.plot($("#updating-chart"), [getRandomData()], options);
//    var plot2 = $.plot($("#userChart"), [getRandomData()], options);

    /* live switch */
    $('input[type="checkbox"]#start_interval').click(function () {
        if ($(this).prop('checked')) {
            $on = true;
            updateInterval = 1500;
            update();
        } else {
            clearInterval(updateInterval);
            $on = false;
        }
    });

    function update() {
        if ($on == true) {
            plot.setData([getRandomData()]);
            plot.draw();
            setTimeout(update, updateInterval);

        } else {
            clearInterval(updateInterval)
        }

    }

    var $on = false;

    /*end updating chart*/

    /* TAB 2: Social Network  */

    $(function () {
        // jQuery Flot Chart
        var twitter = [
            [1, 27],
            [2, 34],
            [3, 51],
            [4, 48],
            [5, 55],
            [6, 65],
            [7, 61],
            [8, 70],
            [9, 65],
            [10, 75],
            [11, 57],
            [12, 59],
            [13, 62]
        ],
                facebook = [
                    [1, 25],
                    [2, 31],
                    [3, 45],
                    [4, 37],
                    [5, 38],
                    [6, 40],
                    [7, 47],
                    [8, 55],
                    [9, 43],
                    [10, 50],
                    [11, 47],
                    [12, 39],
                    [13, 47]
                ],
                data = [{
                        label: "Twitter",
                        data: twitter,
                        lines: {
                            show: true,
                            lineWidth: 1,
                            fill: true,
                            fillColor: {
                                colors: [{
                                        opacity: 0.1
                                    }, {
                                        opacity: 0.13
                                    }]
                            }
                        },
                        points: {
                            show: true
                        }
                    }, {
                        label: "Facebook",
                        data: facebook,
                        lines: {
                            show: true,
                            lineWidth: 1,
                            fill: true,
                            fillColor: {
                                colors: [{
                                        opacity: 0.1
                                    }, {
                                        opacity: 0.13
                                    }]
                            }
                        },
                        points: {
                            show: true
                        }
                    }];

        var options = {
            grid: {
                hoverable: true
            },
            colors: ["#568A89", "#3276B1"],
            tooltip: true,
            tooltipOpts: {
                //content : "Value <b>$x</b> Value <span>$y</span>",
                defaultTheme: false
            },
            xaxis: {
                ticks: [
                    [1, "JAN"],
                    [2, "FEB"],
                    [3, "MAR"],
                    [4, "APR"],
                    [5, "MAY"],
                    [6, "JUN"],
                    [7, "JUL"],
                    [8, "AUG"],
                    [9, "SEP"],
                    [10, "OCT"],
                    [11, "NOV"],
                    [12, "DEC"],
                    [13, "JAN+1"]
                ]
            },
            yaxes: {
            }
        };

        var plot3 = $.plot($("#statsChart"), data, options);
        var plot4 = $.plot($("#dataChart"), data, options);
        var plot5 = $.plot($("#mmsChart"), data, options);
    });

    // END TAB 2

    // TAB THREE GRAPH //
    /* TAB 3: Revenew  */

    $(function () {

        var trgt = [
            [1354586000000, 153],
            [1364587000000, 658],
            [1374588000000, 198],
            [1384589000000, 663],
            [1394590000000, 801],
            [1404591000000, 1080],
            [1414592000000, 353],
            [1424593000000, 749],
            [1434594000000, 523],
            [1444595000000, 258],
            [1454596000000, 688],
            [1464597000000, 364]
        ],
                prft = [
                    [1354586000000, 53],
                    [1364587000000, 65],
                    [1374588000000, 98],
                    [1384589000000, 83],
                    [1394590000000, 980],
                    [1404591000000, 808],
                    [1414592000000, 720],
                    [1424593000000, 674],
                    [1434594000000, 23],
                    [1444595000000, 79],
                    [1454596000000, 88],
                    [1464597000000, 36]
                ],
                sgnups = [
                    [1354586000000, 647],
                    [1364587000000, 435],
                    [1374588000000, 784],
                    [1384589000000, 346],
                    [1394590000000, 487],
                    [1404591000000, 463],
                    [1414592000000, 479],
                    [1424593000000, 236],
                    [1434594000000, 843],
                    [1444595000000, 657],
                    [1454596000000, 241],
                    [1464597000000, 341]
                ],
                toggles = $("#rev-toggles"),
                target = $("#flotcontainer");

        var data = [{
                label: "SMS",
                data: trgt,
                bars: {
                    show: true,
                    align: "center",
                    barWidth: 30 * 30 * 60 * 1000 * 80
                }
            }, {
                label: "Money",
                data: prft,
                color: '#3276B1',
                lines: {
                    show: true,
                    lineWidth: 3
                },
                points: {
                    show: true
                }
            }]

        var options = {
            grid: {
                hoverable: true
            },
            tooltip: true,
            tooltipOpts: {
                //content: '%x - %y',
                //dateFormat: '%b %y',
                defaultTheme: false
            },
            xaxis: {
                mode: "time"
            },
            yaxes: {
                tickFormatter: function (val, axis) {
                    return "$" + val;
                },
                max: 1200
            }

        };

        plot2 = null;

        function plotNow() {
            var d = [];
            toggles.find(':checkbox').each(function () {
                if ($(this).is(':checked')) {
                    d.push(data[$(this).attr("name").substr(4, 1)]);
                }
            });
            if (d.length > 0) {
                if (plot2) {
                    plot2.setData(d);
                    plot2.draw();
                } else {
                    plot2 = $.plot(target, d, options);
                }
            }

        }
        ;

        toggles.find(':checkbox').on('change', function () {
            plotNow();
        });
        plotNow()

    });

}

/*
 * VECTOR MAP
 */

data_array = {
    "US": 4977,
    "AU": 4873,
    "IN": 3671,
    "BR": 2476,
    "TR": 1476,
    "CN": 146,
    "CA": 134,
    "BD": 100
};

// Load Map dependency 1 then call for dependency 2
//loadScript("js/plugin/vectormap/jquery-jvectormap-1.2.2.min.js", loadMapFile);
//
//// Load Map dependency 2 then rendeder Map
//function loadMapFile() {
//    loadScript("js/plugin/vectormap/jquery-jvectormap-world-mill-en.js", renderVectorMap);
//}
renderVectorMap();
function renderVectorMap() {
    $('#vector-map').vectorMap({
        map: 'world_mill_en',
        backgroundColor: '#fff',
        regionStyle: {
            initial: {
                fill: '#c4c4c4'
            },
            hover: {
                "fill-opacity": 1
            }
        },
        series: {
            regions: [{
                    values: data_array,
                    scale: ['#85a8b6', '#4d7686'],
                    normalizeFunction: 'polynomial'
                }]
        },
        onRegionLabelShow: function (e, el, code) {
            if (typeof data_array[code] == 'undefined') {
                e.preventDefault();
            } else {
                var countrylbl = data_array[code];
                el.html(el.html() + ': ' + countrylbl + ' dollar');
            }
        }
    });
}

/*
 * FULL CALENDAR JS
 */

// Load Calendar dependency then setup calendar
//loadScript("js/plugin/fullcalendar/jquery.fullcalendar.min.js", setupCalendar);

setupCalendar();
function setupCalendar() {

    if ($("#calendar").length) {
        var date = new Date();
        var d = date.getDate();
        var m = date.getMonth();
        var y = date.getFullYear();

        var calendar = $('#calendar').fullCalendar({
            editable: true,
            draggable: true,
            selectable: false,
            selectHelper: true,
            unselectAuto: false,
            disableResizing: false,
            header: {
                left: 'title', //,today
                center: 'prev, next, today',
                right: 'month, agendaWeek, agenDay' //month, agendaDay,
            },
            select: function (start, end, allDay) {
                var title = prompt('Event Title:');
                if (title) {
                    calendar.fullCalendar('renderEvent', {
                        title: title,
                        start: start,
                        end: end,
                        allDay: allDay
                    }, true // make the event "stick"
                            );
                }
                calendar.fullCalendar('unselect');
            },
            events: [{
                    title: 'All Day Event',
                    start: new Date(y, m, 1),
                    description: 'long description',
                    className: ["event", "bg-color-greenLight"],
                    icon: 'fa-check'
                }, {
                    title: 'Long Event',
                    start: new Date(y, m, d - 5),
                    end: new Date(y, m, d - 2),
                    className: ["event", "bg-color-red"],
                    icon: 'fa-lock'
                }, {
                    id: 999,
                    title: 'Repeating Event',
                    start: new Date(y, m, d - 3, 16, 0),
                    allDay: false,
                    className: ["event", "bg-color-blue"],
                    icon: 'fa-clock-o'
                }, {
                    id: 999,
                    title: 'Repeating Event',
                    start: new Date(y, m, d + 4, 16, 0),
                    allDay: false,
                    className: ["event", "bg-color-blue"],
                    icon: 'fa-clock-o'
                }, {
                    title: 'Meeting',
                    start: new Date(y, m, d, 10, 30),
                    allDay: false,
                    className: ["event", "bg-color-darken"]
                }, {
                    title: 'Lunch',
                    start: new Date(y, m, d, 12, 0),
                    end: new Date(y, m, d, 14, 0),
                    allDay: false,
                    className: ["event", "bg-color-darken"]
                }, {
                    title: 'Birthday Party',
                    start: new Date(y, m, d + 1, 19, 0),
                    end: new Date(y, m, d + 1, 22, 30),
                    allDay: false,
                    className: ["event", "bg-color-darken"]
                }, {
                    title: 'Smartadmin Open Day',
                    start: new Date(y, m, 28),
                    end: new Date(y, m, 29),
                    className: ["event", "bg-color-darken"]
                }],
            eventRender: function (event, element, icon) {
                if (!event.description == "") {
                    element.find('.fc-event-title').append("<br/><span class='ultra-light'>" + event.description +
                            "</span>");
                }
                if (!event.icon == "") {
                    element.find('.fc-event-title').append("<i class='air air-top-right fa " + event.icon +
                            " '></i>");
                }
            }
        });

    }
    ;

    /* hide default buttons */
    $('.fc-header-right, .fc-header-center').hide();

}

// calendar prev
$('#calendar-buttons #btn-prev').click(function () {
    $('.fc-button-prev').click();
    return false;
});

// calendar next
$('#calendar-buttons #btn-next').click(function () {
    $('.fc-button-next').click();
    return false;
});

// calendar today
$('#calendar-buttons #btn-today').click(function () {
    $('.fc-button-today').click();
    return false;
});

// calendar month
$('#mt').click(function () {
    $('#calendar').fullCalendar('changeView', 'month');
});

// calendar agenda week
$('#ag').click(function () {
    $('#calendar').fullCalendar('changeView', 'agendaWeek');
});

// calendar agenda day
$('#td').click(function () {
    $('#calendar').fullCalendar('changeView', 'agendaDay');
});

//loadDataTableScripts();
//function loadDataTableScripts() {
//
//    loadScript("js/plugin/datatables/jquery.dataTables-cust.min.js", dt_2);
//
//    function dt_2() {
//        loadScript("js/plugin/datatables/ColReorder.min.js", dt_3);
//    }
//
//    function dt_3() {
//        loadScript("js/plugin/datatables/FixedColumns.min.js", dt_4);
//    }
//
//    function dt_4() {
//        loadScript("js/plugin/datatables/ColVis.min.js", dt_5);
//    }
//
//    function dt_5() {
//        loadScript("js/plugin/datatables/ZeroClipboard.js", dt_6);
//    }
//
//    function dt_6() {
//        loadScript("js/plugin/datatables/media/js/TableTools.min.js", dt_7);
//    }
//
//    function dt_7() {
//        loadScript("js/plugin/datatables/DT_bootstrap.js", runDataTables);
//    }
//
//}

//runDataTables();
function runDataTables() {



    /* TABLE TOOLS */
    $('#datatable_tabletools').dataTable({
        "sDom": "<'dt-top-row'Tlf>r<'dt-wrapper't><'dt-row dt-bottom-row'<'row'<'col-sm-6'i><'col-sm-6 text-right'p>>",
        "oTableTools": {
            "aButtons": ["copy", "print", {
                    "sExtends": "collection",
                    "sButtonText": 'Save <span class="caret" />',
                    "aButtons": ["csv", "xls", "pdf"]
                }],
            "sSwfPath": "js/plugin/datatables/media/swf/copy_csv_xls_pdf.swf"
        },
        "fnInitComplete": function (oSettings, json) {
            $(this).closest('#dt_table_tools_wrapper').find('.DTTT.btn-group').addClass('table_tools_group').children('a.btn').each(function () {
                $(this).addClass('btn-sm btn-default');
            });
        }
    });

    /* END TABLE TOOLS */
    /* TABLE TOOLS */
    $('#datatable_prodspec').dataTable({
        "sDom": "<'dt-top-row'Tlf>r<'dt-wrapper't><'dt-row dt-bottom-row'<'row'<'col-sm-6'i><'col-sm-6 text-right'p>>",
        "oTableTools": {
            "aButtons": ["copy", "print", {
                    "sExtends": "collection",
                    "sButtonText": 'Save <span class="caret" />',
                    "aButtons": ["csv", "xls", "pdf"]
                }],
            "sSwfPath": "js/plugin/datatables/media/swf/copy_csv_xls_pdf.swf"
        },
        "fnInitComplete": function (oSettings, json) {
            $(this).closest('#dt_table_tools_wrapper').find('.DTTT.btn-group').addClass('table_tools_group').children('a.btn').each(function () {
                $(this).addClass('btn-sm btn-default');
            });
        }
    });

    /* END TABLE TOOLS */

}