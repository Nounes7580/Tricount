package tgpr.tricount.view;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import tgpr.framework.Layouts;
import tgpr.framework.Margin;
import tgpr.tricount.controller.SignupController;
import tgpr.tricount.model.User;

import java.util.List;
import java.util.regex.Pattern;

public class SignupView extends BasicWindow {
    private SignupController controller;
    private TextBox txtMail;
    private TextBox txtFullName;
    private TextBox txtIban;
    private TextBox txtPassword;
    private TextBox txtConfirmPassword;
    private final Label errMail = new Label("");
    private final Label errFullName = new Label("");
    private final Label errIban = new Label("");
    private final Label errPassword = new Label("");
    private final Label errConfirmPassword = new Label("");
    private Button btnSignup;

    public SignupView(SignupController controller){
        this.controller = controller;
        setTitle("Signup");
        setHints(List.of(Hint.CENTERED));

        Panel root = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL).setSpacing(1));
        setComponent(root);

        createFieldsGrid().addTo(root);
        createButtonsPanel().addTo(root);
        txtMail.takeFocus();
    }

    private Panel createFieldsGrid() {
        var panel = Panel.gridPanel(2, Margin.of(1));

        new Label("Mail:").addTo(panel);
        txtMail = new TextBox().sizeTo(25).addTo(panel)
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errMail.addTo(panel)
                .setForegroundColor(TextColor.ANSI.RED);

        new Label("Full Name:").addTo(panel);
        txtFullName = new TextBox().sizeTo(30).addTo(panel)
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errFullName.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        new Label("Iban:").addTo(panel);
        txtIban = new TextBox().sizeTo(25).addTo(panel)
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errIban.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        new Label("Password:").addTo(panel);
        txtPassword = new TextBox().sizeTo(25).addTo(panel)
                .setMask('*')
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errPassword.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        new Label("Confirm Password:").addTo(panel);
        txtConfirmPassword = new TextBox().sizeTo(25).addTo(panel)
                .setMask('*')
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errConfirmPassword.addTo(panel).setForegroundColor(TextColor.ANSI.RED);




        return panel;
    }
    private Panel createButtonsPanel(){
        Panel panel = new Panel()
                .setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(Layouts.LINEAR_CENTER);
        btnSignup = new Button("Signup", this::signup).addTo(panel).setEnabled(false);
        new Button("Close", this::closee).addTo(panel);

        return panel;
    }
    private void signup(){
        controller.signup();
    }
    private void closee(){
        controller.close();
    }

    private void validate() {
        var errors = controller.validate(
                txtMail.getText(),
                txtFullName.getText(),
                txtIban.getText(),
                txtPassword.getText(),
                txtConfirmPassword.getText()
        );

        errMail.setText(errors.getFirstErrorMessage(User.Fields.Mail));
        errFullName.setText(errors.getFirstErrorMessage(User.Fields.FullName));
        errIban.setText(errors.getFirstErrorMessage(User.Fields.Iban));
        errPassword.setText(errors.getFirstErrorMessage(User.Fields.Password));
        errConfirmPassword.setText(errors.getFirstErrorMessage(User.Fields.ConfirmPassword));

        btnSignup.setEnabled(errors.isEmpty());
    }
}
