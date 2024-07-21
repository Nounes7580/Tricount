package tgpr.tricount.view;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import tgpr.framework.ColumnSpec;
import tgpr.framework.ObjectTable;
import tgpr.framework.ViewManager;
import tgpr.tricount.controller.AddTemplateController;
import tgpr.tricount.controller.Edit_template_Controller;
import tgpr.tricount.controller.View_template_controller;
import tgpr.tricount.model.Template;
import tgpr.tricount.model.TemplateItem;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.model.User;
import tgpr.tricount.view.AddTemplateView;

import java.util.List;

public class View_template extends DialogWindow {

    private CheckBoxList<TemplateItem> templateItemsCheckBoxList = new CheckBoxList<>();
    private Button newButton;
    private Button editTitleButton;
    private Button deleteButton;
    private Button saveButton;
    private Button closeButton;
    private View_template_controller templatesController;
    private User currentUser;
    private Template selectedTemplate;
    private ObjectTable<Template> Table;
    private Panel mainPanel;
    private Button modifiedIndicator;
    private boolean templateModified;
    private Label messageLabel;
    private AddTemplateController controller2;
    private AddTemplateView View2;

    public View_template(String title, View_template_controller controller) {
        super("Custom Tricount Repartition Templates");
        this.templatesController = controller;
        this.controller2 = new AddTemplateController(controller.getTricount());
        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setFixedSize(new TerminalSize(60, 15));
        Panel rootPanel = new Panel();
        rootPanel.setLayoutManager(new GridLayout(1));
        mainPanel = new Panel();
        mainPanel.setLayoutManager(new GridLayout(1));
        Table = new ObjectTable<>(
                new ColumnSpec<>("Templates", Template::getTitle)
        );

        Table.sizeTo(ViewManager.getTerminalColumns(), 7);
        Table.add(controller.getTricount().getTemplates());
        Table.setSelectAction(this::onTemplateSelected);
        Table.addTo(mainPanel);
        mainPanel.addTo(rootPanel);
        messageLabel = new Label("");
        messageLabel.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.BEGINNING, true, false));
        messageLabel.addTo(mainPanel);

        new Label("Repartition: ").addTo(mainPanel);
        // Nouveau bouton pour indiquer la modification
        modifiedIndicator = new Button("");
        modifiedIndicator.setEnabled(false);
        modifiedIndicator.setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1));
        modifiedIndicator.setVisible(false);

        reloadData();

        Panel buttonsPanel = new Panel();
        buttonsPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        messageLabel = new Label("");
        messageLabel.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.BEGINNING, true, false));
        messageLabel.addTo(mainPanel);
        newButton = new Button("New",this::newTemplate);
        editTitleButton = new Button("Edit Title", () -> {
            newTemplate();

        });
        //deleteButton = new Button("Delete");
        deleteButton = new Button("Delete",this::delete);
        saveButton = new Button("Save" , () -> {
            saveTemplate();
        });
        closeButton = new Button("Close", this::close);
        newButton.addTo(buttonsPanel);
        editTitleButton.addTo(buttonsPanel);
        deleteButton.addTo(buttonsPanel);
        saveButton.addTo(buttonsPanel);
        closeButton.addTo(buttonsPanel);
        buttonsPanel.addTo(rootPanel).setLayoutData(
                GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.END, false, true)
        );
        templateItemsCheckBoxList.addListener((CheckBoxList.Listener) (index, checked) -> {
            // Mettez à jour l'état modifié
            setModified(true);
            modifiedIndicator.setVisible(isModified());
            // Affichez un message en fonction de l'état de la case à cocher
            if (checked) {
                showMessage("");
            } else {
                showMessage("Modifier " + templateItemsCheckBoxList.getItemAt(index));
            }
        });
       // addKeyboardListener(Table,this::movetemplate);

        addKeyboardListener(
                templateItemsCheckBoxList,
                this::handleWeightKeyStroke
        );

        setComponent(rootPanel);
    }
    public void delete(){
        templatesController.delete(selectedTemplate);
    }
    // Méthode pour sauvegarder un modèle
    public void saveTemplate() {
        if (isModified()) {
            for (var s :templateItemsCheckBoxList.getCheckedItems() ){
                s.save();
            }
            selectedTemplate.save();
            setModified(false);
            reloadData();
            showMessage("Template has been updated.");
            showConfirmationDialog("Template has been updated.");
        } else {
            // Si le modèle n'est pas modifié, afficher un message indiquant qu'il n'y a rien à enregistrer
            showMessage("No changes to save.");
            messageLabel.setText("No changes to save.");
        }
    }
    // Méthode pour vérifier si le modèle a été modifié
    public boolean isModified() {
        return templateModified;
    }
    // Méthode pour définir l'état modifié
    public void setModified(boolean modified) {
        templateModified = modified;
    }
    // Méthode appelée lorsqu'un modèle est sélectionné
    public void onTemplateSelected() {
        selectedTemplate = Table.getSelected();
        reloadData();
    }
    public void initializeAddEditTemplateController() {
        if (Table.getSelected() != null) {
            controller2 = new AddTemplateController(templatesController.getTricount());
            controller2.setTemplate(Table.getSelected());
        } else {
            // Gérez le cas où aucune template n'est sélectionnée
            System.err.println("Error: No template selected.");
        }
    }
    // Méthode pour recharger les données dans l'interface utilisateur
    public void reloadData() {
        templateItemsCheckBoxList.clearItems();
        selectedTemplate = Table.getSelected();

        // Vérifiez si selectedTemplate est null avant d'accéder à ses propriétés
        if (selectedTemplate != null) {
            List<User> userList = templatesController.getTricount().getParticipants();
            List<TemplateItem> templateItems = selectedTemplate.getTemplateItems();

            // Utilisez les méthodes stream et lambda pour simplifier le code
            templateItems.forEach(item -> templateItemsCheckBoxList.addItem(item).setChecked(item, true));

            // Filtrer les utilisateurs qui ne sont pas déjà dans les templateItems
            userList.stream()
                    .filter(user -> templateItems.stream().noneMatch(item -> item.getUser().getId() == user.getId()))
                    .forEach(user -> templateItemsCheckBoxList.addItem(new TemplateItem(user.getId(), selectedTemplate.getId(), 0)));

            templateItemsCheckBoxList.addTo(mainPanel);
            setModified(true);
            modifiedIndicator.setVisible(isModified());
        } else {
            // Gérez le cas où selectedTemplate est null
            System.err.println("Error: No template selected.");
        }
    }
    // Méthode pour afficher une boîte de dialogue de confirmation
    private void showConfirmationDialog(String message) {
        new MessageDialogBuilder()
                .setTitle("Confirmation")
                .setText(message)
                .build()
                .showDialog(getTextGUI());
    }
    // Méthode pour afficher un message dans la console (utilisé pour le débogage)
    public void showMessage(String message) {
        System.out.println(message);
        messageLabel.setText(message);
    }
    private void newTemplate(){
        templatesController.newTemplate();
        close();
    }
    private Boolean handleWeightKeyStroke(KeyStroke keyStroke) {
        KeyType type = keyStroke.getKeyType();

        int originalWeight = templateItemsCheckBoxList.getSelectedItem().getWeight();

        if (type == KeyType.ArrowLeft && originalWeight > 0) {
            templateItemsCheckBoxList.getSelectedItem().setWeight(originalWeight - 1);
        } else if (type == KeyType.ArrowRight) {
            templateItemsCheckBoxList.getSelectedItem().setWeight(originalWeight + 1);
        }

        // Vérifiez si le poids a changé avant d'afficher le message
        if (templateItemsCheckBoxList.getSelectedItem().getWeight() != originalWeight) {
            templateItemsCheckBoxList.toggleChecked(templateItemsCheckBoxList.getSelectedIndex());
            showMessage("Modifier");
        }

        templateItemsCheckBoxList.invalidate();
        return true;
    }
//public boolean movetemplate(KeyStroke keyStroke){
   // KeyType type = keyStroke.getKeyType();
    //if (type == KeyType.ArrowUp) {
        // Lorsque le curseur est déplacé vers le haut, mettez à jour les répartitions
    //    reloadData();
    //    return true;
  //  } else if (type == KeyType.ArrowDown) {
        // Lorsque le curseur est déplacé vers le bas, vérifiez si c'est depuis le haut de la liste
     //   if (templateItemsCheckBoxList.getSelectedIndex() == 0) {
      //      reloadData();
      //      return true;
    //    }
  //  }

  //  return false;
//}

}


