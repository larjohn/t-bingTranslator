package eu.openbudgets.unifiedviews;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by larjohns on 10/02/2016.
 */
public class LabelEntry {

    private String labelURI;

    public String getLabelURI(){
        return labelURI;
    }



    public LabelEntry()  {

            this.setLabelURI("http://example.com/label");

    }

    public void setLabelURI(String labelURI) {
        this.labelURI = labelURI;
    }
}
