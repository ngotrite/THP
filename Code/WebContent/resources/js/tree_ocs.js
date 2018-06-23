/**
 * huannn
 */

function do_search(parentNode, searchValue) {
	//parentNode: li node of tree
	
	var found = false;
	var childElements = parentNode.childNodes;
	if(childElements && childElements.length > 0) {
		try {
			
			// text of parent node
			var nodeSpan = childElements[0].childNodes[2].firstChild;
			if(childElements[0].childNodes.length == 4) {
				nodeSpan = childElements[0].childNodes[3].firstChild;
			}
			var nodeText = nodeSpan.innerHTML;
			if(nodeText.toLowerCase().indexOf(searchValue.toLowerCase()) > -1) {
				
				found = true;
				nodeSpan.classList.add('treenode-search-found');
			} else {
				nodeSpan.classList.remove('treenode-search-found');
			}
			
			// child of node: ul
			var childNode = childElements[1];				
			for(var i=0; i < childNode.childNodes.length; i++) {				
				if(do_search(childNode.childNodes[i], searchValue)) {
					found = true;
				}
			}
			
			if(!found) {
				parentNode.style.display = 'none';
			} else {
				parentNode.style.display = 'block';
			}
			
			return found;
		} catch (e) {
			console.log(e);
		}
	}
}

function show_all_node(parentNode) {
   
	var nodes = $('li.ui-treenode');
	for(i = 0; i < nodes.length; i++) {
		node = nodes[i];
		node.style.display = 'block';
		
		nodeSpan = node.childNodes[0].childNodes[2].firstChild;
		if(node.childNodes[0].childNodes.length == 4) {
			nodeSpan = node.childNodes[0].childNodes[3].firstChild;
		}
		nodeSpan.classList.remove('treenode-search-found');
	}
}

$('.txtTreeSearchCustom').keyup(function () {
    var searchValue = $.trim(this.value);
    if (searchValue === "") {
    	show_all_node();
    } else {
    	
    	var firstParentNodes = $('ul.ui-tree-container > li.ui-treenode');
    	for(i = 0; i < firstParentNodes.length; i++) {
    		node = firstParentNodes[i];
    		do_search(node, searchValue);	
    	}
    }
});
$('.txtTreeSearchCustom').submit(function () {
    return false;
});
function searchTreeOnDialog(){
	var searchValue = $.trim($('.txtTreeSearchCustom').val());
    if (searchValue === "") {
    	show_all_node();
    } else {

    	var firstParentNodes = $('.customTree ul.ui-tree-container > li.ui-treenode');
    	for(i = 0; i < firstParentNodes.length; i++) {
    		node = firstParentNodes[i];
    		do_search(node, searchValue);	
    	}
    }
}