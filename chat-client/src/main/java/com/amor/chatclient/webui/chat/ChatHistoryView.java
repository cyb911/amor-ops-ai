package com.amor.chatclient.webui.chat;

import com.amor.chatclient.service.chat.ChatHistory;
import com.amor.chatclient.service.chat.ChatHistoryService;
import com.amor.chatclient.webui.VaadinUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.amor.chatclient.service.chat.ChatHistoryService.*;

public class ChatHistoryView extends VerticalLayout {

    private final ChatHistoryService chatHistoryService;
    private final ListBox<ChatHistory> chatHistoryListBox;

    public ChatHistoryView(ChatHistoryService chatHistoryService) {
        this.chatHistoryService = chatHistoryService;

        setHeightFull();
        setSpacing(false);
        setMargin(false);
        getStyle().set("overflow", "hidden");

        this.chatHistoryListBox = new ListBox<>();
        this.chatHistoryListBox.addClassName("custom-list-box");
        this.chatHistoryListBox.setItems(List.of());
        this.chatHistoryListBox.setRenderer(new ComponentRenderer<>(chatHistory -> {
            Span title = new Span(chatHistory.getTitle());
            title.getStyle().set("white-space", "nowrap")
                    .set("overflow", "hidden")
                    .set("text-overflow", "ellipsis")
                    .set("flex-grow", "1");
            title.getElement().setAttribute("title",
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(chatHistory.getCreateTimestamp()), ZoneId.systemDefault())
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            return title;
        }));

        this.chatHistoryListBox.addValueChangeListener(event -> Optional.ofNullable(event.getValue())
                .filter(chatHistory -> Objects.nonNull(event.getOldValue()) && !chatHistory.equals(event.getOldValue()))
                .ifPresent(chatHistory -> this.chatHistoryService.getChatHistoryChangeSupport()
                        .firePropertyChange(CHAT_HISTORY_SELECT_EVENT, event.getOldValue(), chatHistory)));

        Scroller scroller = new Scroller(this.chatHistoryListBox);
        scroller.setSizeFull();
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        add(initChatHistoryHeader(), scroller);

        addAttachListener(event -> {
            // 注册事件监听器
            this.chatHistoryService.getChatHistoryChangeSupport()
                    .addPropertyChangeListener(CHAT_HISTORY_CHANGE_EVENT,
                            e -> updateChatHistoryContent((ChatHistory) e.getNewValue()));
            // 页面 attach 后，第一次加载历史数据
            updateChatHistoryContent(null);
        });
    }

    private Header initChatHistoryHeader() {
        Span appName = new Span("History");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);

        MenuBar menuBar = new MenuBar();
        menuBar.setWidthFull();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_END_ALIGNED, MenuBarVariant.LUMO_TERTIARY_INLINE);

        Icon closeIcon = VaadinUtils.styledIcon(VaadinIcon.CLOSE.create());
        closeIcon.setTooltipText("Delete");
        menuBar.addItem(closeIcon, event -> deleteHistory());

        Icon editIcon = VaadinUtils.styledIcon(VaadinIcon.PENCIL.create());
        editIcon.setTooltipText("Rename");
        menuBar.addItem(editIcon, event -> renameHistory());

        Header header = new Header(appName, menuBar);
        header.getStyle()
                .set("white-space", "nowrap")
                .set("height", "auto")
                .set("width", "100%")
                .set("display", "flex")
                .set("box-sizing", "border-box")
                .set("align-items", "center");
        return header;
    }

    private void renameHistory() {
        getCurrentChatHistoryAsOpt().ifPresent(chatHistory -> {
            Dialog dialog = VaadinUtils.headerDialog("Rename: " + chatHistory.getTitle());
            dialog.setModal(true);
            dialog.setResizable(true);
            dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);

            VerticalLayout layout = new VerticalLayout();
            layout.setAlignItems(Alignment.STRETCH);
            layout.getStyle().set("width", "300px").set("max-width", "100%");
            dialog.add(layout);

            TextField titleField = new TextField();
            titleField.setWidthFull();
            titleField.setValue(chatHistory.getTitle());
            titleField.addFocusListener(event -> titleField.getElement().executeJs("this.inputElement.select();"));
            layout.add(titleField);

            Button saveButton = new Button("Save", e -> {
                this.chatHistoryService.updateChatHistory(chatHistory.setTitle(titleField.getValue()));
                dialog.close();
            });
            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            saveButton.getStyle().set("margin-right", "auto");
            dialog.getFooter().add(saveButton);

            dialog.open();
            titleField.focus();
        });
    }

    private void deleteHistory() {
        getCurrentChatHistoryAsOpt().ifPresent(chatHistory -> {
            Dialog dialog = VaadinUtils.headerDialog("Delete: " + chatHistory.getTitle());
            dialog.setModal(true);
            dialog.add("Are you sure you want to delete this history permanently?");

            Button deleteButton = new Button("Delete", e -> {
                this.chatHistoryService.deleteChatHistory(chatHistory.getChatId());
                clearSelectHistory();
                updateChatHistoryContent(null);
                dialog.close();
            });
            deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            deleteButton.getStyle().set("margin-right", "auto");
            dialog.getFooter().add(deleteButton);

            dialog.open();
        });
    }

    private void updateChatHistoryContent(ChatHistory selectedChatHistory) {
        Optional.ofNullable(VaadinUtils.getUi(this))
                .filter(ui -> ui.isAttached())
                .ifPresent(ui -> ui.access(() -> {
                    this.chatHistoryListBox.removeAll();
                    List<ChatHistory> historyList = this.chatHistoryService.getChatHistoryList();
                    if (historyList.isEmpty()) {
                        this.chatHistoryService.getChatHistoryChangeSupport()
                                .firePropertyChange(EMPTY_CHAT_HISTORY_EVENT, false, true);
                        return;
                    }
                    this.chatHistoryListBox.setItems(historyList);
                    if (Objects.nonNull(selectedChatHistory) && historyList.contains(selectedChatHistory)) {
                        this.chatHistoryListBox.setValue(selectedChatHistory);
                    } else {
                        this.chatHistoryListBox.setValue(historyList.getFirst());
                    }
                }));
    }

    public void clearSelectHistory() {
        this.chatHistoryListBox.setValue(null);
    }

    public Optional<ChatHistory> getCurrentChatHistoryAsOpt() {
        return Optional.ofNullable(this.chatHistoryListBox.getValue());
    }
}
