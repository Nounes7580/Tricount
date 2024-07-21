package tgpr.tricount.controller;

import tgpr.framework.Controller;
import com.googlecode.lanterna.gui2.Window;
import tgpr.framework.Controller;
import tgpr.tricount.model.Template;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.view.Edit_template;
public class Edit_template_Controller extends Controller{
    private final Edit_template view;
    private final Tricount tricount;
    private boolean templateModified;

    public Edit_template_Controller(Tricount tricount) {
        this.tricount = tricount;
        this.view = new Edit_template(this.toString(), this);
    }
    public void editTitle(Template template, String newTitle) {
        template.setTitle(newTitle);
        template.save(); //
    }
    public Tricount getTricount() {
        return tricount;
    }
    @Override
    public Window getView() {
        return view;
    }
}
