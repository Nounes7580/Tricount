package tgpr.tricount.view;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.framework.ColumnSpec;
import tgpr.framework.LocaleFormats;
import tgpr.framework.ObjectTable;
import tgpr.tricount.controller.ViewTricountController;
import tgpr.tricount.model.Operation;
import tgpr.tricount.model.Security;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewTricountView extends DialogWindow {
    private final ViewTricountController controller;
    private Tricount tricount;
    private Panel operation;
    private ObjectTable<Operation> operationTable;
    private Label totalExpenses;
    private Label myBalance;
    private Label  expense;

    public ViewTricountView (ViewTricountController controller, Tricount tricount){
        super("View Tricount Detail");
        this.controller = controller;
        this.tricount = tricount;
        Security.login(User.getByKey(1));
        Panel root = Panel.verticalPanel();
        setComponent(root);
        Panel grid = Panel.gridPanel(2);

        new Label("Title:").addTo(grid);
        Label titleLabel = new Label(tricount.getTitle());
        titleLabel.addTo(grid);

        new Label("Description:").addTo(grid);
        String description = tricount.getDescription() == null ? "no description " : tricount.getDescription();
        Label descriptionLabel = new Label(description);
        descriptionLabel.addTo(grid);

        new Label("Created by:").addTo(grid);
        String createdBy = tricount.getCreator().getFullName();
        Label createdByLabel = new Label(createdBy);
        createdByLabel.addTo(grid);

        new Label("Date:").addTo(grid);
        LocalDateTime createdAt = tricount.getCreatedAt();
        final DateTimeFormatter[] format = {DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")};
        String formattedDate = createdAt.format(format[0]);

        Label dateLabel = new Label(formattedDate);
        dateLabel.addTo(grid);

        new Label("Total Expenses:").addTo(grid);
        totalExpenses = new Label(String.format("%.2f",tricount.getTotalOfTricounts())+"€").addTo(grid);
        totalExpenses.addTo(grid);

        new Label("My Expenses:").addTo(grid);
         expense = new Label (String.format("%.2f",tricount.getMyExpense(Security.getLoggedUser()))+"€").addTo(grid);

        new Label("My Balance:").addTo(grid);
        myBalance = new Label(String.format("%.2f",tricount.getBalance(Security.getLoggedUser()))+"€").addTo(grid);
        root.addComponent(grid);

        var panel = operation = new Panel().fill();

        operationTable = new ObjectTable<>(
                new ColumnSpec<>("Operation", Operation::getTitle).setWidth(30)
                        .setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Amount", operation -> {
                    double amount = operation.getAmount();
                    return String.format("%8s", String.format("%.2f €", amount));
                }),
                new ColumnSpec<>("Paid by", Operation::getInitiator),
                new ColumnSpec<>("Date", operation -> {
                    LocalDate operationDate = operation.getOperationDate();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    return formatter.format(operationDate);
                })
        ).addTo(panel);
        operationTable.setSelectAction(this::viewExpense);
        operation.addTo(root);
        Panel buttonPanel = Panel.horizontalPanel(2);

        Button balanceButton = new Button("Balance", controller::balance);
        Button newExpenseButton = new Button("New Expense", controller::expense);
        Button editTricountButton = new Button("Edit Tricount", controller::edit);
        //Button closeButton = new Button("Close",this::close);
        Button closeButton = new Button("Close",controller::close);

        buttonPanel.addComponent(balanceButton);
        buttonPanel.addComponent(newExpenseButton);
        buttonPanel.addComponent(editTricountButton);
        buttonPanel.addComponent(closeButton);

        root.addComponent(buttonPanel);
        reload();
    }

    private void reload(){
        List<Operation> listOperation = controller.getOperations();
        operation.removeAllComponents();

        if(!listOperation.isEmpty()){
            operation.addComponent(operationTable);
            operationTable.clear();;
            operationTable.add(listOperation);
        }
    }
    public void viewExpense(){
        var op = operationTable.getSelected();
        controller.viewExpense(op);
    }

}

