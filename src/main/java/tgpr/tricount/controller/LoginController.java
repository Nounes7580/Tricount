/*package tgpr.tricount.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.tricount.TricountApp;
import tgpr.tricount.model.User;
import tgpr.tricount.model.Security;
import tgpr.tricount.view.LoginView;
import tgpr.framework.Controller;
import tgpr.framework.Error;
import tgpr.framework.ErrorList;
import tgpr.framework.Model;

import java.util.List;

public class LoginController extends Controller {
    public void exit() {
        System.exit(0);
    }

    public List<Error> login(String mail, String password) {
        var errors = new ErrorList();
        errors.add(UserValidator.isValidMail(mail));
        errors.add(UserValidator.isValidPassword(password));

        if (errors.isEmpty()) {
            var user = User.checkCredentials(mail, password);
            if (user != null) {
                Security.login(user);
                navigateTo(new LoginController());

            } else
                showError(new Error("invalid credentials"));
        } else
            showErrors(errors);

        return errors;
    }

    public void seedData() {
        Model.seedData(TricountApp.DATABASE_SCRIPT_FILE);
    }

    @Override
    public Window getView() {
        return new LoginView(this);
    }

    public void signup() {
    }
}*/
package tgpr.tricount.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.tricount.TricountApp;

import tgpr.tricount.model.*;

import tgpr.tricount.model.User;
import tgpr.tricount.model.UserValidator;

import tgpr.tricount.view.LoginView;
import tgpr.framework.Controller;
import tgpr.framework.Error;
import tgpr.framework.ErrorList;
import tgpr.framework.Model;
import tgpr.framework.Tools;

import java.util.List;

public class LoginController extends Controller {
    public LoginController() {
    }

    public void exit(){
        System.exit(0);
    }


    public List<Error> login(String mail, String password){
        var errors = new ErrorList();
        errors.add(UserValidator.isValidMail(mail));
        errors.add(UserValidator.isValidPassword(password));
        if (errors.isEmpty()){
            var user = User.checkCredentials(mail,password);
            if(user != null){
                Security.login(user);

                navigateTo(new TricountListController());
               // navigateTo(new TestController());

            } else
                showError(new Error("invalid credentials"));
        }else showErrors(errors);

        return errors;
    }


    public void seedData(){
        Model.seedData(TricountApp.DATABASE_SCRIPT_FILE);
    }

    @Override
    public Window getView() {return new LoginView(this);}

    public ErrorList validate(String mail, String password) {
        var errors = new ErrorList();
        errors.add(UserValidator.isValidMail(mail));
        errors.add(UserValidator.isValidPassword(password));



        return errors;
    }
    public void signup(){
        navigateTo(new SignupController());
    }



}


