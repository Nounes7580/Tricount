package tgpr.tricount.controller;

import tgpr.framework.Controller;
import com.googlecode.lanterna.gui2.Window;
import tgpr.framework.Controller;
import tgpr.tricount.model.Template;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.view.Edit_template;
import tgpr.tricount.view.View_template;

public class View_template_controller extends Controller {
        private final View_template view;
        private final Tricount tricount;
        private boolean templateModified;

        public View_template_controller(Tricount tricount) {
            this.tricount = tricount;
            this.view = new View_template(this.toString(), this);
        }

    public void newTemplate() {
        navigateTo(new AddTemplateController(tricount));
        navigateTo(new View_template_controller(tricount));
    }

    public void editTitle(Template template, String newTitle) {
            template.setTitle(newTitle);
            template.save(); //
        }
        public Tricount getTricount() {
            return tricount;
        }
        public void delete(Template template){
            if (askConfirmation("You're about to delete the template: "+template.getTitle()+"\n"+
                                    "Do you confirm!", "Delete Template"))
            {
                template.delete();
                view.close();
                navigateTo(new View_template_controller(tricount));
            }
        }
        @Override
        public Window getView() {
            return view;
        }
    }
