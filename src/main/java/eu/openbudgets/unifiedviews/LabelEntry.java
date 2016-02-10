package eu.openbudgets.unifiedviews;

import java.net.URI;

/**
 * Created by larjohns on 10/02/2016.
 */
public class LabelEntry {

    private URI labelURI;

    public URI getLabelURI(){
        return labelURI;
    }

    public LabelEntry(LabelEntry itemId) {

    }

    public LabelEntry()
    {

    }

    public void setLabelURI(URI labelURI) {
        this.labelURI = labelURI;
    }
}
