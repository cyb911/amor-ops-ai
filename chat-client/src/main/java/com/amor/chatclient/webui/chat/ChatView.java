package com.amor.chatclient.webui.chat;

import com.amor.chatclient.service.chat.ChatHistory;
import com.amor.chatclient.service.chat.ChatHistoryService;
import com.amor.chatclient.service.chat.ChatService;
import com.amor.chatclient.webui.VaadinUtils;
import com.amor.chatclient.webui.WwAiAppLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.Optional;

import static com.amor.chatclient.webui.VaadinUtils.*;

@CssImport("./chat-styles.css")
@RouteAlias(value = "", layout = WwAiAppLayout.class)
@Route(value = "chat", layout = WwAiAppLayout.class)
public class ChatView extends Div {

    private final ChatService chatService;
    private final ChatHistoryService chatHistoryService;
    private final ChatHistoryView chatHistoryView;
    private final SplitLayout splitLayout;
    private final VerticalLayout chatContentLayout;
    private double splitterPosition;
    private boolean sidebarCollapsed;
    private ChatContentView chatContentView;

    public ChatView(ChatService chatService, ChatHistoryService chatHistoryService) {
        setSizeFull();

        this.chatService = chatService.registerCompleteResponseConsumer(this::handleCompleteResponse);
        this.chatHistoryService = chatHistoryService;

        this.splitLayout = new SplitLayout();
        this.splitLayout.setSizeFull();
        this.splitLayout.setSplitterPosition(this.splitterPosition = 15);
        this.splitLayout.addThemeVariants(SplitLayoutVariant.LUMO_SMALL);
        add(this.splitLayout);

        this.chatHistoryView = new ChatHistoryView(chatHistoryService);
        this.chatContentLayout = new VerticalLayout();
        this.chatContentLayout.setSpacing(false);
        this.chatContentLayout.setMargin(false);
        this.chatContentLayout.setPadding(false);
        this.chatContentLayout.setHeightFull();
        this.chatContentLayout.getStyle().set("overflow", "hidden").set("display", "flex")
                .set("flex-direction", "column").set("align-items", "stretch");

        this.splitLayout.addToPrimary(chatHistoryView);
        this.splitLayout.addToSecondary(chatContentLayout);

        this.sidebarCollapsed = false;

        PropertyChangeSupport chatHistoryChangeSupport = this.chatHistoryService.getChatHistoryChangeSupport();
        chatHistoryChangeSupport.addPropertyChangeListener(ChatHistoryService.CHAT_HISTORY_SELECT_EVENT,
                event -> this.changeChatContent((ChatHistory) event.getNewValue()));
        chatHistoryChangeSupport.addPropertyChangeListener(ChatHistoryService.EMPTY_CHAT_HISTORY_EVENT,
                event -> {
                    if ((boolean) event.getNewValue()) {
                        this.changeChatContent(null);
                    }
                });

        addAttachListener(event -> addNewChatContent());
    }

    private void handleCompleteResponse(ChatHistory chatHistory) {
        this.chatHistoryService.updateChatHistory(chatHistory);
    }

    private void addNewChatContent() {
        addNewChatContent(this.chatService.getSystemPrompt(), this.chatService.getDefaultOptions());
    }

    private void addNewChatContent(String systemPrompt, ChatOptions chatOptions) {
        this.chatHistoryView.clearSelectHistory();
        changeChatContent(this.chatHistoryService.createChatHistory(systemPrompt, chatOptions));
    }

    private void changeChatContent(ChatHistory chatHistory) {
        if (Objects.isNull(chatHistory)) {
            chatHistory = this.chatHistoryService.createChatHistory(this.chatService.getSystemPrompt(),
                    this.chatService.getDefaultOptions());
        }

        if (Objects.nonNull(this.chatContentView)
                && chatHistory.getChatId().equals(this.chatContentView.getChatId())) {
            this.chatContentView.updateChatHistory(chatHistory);
            return;
        }

        this.chatContentView = new ChatContentView(this.chatService, chatHistory);
        ChatOptions chatOptions = chatHistory.getChatOptions();

        Optional.ofNullable(VaadinUtils.getUi(this))
                .filter(ui -> ui.isAttached())
                .ifPresent(ui -> ui.access(() -> {
                    this.chatContentLayout.removeAll();
                    this.chatContentLayout.add(createChatContentHeader(chatOptions), this.chatContentView);
                }));
    }

    private HorizontalLayout createChatContentHeader(ChatOptions chatOptions) {
        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(false);
        header.setMargin(false);
        header.getStyle().setPadding("var(--lumo-space-m) 0 0 0");
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Button toggleButton = styledButton("Hide History", VaadinIcon.CHEVRON_LEFT.create(), null);
        Component leftArrowIcon = toggleButton.getIcon();
        Icon rightArrowIcon = styledIcon(VaadinIcon.CHEVRON_RIGHT.create());
        rightArrowIcon.setTooltipText("Show History");
        toggleButton.addClickListener(event -> {
            sidebarCollapsed = !sidebarCollapsed;
            toggleButton.setIcon(sidebarCollapsed ? rightArrowIcon : leftArrowIcon);
            if (sidebarCollapsed) {
                chatHistoryView.removeFromParent();
            } else {
                this.splitLayout.addToPrimary(chatHistoryView);
            }
            if (this.splitLayout.getSplitterPosition() > 0) {
                this.splitterPosition = this.splitLayout.getSplitterPosition();
            }
            this.splitLayout.setSplitterPosition(sidebarCollapsed ? 0 : splitterPosition);
        });
        header.add(toggleButton);

        Button newChatButton = styledButton("新聊天", VaadinIcon.CHAT.create(), event -> addNewChatContent());
        header.add(newChatButton);

        H4 chatModelServiceText = new H4(String.format("%s: %s", this.chatService.getChatModelServiceName(), chatOptions.getModel()));
        chatModelServiceText.getStyle().set("white-space", "nowrap");
        Div chatModelServiceTextDiv = new Div(chatModelServiceText);
        chatModelServiceTextDiv.getStyle().set("display", "flex").set("justify-content", "center").set("align-items", "center").set("height", "100%");

        HorizontalLayout modelLabelLayout = new HorizontalLayout(chatModelServiceTextDiv);
        modelLabelLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        modelLabelLayout.setWidthFull();
        header.add(modelLabelLayout);

        Icon chatModelSettingIcon = styledIcon(VaadinIcon.COG_O.create());
        chatModelSettingIcon.getStyle().set("marginRight", "var(--lumo-space-l)");
        chatModelSettingIcon.setTooltipText("Chat Model Setting");

        Popover chatModelSettingPopover = headerPopover(chatModelSettingIcon, "Chat Model Setting");
        chatModelSettingPopover.setWidth("400px");
        chatModelSettingPopover.addThemeVariants(PopoverVariant.ARROW, PopoverVariant.LUMO_NO_PADDING);
        chatModelSettingPopover.setPosition(PopoverPosition.BOTTOM);
        chatModelSettingPopover.setModal(true);

        ChatModelSettingView chatModelSettingView = new ChatModelSettingView(this.chatService.getModels(),
                this.chatContentView.getSystemPrompt(), this.chatContentView.getChatOption());
        chatModelSettingView.getStyle().set("padding", "0 var(--lumo-space-m) 0 var(--lumo-space-m)");

        Button applyNewChatButton = new Button("Apply & New Chat", clickEvent -> {
            addNewChatContent(chatModelSettingView.getSystemPromptTextArea(), chatModelSettingView.getChatOptions());
            chatModelSettingPopover.close();
        });
        applyNewChatButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        HorizontalLayout applyNewChatButtonLayout = new HorizontalLayout(applyNewChatButton);
        applyNewChatButtonLayout.setWidthFull();
        applyNewChatButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        applyNewChatButtonLayout.getStyle().set("padding", "var(--lumo-space-m) 0 var(--lumo-space-m) 0");

        chatModelSettingView.add(applyNewChatButtonLayout);
        chatModelSettingPopover.add(chatModelSettingView);

        MenuBar chatModelSettingMenuBar = new MenuBar();
        chatModelSettingMenuBar.addThemeVariants(MenuBarVariant.LUMO_END_ALIGNED, MenuBarVariant.LUMO_TERTIARY_INLINE);
        chatModelSettingMenuBar.addItem(chatModelSettingIcon);
        header.add(chatModelSettingMenuBar);

        return header;
    }
}