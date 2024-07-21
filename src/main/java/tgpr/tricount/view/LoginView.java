/*package tgpr.tricount.view;

import com.googlecode.lanterna.gui2.*;
import tgpr.tricount.controller.LoginController;
import tgpr.framework.Configuration;
import tgpr.framework.Layouts;

import java.util.List;

public class LoginView extends BasicWindow {

    private final LoginController controller;
    private final TextBox txtMail;
    private final TextBox txtPassword;
    private final Button btnLogin;

    public LoginView(LoginController controller) {
        this.controller = controller;

        setTitle("Login");
        setHints(List.of(Hint.CENTERED));

        Panel root = new Panel();
        setComponent(root);

        Panel panel = new Panel().setLayoutManager(new GridLayout(2).setTopMarginSize(1).setVerticalSpacing(1))
                .setLayoutData(Layouts.LINEAR_CENTER).addTo(root);
        panel.addComponent(new Label("Mail:"));
        txtMail = new TextBox().addTo(panel);
        panel.addComponent(new Label("Password:"));
        txtPassword = new TextBox().setMask('*').addTo(panel);

        new EmptySpace().addTo(root);

        Panel buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(Layouts.LINEAR_CENTER).addTo(root);
        btnLogin = new Button("Login", this::login).addTo(buttons);
        Button btnSignup = new Button("Signup", this::signup).addTo(buttons);
        Button btnExit = new Button("Exit", this::exit).addTo(buttons);

        new EmptySpace().addTo(root);

        Button btnSeedData = new Button("Reset Database", this::seedData);
        Panel debug = Panel.verticalPanel(LinearLayout.Alignment.Center,
                new Button("Login as default admin", this::logAsDefaultAdmin),
                new Button("Login as default user", this::logAsDefaultMember),
                new Button("Login as default user", this::logAsDefaultMember),

                btnSeedData
        );
        debug.withBorder(Borders.singleLine(" For debug purpose ")).addTo(root);

        txtMail.takeFocus();
    }

    private void seedData() {
        controller.seedData();
        btnLogin.takeFocus();
    }

    private void exit() {
        controller.exit();
    }
    private void signup() {
        controller.signup();
    }

    private void login() {
        var errors = controller.login(txtMail.getText(), txtPassword.getText());
        if (!errors.isEmpty()) {
            txtMail.takeFocus();
        }
    }

    private void logAsDefaultAdmin() {
        controller.login(Configuration.get("default.admin.mail"), Configuration.get("default.admin.password"));
    }

    private void logAsDefaultMember() {
        controller.login(Configuration.get("default.user.mail"), Configuration.get("default.user.password"));
    }
}*/
package tgpr.tricount.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import tgpr.tricount.controller.LoginController;
import tgpr.tricount.model.User;
import tgpr.framework.*;
import tgpr.framework.*;

import java.util.List;

public class LoginView extends BasicWindow {

    private final LoginController controller;

    private final TextBox txtMail = new TextBox(new TerminalSize(20, 1));
    private final TextBox txtPassword = new TextBox(new TerminalSize(20, 1));

    private  Button buttonLogin;

    private final Label errMail = new Label("");
    private final Label errPassword = new Label("");


    public LoginView(LoginController controller) {
        this.controller = controller;

        setTitle("Login");
        setHints(List.of(Hint.CENTERED));

        Panel root = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL).setSpacing(1));
        setComponent(root);

        createFieldsPanel().addTo(root);
        createButtonsPanel().addTo(root);
        createDebugPanel().addTo(root).setLayoutData(Layouts.LINEAR_CENTER);
        buttonLogin.takeFocus();

    }

    private Panel createFieldsPanel(){
        Panel panel = new Panel().setLayoutManager(new GridLayout(2)
                        .setTopMarginSize(1)
                        .setVerticalSpacing(0))
                .setLayoutData(Layouts.LINEAR_CENTER);
        new Label("Mail:").addTo(panel);

        txtMail.setTextChangeListener((txt, chged) -> validate()).addTo(panel).takeFocus();
        new EmptySpace().addTo(panel);
        errMail.addTo(panel)
                .setForegroundColor(TextColor.ANSI.RED);

        new Label("Password:").addTo(panel);
        txtPassword.setMask('*').setTextChangeListener((txt, chged) -> validate()).addTo(panel);
        new EmptySpace().addTo(panel);

        errPassword.addTo(panel)
                .setForegroundColor(TextColor.ANSI.RED);




        new EmptySpace().addTo(panel);

        return panel;
    }

    private Panel createButtonsPanel(){
        Panel panel = new Panel()
                .setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(Layouts.LINEAR_CENTER);
        buttonLogin = new Button("Login", this::login).addTo(panel);
        Button btnSignup = new Button("Signup", this::signup).addTo(panel);
        Button btnExit = new Button("Exit", this::exit).addTo(panel);

        return panel;
    }



    private Border createDebugPanel() {
        Button btnSeedData = new Button("Reset Database", this::seedData);
        Panel panel = Panel.verticalPanel(LinearLayout.Alignment.Center,
                new Button("Login as default admin", this::logAsDefaultAdmin),
                new Button("Login as default boverhaegen@epfc.eu", this::logAsDefaultUser),
                new Button("Login as default bepenelle@epfc.eu", this::logAsDefaultUser1),
                btnSeedData
        );
        Border border = panel.withBorder(Borders.singleLine(" For debug purpose "));

        //ViewManager.addShortcut(this, btnSeedData, KeyStroke.fromString("<A-r>"));

        return border;
    }


    private void seedData(){controller.seedData();}

    private void exit() {
        controller.exit();
    }
    private void login(){
        var errors = controller.login(txtMail.getText(), txtPassword.getText());
        if(!errors.isEmpty()){
            txtMail.takeFocus();

        }

    }

    private void logAsDefaultAdmin() {
        controller.login(Configuration.get("default.admin.mail"), Configuration.get("default.admin.password"));
    }
    private void logAsDefaultUser() {
        controller.login(Configuration.get("default.user.mail"), Configuration.get("default.user.password"));

    }
    private void logAsDefaultUser1() {

        controller.login(Configuration.get("default.user1.mail"), Configuration.get("default.user1.password"));

    }


    private void validate() {
        var errors = controller.validate(
                txtMail.getText(),
                txtPassword.getText()

        );

        errMail.setText(errors.getFirstErrorMessage(User.Fields.Mail));
        errPassword.setText(errors.getFirstErrorMessage(User.Fields.Password));

    }
    private void signup() {
        controller.signup();
    }


}

