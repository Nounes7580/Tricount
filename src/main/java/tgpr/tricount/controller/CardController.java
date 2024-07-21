package tgpr.tricount.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.framework.Controller;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.view.CardView;

public class CardController extends Controller {
    Tricount tricount;
    public CardController(Tricount tricount) {
        this.tricount = tricount;
    }
    public Tricount openTricount(){
        return null;
    }

    @Override
    public Window getView() {
        return new CardView(tricount);
    }
}
