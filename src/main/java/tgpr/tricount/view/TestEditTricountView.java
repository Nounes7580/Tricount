package tgpr.tricount.view;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.tricount.controller.TestEditTricount;
import tgpr.tricount.model.Subscription;
import tgpr.tricount.model.Tricount;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import tgpr.tricount.model.User;


import java.security.Key;
import java.util.List;

import static tgpr.framework.Controller.showError;

public class TestEditTricountView  extends DialogWindow {
    private final TestEditTricount controller;
    private Tricount tricount;
    private Subscription sub;
    private User user;

    public TestEditTricountView(TestEditTricount controller) {
        super("Delete participant");
        this.controller = controller;

        setHints(List.of(Hint.CENTERED,Hint.MODAL));
        setCloseWindowWithEscape(true);

        Panel root = Panel.verticalPanel();
        setComponent(root);



    }


    public void errMessage() {
        showError("You may not remove this participant because he is the creator he is implied is one or more expenses !", "Error") ;
    }

    private void delete(){
        controller.deleteUser(user.getFullName());
    }
}