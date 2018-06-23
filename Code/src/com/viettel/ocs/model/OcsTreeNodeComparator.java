package com.viettel.ocs.model;

import java.util.Comparator;

import org.primefaces.model.TreeNode;

import com.viettel.ocs.entity.BaseEntity;

public class OcsTreeNodeComparator implements Comparator<TreeNode> {

	@Override
	public int compare(TreeNode o1, TreeNode o2) {
		// TODO Auto-generated method stub
		
		if(o1 == null || o2 == null || o1.getData() == null || o2.getData() == null)
			return 0;
		
		BaseEntity entity1 = (BaseEntity) o1.getData();
		BaseEntity entity2 = (BaseEntity) o2.getData();
		
		return entity1.getTreePosIdx() - entity2.getTreePosIdx();
	}
}