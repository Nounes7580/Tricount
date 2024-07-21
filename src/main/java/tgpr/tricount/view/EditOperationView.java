package tgpr.tricount.view;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.tricount.controller.EditOperationController;
import tgpr.tricount.model.Operation;

import java.util.List;

public class EditOperationView extends DialogWindow {
    private EditOperationController controller;
    private Operation operation;
    private final Button BouttonyesdeleteOperation;
    public EditOperationView(EditOperationController controller, Operation operation) {
        super("Delete operation");
        this.controller = controller;
        this.operation = operation;
        setHints(List.of(Hint.CENTERED,Hint.MODAL));
        setCloseWindowWithEscape(true);


        Panel root = new Panel();
        setComponent(root);
        new Label("You're about to delete this operation.\nDo you confirm!").addTo(root);
        new EmptySpace().addTo(root);

        var buttons = new Panel().addTo(root).setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        for (int i = 0; i < 10; i++) {
            new EmptySpace().addTo(buttons);
        }
        BouttonyesdeleteOperation = new Button("Oui",this::delete).addTo(buttons);
        new Button("Non",this::close).addTo(buttons);


        }
    private void delete(){
        controller.delete(operation);
    }
}