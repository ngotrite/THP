/* 
 * Copyright YYYY Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
pageSetUp();
//Color legend
var color = ["#42A7FF", "#1269FF", "#C6FF7A", "#FFCF4D", "#FF9C45", "#FF482B"];
//Jvectormap
var map;
//HighChart
Highcharts.setOptions(Highcharts.theme);
Highcharts.setOptions({
    global: {
        useUTC: false
    }
});
//WebSocket
var webSocket = new WebSocket("ws://" + document.location.host + "/" + window.location.pathname.split("/")[1] + "/summarySocket");
//Close socket
$(window).on("beforeunload", function () {
    webSocket.close();
});
webSocket.onerror = function (event) {
    console.log(event);
};
webSocket.onopen = function () {
    console.log("CONNECTION IS OPENED !!!");
};
webSocket.onclose = function () {
    console.log("CONNECTION IS CLOSED");
};
webSocket.onmessage = function (message) {
    var chartId;
    var response = $.trim(message.data);
    if (isEmptyString(response)) {
        console.log("RESPONSE SOCKET IS EMPTY !!!");
        return;
    }
    var jsonResponse = $.parseJSON(response);
    if (jsonResponse.type === 1) {
        if (parseInt(jsonResponse.continue) === 1) {
            updateChart("revenueChart", jsonResponse.data);
        } else {
            addNewPoint("revenueChart", jsonResponse);
        }
    } else {
        switch (parseInt(jsonResponse.service)) {
            case 1:
                chartId = "voiceChart";
                break;
            case 2:
                chartId = "dataChart";
                break;
            case 3:
                chartId = "smsChart";
                break;
            case 4:
                chartId = "mmsChart";
                break;
            case 5:
                chartId = "userChart";
                break;
        }
        redrawChart(chartId, jsonResponse);
    }
};

$(function () {
    //Highchart
    drawRevenue("revenueChart");
    drawTotalRevenue("totalChart");
    drawService("voiceChart", "Voice Service Growth");
    drawService("smsChart", "SMS Service Growth");
    drawService("mmsChart", "MMS Service Growth");
    drawService("dataChart", "Data Service Growth");
    drawUserNumber("userChart", "Active User Growth");
    drawPercentService("percentServiceChart");
    drawTopup("topupChart");
    drawSubKpi("subKpiChart", 0);
    drawProdSpec("formProdSpec", "prodspecChart");
    //Jvectormap
    intitalMap();
    fillMap(1);
    //Date picker
    getDateTimePicker();
});
//Change type map
$("#typeMap").on("change", function () {
    fillMap(parseInt(this.value));
});
//Draw growth rate
$("#voiceStat").circliful();
$("#dataStat").circliful();
$("#smsStat").circliful();
$("#mmsStat").circliful();
//CSS up down percent
$(".stat-rate span").each(function () {
    var text = $(this).text().trim();
    if (/[-]/g.test(text)) {
        text = text.substr(1);
        $(this).find("span").text(text);
        $(this).find("i").removeClass("fa-caret-up");
        $(this).find("i").addClass("fa-caret-down");
    } else if (text === "0%") {
        $(this).find("i").removeClass("fa-caret-up");
        $(this).find("i").addClass("fa-sort");
    }
});
//Auto update revenue
$("input[type='checkbox']#start_interval").click(function () {
    var isContinue = $(this).prop("checked") === true ? 1 : 0;
    requestUpdateChart(isContinue);
    $(this).blur();
});
//Change style button
$("a.btn-stat").click(function () {
    $(this).closest("div.group-button").find("a").removeClass("btn-active");
    $(this).addClass("btn-active");
    $(this).blur();
});
//Query data (service voice, data, sms, mms) by datetime, option
function requestChart(service, option, idServiceTab) {
    var optionTime = parseInt($("#" + option).find('.btn-active').attr("option"));
    var startTime = $("#" + idServiceTab + "-start").find('.view').val().trim();
    var endTime = $("#" + idServiceTab + "-end").find('.view').val().trim();
    webSocket.send(JSON.stringify({
        type: 0,
        service: service,
        option: optionTime,
        start: startTime,
        end: endTime
    }));
}
//Change option time
$("button.btn-chart").click(function () {
    //Change style button option time
    $(this).closest("div.btn-group").find("button").removeClass("btn-active");
    $(this).addClass("btn-active");
    //Show textbox datetime with option time
    var optionTime = parseInt($(this).attr("option"));
    var serviceTab = $(this).closest("div.tab-pane").attr("id");
    var startTime = getTextBoxDateTimeWithOption("#" + serviceTab + "-start", optionTime);
    var endTime = getTextBoxDateTimeWithOption("#" + serviceTab + "-end", optionTime);
    webSocket.send(JSON.stringify({
        type: 0,
        service: $(this).attr("service"),
        option: optionTime,
        start: startTime,
        end: endTime
    }));
    $(this).blur();
});
//View Sub KPI
$(".rbSubKpi").click(function () {
    var type = parseInt($(this).val());
    drawSubKpi("subKpiChart", type);
    $(this).blur();
});
//Config HighChart
function configOneChart() {
    var highChart = {
        exporting: {
            enabled: false
        },
        credits: {
            enabled: false
        },
        title: {
            text: "Active User",
            floating: true,
            align: 'center'
        },
        tooltip: {
            xDateFormat: "%d-%m-%Y"
        },
        xAxis: [{
                type: "datetime",
                title: {
                    text: "Time"
                },
                labels: {
                    formatter: function () {
                        return Highcharts.dateFormat("%d-%m", this.value);
                    }
                },
                categories: []
            }],
        yAxis: [{
                min: 0,
                gridLineWidth: 1,
                title: {
                    text: "Number",
                    x: -15
                },
                labels: {
                    style: {
                        fontWeight: "bold"
                    },
                    align: "left",
                    format: "{value}"
                }
            }],
        legend: {
            verticalAlign: "top",
            layout: "vertical",
            align: "left",
            floating: true,
            x: 100,
            y: -10
        },
        series: [{
                name: "Number",
                type: "line",
                data: []
            }]
    };
    return highChart;
}

function configTwoChart() {
    var highChart = {
        exporting: {
            enabled: false
        },
        credits: {
            enabled: false
        },
        title: {
            text: "",
            floating: true,
            align: 'center'
        },
        tooltip: {
            shared: true,
            crosshairs: true,
            xDateFormat: "%d-%m-%Y"
        },
        xAxis: {
            type: "datetime",
            title: {
                text: "Time"
            },
            labels: {
                formatter: function () {
                    return Highcharts.dateFormat("%d-%m", this.value);
                }
            },
            categories: []
        },
        yAxis: [{
                min: 0,
                tickAmount: 6,
                gridLineWidth: 1,
                title: {
                    text: "Volume",
                    x: -15
                },
                labels: {
                    style: {
                        fontWeight: "bold"
                    },
                    align: "left",
                    format: "{value}"
                }
            }, {
                min: 0,
                tickAmount: 5,
                gridLineWidth: 0,
                title: {
                    text: "Revenue",
                    x: 15
                },
                labels: {
                    style: {
                        fontWeight: "bold"
                    },
                    align: "right",
                    format: "{value}"
                },
                opposite: true
            }],
        legend: {
            layout: "vertical",
            verticalAlign: "top",
            align: "left",
            y: -20,
            x: 100,
            floating: true
        },
        series: [{
                name: "Volume",
                type: "line",
                data: []
            }, {
                name: "Revenue",
                type: "spline",
                yAxis: 1,
                data: []
            }]
    };
    return highChart;
}

function configAreaChart() {
    var highChart = {
        chart: {
            height: 350,
            type: "area"
        },
        exporting: {
            enabled: false
        },
        credits: {
            enabled: false
        },
        title: {
            text: "Live Revenue"
        },
        legend: {
            verticalAlign: "top",
            align: "right",
            floating: true
        },
        plotOptions: {
            area: {
                fillColor: {
                    linearGradient: {
                        x1: 0,
                        y1: 0,
                        x2: 0,
                        y2: 1
                    },
                    stops: [
                        [0, Highcharts.getOptions().colors[0]],
                        [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get("rgba")]
                    ]
                },
                lineWidth: 1,
                marker: {
                    radius: 3
                }
            }
        },
        xAxis: {
            type: "datetime",
            dateTimeLabelFormats: {
                hour: "%H:%M"
            },
            labels: {
                style: {
                    fontWeight: "bold"
                }
            }
        },
        yAxis: {
            min: 0,
            floating: true,
            gridLineWidth: 1,
            title: {
                text: "Revenue"
            },
            labels: {
                style: {
                    fontWeight: "bold"
                }
            }
        },
        series: [{
                name: "Revenue",
                data: []
            }]
    };
    return highChart;
}

function configPieChart() {
    var highChart = {
        title: {
            text: "Revenue Rate By Service (Today)"
        },
        exporting: {
            enabled: false
        },
        credits: {
            enabled: false
        },
        chart: {
            height: 250,
            plotBackgroundColor: null,
            plotShadow: false,
            type: "pie"
        },
        tooltip: {
            pointFormat: "Revenue : <b>{point.y}</b>"
        },
        legend: {
            enabled: true,
            layout: "vertical",
            align: "right",
            verticalAlign: "top",
            useHTML: true,
            borderWidth: 0,
            y: 65,
            x: -30,
            itemStyle: {
                paddingBottom: "10px"
            },
            labelFormatter: function () {
                return '<div style="width:120px; font-size: 15px;"><span style="float:left">' + this.name + ' : </span><span style="float:right">' + this.percentage.toFixed(2) + ' %</span></div>';
            }
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: "pointer",
                dataLabels: {
                    enabled: false
                },
                showInLegend: true
            }
        },
        series: [{
                name: "Service",
                innerSize: "50%",
                colorByPoint: true,
                data: []
            }]
    };
    return highChart;
}

function configComboChart() {
    var highChart = {
        exporting: {
            enabled: false
        },
        credits: {
            enabled: false
        },
        chart: {
            height: 350
        },
        title: {
            text: "Major Product Stats"
        },
        tooltip: {
            shared: true
        },
        legend: {
            verticalAlign: "top",
            floating: true,
            enabled: true,
            align: "left",
            x: 30
        },
        xAxis: {
            labels: {
                style: {
                    fontWeight: "bold"
                }
            },
            categories: []
        },
        yAxis: [{
                min: 0,
                labels: {
                    format: '{value}',
                    style: {
                        color: Highcharts.getOptions().colors[0]
                    }
                },
                title: {
                    text: 'Volume Voice',
                    style: {
                        color: Highcharts.getOptions().colors[0]
                    }
                }
            }, {
                min: 0,
                gridLineWidth: 0,
                title: {
                    text: 'Volume Data',
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    }
                },
                labels: {
                    format: '{value}',
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    }
                }
            }, {
                min: 0,
                gridLineWidth: 0,
                title: {
                    text: 'Volume Sms',
                    style: {
                        color: Highcharts.getOptions().colors[2]
                    }
                },
                labels: {
                    format: '{value}',
                    style: {
                        color: Highcharts.getOptions().colors[2]
                    }
                },
                opposite: true
            }, {
                min: 0,
                gridLineWidth: 0,
                title: {
                    text: 'Revenue',
                    style: {
                        color: Highcharts.getOptions().colors[3]
                    }
                },
                labels: {
                    format: '{value}',
                    style: {
                        color: Highcharts.getOptions().colors[3]
                    }
                },
                opposite: true
            }],
        series: [{
                name: 'Voice',
                type: 'column',
                yAxis: 0,
                data: []
            }, {
                name: 'Data',
                type: 'column',
                yAxis: 1,
                data: []
            }, {
                name: 'Sms',
                type: 'column',
                yAxis: 2,
                data: []
            }, {
                name: 'Revenue',
                type: 'column',
                yAxis: 3,
                data: []
            }]
    };
    return highChart;
}

//Send update request 
function requestUpdateChart(isContinue) {
    var request = {
        type: 1,
        service: 0,
        continue: isContinue
    };
    webSocket.send(JSON.stringify(request));
}
//Jvectormap
function intitalMap() {
    $("#map").empty();
    map = new jvm.Map({
        map: "vietnam_map_en",
        container: $("#map"),
        zoomAnimate: true,
        regionsSelectable: false,
        backgroundColor: "#4c4f53",
        regionStyle: {
            initial: {
                fill: "white"
            }
        },
        series: {
            regions: [{
                    values: {},
                    normalizeFunction: "polynomial"
                }, {
                    values: {},
                    normalizeFunction: "polynomial"
                }]
        },
        onRegionTipShow: function (event, label, code) {
            var mapObject = $('#map').vectorMap('get', 'mapObject');
            label.html(label.html() + ' (' + code.toString() + ')<br/>Value : ' + mapObject.series.regions[0].values[code]);
        }
    });
}

function fillMap(mapType) {
    var json = getParseJSON("formLocation\\:value-typeMap");
    if (jQuery.isEmptyObject(json)) {
        return;
    }
    var data = json["data"][mapType - 1];
    //Show map legend
    var legend = json["legend"][mapType - 1];
    var unit = json["unit"][mapType - 1];
    createLegend(legend, unit);
    if (jQuery.isEmptyObject(data)) {
        return;
    }
    //Fill map data
    var mapObject = $("#map").vectorMap("get", "mapObject");
    mapObject.series.regions[0].setValues(data);
    setRangeLegend(mapObject, legend);
}

function createLegend(legend, unit) {
    $("#legend-map").empty();
    var mapLegend = '';
    var indexRange = 0;
    var sizeLegend = legend.length - 1;
    if (unit !== "") {
        unit = ' (' + unit + ')';
    }
    if (sizeLegend === 0) {
        mapLegend += '<li><span style="background:' + color[0] + ';"></span> <= ' + legend[0].toLocaleString() + '</li>';
        mapLegend += '<li><span style="background:' + color[1] + ';"></span> >= ' + (legend[0] + 1).toLocaleString() + '</li>';
    } else {
        for (var index = 0; index <= sizeLegend; index++) {
            mapLegend += '<li><span style="background:' + color[indexRange] + ';"></span>';
            if (index === 0) {
                mapLegend += ' <= ' + legend[index].toLocaleString() + unit + '</li>';
                indexRange++;
                mapLegend += '<li><span style="background:' + color[indexRange] + ';"></span>';
                mapLegend += (legend[index] + 1).toLocaleString() + ' - ' + legend[index + 1].toLocaleString();
            } else if (index === sizeLegend) {
                mapLegend += ' >= ' + (legend[index] + 1).toLocaleString();
            } else {
                mapLegend += (legend[index] + 1).toLocaleString() + ' - ' + legend[index + 1].toLocaleString();
            }
            mapLegend += unit + '</li>';
            indexRange++;
        }
    }
    $("#legend-map").append(mapLegend);
}

function setRangeLegend(mapObject, legend) {
    var range;
    var result = '{';
    var data = mapObject.series.regions[0].values;
    $.each(data, function (key, value) {
        range = getIndexRange(legend, parseInt(value));
        result += '"' + key + '\":"' + color[range] + '",';
    });
    result = result.slice(0, -1);
    result += '}';
    mapObject.series.regions[1].setValues($.parseJSON(result));
}

function getIndexRange(legend, value) {
    for (var i = 0; i < legend.length; i++) {
        if (value <= legend[i]) {
            return i;
        }
    }
    return legend.length;
}
//Draw chart
function drawRevenue(chartId) {
    var highChart = configAreaChart();
    var response = getParseJSON("value-" + chartId);
    if (response) {
        highChart.series[0].data = response;
    }
    $("#" + chartId).highcharts(highChart);
}
//Bieu do Sub kpi: type=0 - tong sub theo trang thai; type=1 - tong sub theo trang thai them moi trong ngay
function drawSubKpi(chartId, type) {
    var kpi;
    var index;
    var element;
    var dataKpi;
    var dataTime = [];
    var series = [];
    var response = getParseJSON("value-" + chartId);
    if (response) {
        dataTime = response["time"];
        dataKpi = response["data"][type];
        for (index in dataKpi) {
            element = dataKpi[index];
            for (kpi in element) {
                if (kpi === "active") {
                    series.push({
                        data: element[kpi],
                        type: "line",
                        yAxis: 1,
                        name: kpi
                    });
                } else {
                    series.push({
                        data: element[kpi],
                        type: "spline",
                        yAxis: 0,
                        name: kpi
                    });
                }
            }
        }
    }
    $("#" + chartId).highcharts({
        exporting: {
            enabled: false
        },
        credits: {
            enabled: false
        },
        chart: {
            type: "spline",
            height: 240
        },
        tooltip: {
            shared: true,
            xDateFormat: "Date : %d-%m-%Y"
        },
        title: {
            text: ""
        },
        xAxis: {
            title: {
                enabled: false
            },
            labels: {
                step: 3,
                formatter: function () {
                    return Highcharts.dateFormat("%d-%m", this.value);
                }
            },
            type: "datetime",
            categories: dataTime
        },
        yAxis: [{
                min: 0,
                tickAmount: 6,
                gridLineWidth: 1,
                title: {
                    enabled: false
                },
                labels: {
                    style: {
                        fontWeight: "bold"
                    }
                }
            }, {
                min: 0,
                tickAmount: 6,
                gridLineWidth: 0,
                title: {
                    enabled: false
                },
                labels: {
                    style: {
                        fontWeight: "bold"
                    }
                },
                opposite: true
            }],
        legend: {
            align: "center",
            verticalAlign: "top",
            y: -15,
            floating: true
        },
        series: series
    });
}
//Bieu do Topup
function drawTopup(chartId) {
    var key;
    var dateTime = [];
    var data = [];
    var response = getParseJSON("value-" + chartId);
    if (response) {
        for (key in response) {
            if (key === "time") {
                dateTime = response[key];
                continue;
            }
            data.push({
                data: response[key],
                name: key
            });
        }
    }
    $("#" + chartId).highcharts({
        exporting: {
            enabled: false
        },
        credits: {
            enabled: false
        },
        chart: {
            type: "column",
            height: 240
        },
        title: {
            text: ""
        },
        tooltip: {
            shared: true,
            xDateFormat: "Date : %d-%m-%Y"
        },
        xAxis: {
            title: {
                enabled: false
            },
            labels: {
                formatter: function () {
                    return Highcharts.dateFormat("%d-%m", this.value);
                }
            },
            type: "datetime",
            categories: dateTime
        },
        yAxis: {
            tickAmount: 6,
            title: {
                enabled: false
            },
            labels: {
                style: {
                    fontWeight: "bold"
                }
            }
        },
        legend: {
            align: "center",
            verticalAlign: "top",
            y: -15,
            floating: true
        },
        plotOptions: {
            series: {
                stacking: "normal",
                dataLabels: {
                    enabled: false
                }
            }
        },
        series: data
    });
}

function drawProdSpec(formId, chartId) {
    var name;
    var legend;
    var configChart = configComboChart();
    var response = getParseJSON(formId + "\\:value-" + chartId);
    $("#" + chartId).empty();
    if (response) {
        configChart.xAxis.categories = response.prodspec;
        for (var i in response.chart) {
            configChart.series[i].data = response.chart[i].data;
        }
    }
    $("#" + chartId).highcharts(configChart);
    var chart = $("#" + chartId).highcharts();
    for (var i in chart.legend.allItems) {
        legend = chart.legend.allItems[i];
        name = legend.name;
        legend.update({name: name + " " + response.unit[i]});
    }
}

function drawPercentService(chartId) {
    var highchart = configPieChart();
    var response = getParseJSON("value-" + chartId);
    $("#" + chartId).empty();
    if (response) {
        highchart.series[0].data = response;
    }
    $("#" + chartId).highcharts(highchart);
}

function drawService(chartId, nameChart) {
    var legend;
    var name;
    var chart;
    var configChart = configTwoChart();
    $("#" + chartId).empty();
    var response = getParseJSON("value-" + chartId);
    configChart.title.text = nameChart;
    if (response) {
        if (response["time"].length <= 6) {
            configChart.series[0].type = "column";
            configChart.series[1].type = "column";
        }
        configChart.xAxis.categories = response.time;
        configChart.series[1].data = response.revenue;
        configChart.series[0].data = response.volume;
    }
    $("#" + chartId).highcharts(configChart);
    chart = $("#" + chartId).highcharts();
    for (var i in chart.legend.allItems) {
        legend = chart.legend.allItems[i];
        name = legend.name;
        legend.legendItem.attr({text: name + " " + response.unit[i]});
    }
}

function drawTotalRevenue(chartId) {
    var response = getParseJSON("value-" + chartId);
    if (response) {
        var highChart = {
            chart: {
                height: 360,
                type: "area"
            },
            credits: {
                enabled: false
            },
            exporting: {
                enabled: false
            },
            rangeSelector: {
                selected: 1,
                inputEnabled: false,
                inputDateFormat: '%Y-%m-%d',
                inputEditDateFormat: '%Y-%m-%d',
                buttons: [{
                        type: 'month',
                        count: 1,
                        text: '1 m'
                    }, {
                        type: 'month',
                        count: 3,
                        text: '3 m'
                    }, {
                        type: 'month',
                        count: 6,
                        text: '6 m'
                    }, {
                        type: 'year',
                        count: 1,
                        text: '1 y'
                    }, {
                        type: 'all',
                        text: 'All'
                    }]
            },
            title: {
                floating: true,
                align: 'center',
                text: 'Total Revenue Of Network'
            },
            fillColor: {
                linearGradient: {
                    x1: 0,
                    y1: 0,
                    x2: 0,
                    y2: 1
                },
                stops: [
                    [0, Highcharts.getOptions().colors[0]],
                    [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                ]
            },
            legend: {
                verticalAlign: "top",
                floating: true,
                enabled: true,
                align: "left",
                x: 350,
                y: -3
            },
            yAxis: [{
                    min: 0
                }],
            series: [{
                    name: 'Revenue',
                    type: 'areaspline',
                    threshold: null,
                    data: response,
                    tooltip: {
                        valueDecimals: 2
                    }
                }]
        };
        $("#" + chartId).highcharts('StockChart', highChart);
    }
}

function drawUserNumber(chartId) {
    var chart = configOneChart();
    var response = getParseJSON("value-" + chartId);
    if (response) {
        if (response["time"].length <= 6) {
            chart.series[0].type = "column";
        }
        chart.xAxis[0].categories = response.time;
        chart.series[0].data = response.number;
    }
    $("#" + chartId).highcharts(chart);
}
//Update chart
function redrawChart(chartId, response) {
    var formatDate;
    var legend;
    var name;
    var optionTime = parseInt(response.option);
    var serviceType = parseInt(response.service);
    var chart = $("#" + chartId).highcharts();
    if (optionTime === 2) {
        formatDate = "%d-%m";
    } else if (optionTime === 4) {
        formatDate = "%m-%Y";
    } else {
        formatDate = "%Y";
    }
    chart.xAxis[0].setCategories(response.data.time, false, true);
    chart.xAxis[0].update({
        labels: {
            formatter: function () {
                return Highcharts.dateFormat(formatDate, this.value);
            }
        }
    });
    if (serviceType !== 5) {
        chart.series[0].setData(response.data.volume, false, true);
        chart.series[1].setData(response.data.revenue, false, true);
    } else {
        chart.series[0].setData(response.data.number, false, true);
    }
    if (response.data.time.length <= 6) {
        chart.series[0].update({
            type: "column"
        });
        if (serviceType !== 5) {
            chart.series[1].update({
                type: "column"
            });
        }
    } else {
        chart.series[0].update({
            type: "line"
        });
        if (serviceType !== 5) {
            chart.series[1].update({
                type: "spline"
            });
        }
    }
    if (!response.data.unit) {
        return;
    }
    for (var i in chart.legend.allItems) {
        legend = chart.legend.allItems[i];
        name = legend.name;
        legend.legendItem.attr({text: name + " " + response.data.unit[i]});
    }
}

function addNewPoint(chartId, response) {
    var chart = $("#" + chartId).highcharts();
    var point = response.point;
    chart.series[0].addPoint([point[0], point[1]], true, true);
}

function updateChart(chartId, response) {
    var chart = $("#" + chartId).highcharts();
    var arrayData = $.parseJSON(response);
    chart.series[0].setData(arrayData, true, true);
}
//Date time
function getDateTimePicker() {
    $('.dateTimePicker').datepicker({
        todayBtn: "linked",
        todayHighlight: true,
        format: "dd-mm-yyyy",
        autoclose: true
    }).on("show", function (event) {
        if (event.date) {
            $(this).data("stickyDate", event.date);
        } else {
            $(this).data("stickyDate", null);
        }
    }).on("hide", function (event) {
        var stickyDate = $(this).data("stickyDate");
        if (!event.date && stickyDate) {
            $(this).datepicker('setDate', stickyDate);
            $(this).data("stickyDate", null);
        }
    });
    $('.monthTimePicker').datepicker({
        format: "mm-yyyy",
        viewMode: "months",
        minViewMode: "months",
        autoclose: true
    }).on("show", function (event) {
        if (event.date) {
            $(this).data("stickyDate", event.date);
        } else {
            $(this).data("stickyDate", null);
        }
    }).on("hide", function (event) {
        var stickyDate = $(this).data("stickyDate");
        if (!event.date && stickyDate) {
            $(this).datepicker('setDate', stickyDate);
            $(this).data("stickyDate", null);
        }
    });
    $('.yearTimePicker').datepicker({
        format: "yyyy",
        viewMode: "years",
        minViewMode: "years",
        autoclose: true
    }).on("show", function (event) {
        if (event.date) {
            $(this).data("stickyDate", event.date);
        } else {
            $(this).data("stickyDate", null);
        }
    }).on("hide", function (event) {
        var stickyDate = $(this).data("stickyDate");
        if (!event.date && stickyDate) {
            $(this).datepicker('setDate', stickyDate);
            $(this).data("stickyDate", null);
        }
    });
}

function isDateFormat(dateString) {
    if (isEmptyString(dateString)) {
        return false;
    }
    var value = dateString.match(/^(\d{1,2})-(\d{1,2})-(\d{4})$/);
    var day = parseInt(value[1]);
    var month = parseInt(value[2]);
    var year = parseInt(value[3]);
    if (day < 1 || day > 31 || month < 1 || month > 12 || day > dayInMonth(month, year)) {
        return false;
    }
    return true;
}

function isMonthFomat(monthString) {
    if (isEmptyString(monthString)) {
        return false;
    }
    var value = monthString.match(/^(\d{1,2})-(\d{4})$/);
    var month = parseInt(value[1]);
    if (month < 1 || month > 12) {
        return false;
    }
    return true;
}

function isYearFomat(yearString) {
    if (isEmptyString(yearString)) {
        return false;
    }
    return /^(\d{4})$/.test(yearString);
}

function dayInMonth(month, year) {
    switch (month) {
        case 2 :
            return ((year % 4 === 0) && (year % 100)) || (year % 400 === 0) ? 29 : 28;
        case 4 :
        case 6 :
        case 9 :
        case 11 :
            return 30;
        default :
            return 31;
    }
}
//Check null
function isEmptyString(string) {
    string = $.trim(string);
    return (!string || (0 === string.length));
}
//json
function getParseJSON(tagId) {
    var value = $("#" + tagId).text();
    value = $.trim(value);
    return isEmptyString(value) === true ? null : $.parseJSON(value);
}
//Convert
function getNumberWithCommas(number) {
    return number.toString().replace(/\d(?=(\d{3})+(?!\d))/g, ",");
}

function getTextBoxDateTimeWithOption(id, optionTime) {
    var dateTime;
    switch (optionTime) {
        case 2 :
            optionTime = 0;
            break;
        case 4 :
            optionTime = 1;
            break;
        case 8 :
            optionTime = 2;
            break;
    }
    $(id).children().each(function (i) {
        if (i === optionTime) {
            $(this).removeClass("hide");
            $(this).addClass("view");
            dateTime = $(this).val().trim();
        } else {
            $(this).removeClass("view");
            $(this).addClass("hide");
        }
    });
    return dateTime;
}
//Change style map
function changeDataMap(element) {
    //Change active button
    var $this = $("#" + element);
    $this.closest("div.group-button").find("a").removeClass("btn-active");
    $this.addClass("btn-active");
    $this.blur();
    //Reload map
    intitalMap();
    //Fill data with option map type
    fillMap(parseInt($("#typeMap").val()));
}