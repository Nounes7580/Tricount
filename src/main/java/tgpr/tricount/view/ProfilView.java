package tgpr.tricount.view;

import com.googlecode.lanterna.gui2.*;
import tgpr.tricount.controller.ProfilController;
import tgpr.tricount.model.Security;

import java.util.List;

public class ProfilView extends BasicWindow {


    private final ProfilController controller;

    public ProfilView(ProfilController controller) {
        super("Profil");
        this.controller = controller;

        setHints(List.of(Hint.CENTERED));
        setCloseWindowWithEscape(true);

        Panel root = new Panel();
        setComponent(root);

        Panel fields = new Panel().setLayoutManager(new GridLayout(1).setTopMarginSize(1)).addTo(root);

        fields.addComponent(new Label("Hey "+Security.getLoggedUser().getFullName()+"!"));
        fields.addComponent(new Label(""));


        fields.addComponent(new Label("I know your email address is "+Security.getLoggedUser().getMail()+"."));
        fields.addComponent(new Label(""));


        fields.addComponent(new Label("What can i do for you ? "));
        fields.addComponent(new Label(""));

        new EmptySpace().addTo(root);

        var buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        new Button("Edit Profile", this::edit_profile).addTo(buttons);
        new Button("Change Password", this::change_Password).addTo(buttons);
        new Button("Close", this::close1).addTo(buttons);
        root.addComponent(buttons);
    }

    private void close1() {
        controller.close1();
    }


    private void change_Password() {
    }

    private void edit_profile() {
    }
}