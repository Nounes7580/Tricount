package tgpr.tricount.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.framework.ErrorList;
import tgpr.tricount.model.Template;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.model.User;
import java.util.List;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import tgpr.tricount.model.Subscription;
import tgpr.tricount.view.EditTricountView;
import tgpr.framework.Controller;

public class EditTricountController extends Controller {
    private Tricount tricount;
    private List<Subscription> subscriptions;
    private final EditTricountView view;
    private  Subscription sub;
    private User user1;



    public EditTricountController(Tricount tricount) {
        this.tricount = tricount;
        view = new EditTricountView(this,tricount);
    }
    public boolean isDeleteButtonVisible() {
        // Ajoutez la logique nécessaire ici pour déterminer si le bouton "Delete" doit être affiché.
        // Par exemple, vous pourriez vérifier une condition ou un état spécifique.
        return true; // Changez cela en fonction de votre logique.
    }

    public boolean deleteTricount(Tricount tricount) {
        return true;
    }
    public void setUser1(User user1) {
        this.user1 = user1;
    }
    public List<User> getUsers() {
        return (List<User>) User.getAll();
    }

    public List<User> getSubscribers(Tricount tricount){
        return tricount.getParticipants();
    }
    public void setTricount(Tricount tricount) {
        this.tricount = tricount;
    }



    @Override
    public Window getView() {
        return view;
    }

    public Tricount getTricount(){return tricount;}


    public void save(String title, String description, List<Subscription> listSub) {
        tricount.setTitle(title);
        tricount.setDescription(description);
        for (Subscription sub : listSub) {
            sub.save();
            System.out.println("THIS IS THE SUBSCRIPTION SAVED" +sub);
        }
        tricount.save();
        tricount.reload();
    }
    public boolean canDelete(User user) {
        boolean canDelete = true;
        User creator = tricount.getCreator();
        if (user == creator) {
            canDelete = false;
        }
        return canDelete;
    }

    public boolean deleteUser(User user) {
        //System.out.println("USER ID" +user.getId());
        //System.out.println("TRICOUNT ID" +tricount.getId());
        Subscription subToDel =  Subscription.getByKey(tricount.getId(), user.getId());
        System.out.println(subToDel);

        //   KeyStroke key = null;
        if (!canDelete(user)) {
            view.errorMessage();
            return false;
        } else {
            if (subToDel !=null) {
                subToDel.delete();
                view.deleteUser();
                return true;
            }
        }
        return false;
    }

    public void deleteTricount() {
        if (askConfirmation("You are about to delete this tricount. Please confirm.", "Delete the tricount")) {
            var id = tricount.getId();
            tricount.delete();
            view.close();
            navigateTo(new TricountListController());
            if(Tricount.getByKey(id) == null){
                System.out.println("test"); view.close();
            }
            tricount = null;

        }
    }

    public ErrorList validateTitle(String title) {
        var errors = new ErrorList();
        if (title.length() < 3) {
            errors.add("minimun 3 chars", Tricount.Fields.Title);
        }
        for (Tricount tr : Tricount.getAll()){
            if (tr.getTitle().compareTo(title) == 0 && tricount.getId() !=tr.getId()) {
                errors.add("title already exists", Tricount.Fields.Title);
            }
        }

        return errors;
    }
    public ErrorList validateDescription(String description) {
        var errors = new ErrorList();
        if (description.length() < 3) {
            errors.add("minimun 3 chars", Tricount.Fields.Description);
        }
        if (description ==""){
            errors = new ErrorList();
        }
        return errors;
    }

    public void templates() {
        navigateTo(new View_template_controller(tricount));
    }
}
