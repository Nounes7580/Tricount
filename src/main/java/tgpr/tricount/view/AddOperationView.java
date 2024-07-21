package tgpr.tricount.view;
import java.util.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyType;
import tgpr.tricount.controller.AddOperationController;
import tgpr.tricount.model.*;
import tgpr.tricount.model.TemplateItem;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import java.time.LocalDate;
import java.util.regex.Pattern;
import static manifold.ext.rt.extensions.manifold.rt.api.Array.ManArrayExt.forEach;
//import static sun.security.util.KeyUtil.validate;
public class AddOperationView extends DialogWindow {
    private final AddOperationController controller;
    private Tricount tricount ;
    private Operation operation;



    /*--------------------------------------------------------------------------------------------*/
    private final TextBox txtTitle;
    private final TextBox txtAmount;
    private final TextBox txtDate;

    /*--------------------------------------------------------------------------------------------*/
    public TextBox getTxtTitle() {
        return txtTitle;
    }

    public TextBox getTxtAmount() {
        return txtAmount;
    }

    public TextBox getTxtDate() {
        return txtDate;
    }

    /*--------------------------------------------------------------------------------------------*/
    public ComboBox<String> comboPaidBy;
    public ComboBox<Template> comboRep;
    public CheckBoxList<Repartition> checkForWhom;
    private CheckBoxList<Repartition> checkBoxRep = new CheckBoxList<>();


    /*--------------------------------------------------------------------------------------------*/
    private final Label errTitle;
    private final Label errAmount;
    private final Label errdate;
    private final Label errCheckForWhom = new Label("").setForegroundColor(TextColor.ANSI.RED);
    /*--------------------------------------------------------------------------------------------*/
    //private final List<Repartition> repartitions = new ArrayList<>();


    private final List<Repartition> repartitions = new ArrayList<>();

    private List<TemplateItem> templateItem = new ArrayList<>();

    public Button delete = new Button("Delete");
    public Button save = new Button("Save");

    public AddOperationView( String title, AddOperationController controller , Tricount tricount) {
        super(title);
        this.controller = controller;
        operation = controller.getOperation();
        this.tricount = tricount;

        Panel root = new Panel().asGridPanel(2);
        //setFixedSize(new TerminalSize(70, 20));
        // root.setLayoutManager(new GridLayout(2));

        setHints(List.of(Hint.CENTERED));
        setCloseWindowWithEscape(true);

        setComponent(root);


        new Label("Title:").addTo(root);
        txtTitle = new TextBox(new TerminalSize(30, 1)).addTo(root)
                .setValidationPattern(Pattern.compile(".*"))
                .setTextChangeListener((txt, byUser) -> validate());
        new EmptySpace().addTo(root);
        txtTitle.takeFocus();
        errTitle = new Label("minmum 3 chars").setForegroundColor(TextColor.ANSI.RED).addTo(root);
        root.addComponent(txtTitle);


        //Amount
        new Label("Amount:").addTo(root);
        txtAmount = new TextBox(new TerminalSize(11, 1)).addTo(root)
                .setValidationPattern(Pattern.compile("^[.0-9]+$"))
                .setTextChangeListener((txt, byUser) -> validate());
        new EmptySpace().addTo(root);
        errAmount = new Label("").setForegroundColor(TextColor.ANSI.RED).addTo(root);
        root.addComponent(txtAmount);


        //Date
        new Label("Date:").addTo(root);
        txtDate = new TextBox(new TerminalSize(15, 1)).addTo(root)
                .setValidationPattern(Pattern.compile("[/\\d]{0,10}"))
                .setTextChangeListener((txt, byUser) -> validate());
        new EmptySpace().addTo(root);
        errdate = new Label("").setForegroundColor(TextColor.ANSI.RED).addTo(root);
        root.addComponent(txtDate);

        //paidBy
        new Label("Paid by:").addTo(root);
        comboPaidBy = new ComboBox<String>().setPreferredSize(new TerminalSize(11, 1));
        for (User elem : tricount.getParticipants()) {
            comboPaidBy.addItem(elem.getFullName());
        }
        comboPaidBy.addTo(root);
        //comboPaidBy.setSelectedItem((User) listUser);
        new EmptySpace().addTo(root);
        new EmptySpace().addTo(root);

        Panel panelHoriz = new Panel();
        panelHoriz.asHorizontalPanel();

        //Use a repartion template
        panelHoriz.addComponent(new Label("Use a repartition \ntemplate (optional):"));
        comboRep = new ComboBox<Template>()
                .setPreferredSize(new TerminalSize(60, 1)).addTo(panelHoriz);
        comboRep.addItem(new Template("No I use a custom repartition", tricount.getId()));
        for (Template elem : Template.getByTricount(tricount.getId())) {
            comboRep.addItem(elem);
        }

        var buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        new Button("Apply", this::createCheckBoxTemp).addTo(buttons);
        panelHoriz.addComponent(buttons, LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        panelHoriz.addTo(root);
        new EmptySpace().addTo(root);
        new EmptySpace().addTo(root);


        root.addComponent(new Label("For whom: \n(weight: \u2190/\u2192 or -/+)"))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING,GridLayout.Alignment.BEGINNING,true,true));
        checkForWhom = createCheckbox();
        checkForWhom.addListener((index, checked) -> {
            checkForWhom.getSelectedItem().setWeight(checked ? 1 : 0);
            validate();
        });
        //metre un ecouteur sur toute la fenetre
        this.addKeyboardListener(checkForWhom, keyStroke -> {
            var character = keyStroke.getCharacter();
            var type = keyStroke.getKeyType();
            if (type == KeyType.ArrowRight || character != null && character == '+') {
                checkForWhom.getSelectedItem().setWeight(checkForWhom.getSelectedItem().getWeight() + 1);
                if (checkForWhom.getSelectedItem().getWeight() == 1) {
                    checkForWhom.setChecked(checkForWhom.getSelectedItem(), true);
                    validate();
                }
            } else if ((type == KeyType.ArrowLeft || character != null && character == '-') && checkForWhom.getSelectedItem().getWeight() > 0) {
                checkForWhom.getSelectedItem().setWeight(checkForWhom.getSelectedItem().getWeight() - 1);
                if (checkForWhom.getSelectedItem().getWeight() == 0) {
                    checkForWhom.setChecked(checkForWhom.getSelectedItem(), false);
                    validate();
                }
            }
            return true;
        });

        checkForWhom.addTo(root);
        new EmptySpace().addTo(root);
        errCheckForWhom.addTo(root);

        new EmptySpace().addTo(root);
        createButtonsPanelH().addTo(root);

    }


    private void validate() {
        String title = getTxtTitle().getText();

        String txtAmount = getTxtAmount().getText();
        Double amount = txtAmount.toDouble();

        String txtDate = getTxtDate().getText();
        LocalDate date = txtDate.toDate();

        var error = controller.validate(title, amount, date, checkForWhom);
        errTitle.setText(error.getFirstErrorMessage(Operation.Fields.Title));
        errAmount.setText(error.getFirstErrorMessage(Operation.Fields.Amount));
        errdate.setText(error.getFirstErrorMessage(Operation.Fields.CreatedAt));
        errCheckForWhom.setText(error.getFirstErrorMessage(Operation.Fields.Repartition));
    }

    private Panel createButtonsPanelH() {
        var panel = Panel.horizontalPanel();
        buttonDelete(panel);
        /*if (operation.getTitle() != null){
            buttonSaveEdit(panel);
        }*/
       // else {
            new Button("Save", this::save).addTo(panel);
       // }
        new Button("Save Repartition as Template", this::saveTemplate).addTo(panel);
        new Button("Cancel", this::close).addTo(panel);
        return panel;
    }
    private void buttonDelete(Panel panel){
        Button delete = new Button("Delete" , this::delete);
        delete.setVisible(operation.getTitle() != null);
        panel.addComponent(delete);

    }
    /*private void buttonSaveEdit(Panel panel){
        Button saveEdit = new Button("Save",this ::saveEdit);
        saveEdit.setVisible(operation.getTitle() != null);
        panel.addComponent(saveEdit);
    }*/
    private void saveTemplate(){
        controller.saveTemplate();
    }
    private CheckBoxList<Repartition> createCheckbox() {
        checkForWhom = new CheckBoxList<>();
        List<Repartition> rep = new ArrayList<>();

        for (User elem : tricount.getParticipants()) {
            if (Repartition.getByKey(operation.getId(), elem.getId()) == null) {
                rep.add(new Repartition(operation.getId(), elem.getId(), 0));
            } else {
                rep.add(Repartition.getByKey(operation.getId(), elem.getId()));
            }
        }

        for (Repartition elme : rep) {
            checkForWhom.addItem(elme, true);
        }

        checkForWhom.addListener((index, checked) -> {
            checkForWhom.getSelectedItem().setWeight(checked ? 1 : 0);
            validate();
        });

        return checkForWhom;
    }

    public CheckBoxList<Repartition> createCheckBoxTemp() {
        if (comboRep.getSelectedItem() != null) {
            templateItem = controller.getTemplateById(comboRep.getSelectedItem().getId());
        }
        return checkBoxRep = comboUpdated(checkForWhom, templateItem);
    }
    public CheckBoxList<Repartition> comboUpdated(CheckBoxList<Repartition> checkForWhom,List<TemplateItem> templateItem){
        if(templateItem.isEmpty()){
            for(Repartition rep : repartitions){
                if(rep.getWeight()> 0) {
                    checkForWhom.addItem(rep, true);
                } else {
                    checkForWhom.addItem(rep, false);
                }
            }
        }else {
            checkForWhom.clearItems();
            for(Repartition rep : repartitions){
                boolean condition = false;
                for(TemplateItem tempItem :templateItem){
                    if(rep.getUserId()== tempItem.getUserId()){
                        checkForWhom.addItem(new Repartition(tempItem.getUserId(),tempItem.getWeight()),true);
                        condition = true;
                    }
                }if(!condition){
                    checkForWhom.addItem(new Repartition(rep.getUserId(),0),false);
                }
            }
        }
        return checkForWhom;
    }



    private void save () {
        String title = txtTitle.getText();

        String txtAmount = getTxtAmount().getText();
        Double amount = txtAmount.toDouble();

        String txtDate = getTxtDate().getText();
        LocalDate date = txtDate.toDate();
        if (!(controller.validate(title, amount,date,checkForWhom)).hasErrors()){
            controller.save(title,amount,date,comboPaidBy.getSelectedIndex());
        }
    }

    /*private void saveEdit(){
        controller.saveEdit();
    }*/

    public Operation getCurrentOp(){
        return this.operation;
    }

    private void  delete() {controller.delete();
    }
}






