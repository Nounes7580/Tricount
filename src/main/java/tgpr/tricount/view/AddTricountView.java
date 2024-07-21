package tgpr.tricount.view;


import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2 .*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.tricount.controller.TricountListController;
import tgpr.tricount.controller.add_tricountController;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.model.User;

import java.util.List;
import java.util.regex.Pattern;




    public class AddTricountView extends DialogWindow {

        private add_tricountController controller;

        private int idUser;
        private Tricount tricount;
        private TextBox txtTitle;
        private TextBox txtDescription;
        private Label errTitle;
        private Label errDesc;
        private Button btncreate;

        public AddTricountView(add_tricountController controller, int idUser) {
            super("Create ADD Tricount");

            this.controller = controller;
            this.idUser = idUser;


            initializeUI();
        }

        private void initializeUI() {
            setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
            setCloseWindowWithEscape(true);
            setFixedSize(new TerminalSize(70, 15));
            Panel root = new Panel();
            root.setLayoutManager(new GridLayout(2).setTopMarginSize(1));
            new Label("Title:").addTo(root);
            txtTitle = new TextBox(new TerminalSize(11, 1))
                    .addTo(root)
                    .setValidationPattern(Pattern.compile("[a-zA-Z][a-zA-Z\\d]{0,7}"))
                    .setTextChangeListener((txt, byUser) -> validate());


            // Ajout d'un espace vide pour l'espacement
            new EmptySpace().addTo(root);
            txtTitle.takeFocus();
            errTitle = new Label("")
                    .setForegroundColor(TextColor.ANSI.RED).addTo(root);
            root.addComponent(txtTitle);
            new Label("Description:").addTo(root);
            txtDescription = new TextBox(new TerminalSize(40, 5))
                    .addTo(root)
                    .setValidationPattern(Pattern.compile("[a-zA-Z][a-zA-Z\\d]{0,7}"))
                    .setTextChangeListener((txt, byUser) -> validate());

            new EmptySpace().addTo(root);
            root.addComponent(txtDescription);
            
            errDesc = new Label("")
                    .setForegroundColor(TextColor.ANSI.RED).addTo(root);

            new EmptySpace().addTo(root);
            new EmptySpace().addTo(root);

            var buttons = new Panel().addTo(root).setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
            btncreate = new Button("Create", this::create).addTo(buttons);
            new Button("Cancel", this::close).addTo(buttons);

            setComponent(root);
        }



        private void create() {
            tricount = new Tricount(txtTitle.getText(), txtDescription.getText(), idUser);
            controller.add_tricount(tricount);
           close();
           controller.navigateTo(new TricountListController());
        }


        private void validate() {

            var error = controller.validate(txtTitle.getText(),txtDescription.getText());
            errTitle.setText(error.getFirstErrorMessage(Tricount.Fields.Title));
            errDesc.setText(error.getFirstErrorMessage(Tricount.Fields.Description));
            btncreate.setEnabled(error.isEmpty());

        }


}

