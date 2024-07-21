package tgpr.tricount.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.framework.*;
import tgpr.tricount.model.*;
import tgpr.tricount.view.AddTemplateView;

import static tgpr.framework.Controller.showError;

public class AddTemplateController extends Controller {

    private Template template;

    private final Tricount tricount;
    
    public AddTemplateController(Tricount tricount) {
        super();
        this.tricount = tricount;
    }
    public void setTemplate(Template template) {
        this.template = template;
    }
    public void create(String title) {
        var errors = validate(title);
        if (errors.isEmpty()) {
            if (template == null) {
                new Template(title, tricount.getId()).save();
            } else {
                template.setTitle(title);
                template.save();
            }
        } else {
            showErrors(errors);
        }
        //refresh();
    }

    public ErrorList validate(String title) {
        var errors = new ErrorList();
        if (title.length() < 3) {
            errors.add("minimun 3 chars", Template.Fields.Title);
        }
        //TODO if pour un titre unique
        for (Template template : Template.getAll()){
            if (template.getTitle().compareTo(title) == 0) {
                errors.add("title already exists", Template.Fields.Title);
            }
        }

        return errors;
    }

    @Override
    public Window getView() {
        return new AddTemplateView(this);
    }



    public void refresh() {
        tgpr.framework.Controller.navigateTo(new View_template_controller(tricount));
    }
    public void modifyTemplateName(Template template, String newTitle) {
        var errors = validate(newTitle);
        if (errors.isEmpty() && template != null) {
            template.setTitle(newTitle);
            template.save();
        } else {
            showErrors(errors);
        }
        refresh();
    }
}

