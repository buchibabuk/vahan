/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.dashBoard;

import java.io.Serializable;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Dhananjay
 */
public class PendingApplDashBoardDobj implements Serializable {

    private String offName;
    private TreeNode treeNodeActionWise;
    private int treeNodeTotalActionWise;
    private TreeNode treeNodeVhClassWise;
    private int treeNodeTotalVhClassWise;
    private TreeNode treeNodePermitWise;
    private int treeNodeTotalPermitWise;

    public String getOffName() {
        return offName;
    }

    public void setOffName(String offName) {
        this.offName = offName;
    }

    public TreeNode getTreeNodeActionWise() {
        return treeNodeActionWise;
    }

    public void setTreeNodeActionWise(TreeNode treeNodeActionWise) {
        this.treeNodeActionWise = treeNodeActionWise;
    }

    public int getTreeNodeTotalActionWise() {
        return treeNodeTotalActionWise;
    }

    public void setTreeNodeTotalActionWise(int treeNodeTotalActionWise) {
        this.treeNodeTotalActionWise = treeNodeTotalActionWise;
    }

    public TreeNode getTreeNodeVhClassWise() {
        return treeNodeVhClassWise;
    }

    public void setTreeNodeVhClassWise(TreeNode treeNodeVhClassWise) {
        this.treeNodeVhClassWise = treeNodeVhClassWise;
    }

    public int getTreeNodeTotalVhClassWise() {
        return treeNodeTotalVhClassWise;
    }

    public void setTreeNodeTotalVhClassWise(int treeNodeTotalVhClassWise) {
        this.treeNodeTotalVhClassWise = treeNodeTotalVhClassWise;
    }

    public TreeNode getTreeNodePermitWise() {
        return treeNodePermitWise;
    }

    public void setTreeNodePermitWise(TreeNode treeNodePermitWise) {
        this.treeNodePermitWise = treeNodePermitWise;
    }

    public int getTreeNodeTotalPermitWise() {
        return treeNodeTotalPermitWise;
    }

    public void setTreeNodeTotalPermitWise(int treeNodeTotalPermitWise) {
        this.treeNodeTotalPermitWise = treeNodeTotalPermitWise;
    }
}
