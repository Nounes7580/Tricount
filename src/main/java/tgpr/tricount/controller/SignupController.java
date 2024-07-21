package tgpr.tricount.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.framework.ErrorList;
import tgpr.framework.Tools;
import tgpr.tricount.model.Security;
import tgpr.tricount.model.Template;
import tgpr.tricount.model.User;
import tgpr.tricount.model.UserValidator;
import tgpr.tricount.view.AddTemplateView;
import tgpr.tricount.view.SignupView;

public class SignupController extends tgpr.framework.Controller{

    private User user;
    public SignupController(){

    }

    public ErrorList validate(String mail, String fullName, String iban, String password, String confirmPassword) {
        var errors = new ErrorList();

        //vérifie l'unicité de mail
        //errors.add(UserValidator.isValidAvailableMail(mail));
        for (User user : User.getAll()){
            if (user.getMail().compareTo(mail) == 0) {
                errors.add("not available", User.Fields.Mail);
            }
        }
        //verifie le mail
        errors.add(UserValidator.isValidMail(mail));

        if (fullName.length() < 3) {
            errors.add("minimun 3 chars", User.Fields.FullName);
        }
        if (fullName.isBlank() || fullName == null)
            errors.add("name required", User.Fields.FullName);
        //verifie l'unicite du nom
        for (User user : User.getAll()){
            if (user.getFullName().compareToIgnoreCase(fullName) == 0) {
                errors.add("not available", User.Fields.FullName);
            }
        }



        if (!iban.isBlank())
            errors.add(UserValidator.isValidIban(iban));

        //verifie le password
        errors.add(UserValidator.isValidPasswordForNewUser(password));

        if (!password.equals(confirmPassword))
            errors.add("must match password", User.Fields.ConfirmPassword);

        if (errors.isEmpty()) {

            var hashedPassword = Tools.hash(password);
            user = new User(mail, hashedPassword, fullName, User.Role.User,iban).save();
        }
        return errors;
    }
    private boolean isvalidIban(){
        return true;
    }
    public void signup(){
        Security.login(user);
        navigateTo(new TricountListController());
    }
    public void close(){
        navigateTo(new LoginController());
    }
    @Override
    public Window getView() {
        return new SignupView(this);
    }
}
