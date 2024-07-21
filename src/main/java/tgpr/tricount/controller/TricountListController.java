package tgpr.tricount.controller;
import com.googlecode.lanterna.gui2.Window;
import tgpr.tricount.model.*;
import tgpr.tricount.view.ProfilView;
import tgpr.tricount.view.TricountListView;
import tgpr.framework.Controller;
import java.util.List;


public class TricountListController extends Controller {
    @Override
    public Window getView() {
        return new TricountListView(this);
    }

    public void logout() {
        Security.logout();
        navigateTo(new LoginController());
    }

    public void exit() {
        System.exit(0);
    }
    public static List<Tricount> getAllCard(int i, int id, String search) {
        return Tricount.getAllTricounts(i,id,search);
    }

    public static int getCount(int id) {
        return Tricount.getCount(id);
    }
    public void openTricount (Tricount tricount){
        Controller.navigateTo(new ViewTricountController(tricount));

    }
    public void addTricount() {
        navigateTo(new add_tricountController(Security.getLoggedUser().getId()));
    }

    public void viewProfile() {
        navigateTo(new ProfilController());
    }

}

