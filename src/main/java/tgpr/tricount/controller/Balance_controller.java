package tgpr.tricount.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.framework.Controller;
import tgpr.tricount.model.Security;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.model.User;
import tgpr.tricount.view.ViewBalanceView;
import tgpr.tricount.view.ViewTricountView;

public class Balance_controller extends Controller {
    private ViewBalanceView view;
    private Tricount tricount;

    public Balance_controller(Tricount tricount) {
        this.tricount = tricount;
    }
    
    @Override
    public Window getView() {
        User user = User.getByKey(Security.getLoggedUserId());
        return new ViewBalanceView(this,this.tricount,user);
    }
}
