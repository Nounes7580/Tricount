package tgpr.tricount.controller;



import com.googlecode.lanterna.gui2.Window;

import tgpr.framework.Model;

import tgpr.tricount.model.Tricount;

import tgpr.tricount.model.Security;



import com.googlecode.lanterna.gui2.Window;

import tgpr.tricount.model.User;
import com.googlecode.lanterna.gui2.Window;
import tgpr.tricount.view.TestView;
import tgpr.framework.Controller;
import tgpr.tricount.model.Tricount;

import java.util.List;

public class TestController extends Controller {
    private final TestView view = new TestView(this);

    private Tricount tricount;

    private User user1;

    @Override
    public Window getView() {
        return view ;
    }
    public void logout() {
        Security.logout();
        navigateTo(new LoginController());
    }
    public void exit() {
        System.exit(0);
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public List<User> getSubscribers(Tricount tricount){
        return tricount.getParticipants();
    }

    public List<User> getUsers() {
        return (List<User>) User.getAll();
    }

    public void setTricount(Tricount tricount) {
        this.tricount = tricount;
    }

    public void delete() {
        if (askConfirmation("You are about to delete this tricount. Please confirm.", "Delete the tricount")) {
            tricount.delete();
            view.close();
            tricount = null;
        }
    }

}

