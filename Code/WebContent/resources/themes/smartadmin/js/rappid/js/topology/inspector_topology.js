// var CommonInspectorInputs = {

//     size: {
//         width: { type: 'number', min: 1, max: 500, group: 'geometry', label: 'width', index: 1 },
//         height: { type: 'number', min: 1, max: 500, group: 'geometry', label: 'height', index: 2 }
//     },
//     position: {
//         x: { type: 'number', min: 1, max: 2000, group: 'geometry', label: 'x', index: 3 },
//         y: { type: 'number', min: 1, max: 2000, group: 'geometry', label: 'y', index: 4 }
//     },
//     custom: { type: 'text', group: 'data', index: 1, label: 'Custom data', attrs: { 'label': { 'data-tooltip': 'An example of setting custom data via Inspector.' } } }
// };

var CommonInspectorGroups = {

    text: { label: 'Text', index: 1 },
    presentation: { label: 'Presentation', index: 2 },
    geometry: { label: 'Geometry', index: 3 },
    data: { label: 'Data', index: 4 }
};

var CommonInspectorTextInputs = {
    'text': { type: 'textarea', group: 'text', index: 1 },
    'font-size': { type: 'range', min: 5, max: 80, unit: 'px', group: 'text', index: 2 },
    'font-family': { type: 'select', options: ['Arial', 'Helvetica', 'Times New Roman', 'Courier New', 'Georgia', 'Garamond', 'Tahoma', 'Lucida Console', 'Comic Sans MS'], group: 'text', index: 3 },
    'font-weight': { type: 'range', min: 100, max: 900, step: 100, defaultValue: 400, group: 'text', index: 4 },
    'fill': { type: 'color', group: 'text', index: 5 },
    'stroke': { type: 'color', group: 'text', index: 6, defaultValue: '#000000' },
    'stroke-width': { type: 'range', min: 0, max: 5, step: .5, defaultValue: 0, unit: 'px', group: 'text', index: 7 },
    'ref-x': { type: 'range', min: 0, max: .9, step: .1, defaultValue: .5, group: 'text', index: 8 },
    'ref-y': { type: 'range', min: 0, max: .9, step: .1, defaultValue: .5, group: 'text', index: 9 }
};

var InputDefs = {
    text: { type: 'textarea', label: 'Text' },
    'font-size': { type: 'range', min: 5, max: 80, unit: 'px', label: 'Font size' },
    'font-family': { type: 'select', options: ['Arial', 'Helvetica', 'Times New Roman', 'Courier New', 'Georgia', 'Garamond', 'Tahoma', 'Lucida Console', 'Comic Sans MS'], label: 'Font family' },
    'font-weight': { type: 'range', min: 100, max: 900, step: 100, defaultValue: 400, label: 'Font weight' },
    'fill': { type: 'color', label: 'Fill color' },
    'stroke': { type: 'color', defaultValue: '#000000', label: 'Stroke' },
    'stroke-width': { type: 'range', min: 0, max: 5, step: .5, defaultValue: 0, unit: 'px', label: 'Stroke width' },
    'ref-x': { type: 'range', min: 0, max: .9, step: .1, defaultValue: .5, label: 'Horizontal alignment' },
    'ref-y': { type: 'range', min: 0, max: .9, step: .1, defaultValue: .5, label: 'Vertical alignment' },
    'ref-dx': { type: 'range', min: 0, max: 50, step: 1, defaultValue: 0, label: 'Horizontal offset' },
    'ref-dy': { type: 'range', min: 0, max: 50, step: 1, defaultValue: 0, label: 'Vertical offset' },
    'dx': { type: 'range', min: 0, max: 50, step: 1, defaultValue: 0, label: 'Horizontal distance' },
    'dy': { type: 'range', min: 0, max: 50, step: 1, defaultValue: 0, label: 'Vertical distance' },
    'stroke-dasharray': { type: 'select', options: ['0', '1', '5,5', '5,10', '10,5', '3,5', '5,1', '15,10,5,10,15'], label: 'Stroke dasharray' },
    rx: { type: 'range', min: 0, max: 30, defaultValue: 1, unit: 'px', label: 'X-axis radius' },
    ry: { type: 'range', min: 0, max: 30, defaultValue: 1, unit: 'px', label: 'Y-axis radius' },
    'xlink:href': { type: 'text', label: 'Image URL' }
};

function inp(defs) {
    var ret = {};
    _.each(defs, function(def, attr) {

        ret[attr] = _.extend({}, InputDefs[attr], def);
    });
    return ret;
}

var listRoutingType = JSON.parse(listRoutingType);

// Spout init
var inputS = { value: "", content: "Chose type spout"};
var listSpoutAry = [inputS];

if(listSpout) {
    var listSpoutObject = JSON.parse(listSpout);
    for (var i = 0; i < listSpoutObject.length; i++) {
        var inputS = {
            value: listSpoutObject[i].spoutId,
            content: formatBoltSpoutName(listSpoutObject[i].spoutName)
        }
        listSpoutAry.push(inputS);
    };    
}

//end

// Bolt Init
var inputB = { value: "", content: "Chose type bolt"};
var listBoltAry = [inputB];

if(listBolt) {
    var listBoltObject = JSON.parse(listBolt);
    for (var i = 0; i < listBoltObject.length; i++) {
        var inputB = {
            value: listBoltObject[i].BoltId,
            content: formatBoltSpoutName(listBoltObject[i].boltName)
        }
        listBoltAry.push(inputB);
    };
}

// end

// Parse Json CDR Service
var inputCDR = {value: "", content: "Chose CDR service"};
var aryCDR = [inputCDR];

if(listCdrService) {
    var listCdrObject = JSON.parse(listCdrService);
    for (var i = 0; i < listCdrObject.length; i++) {
        var inputCDR = {
            value: listCdrObject[i].cdrServiceId,
            content: formatBoltSpoutName(listCdrObject[i].cdrServiceCode)
        }
        aryCDR.push(inputCDR);
    };
}

function formatBoltSpoutName(name) {
    var ary = name.split('.');
    return ary[ary.length-1];
}

var InspectorDefs = {

    'link': {
        inputs: {
            labels: {
                type: 'list',
                group: 'properties link',                
                item: {
                    type: 'object',
                    properties: {                        
                        attrs: {
                            text: {
                                text: { type: 'text', label: 'label', 'font-size': 10,defaultValue: '', index: 2, attrs: { label: { 'data-tooltip': 'Set text of the label' } } }
                            }                            

                        }
                    }
                }
            },
            routingType: {
                type: 'select',                    
                options: listRoutingType,
                label: 'Routing Type',
                group: 'properties link',
                index: 1
            },
            Field: {
                type: 'text', group: 'properties link', label: 'Filed', index: 2
            },
            is_Use_Stream: {
                type: 'select',                    
                options: [
                    {value: "", content: "Chose options"},
                    {value: "0", content: "False"},
                    {value: "1", content: "True"}
                ],
                label: 'Chose Is Use Stream',
                group: 'properties link',
                index: 3
            }         

        },
        groups: {
            labels: { label: 'Labels', index: 1 },
            'connection': { label: 'Connection', index: 2 },
            'marker-source': { label: 'Source marker', index: 3 },
            'marker-target': { label: 'Target marker', index: 4 }
        }
    },

    // Basic
    // -----
    //@author bachtx3
    // Simple Bolt
    'basic.Rect': {
         inputs: _.extend({
            boltName: {
                type: 'text',
                group: 'properties bolt', index: 1, label: 'Bolt name' 
            },
            attrs: {
                '.remark': inp({
                    text: { group: 'properties bolt', type: 'text', index: 2, label: 'Description'}                    
                }) 
            },           
            effectDate: {
                type: 'text', group: 'properties bolt', label: 'effectDate', index: 3
            },
            Hint: {
                type: 'text', group: 'properties bolt', label: 'Hint', index: 4
            },
            No_Task: {
                type: 'text', group: 'properties bolt', label: 'No_Task', index: 5
            }      
        }),
        groups: CommonInspectorGroups
    },
    // Simple Spout 
    'basic.Circle': {
         inputs: _.extend({
            attrs: {
                '.name': inp({
                    text: { 
                        type: 'text', group: 'properties spout', index: 1, label: 'Name' 
                    }                    
                }),
                '.remark': inp({
                    text: { type: 'text', group: 'properties spout', index: 2, label: 'Description'}                    
                })        
            },
            effectDate: {
                type: 'text', group: 'properties spout', label: 'effectDate', index: 3
            },
            Hint: {
                type: 'text', group: 'properties spout', label: 'Hint', index: 4
            } 

        }),
        groups: CommonInspectorGroups
    },
    
    'basic.Rect_Init': {
         inputs: _.extend({
            attrs: {
                '.remark': inp({
                    text: { type: 'text', group: 'properties init', index: 1, label: 'Name'}                    
                })        
            },
            typeCdr: {
                type: "select",
                options: aryCDR,
                label: 'CDR Service',
                group: 'properties init',
                index: 2

            },
            typeBolt: {
                type: "select",
                options: listBoltAry,
                label: 'Bolt Type',
                group: 'properties init',
                index: 3
            },
            Hint: {
                type: 'text', group: 'properties init', label: 'Hint', index: 4
            } 

        }),
        groups: CommonInspectorGroups
    },



   'basic.Circle_Init': {

         inputs: _.extend({
            attrs: {
               ".remark": inp({
                    text: { 
                        type: 'text',
                        group: 'properties init', index: 1, label: 'Name' 
                    }                    
                })
            },
            typeSpout: {
                type: "select",
                options: listSpoutAry,
                label: 'Spout Type',
                group: 'properties init',
                index: 2

            },
            Hint: {
                type: 'text', group: 'properties init', label: 'Hint', index: 3
            },
            No_Task: {
                type: 'text', group: 'properties init', label: 'No_Task', index: 4
            }            
        }),
        groups: CommonInspectorGroups
    },

    'bpmn.Annotation': {
         inputs: _.extend({
           content: {
                type: 'textarea',
                label: 'Content',
                group: 'general',
                index: 1
            },
        }),
        groups: CommonInspectorGroups
    },

    'bpmn.Message': {
         inputs: _.extend({
            attrs: {
                '.label': inp({
                    text: { type:'text', group: 'properties message', index: 1, label: 'Message name' }
                }),
            }
        }),
        groups: CommonInspectorGroups
    },

    'bpmn.Group': {
         inputs: _.extend({
            attrs: {
                '.label': inp({
                    text: { type:'text', group: 'properties group', index: 1, label:'Group name' }
                }),
            }
        }),
        groups: CommonInspectorGroups
    },


};
