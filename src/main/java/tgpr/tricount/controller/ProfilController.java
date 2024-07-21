package tgpr.tricount.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.tricount.view.ProfilView;



public class ProfilController extends tgpr.framework.Controller {
    private  ProfilView view = new ProfilView(this);


    @Override
    public Window getView() {
        return view;
    }
    public void close1(){
        navigateTo(new TricountListController());
    }
}