package tgpr.tricount.controller;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.CheckBoxList;
import com.googlecode.lanterna.gui2.Window;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;
import tgpr.tricount.model.*;
import tgpr.tricount.view.AddOperationView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static tgpr.framework.Controller.askConfirmation;


public class AddOperationController extends Controller {
    private  Operation operation;
    private AddOperationView view;
    private Tricount tricount;
    private EditTricountController controller;

    /*_____________________________________________________________________________________________________*/

    //le premier constructeur pour ajouter une operation
    public AddOperationController(Tricount tricount) {
        this(tricount,new Operation() , new EditTricountController(tricount));
    }
    //le deuxieme pour editer l'operation
    public AddOperationController(Tricount tricount, Operation operation, EditTricountController editTricountController) {
        this.operation = operation;
        this.tricount = tricount;
        String title = operation.getTitle() == null ? "Add New Expense": "Edit Expense";
        view = new AddOperationView(title,this,tricount);

        /*if (editTricountController.isDeleteButtonVisible()) {
            Button deleteButton = new Button("Delete", this::delete);
            // Ajoutez le bouton à votre vue (probablement à AddOperationView).
            // Exemple : view.addButton(deleteButton);
        }*/
        if(!operation.isNew()) {
            view.getTxtTitle().setText(operation.getTitle());
            try {
                view.getTxtAmount().setText(String.valueOf(operation.getAmount()));
            } catch (Exception e) {
                view.getTxtAmount().setText("0");
            }
            view.getTxtDate().setText(operation.getOperationDate().asString());

            view.comboPaidBy.setSelectedItem(operation.getInitiator().getFullName());

            view.delete.setVisible(true);
            view.save.setEnabled(false);

        }




    }



    public Operation getOperation() {
        return operation;
    }

    public Tricount getTricount() {
        return tricount;
    }

    public void delete(){
        if (askConfirmation("You're about to delete this operation. \n Do you confirm!","Delete Operation")){
            var id = operation.getId();
            operation.delete();
        }
        view.close();



    }

    public void save(String title, Double amount, LocalDate date, Integer initiator) {
        LocalDate dateNow = LocalDate.now();
        initiator = Security.getLoggedUserId();
        //System.out.println(title);
        Operation op = view.getCurrentOp();
       if (op.isNew()){
           op = new Operation(title, tricount.getId(), amount, date, initiator, LocalDateTime.now()).save();
            view.close();
            navigateTo(new ViewTricountController(tricount));

       }
       else {
            op.setTitle(title);
            op.setAmount(amount);
            op.setOperationDate(date);
            op.setInitiatorId(initiator);
            op.save();
            view.close();
           navigateTo(new ViewOperationController(operation));


       }

    }

    public void saveEdit(){
        Operation op = view.getCurrentOp();

    }



        //methode validate des inputs
    public ErrorList validate(String Title, Double Amount, LocalDate Date , CheckBoxList<Repartition> checkForWhom ) {
        var error = new ErrorList();
        if (Title.length() < 3) {
            error.add("minimun 3 chars", Operation.Fields.Title);
        }
        List<Operation> operationList = Operation.getAll();
        for (Operation op : operationList ){
            if (op.getTitle().compareTo(Title) == 0){
                error.add("title already exsits",Operation.Fields.Title);
            }
        }
        if (Amount == 0) {
            error.add("amount must be positive", Operation.Fields.Amount);
        }
        LocalDate currentDate = LocalDate.now();
        if (Date == null) {
            error.add("date invalid", Operation.Fields.CreatedAt);
        } else {
            if (dateInFuture(Date)) {
                error.add("date may not be in the future", Operation.Fields.CreatedAt);
            }
        }
        if (checkForWhom.getSelectedItem()==null){
            error.add("you must select at least one",Operation.Fields.Initiator);
        }

        return error;
    }

    //verification de la date
    public boolean dateInFuture(LocalDate Date) {
        return (Date.isAfter(LocalDate.now()));
    }
    /*_____________________________________________________________________________________________________*/

    //public void add(String title, Double amount, LocalDate date, Integer initiator) {}


    @Override
    public Window getView() {
        return view;
    }

    public void saveTemplate() {
        navigateTo(new AddTemplateController(tricount));
    }
    public List<TemplateItem> getTemplateById(int tempalteId){
        return (List<TemplateItem>) TemplateItem.getById(tempalteId);
    }








}
