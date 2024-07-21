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
import tgpr.framework.Margin;
import tgpr.framework.Spacing;
import tgpr.tricount.controller.TestController;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.model.User;

import java.util.List;

public class TestView extends DialogWindow {
    private final TestController controller;
    // la première combo contient des objets de type User
    private final ComboBox<User> cbo1;
    // la seconde combo contient des strings car on y mélange des objets User et un string pour la première entrée
    private final ComboBox<String> cbo2;
    private final Label lbl;

    private final Button delete;

    private final Button addButon;

    private ActionListBox subscribers;

    private ComboBox<User> user ;


    private final Menu menuFile = new Menu("File");


    public TestView(TestController controller) {
        super("Test");
        setHints(List.of(Hint.EXPANDED));

        this.controller = controller;

        var root = Panel.gridPanel(1, Margin.of(1), Spacing.of(1));
        setComponent(root);

        cbo1 = new ComboBox<User>()
                .sizeTo(25)
                .addTo(root)
                // On ajoute un listener sous la forme d'une méthode lambda qui sera appelée lorsque la valeur
                // sélectionnée dans la combo change. Ce listener prend trois paramètres :
                // - newIndex : l'indice du nouvel élément sélectionné
                // - oldIndex : l'indice de l'ancien élément sélectionné
                // - byUser : un booléen qui indique si le changement de sélection a été déclenché par l'utilisateur (true)
                //            ou par du code (false)
                .addListener((newIndex, oldIndex, byUser) -> {
                    // lorsque la sélection change, on doit rafraîchir la deuxième combo pour n'y retrouver que les users
                    // différents du user sélectionné
                    refreshCbo2();
                });

        cbo2 = new ComboBox<String>()
                .sizeTo(25)
                .addTo(root)
                // on ajoute un listener suivant le même principe que pour la première combo
                .addListener((newIndex, oldIndex, byUser) -> {
                    // quand la sélection change, on doit rafraîchir le libellé
                    refreshLabel();
                });

        lbl = new Label("")
                .addTo(root);
        controller.setTricount(Tricount.getByKey(2));
        delete = new Button("delete",() -> delete()).addTo(root);

        cbo1.takeFocus();

        createSub();

        createUsers();

        Label label = new Label("subscribers :").addTo(root);

        subscribers.addTo(root);

        Label label1 = new Label("Select Users : ").addTo(root);

        user.addTo(root);


        addButon = new Button("add",() -> addUser()).addTo(root);



        // refresh initial
        refresh();
    }
    private void delete(){
        controller.delete();
    }
    private void refresh() {
        refreshCbo1();
        refreshCbo2();
        refreshLabel();
    }

    private void refreshCbo1() {
        // on ajoute tous les users dans la première combo
        for (var user : controller.getUsers())
            cbo1.addItem(user);
    }

    private void refreshCbo2() {
        // on garde en mémoire l'élément couramment sélectionné
        var current = cbo2.getSelectedItem();

        // on vide puis on re-remplit la combo avec tous les users différents du user sélectionné dans cbo1
        // plus un string en première position
        cbo2.clearItems();
        cbo2.addItem("-- Please choose --");
        for (var user : controller.getUsers())
            if (!user.equals(cbo1.getSelectedItem()))
                cbo2.addItem(user.toString());

        // si un élément était sélectionné auparavant, on le re-sélectionne.
        if (current != null)
            cbo2.setSelectedItem(current);
    }

    private void refreshLabel() {
        var user = getSelectedSecondUser();
        if (user == null) {
            lbl.setText("Please choose two different users!");
            lbl.setForegroundColor(TextColor.ANSI.RED);
        } else {
            lbl.setText(cbo1.getSelectedItem().getFullName() + " - " + user.getFullName());
            lbl.setForegroundColor(TextColor.ANSI.BLUE);
        }
    }

    // Récupère le user sélectionné dans cbo2
    private User getSelectedSecondUser() {
        String fullName = cbo2.getSelectedItem();
        if (fullName == null) return null;
        return User.getByFullName(fullName);
    }


    private void createUsers(){
        var listUser = controller.getUsers();
        this.user = new ComboBox<User>();
        var listParticipant = controller.getSubscribers(Tricount.getByKey(2));

        for (var i :listUser) {
            if(!listParticipant.contains(i)){
                this.user.addItem(i);
            }

        }
    }

    private void createSub(){
        var listParticipant = controller.getSubscribers(Tricount.getByKey(2));
        this.subscribers = new ActionListBox();
        for (var i : listParticipant) {
                this.subscribers.addItem(i.getFullName(),()->doSomethingWithUser(i));
        }
    }
    private void doSomethingWithUser(User user) {
        System.out.println(user.getFullName() + "has been pressed");
    }
    private void addUser(){
        if(user.getItemCount()> 0){
            var selectedUser = user.getSelectedIndex();
            var userName = user.getItem(selectedUser);
            user.removeItem(selectedUser);
            subscribers.addItem(userName.getFullName(),()->doSomethingWithUser(userName));
        }
        else {
            user.setEnabled(false);
        }



    }


    private MenuBar createMenu() {
        //creation of the bar where we can find the logout item.
        MenuBar menuBar = new MenuBar();
        menuBar.add(menuFile);
        addShortcut(menuFile, KeyStroke.fromString("<A-f>"));
        MenuItem menuLogout = new MenuItem("Logout", controller::logout);
        menuFile.add(menuLogout);
        MenuItem menuExit = new MenuItem("Exit", controller::exit);
        menuFile.add(menuExit);
        return menuBar;
    }

}
