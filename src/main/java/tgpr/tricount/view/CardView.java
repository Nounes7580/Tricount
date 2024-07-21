package tgpr.tricount.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.tricount.controller.CardController;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.model.User;

import java.util.ArrayList;
import java.util.List;

public class CardView extends DialogWindow  {
    private Tricount tricount;
    private int creatorId;
    private CardController controller;
    private Label descriptionLabel;
    private Label titleLabel;
    private Label creatorNameLabel;
    private Label participantsLabel;
    private List<String> listInformation = new ArrayList<>();
    private Button openBtn;
    private Panel root;

    public CardView(Tricount tricount) {
        super("");
        this.tricount = tricount;

        fillCard();
    }
    public String getNameOfCreator() {
        return "Created by " + User.getByKey(creatorId).toString();
    }


    public void fillCard() {
        setListInformation();

        titleLabel = new Label(listInformation.get(0) + "\n");
        descriptionLabel = new Label(listInformation.get(1) + "\n");
        creatorNameLabel = new Label("Created by "+listInformation.get(2) + "\n");
        participantsLabel = new Label("With "+tricount.getNumberOfParticipants()+" "+listInformation.get(3) + "\n");

        root = Panel.verticalPanel(LinearLayout.Alignment.Center).sizeTo(34, 5);

        root.addComponent(titleLabel.setForegroundColor(TextColor.ANSI.BLUE).center());
        root.addComponent(descriptionLabel.setForegroundColor(new TextColor.RGB(128, 128, 128)).addStyle(SGR.ITALIC).center());
        root.addComponent(creatorNameLabel.center());
        root.addComponent(participantsLabel.center());

        createOpenButton().addTo(root).center();

        setComponent(root.withBorder(Borders.singleLine()));
    }
    private Button createOpenButton() {
        openBtn = new Button("Open", this::openTricount);
        addShortcut(openBtn, KeyStroke.fromString("<A-l>"));

        return openBtn;
    }
    private Tricount openTricount() {
        return controller.openTricount();
    }

    public void setListInformation() {
        listInformation.add(tricount.getTitle());
        listInformation.add(tricount.getDescription());
        listInformation.add(tricount.getCreator().getFullName());
        listInformation.add(tricount.getParticipants().toString());
    }

}
