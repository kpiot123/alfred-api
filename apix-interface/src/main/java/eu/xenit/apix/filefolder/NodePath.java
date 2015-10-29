package eu.xenit.apix.filefolder;

/**
 * The Node Path component displays the full path to the specified Node. The displaypath is the human readable
 * representation while the qnamePath is the technical representation.
 */
public class NodePath {

    private String displayPath;
    private String qnamePath;

    public String getDisplayPath() {
        return displayPath;
    }

    public void setDisplayPath(String displayPath) {
        this.displayPath = displayPath;
    }

    public String getQnamePath() {
        return qnamePath;
    }

    public void setQnamePath(String qnamePath) {
        this.qnamePath = qnamePath;
    }
}
