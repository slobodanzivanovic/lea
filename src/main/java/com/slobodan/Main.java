package com.slobodan;

import com.slobodan.controller.PasswordController;
import com.slobodan.model.PasswordModel;
import com.slobodan.view.PasswordView;

public class Main {
    public static void main(String[] args) {
        PasswordModel model = new PasswordModel();
        PasswordView view = new PasswordView();
        PasswordController controller = new PasswordController(model, view);

        controller.addPassword("gmail", "jakasifra123");
        controller.addPassword("facebook", "slobodanz123");
        controller.getPasswords();
        controller.copyPassword("gmail");
        controller.startApplication();
    }
}