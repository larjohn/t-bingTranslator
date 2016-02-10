package eu.openbudgets.unifiedviews;

import com.memetix.mst.language.Language;
import com.vaadin.data.Container;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Vaadin configuration dialog for BingTranslator.
 *
 * @author Unknown
 */
@SuppressWarnings("serial")
public class BingTranslatorVaadinDialog extends AbstractDialog<BingTranslatorConfig_V1> {

    private final Container container = new BeanItemContainer<>(LabelEntry.class);


    private ObjectProperty<String> sourceLanguage = new ObjectProperty<>("el");
    private ObjectProperty<String> targetLanguage = new ObjectProperty<>("en");
    private ObjectProperty<String> outputGraphName = new ObjectProperty<>("http://example.org/data/");
    private ObjectProperty<String> bingClientId = new ObjectProperty<>("");
    private ObjectProperty<String> bingClientSecret = new ObjectProperty<>("");
    private ObjectProperty<Boolean> strictLabelLanguageMatching = new ObjectProperty<>(Boolean.FALSE);

    public BingTranslatorVaadinDialog() {
        super(BingTranslator.class);
    }

    @Override
    protected void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setHeight("-1px");
        mainLayout.setWidth("100%");
        mainLayout.setImmediate(false);
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        final Button addLabelEntry = new Button("+");
        addLabelEntry.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                container.addItem(new LabelEntry());
            }

        });

        final Table table = new Table("Label predicates");
        table.addGeneratedColumn("remove", new Table.ColumnGenerator() {

            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                Button result = new Button("-");
                final Object itemIdFinal = itemId;

                result.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        container.removeItem(itemIdFinal);
                    }

                });

                return result;
            }

        });
        table.setContainerDataSource(container);
        table.setColumnHeaderMode(Table.ColumnHeaderMode.EXPLICIT);
        table.setColumnHeader("labelURI", ctx.tr("Predicate URI"));
        table.setColumnWidth("remove", 40);

        table.setEditable(true);
        table.setSizeFull();
        table.setTableFieldFactory(new TableFieldFactory() {

            @Override
            public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
                AbstractTextField result = new TextField();

                if (propertyId.equals("labelURI")) {
                    result.setDescription("Label URI");
                }

                result.setWidth("100%");

                return result;
            }

        });
        table.setVisibleColumns("remove", "labelURI");
        mainLayout.addComponent(addLabelEntry);
        addLabelEntry.setClickShortcut(ShortcutAction.KeyCode.INSERT);
        addLabelEntry.setDescription("Add Label Predicate");
        mainLayout.addComponent(table);
        mainLayout.setExpandRatio(addLabelEntry, 0.0f);


        TextField txtSourceLanguage = new TextField("Source Language", sourceLanguage);
        txtSourceLanguage.setNullRepresentation("");
        txtSourceLanguage.setImmediate(true);
        txtSourceLanguage.setLocale(ctx.getDialogMasterContext().getDialogContext().getLocale());
        txtSourceLanguage.addValidator(new Validator() {

            @Override
            public void validate(Object value) throws InvalidValueException {
                if (value != null) {

                    if(Language.fromString((String) value)==null){
                        throw new InvalidValueException("Invalid Language");
                    }
                }
            }
        });

        TextField txtTargetLanguage = new TextField("Target Language", targetLanguage);
        txtTargetLanguage.setNullRepresentation("");
        txtTargetLanguage.setImmediate(true);
        txtTargetLanguage.setLocale(ctx.getDialogMasterContext().getDialogContext().getLocale());
        txtTargetLanguage.addValidator(new Validator() {

            @Override
            public void validate(Object value) throws InvalidValueException {
                if (value != null) {

                    if(Language.fromString((String) value)==null){
                        throw new InvalidValueException("Invalid Language");
                    }
                }
            }
        });

        TextField txtBingClientId = new TextField("Bing Client Id", bingClientId);
        txtBingClientId.setNullRepresentation("");
        txtBingClientId.setImmediate(true);
        txtBingClientId.setLocale(ctx.getDialogMasterContext().getDialogContext().getLocale());


        TextField txtBingClientSecret = new TextField("Bing Client Sercret", bingClientSecret);
        txtBingClientSecret.setNullRepresentation("");
        txtBingClientSecret.setImmediate(true);
        txtBingClientSecret.setLocale(ctx.getDialogMasterContext().getDialogContext().getLocale());


        TextField txtOutputGraphName = new TextField("Output graph name", outputGraphName);
        txtOutputGraphName.setNullRepresentation("");
        txtOutputGraphName.setImmediate(true);
        txtOutputGraphName.setLocale(ctx.getDialogMasterContext().getDialogContext().getLocale());






        VerticalLayout bottomLayout = new VerticalLayout();
        bottomLayout.setWidth("100%");

        CheckBox strictLanguageLabelMatching = new CheckBox(ctx.tr("Strict language label matching"), strictLabelLanguageMatching);
        strictLanguageLabelMatching.setDescription("If you enable this option, only labels with language (@) annotation equal to the source language will be selected and translated. Otherwise, all labels are assumed to be in the source language and are therefore translated.");
        bottomLayout.addComponent(txtSourceLanguage);
        bottomLayout.addComponent(txtTargetLanguage);
        bottomLayout.addComponent(txtOutputGraphName);
        bottomLayout.addComponent(txtBingClientId);
        bottomLayout.addComponent(txtBingClientSecret);
        bottomLayout.addComponent(strictLanguageLabelMatching);
        mainLayout.addComponent(bottomLayout);

        setCompositionRoot(mainLayout);
    }




    @Override
    protected BingTranslatorConfig_V1 getConfiguration() throws DPUConfigException {
        List<LabelEntry> labelEntries = new ArrayList<>();

        if (isContainerValid(true)) {
            try {
                for (Object itemId : container.getItemIds()) {
                    LabelEntry labelEntry = (LabelEntry) itemId;

                    labelEntry.setLabelURI(new URI(labelEntry.getLabelURI()).normalize().toString());

                    labelEntries.add(labelEntry);
                }
            } catch (Exception e) {
                throw new DPUConfigException(ctx.tr("A configuration exception was thrown"), e);
            }
        }

        BingTranslatorConfig_V1 result = new BingTranslatorConfig_V1();
        result.setLabelPredicates(labelEntries);
        result.setBingClientId(bingClientId.getValue());
        result.setBingClientSecret(bingClientSecret.getValue());
        result.setInputLanguage(sourceLanguage.getValue());
        result.setOutputLanguage(targetLanguage.getValue());
        result.setOutputGraphName(outputGraphName.getValue());
        result.setStrictLabelLanguageMatching(strictLabelLanguageMatching.getValue());

        return result;
    }

    private boolean isContainerValid(boolean throwException) throws DPUConfigException {
        boolean result = true;
        DPUConfigException resultException = null;

        try {
            for (Object itemId : container.getItemIds()) {
                LabelEntry labelEntry = (LabelEntry) itemId;

                if (StringUtils.isBlank(labelEntry.getLabelURI())) {
                    result = false;
                    resultException = new DPUConfigException("A non-empty URI is required for each label predicate.");
                    break;
                }

            }
        } catch (Exception e) {
            result = false;
            resultException = new DPUConfigException("Invalid URI", e);
        }

        if (throwException && resultException != null) {
            throw resultException;
        }

        return result;
    }

    @Override
    protected void setConfiguration(BingTranslatorConfig_V1 config) throws DPUConfigException {
        if (isContainerValid(false)) {
            try {
                container.removeAllItems();

                for (LabelEntry labelEntry : config.getLabelPredicates()) {
                    labelEntry.setLabelURI(new URI(labelEntry.getLabelURI()).normalize().toString());

                    container.addItem(labelEntry);
                }
            } catch (Exception e) {
                throw new DPUConfigException(ctx.tr("An exception was thrown"), e);
            }
        }
        sourceLanguage.setValue(config.getInputLanguage());
        targetLanguage.setValue(config.getOutputLanguage());
        outputGraphName.setValue(config.getOutputGraphName());
        bingClientId.setValue(config.getBingClientId());
        bingClientSecret.setValue(config.getBingClientSecret());
        strictLabelLanguageMatching.setValue(config.isStrictLabelLanguageMatching());
    }

    @Override
    public String getDescription() {
        return "Bing Translator (" + sourceLanguage.getValue()+"->"+targetLanguage.getValue() +")";
    }

}
