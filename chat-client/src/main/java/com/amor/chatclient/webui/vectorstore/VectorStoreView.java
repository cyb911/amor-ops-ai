package com.amor.chatclient.webui.vectorstore;

import com.amor.chatclient.po.VectorStoreDocumentInfo;
import com.amor.chatclient.service.vectorstore.VectorStoreDocumentService;
import com.amor.chatclient.service.vectorstore.VectorStoreService;
import com.amor.chatclient.webui.VaadinUtils;
import com.amor.chatclient.webui.WwAiAppLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingOptions;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.amor.chatclient.service.vectorstore.VectorStoreDocumentService.*;
import static com.amor.chatclient.webui.VaadinUtils.*;

@CssImport("./vectorstore-styles.css")
@Route(value = "vector", layout = WwAiAppLayout.class)
public class VectorStoreView extends Div {

    public static final String DOC_INFO_ID = "docInfoId";
    private final VectorStoreService vectorStoreService;
    private final VectorStoreDocumentService vectorStoreDocumentService;
    private final VectorStoreDocumentView vectorStoreDocumentView;
    private final VectorStoreContentView vectorStoreContentView;
    private final SplitLayout splitLayout;
    private double splitterPosition;
    private boolean sidebarCollapsed;

    public VectorStoreView(VectorStoreService vectorStoreService,
            VectorStoreDocumentService vectorStoreDocumentService) {
        setSizeFull();

        this.splitLayout = new SplitLayout();
        this.splitLayout.setSizeFull();
        this.splitLayout.setSplitterPosition(this.splitterPosition = 15);
        this.splitLayout.addThemeVariants(SplitLayoutVariant.LUMO_SMALL);
        add(this.splitLayout);

        this.vectorStoreService = vectorStoreService;
        this.vectorStoreDocumentService = vectorStoreDocumentService;

        this.vectorStoreDocumentView = new VectorStoreDocumentView(vectorStoreDocumentService);
        this.splitLayout.addToPrimary(this.vectorStoreDocumentView);
        this.vectorStoreContentView = new VectorStoreContentView(vectorStoreService);
        this.vectorStoreDocumentService.getDocumentInfoChangeSupport().addPropertyChangeListener(changeEvent -> {
            if (Objects.isNull(changeEvent.getNewValue()) || ((Collection<?>) changeEvent.getNewValue()).isEmpty())
                return;
            switch (changeEvent.getPropertyName()) {
                case DOCUMENT_ADDING_EVENT ->
                        handleDocumentAdding((List<VectorStoreDocumentInfo>) changeEvent.getNewValue());
                case DOCUMENT_SELECTING_EVENT ->
                        handleDocumentSelecting((Collection<VectorStoreDocumentInfo>) changeEvent.getNewValue());
                case DOCUMENTS_DELETE_EVENT ->
                        handleDocumentDeleting((Collection<VectorStoreDocumentInfo>) changeEvent.getNewValue());
            }
        });
        vectorStoreContentView.setSpacing(false);
        vectorStoreContentView.setMargin(false);
        vectorStoreContentView.setPadding(false);

        VerticalLayout vectorStoreContentLayout = new VerticalLayout();
        vectorStoreContentLayout.setSpacing(false);
        vectorStoreContentLayout.setMargin(false);
        vectorStoreContentLayout.setPadding(false);
        vectorStoreContentLayout.setHeightFull();
        vectorStoreContentLayout.getStyle().set("overflow", "hidden").set("display", "flex")
                .set("flex-direction", "column").set("align-items", "stretch");
        vectorStoreContentLayout.add(createDocumentContentHeader(), vectorStoreContentView);

        this.splitLayout.addToSecondary(vectorStoreContentLayout);
        this.sidebarCollapsed = false;
    }

    private void handleDocumentAdding(List<VectorStoreDocumentInfo> newEventDocumentInfos) {
        this.vectorStoreService.add(
                newEventDocumentInfos.stream()
                        .map(docInfo -> vectorStoreDocumentService.getDocumentSupplier(docInfo.getDocInfoId()))
                        .map(Supplier::get)
                        .flatMap(List::stream).toList());
        handleDocumentSelecting(newEventDocumentInfos);
    }

    private void handleDocumentSelecting(Collection<VectorStoreDocumentInfo> newEventDocumentInfos) {
        this.vectorStoreContentView.showDocuments(
                newEventDocumentInfos.stream().map(VectorStoreDocumentInfo::getDocInfoId).toList());
    }

    private void handleDocumentDeleting(Collection<VectorStoreDocumentInfo> newEventDocumentInfos) {
        this.vectorStoreService.delete(
                newEventDocumentInfos.stream()
                        .map(docInfo -> vectorStoreDocumentService.getDocumentSupplier(docInfo.getDocInfoId()))
                        .filter(Objects::nonNull)
                        .map(Supplier::get)
                        .flatMap(List::stream)
                        .map(Document::getId)
                        .toList());
        vectorStoreContentView.showAllDocuments();
    }

    private HorizontalLayout createDocumentContentHeader() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(false);
        horizontalLayout.setMargin(false);
        horizontalLayout.getStyle().setPadding("var(--lumo-space-m) 0 0 0");
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Button toggleButton = styledButton("Hide Documents", VaadinIcon.CHEVRON_LEFT.create(), null);
        Component leftArrowIcon = toggleButton.getIcon();
        Icon rightArrowIcon = styledIcon(VaadinIcon.CHEVRON_RIGHT.create());
        rightArrowIcon.setTooltipText("Show Documents");
        toggleButton.addClickListener(event -> {
            sidebarCollapsed = !sidebarCollapsed;
            toggleButton.setIcon(sidebarCollapsed ? rightArrowIcon : leftArrowIcon);
            if (sidebarCollapsed)
                vectorStoreDocumentView.removeFromParent();
            else
                this.splitLayout.addToPrimary(vectorStoreDocumentView);
            if (this.splitLayout.getSplitterPosition() > 0)
                this.splitterPosition = this.splitLayout.getSplitterPosition();
            this.splitLayout.setSplitterPosition(sidebarCollapsed ? 0 : splitterPosition);
        });
        horizontalLayout.add(toggleButton);

        Button newDocumentButton = styledButton("New Document", VaadinIcon.FILE_ADD.create(), null);
        horizontalLayout.add(newDocumentButton);

        Popover newDocumentPopover = headerPopover(newDocumentButton, "Upload Document");
        newDocumentPopover.addThemeVariants(PopoverVariant.ARROW, PopoverVariant.LUMO_NO_PADDING);
        newDocumentPopover.setPosition(PopoverPosition.BOTTOM_END);
        newDocumentPopover.setModal(true);

        VectorStoreDocumentUpload vectorStoreDocumentUpload =
                new VectorStoreDocumentUpload(this.vectorStoreDocumentService);
        vectorStoreDocumentUpload.getStyle().set("padding", "0 var(--lumo-space-m) 0 var(--lumo-space-m)");
        newDocumentPopover.add(vectorStoreDocumentUpload);

        HorizontalLayout chunkDocumentHorizontalLayout = new HorizontalLayout();
        vectorStoreDocumentUpload.add(chunkDocumentHorizontalLayout);
        chunkDocumentHorizontalLayout.setWidthFull();
        chunkDocumentHorizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        chunkDocumentHorizontalLayout.getStyle().set("padding", "var(--lumo-space-m) 0 var(--lumo-space-m) 0");

        Button chunkDocumentButton = new Button("Chunk Document");
        chunkDocumentButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        chunkDocumentHorizontalLayout.add(chunkDocumentButton);
        chunkDocumentButton.addClickListener(buttonClickEvent -> {
            newDocumentPopover.close();
            List<String> uploadedFileNames = new ArrayList<>(vectorStoreDocumentUpload.getUploadedFileNames());
            vectorStoreDocumentUpload.clearFileList();
            if (uploadedFileNames.isEmpty()) {
                VaadinUtils.showInfoNotification("No uploaded files found");
                return;
            }
            Map<String, List<Document>> uploadedDocumentItems =
                    this.vectorStoreDocumentService.extractDocumentItems(uploadedFileNames);
            Set<Document> chunks =
                    uploadedDocumentItems.values().stream().flatMap(List::stream).collect(Collectors.toSet());
            if (chunks.isEmpty()) {
                VaadinUtils.showInfoNotification("No chunks found");
                return;
            }

            MultiSelectListBox<Document> documentListBox = new MultiSelectListBox<>();
            documentListBox.setRenderer(
                    new ComponentRenderer<Component, Document>(document -> new Span(document.getText())));

            documentListBox.setItems(chunks);
            documentListBox.select(chunks);

            Dialog confirmationDialog = VaadinUtils.headerDialog(
                    String.format("Chunk Summary - %d chunks successfully extracted", chunks.size()));
            confirmationDialog.setModal(false);
            Button confirmButton = new Button("Embed and Insert Confirm");
            confirmationDialog.add(confirmButton, documentListBox);
            confirmationDialog.open();
            confirmButton.addClickListener(event -> {
                confirmationDialog.setEnabled(false);
                confirmationDialog.close();
                Set<Document> selectedItems = documentListBox.getSelectedItems();
                Map<String, List<Document>> filenameDocuments =
                        uploadedDocumentItems.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                                entry -> entry.getValue().stream().filter(selectedItems::contains).toList()));
                this.vectorStoreDocumentView.addDocumentContent(uploadedFileNames, filenameDocuments);
            });
        });

        EmbeddingOptions embeddingOptions = this.vectorStoreService.getEmbeddingOptions();
        H4 embeddingModelServiceText = new H4(Objects.nonNull(embeddingOptions.getDimensions())
                ? String.format("%s - %s - %d", this.vectorStoreService.getEmbeddingModelServiceName(),
                embeddingOptions.getModel(), embeddingOptions.getDimensions())
                : String.format("%s - %s", this.vectorStoreService.getEmbeddingModelServiceName(),
                embeddingOptions.getModel()));


        embeddingModelServiceText.getStyle().set("white-space", "nowrap");
        Div embeddingModelServiceTextDiv = new Div(embeddingModelServiceText);
        embeddingModelServiceTextDiv.getStyle().set("display", "flex").set("justify-content", "center")
                .set("align-items", "center").set("height", "100%");

        HorizontalLayout vectorStoreLabelLayout = new HorizontalLayout(embeddingModelServiceTextDiv);
        vectorStoreLabelLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        vectorStoreLabelLayout.setWidthFull();
        horizontalLayout.add(vectorStoreLabelLayout);

        Icon searchSettingIcon = styledIcon(VaadinIcon.COG_O.create());
        searchSettingIcon.getStyle().set("marginRight", "var(--lumo-space-l)");
        Popover searchSettingPopover = headerPopover(searchSettingIcon, "Search Settings");
        searchSettingPopover.setWidth("250px");
        searchSettingPopover.setHoverDelay(0);
        searchSettingPopover.addThemeVariants(PopoverVariant.ARROW, PopoverVariant.LUMO_NO_PADDING);
        searchSettingPopover.setPosition(PopoverPosition.BOTTOM);
        searchSettingPopover.setModal(true);

        VectorStoreSearchSettingView vectorStoreSearchSettingView =
                new VectorStoreSearchSettingView(this.vectorStoreService);
        vectorStoreSearchSettingView.getStyle()
                .set("padding", "0 var(--lumo-space-m) var(--lumo-space-m) var(--lumo-space-m)");
        searchSettingPopover.add(vectorStoreSearchSettingView);

        MenuBar searchSettingMenuBar = new MenuBar();
        searchSettingMenuBar.addThemeVariants(MenuBarVariant.LUMO_END_ALIGNED);
        searchSettingMenuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        searchSettingMenuBar.addItem(searchSettingIcon);

        horizontalLayout.add(searchSettingMenuBar);
        return horizontalLayout;
    }

}
