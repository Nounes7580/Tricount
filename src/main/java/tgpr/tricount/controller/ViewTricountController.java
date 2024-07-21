package tgpr.tricount.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.framework.Controller;
import tgpr.tricount.model.Operation;
import tgpr.tricount.model.Security;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.view.ViewTricountView;

import java.util.List;

public class ViewTricountController extends Controller {
    private final Tricount tricount;
    private final ViewTricountView view;
    private List<Operation> listOperation;

    private Operation operation;


    public ViewTricountController(Tricount tricount) {
        this.tricount = tricount;
        this.view = new ViewTricountView(this,tricount);
    }


    @Override
    public Window getView() {
        return view;
    }
    public String getTitle(){
        return tricount.getTitle();
    }
    public List<Operation> getOperations() {
        return tricount.getOperations();
    }
    public void balance(){
    navigateTo(new Balance_controller(tricount));
    }
    public void expense() {
        navigateTo(new AddOperationController(tricount));
    }
    public void edit(){
        var id = tricount.getId();
        navigateTo(new EditTricountController(tricount));
        if(Tricount.getByKey(id) == null){
            view.close();
        }
        else{
            view.close();
            navigateTo(new ViewTricountController(Tricount.getByKey(id)));
        }
    }
    public void viewExpense(Operation operation){
        var controller = new ViewOperationController(operation);
        navigateTo(controller);
    }
    public void close(){
        view.close();
        //navigateTo(new TricountListController());
    }

}
