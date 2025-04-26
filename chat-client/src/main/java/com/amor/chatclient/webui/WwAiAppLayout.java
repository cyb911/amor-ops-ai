package com.amor.chatclient.webui;

import com.amor.chatclient.webui.chat.ChatView;
import com.amor.chatclient.webui.vectorstore.VectorStoreView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;

import java.util.Map;

@PageTitle("Ww AI Playground")
public class WwAiAppLayout  extends AppLayout {
    private final Map<String, Class<? extends Component>> tabContents;

    public WwAiAppLayout() {
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setPadding(true);
        titleLayout.setSpacing(true);
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        Image springImg = new Image("https://spring.io/img/projects/spring.svg", "Ww AI Playground");
        springImg.getStyle().set("width", "var(--lumo-icon-size-m)").set("height", "var(--lumo-icon-size-m)");
        Div springImgDiv = new Div(springImg);
        springImgDiv.getStyle().set("display", "flex").set("justify-content", "center").set("align-items", "center");
        titleLayout.add(springImgDiv, new H3("网文智慧体"));
        Tabs tabs = new Tabs();
        addToNavbar(titleLayout, tabs);

        this.tabContents = Map.of("大模型", ChatView.class,"矢量数据库", VectorStoreView.class);
        tabs.add(new Tab("大模型"));
        tabs.add(new Tab("矢量数据库"));
        tabs.addSelectedChangeListener(
                event -> UI.getCurrent().navigate(tabContents.get(event.getSelectedTab().getLabel())));
    }
}
