package Controller.Observer;

import Controller.Controller;
import View.MainView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// 0~9 입력 처리
public class NumberObserver implements ActionListener {
    MainView mainView;
    Controller controller;

    public NumberObserver(MainView mainView, Controller controller){
        this.mainView = mainView;
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
