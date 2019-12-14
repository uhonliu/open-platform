package com.opencloud.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 */
public class TreeNode implements Serializable {
    private static final long serialVersionUID = 8772115911922451037L;
    protected int id;
    protected int parentId;
    protected List<TreeNode> children = new ArrayList<TreeNode>();

    public void add(TreeNode node) {
        children.add(node);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }
}
