package eu.xenit.apix.node;

import java.util.List;

/**
 * Datastructure that represents a list of NodeAssociations.
 */
public class NodeAssociations {

    private List<ChildParentAssociation> children;
    private List<ChildParentAssociation> parents;
    private List<NodeAssociation> targets;

    public NodeAssociations() {
    }

    public NodeAssociations(List<ChildParentAssociation> children, List<ChildParentAssociation> parents,
            List<NodeAssociation> targets) {
        this.children = children;
        this.parents = parents;
        this.targets = targets;
    }

    public List<ChildParentAssociation> getChildren() {
        return children;
    }

    public void setChildren(List<ChildParentAssociation> children) {
        this.children = children;
    }

    public List<ChildParentAssociation> getParents() {
        return parents;
    }

    public void setParents(List<ChildParentAssociation> parents) {
        this.parents = parents;
    }

    public List<NodeAssociation> getTargets() {
        return targets;
    }

    public void setTargets(List<NodeAssociation> targets) {
        this.targets = targets;
    }
}
