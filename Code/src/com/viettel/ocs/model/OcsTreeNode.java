package com.viettel.ocs.model;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.viettel.ocs.entity.BaseEntity;

public class OcsTreeNode extends DefaultTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4374224933556187017L;

	public OcsTreeNode() {
		// TODO Auto-generated constructor stub
	}
	
	public OcsTreeNode(BaseEntity data, TreeNode parent) {
		
		super(data, parent);
	}	
}