var Stencil = {};

Stencil.groups = {
    // Spout: { index: 2, label: 'Spout', closed: false},
    Bolt: { index: 3, label: 'Bolt', closed: false},
    general: { index: 1, label: 'General simple', closed: false},
};

//TODO @author bachtx3
function objBolt(boltObj) {    
    var cell = new joint.shapes.basic.Rect({
        size: { width: 80, height: 45 },
        attrs: {
            rect: { 
                fill: '#FFF',
                rx: 5, ry:5 
            },
            '.name': {
                text: formatBoltSpoutName(boltObj.boltName), 
                "font-size": 12
            },
            '.remark': {
                text: "Bolt", 
                "font-size": 12
            }
        },
        BoltId: boltObj.BoltId,
        revisionNo: boltObj.revisionNo,
        boltName: boltObj.boltName,
        boltTypeId: boltObj.BoltId,
        createDate: boltObj.createDate,
        diagFlowId: boltObj.diagFlowId,
        effectDate: boltObj.effectDate,
        maxRevNo: boltObj.vmaxRevNo,
        approved: boltObj.approved,
        boltTypeName: boltObj.boltTypeName,
        diagFlowName: boltObj.diagFlowName,
        remark: "remark",
        Tp_Bolt_ID: "",
        Hint: "",
        cusType: "simpleBolt",
        tooltips: boltObj.boltName        

    });
    return cell;
};

function objSpout(spoutObj) {    
   var cell = new joint.shapes.basic.Rect({
        size: { width: 70, height: 70 },          
        attrs: {
            rect: {
                rx: 5, ry: 2,
                fill: '#ffffff'
            },        
             '.name': {
                text: formatBoltSpoutName(spoutObj.spoutName), 
                "font-size": 12
            },
            '.remark': {
                text: "Spout", 
                "font-size": 12
            } 
        },
        spoutId: spoutObj.spoutId,
        revisionNo: spoutObj.revisionNo,
        approved: spoutObj.approved,
        createDate: spoutObj.createDate,
        diagFlowId: spoutObj.diagFlowId,
        effectDate: spoutObj.effectDate,
        maxRevNo: spoutObj.maxRevNo,
        spoutName: spoutObj.spoutName,
        diagFlowName: spoutObj.spoutName,
        remark: "remark",
        Tp_Spout_ID: "",
        Hint: "",
        TopologyID: "",
        No_Task: "",
        cusType: "simpleSpout",
        tooltips: formatBoltSpoutName(spoutObj.spoutName)
    })       
    return cell;
};

// parse json Bolt object
var aryBolt = [];
if (listBolt) {
    var aryBoltObj = JSON.parse(listBolt); 
    for (var i = 0; i < aryBoltObj.length; i++) {
        aryBolt.push(objBolt(aryBoltObj[i]));
    };    
}


// parse json Spout object
var arySpout = [];
if(listSpout) {
    var arySpoutObj = JSON.parse(listSpout);
    for(var i =0; i< arySpoutObj.length; i++){
        arySpout.push(objSpout(arySpoutObj[i]));
    }    
}

function formatBoltSpoutName(name) {
   var ary = name.split('.'); 
    return ary[ary.length-1];
}

function formatBoltSpoutShortName(name) {
    var ary = name.split('.');
    var boltName = ary[ary.length-1];
    if(boltName.length > 15) {
        boltName = boltName.substr(0, 15) + "...";  
    }  
    return boltName;    
}

// General  Tab
var initSpout = new joint.shapes.basic.Circle({
    size: { width: 48, height: 48 },          
    attrs: {
        circle: {
            rx: 5, ry: 2,
            fill: '#ffffff',
            'stroke-width': 3
        },
        '.remark': {
            text: "Init", 
            "font-size": 12,
            'font-weight': 'bold'
        } 
    },
    Tp_Spout_ID: "",
    Hint: "",
    TopologyID: "",
    No_Task: "",
    cusType: "initSpout",
    tooltips: "initSpout"
    
});

var initBolt = new joint.shapes.basic.Rect({
    size: { width: 78, height: 45 },          
    attrs: {
        rect: {
            rx: 5, ry: 2,
            fill: '#ffffff',
            'stroke-width': 3
        },
        '.remark': {
            text: "Bolt Init", 
            "font-size": 12,
            'font-weight': 'bold'
        } 
    },
    Tp_Bolt_ID: "",
    Hint: "",
    cusType: "initBolt",
    tooltips: "initBolt"
    
});

var objComment = new joint.shapes.bpmn.Annotation({
    size: { width: 90, height: 50 },
    content: "Comment",
    cusType: "comment",
    tooltips: "Comment"
});

var objMess = new joint.shapes.bpmn.Message({
    size: { width: 80 , height: 45 },
    attrs: {
        '.label': {
            text: 'Message', 
            "font-size": 12
        }                
    },
    cusType: "message",
    tooltips: "Message"
});

var group = new joint.shapes.bpmn.Group({
    attrs: {
        '.': { magnet: false },
        '.label': { text: 'Group', "font-size": 12}
    },         
    size: { width: 80, height: 50 },
    cusType: "group",
    tooltips: "Group"
});

// end General

Stencil.shapes = {
    Bolt: aryBolt,
    // Spout: arySpout,
    general: [       
        initSpout, initBolt, objComment, objMess, group
    ],

};
