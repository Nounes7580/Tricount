package tgpr.tricount.controller;
import com.googlecode.lanterna.gui2.Window;
import tgpr.tricount.model.*;
import tgpr.tricount.view.ViewOperationView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;
import java.util.List;
public class ViewOperationController extends Controller {
    private Operation operation;
    private ViewOperationView view;
    private Tricount tricount = Tricount.getByKey(4);
    private EditTricountController editTricountController;
    public ViewOperationController(Operation operation) {
        this.operation = operation;
        view = new ViewOperationView(this,operation);
    }
    @Override
    public Window getView() {
        return view;
    }
    public Operation getOperation(){return operation;}

    public int getTotalWeight() {
        List<Repartition> listRep = operation.getRepartitions();
        int totalWeight = 0;                                         // DANS LE CONTROLLER
        for (int i = 0; i < listRep.size(); i++) {
            totalWeight += listRep.get(i).getWeight();
        }
        return totalWeight;
    }
    public double getAmountPart() {
        double amountPart = operation.getAmount() / getTotalWeight();
        return amountPart;
    }

    public Operation down() {
        List<Operation> listOp = Operation.getAll();
        int index = listOp.indexOf(operation);
        if (index < listOp.size() - 1) {
            index = index + 1;
        }
        operation = listOp.get(index);
      //  navigateTo(new ViewOperationController(operation));
        return operation;
    }

    public Operation up() {
        List<Operation> listOp = Operation.getAll();
        int index = listOp.indexOf(operation);
        if (index > 0) {
            index = index - 1;
        }
        operation = listOp.get(index);
       // navigateTo(new ViewOperationController(operation));
        return operation;
    }

    public void edit() {
        view.close();
        navigateTo(new AddOperationController(tricount,operation,editTricountController));
    }

    public boolean first() {
        List<Operation> listOp = Operation.getAll();
        if (listOp.indexOf(operation) == 0){
            return true;
        }
        return false;
    }

    public boolean last() {
        List<Operation> listOp = Operation.getAll();
        if (listOp.indexOf(operation) == listOp.size()-1){
            return true;
        }
        return false;
    }
}

