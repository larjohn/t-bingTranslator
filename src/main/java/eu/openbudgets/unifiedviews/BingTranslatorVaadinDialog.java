package eu.openbudgets.unifiedviews;

import com.vaadin.data.Container;
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

    private ObjectProperty<Boolean> ignoreTlsErrors = new ObjectProperty<>(Boolean.FALSE);


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

        final Button addVfsFile = new Button("+");
        addVfsFile.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                container.addItem(new LabelEntry());
            }

        });

        final Table table = new Table();
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
        table.setColumnHeader("labelURI", ctx.tr("labelURI"));

        table.setEditable(true);
        table.setSizeFull();
        table.setTableFieldFactory(new TableFieldFactory() {

            @Override
            public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
                AbstractTextField result = new TextField();

                if (propertyId.equals("labelURI")) {
                    result.setDescription(ctx.tr("FilesDownloadVaadinDialog.uri.description"));
                }

                result.setWidth("100%");

                return result;
            }

        });
        table.setVisibleColumns("remove", "labelURI");
        mainLayout.addComponent(addVfsFile);
        addVfsFile.setClickShortcut(ShortcutAction.KeyCode.INSERT);
        addVfsFile.setDescription(ctx.tr("FilesDownloadVaadinDialog.addButton.description"));
        mainLayout.addComponent(table);
        mainLayout.setExpandRatio(addVfsFile, 0.0f);



        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setWidth("100%");

        CheckBox chkIgnoreTlsErrors = new CheckBox(ctx.tr("FilesDownloadVaadinDialog.ignoreTlsErrors.caption"), ignoreTlsErrors);
        chkIgnoreTlsErrors.setDescription(ctx.tr("FilesDownloadVaadinDialog.ignoreTlsErrors.description"));
        bottomLayout.addComponent(chkIgnoreTlsErrors);
        mainLayout.addComponent(bottomLayout);

        setCompositionRoot(mainLayout);
    }




    @Override
    protected BingTranslatorConfig_V1 getConfiguration() throws DPUConfigException {
        List<LabelEntry> labelEntries = new ArrayList<>();

        if (isContainerValid(true)) {
            try {
                for (Object itemId : container.getItemIds()) {
                    LabelEntry labelEntry = new LabelEntry((LabelEntry) itemId);
                    URI uri = labelEntry.getLabelURI().normalize();

                    labelEntry.setLabelURI(uri.normalize());

                    labelEntries.add(labelEntry);
                }
            } catch (Exception e) {
                throw new DPUConfigException(ctx.tr("FilesDownloadVaadinDialog.getConfiguration.exception"), e);
            }
        }

        BingTranslatorConfig_V1 result = new BingTranslatorConfig_V1();
        result.setLabelPredicates(labelEntries);


        return result;
    }

    private boolean isContainerValid(boolean throwException) throws DPUConfigException {
        boolean result = true;
        DPUConfigException resultException = null;

        try {
            for (Object itemId : container.getItemIds()) {
                LabelEntry labelEntry = (LabelEntry) itemId;

                if (StringUtils.isBlank(labelEntry.getLabelURI().toString())) {
                    result = false;
                    resultException = new DPUConfigException(ctx.tr("FilesDownloadVaadinDialog.uri.required"));
                    break;
                }





            }
        } catch (Exception e) {
            result = false;
            resultException = new DPUConfigException(ctx.tr("FilesDownloadVaadinDialog.uri.invalid"), e);
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
                    LabelEntry labelEntryInContainer = new LabelEntry(labelEntry);
                    labelEntryInContainer.setLabelURI(labelEntry.getLabelURI().normalize());

                    container.addItem(labelEntryInContainer);
                }
            } catch (Exception e) {
                throw new DPUConfigException(ctx.tr("FilesDownloadVaadinDialog.setConfiguration.exception"), e);
            }
        }
    }

    @Override
    public String getDescription() {
        return ctx.tr("FilesDownloadVaadinDialog.getDescription", new Object[] { container.getItemIds().size() });
    }

}
