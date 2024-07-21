package tgpr.tricount.model;

import com.googlecode.lanterna.gui2.Window;
import tgpr.framework.Controller;
import tgpr.framework.Error;
import tgpr.framework.Tools;
import tgpr.tricount.model.User;

import java.util.regex.Pattern;

public abstract class UserValidator {
    public static Error isValidMail(String mail) {
        if (mail == null || mail.isBlank()) {
            return new Error("Mail required", User.Fields.Mail);
        }

        if (!Pattern.matches("[a-zA-Z0-9+_.-]{3,}@[a-zA-Z0-9.-]{4,}\\.[a-zA-Z]{2,}", mail)) {
            return new Error("invalid mail", User.Fields.Mail);
        }

        return Error.NOERROR;
    }
    public static Error isValidPassword(String password) {
        if (password == null || password.isBlank())
            return new Error("password required", User.Fields.Password);
        if (!Pattern.matches("[a-zA-Z0-9,!;\\-@]{8,}", password))
            if (!Pattern.matches("[a-zA-Z0-9+_.-]+@(.+)$", password) )
                return new Error("invalid password", User.Fields.Password);
        return Error.NOERROR;
    }


    public static boolean isItsPassword(String password){
        User user = new User();
        return user.getHashedPassword().equals(Tools.hash(password));
    }

    public static Error isValidAvailableMail(String mail) {
        var error = isValidMail(mail);
        if (error != Error.NOERROR)
            return error;
        if (User.getByMail(mail) != null)
            return new Error("not available", User.Fields.Mail);
        return Error.NOERROR;
    }

    public static Error isValidPasswordForNewUser(String password) {
        if (password == null || password.isBlank())
            return new Error("password required", User.Fields.Password);

        if (password.length() < 8) {
            return new Error("at least 8 chars", User.Fields.Password);
        }

        if (!Pattern.compile(".*[A-Z].*").matcher(password).matches()) {
            return new Error("missing an uppercase letter", User.Fields.Password);
        }

        if (!Pattern.compile(".*\\d.*").matcher(password).matches()) {
            return new Error("missing a digit", User.Fields.Password);
        }

        if (!Pattern.compile(".*[!@#$%^&*(),;.?\":{}|<>].*").matcher(password).matches()) {
            return new Error("missing a special character", User.Fields.Password);
        }

        return Error.NOERROR;
    }

    public static Error isValidIban(String iban) {
        // Format de l'IBAN : BE99 9999 9999 9999
        if (!Pattern.matches("^[A-Z]{2}\\d{2}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?$", iban)) {
            return new Error("bad format (BE99 9999 9999 9999)", User.Fields.Iban);
        }

        return Error.NOERROR;
    }


}
