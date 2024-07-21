package tgpr.tricount.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;
import tgpr.tricount.model.Template;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.view.AddTricountView;
import tgpr.tricount.model.User;

public class add_tricountController extends Controller {


    public AddTricountView view;

    private int idUser;

    public add_tricountController(int idUser) {
        this.idUser = idUser;
        view = new AddTricountView(this, idUser);
    }


    public ErrorList validate(String title,String description) {
        var errors = new ErrorList();
        if (title == null || title.length() < 3) {
            errors.add("minimum 3 characters for title", Tricount.Fields.Title);
        }

        if (description == null || description.length() < 3) {
            errors.add("minimum 3 characters for description", Tricount.Fields.Description);
        }


        return errors;

    }
    public void add_tricount(Tricount tricount) {

        var errors = validate(tricount.getTitle(),tricount.getTitle());
        if (errors.isEmpty()) {
           tricount.save();

        }
        else
            showErrors(errors);
    }


    @Override
    public Window getView() {
        return view;


    }


}
