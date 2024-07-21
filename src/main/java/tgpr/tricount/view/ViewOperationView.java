package tgpr.tricount.view;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.framework.*;
import tgpr.tricount.controller.ViewOperationController;
import tgpr.tricount.model.*;
import com.googlecode.lanterna.gui2.TextBox;
import java.util.List;

public class ViewOperationView extends DialogWindow {
    private final ViewOperationController controller;
    private Operation operation;
    private final Label lblTitle = new Label("");
    private final Label lblAmount = new Label("");
    private final Label lblDate = new Label("");
    private final Label lblPaidBy = new Label("");
    private final TextBox txtBody = new TextBox("",TextBox.Style.MULTI_LINE);
    private Button btnUp = new Button("Up");
    private Button btnDown = new Button("Down");

    private ObjectTable<Repartition> table;
    public ViewOperationView(ViewOperationController controller, Operation operation) {
        super("View Expense Detail");
        this.controller = controller;
        this.operation = operation;
        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        Panel root = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL).setSpacing(1));
        setComponent(root);
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(50,15));
        setFixedSize(new TerminalSize(50,15));
        txtBody.takeFocus();
        createFields().addTo(root);
        refresh();
        createBody().addTo(root);
        createButtonsPanel().addTo(root);
    }
    private Panel createFields(){
        Panel panel = Panel.gridPanel(2, Margin.of(1, 1, 1, 0));

        new Label("Title:").addTo(panel);
        lblTitle.addTo(panel).addStyle(SGR.BOLD);

        new Label("Amount:").addTo(panel);
        lblAmount.addTo(panel).addStyle(SGR.BOLD);

        new Label("Date:").addTo(panel);
        lblDate.addTo(panel).addStyle(SGR.BOLD);

        new Label("Paid by:").addTo(panel);
        lblPaidBy.addTo(panel).addStyle(SGR.BOLD);
        new Label("For whom:").addTo(panel);
        lblPaidBy.addTo(panel).addStyle(SGR.BOLD);
        return panel;
    }

    private Panel createButtonsPanel(){
        var panel = new Panel()
                .setLayoutManager(new LinearLayout((Direction.HORIZONTAL)))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.End));
        btnUp = new Button("Up", this::up);
        btnDown = new Button("Down", this::down);
        btnUp.addTo(panel);
        btnDown.addTo(panel);
        new Button("Edit", this::edit).addTo(panel);
        new Button("Close", this::close).addTo(panel);
        btnUp.setEnabled(!controller.first());
        btnDown.setEnabled(!controller.last());
        return panel;
    }

    private void up() {
        operation = controller.up();
        reloadData();
        refresh();
    }
    private void down() {
        operation = controller.down();
        reloadData();
        refresh();
    }
    private void edit() {
        controller.edit();
    }

    private void refresh() {
        if (operation != null) {
            lblTitle.setText(operation.getTitle());
            lblAmount.setText(String.valueOf(operation.getAmount()) +" €");
            lblDate.setText(operation.getOperationDate().toString());
            lblPaidBy.setText(operation.getInitiator().getFullName());
            // refresh aussi les boutons
            btnUp.setEnabled(!controller.first());
            btnDown.setEnabled(!controller.last());
        }
    }
    private Panel createBody() {
        Panel panel = Panel.gridPanel(1);
        createContent().addTo(panel);
        return panel;
    }

    private Panel createContent() {
        Panel panel = Panel.verticalPanel(1);
        createTable().addTo(panel);
        return panel;
    }

    private Component createTable() {
        table = new ObjectTable<>(
                new ColumnSpec<Repartition>("Participants", r -> r.getUser().getFullName()),
                new ColumnSpec<Repartition>("Weight", r -> r.getWeight()),
                new ColumnSpec<Repartition>("Amount", r -> formatAmount(r.getWeight() * controller.getAmountPart()))
                );

        table.sizeTo(ViewManager.getTerminalColumns(), 15);
        reloadData();

        return table;
    }
    private String formatAmount(double amount) {
        return String.format("%.2f €", amount);
    }


    public void reloadData() {
        table.clear();
        var repartition = operation.getRepartitions();
        for (Repartition r: repartition) {
                table.add(r);
        }
    }
}
