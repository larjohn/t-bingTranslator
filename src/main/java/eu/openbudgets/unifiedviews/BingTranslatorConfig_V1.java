package eu.openbudgets.unifiedviews;

import eu.unifiedviews.helpers.dpu.ontology.EntityDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class for BingTranslator.
 *
 * @author Unknown
 */
@EntityDescription.Entity(type = BingTranslatorVocabulary.STR_CONFIG_CLASS)
public class BingTranslatorConfig_V1 {

    private boolean strictLabelLanguageMatching = false;
    @EntityDescription.Property(uri = BingTranslatorVocabulary.STR_CONFIG_INPUT_LANGUAGE)
    private String inputLanguage;
    @EntityDescription.Property(uri = BingTranslatorVocabulary.STR_CONFIG_OUTPUT_LANGUAGE)
    private String outputLanguage = "en";
    @EntityDescription.Property(uri = BingTranslatorVocabulary.STR_CONFIG_OUTPUT_GRAPH_NAME)
    private String outputGraphName;
    @EntityDescription.Property(uri = BingTranslatorVocabulary.STR_CONFIG_LABEL_PREDICATES)
    private List<LabelEntry> labelPredicates = new ArrayList<>();
    @EntityDescription.Property(uri = BingTranslatorVocabulary.STR_CONFIG_BING_CLIENT_ID)
    private String bingClientId;
    @EntityDescription.Property(uri = BingTranslatorVocabulary.STR_CONFIG_BING_CLIENT_SECRET)
    private String bingClientSecret;

    public BingTranslatorConfig_V1() {

    }

    public String getBingClientSecret() {
        return bingClientSecret;
    }

    public void setBingClientSecret(String bingClientSecret) {
        this.bingClientSecret = bingClientSecret;
    }

    public String getBingClientId() {
        return bingClientId;
    }

    public void setBingClientId(String bingClientId) {
        this.bingClientId = bingClientId;
    }

    public boolean isStrictLabelLanguageMatching() {
        return strictLabelLanguageMatching;
    }

    public void setStrictLabelLanguageMatching(boolean strictLabelLanguageMatching) {
        this.strictLabelLanguageMatching = strictLabelLanguageMatching;
    }

    public String getInputLanguage() {
        return inputLanguage;
    }

    public void setInputLanguage(String inputLanguage) {
        this.inputLanguage = inputLanguage;
    }

    public String getOutputLanguage() {
        return outputLanguage;
    }

    public void setOutputLanguage(String outputLanguage) {
        this.outputLanguage = outputLanguage;
    }

    public String getOutputGraphName() {
        return outputGraphName;
    }

    public void setOutputGraphName(String outputGraphName) {
        this.outputGraphName = outputGraphName;
    }

    public List<LabelEntry> getLabelPredicates() {
        return labelPredicates;
    }

    public void setLabelPredicates(List<LabelEntry> labelPredicates) {
        this.labelPredicates = labelPredicates;
    }


}
