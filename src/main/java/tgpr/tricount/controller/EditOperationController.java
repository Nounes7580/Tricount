package tgpr.tricount.controller;
import com.googlecode.lanterna.gui2.Window;
import tgpr.framework.Controller;
import tgpr.tricount.model.*;
import tgpr.tricount.model.Operation;
import tgpr.tricount.view.EditOperationView;

public class EditOperationController extends Controller{
    private final Operation operation;
    private final EditOperationView view ;
    public EditOperationController(Operation operation) {
        this.operation = operation;
        view = new EditOperationView(this, operation);
    }


    public void delete(Operation operation) {
            operation.delete();
            view.close();
    }

    @Override
    public Window getView() {
        return view;
    }

    public Operation getOperation(){
        return operation;
    }
}