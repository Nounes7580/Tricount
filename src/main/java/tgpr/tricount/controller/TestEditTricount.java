package tgpr.tricount.controller;


import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import tgpr.framework.Controller;
import tgpr.tricount.model.Subscription;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.view.TestEditTricountView;

public class TestEditTricount extends Controller{

    private final Subscription sub;

    private TestEditTricountView view ;

    private Tricount tricount;

    public TestEditTricount(Subscription sub) {
        this.sub = sub;
        view = new TestEditTricountView(this);
    }

    public void deleteUser(String user) {
        KeyStroke key=null;
        if (!canDelete(user)) {
            view.errMessage();
        }
        else {
            if (key.getKeyType()== KeyType.Enter){
                sub.delete();
            }
        }
    }

    private boolean canDelete(String user) {
        boolean del = true;
        String creator = tricount.getCreator().getFullName();
        if (user.contains(creator)) {
            del = false;
        }
        return del;
    }



    @Override
    public Window getView() {return view;}




}