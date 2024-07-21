package tgpr.tricount.view;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.ComboBox;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import tgpr.framework.Margin;
import tgpr.framework.Spacing;
import tgpr.tricount.controller.TestController;
import tgpr.tricount.model.*;

import java.util.List;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.framework.ColumnSpec;
import tgpr.framework.ObjectTable;
import tgpr.tricount.controller.EditTricountController;
import tgpr.tricount.model.Tricount;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import tgpr.tricount.model.User;

import java.util.List;
import java.util.Locale;

import static tgpr.framework.Controller.showError;
import static tgpr.framework.Controller.showError;
import static tgpr.framework.Controller.showError;
import static tgpr.framework.Controller.showError;
import static tgpr.framework.Controller.showError;

public class EditTricountView extends DialogWindow {

    private final EditTricountController controller;
    private final Tricount tricount;
    private Panel root = new Panel();

    private final TextBox txtBody = new TextBox("",TextBox.Style.MULTI_LINE);
    private final TextBox txtTitle = new TextBox();
    private  Label lblErrorTitle = new Label("");
    private  Label lblErrorDescription = new Label("");
    private final TextBox txtDescription = new TextBox();
    private  Button btnSave;
    private  Button btnTemplate;
    private  Button btnDelete;
    private  Button btnCancel;

    private Button addButon;
    private ActionListBox subscribers;

    private ComboBox<User> user ;


    public EditTricountView(EditTricountController controller, Tricount tricount) {
        super("Edit tricount");
        this.controller = controller;
        this.tricount = tricount;
        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setComponent(root);
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(55,23));
        txtBody.takeFocus();

        createFields().addTo(root);

        sub().addTo(root);

        createButtonsPanel().addTo(root);
    }
    private Panel sub(){
        Panel sub = new Panel();
        createSub();

        createUsers();

        Label label = new Label("subscribers :").addTo(root);

        subscribers.addTo(root);

        Label label1 = new Label("Select Users : ").addTo(root);

        user.addTo(root);


        addButon = new Button("add",() -> addUser()).addTo(root);
        return sub;
    }
    private void createUsers(){
        var listUser = controller.getUsers();
        this.user = new ComboBox<User>();
        var listParticipant = controller.getSubscribers(tricount);

        for (var i :listUser) {
            if(!listParticipant.contains(i)){
                this.user.addItem(i);
            }
        }
        for ( User u : listParticipant) {
            System.out.println(u.getFullName());
        }
    }

    private void createSub(){
        var listParticipant = controller.getSubscribers(tricount);
        this.subscribers = new ActionListBox();
        for (var i : listParticipant) {
            this.subscribers.addItem(getNameForActionBox(i),()->doSomethingWithUser(i));
        }
    }
    private String getNameForActionBox(User user){
        int amountPart = 0;
        for (Operation op : tricount.getOperations()) {
          Repartition rep = Repartition.getByKey(op.getId(),user.getId());
          if (rep != null){
              amountPart +=1;
          }
        }
        String name = user.getFullName();
        if (user.getId() == tricount.getCreator().getId() || amountPart >0 ){
            name += " (*)";
        }
        return name;
    }
    private void doSomethingWithUser(User user) {
        subscribers.removeItem(subscribers.getSelectedIndex());
        this.user.addItem(user);

    }
    private void addUser(){
        if(user.getItemCount()> 0){
            var selectedUser = user.getSelectedIndex();
            User selectedUserObj = user.getSelectedItem();
            if (selectedUser !=-1){
            var userName = user.getItem(selectedUser);
            user.removeItem(selectedUser);
            subscribers.addItem(userName.getFullName(),()->doSomethingWithUser(userName));
            if (selectedUserObj != null){
                Subscription newSub =new Subscription(tricount.getId(),selectedUserObj.getId());
            tricount.getSubscriptions().add(new Subscription(tricount.getId(),selectedUserObj.getId()));
            }
            }
        }
        else {
            user.setEnabled(false);
        }

    }

    private Panel createFields(){
        Panel panel = new Panel().setLayoutManager(new GridLayout(2).setTopMarginSize(1));
        new Label("Title:").addTo(panel);
        txtTitle.setPreferredSize(new TerminalSize(30,1)).addTo(panel)
                        .setTextChangeListener((txt, byUser) -> validateTitle());

        txtTitle.setText(tricount.getTitle());
        txtTitle.addTo(panel);
        new EmptySpace().addTo(panel);
        lblErrorTitle = new Label("").setForegroundColor(TextColor.ANSI.RED).addTo(panel);



        new Label("Description :").addTo(panel);
       // TextBox txtDescription = new TextBox();
        txtDescription.setPreferredSize(new TerminalSize(30, 4)).addTo(panel)
                .setTextChangeListener((txt, byUser) -> {
                        validateDescription();
                });
        if (tricount.getDescription()!=null)
            txtDescription.setText(tricount.getDescription());
        else
            txtDescription.setText("null");

        txtDescription.addTo(panel);
        new EmptySpace().addTo(panel);
        lblErrorDescription = new Label("").setForegroundColor(TextColor.ANSI.RED).addTo(panel);


        return panel;
    }

    private Panel createButtonsPanel(){
        var panel = new Panel()
                .setLayoutManager(new LinearLayout((Direction.HORIZONTAL)))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        btnDelete = new Button("Delete",this::delete).addTo(panel).setEnabled(txtBody != null);
        btnSave = new Button("Save",this::save).addTo(panel).setEnabled(txtBody != null);
        btnTemplate = new Button("Templates",this::templates).addTo(panel).setEnabled(txtBody != null);
        btnCancel = new Button("Cancel", this::close).addTo(panel).setEnabled(true);

        return panel;
    }
    private void templates(){
        controller.templates();
    }

    private void save() {
        controller.save(txtTitle.getText(),txtDescription.getText(), tricount.getSubscriptions());
        close();
    }

    private void validateTitle() {
        var error = controller.validateTitle(txtTitle.getText());
        lblErrorTitle.setText(error.getFirstErrorMessage(Tricount.Fields.Title));

    }
    private void validateDescription() {
        var error = controller.validateDescription(txtDescription.getText());
        lblErrorDescription.setText(error.getFirstErrorMessage(Tricount.Fields.Description));
        if (txtDescription.getText() == null){
            lblErrorDescription.setText("");
        }

    }


    public void delete(){
        controller.deleteTricount();

    }
    public void errorMessage() {
        showError("You may not remove this participant because he is the creator he is implied is one or more expenses !", "Error") ;
    }
    
    public void deleteUser(){
        this.addKeyboardListener(user, keyStroke -> {
            var character = keyStroke.getCharacter();
            var type = keyStroke.getKeyType();
            if (type == KeyType.Enter || character != null) {
                user.getSelectedItem().delete();
                controller.deleteUser(user.getSelectedItem());
                User selectedUserObj = user.getSelectedItem();
                Subscription newSub = Subscription.getByKey(tricount.getId(),selectedUserObj.getId());
                tricount.getSubscriptions().remove(new Subscription(tricount.getId(),selectedUserObj.getId()));
                refresh();
            }
            return true;
        });
    }

    public void refresh(){
        createSub();
    }


}
