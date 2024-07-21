package tgpr.tricount.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.framework.Margin;
import tgpr.tricount.controller.AddTemplateController;
import tgpr.tricount.model.Template;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.model.User;

import java.util.List;
import java.util.regex.Pattern;

public class AddTemplateView extends DialogWindow {

    private final AddTemplateController controller;
    private TextBox title;
    private Label errTitle = new Label("");
    private Button btnCreate;
    public TextBox getTextTitle() {
        return title;
    }

    public AddTemplateView(AddTemplateController controller) {
        super("Create a new template");

        this.controller = controller;

        setHints(List.of(Hint.CENTERED, Hint.MODAL));
        setCloseWindowWithEscape(true);

        Panel root = Panel.verticalPanel();
        setComponent(root);

        createFields().addTo(root);
        createButtons().addTo(root);

        title.takeFocus();
    }

    private Panel createFields() {
        Panel panel = Panel.gridPanel(2, Margin.of(1));

        new Label("Title:").addTo(panel);
        title = new TextBox(new TerminalSize(30, 1)).addTo(panel)
                .setValidationPattern(Pattern.compile("[a-z][a-z\\d]{0,20}"))
                .setTextChangeListener((txt, byUser) -> validate())
                .setReadOnly(title != null);;
        new EmptySpace().addTo(panel);
        title.takeFocus();
        errTitle = new Label("").setForegroundColor(TextColor.ANSI.RED).addTo(panel);
        panel.addComponent(title);

        panel.addEmpty();

        return panel;
    }
    private void validate() {

        var error = controller.validate(title.getText());
        errTitle.setText(error.getFirstErrorMessage(Template.Fields.Title));
        btnCreate.setEnabled(error.isEmpty());

    }

    private Panel createButtons() {
        var panel = Panel.horizontalPanel().center();

        btnCreate = new Button("Create", this::create).addTo(panel).setEnabled(false);

        new Button("Cancel", this::close).addTo(panel);

        return panel;
    }
    //TODO  private void refresh()
    

    public void create() {
        controller.create(getTextTitle().getText());
        close();
    }


}
