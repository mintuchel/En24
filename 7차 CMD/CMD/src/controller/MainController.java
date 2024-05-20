package controller;

import model.VO.InputVO;
import model.VO.OutputVO;
import service.MainService;
import view.MainView;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

public class MainController {

    private String rootDirectory;
    private FileSystem fileSystem;
    private String separator;

    private String curDirectory;

    private MainView mainView;
    private MainService mainService;
    
    private String command;
    private String parameters;

    public MainController(){
        initializeCMD();

        mainView = new MainView();
        mainService = new MainService(fileSystem, rootDirectory);
    }

    // OS에 따른 FileSystem, rootDirectory, seperator 지정해주기
    private void initializeCMD(){
        fileSystem = FileSystems.getDefault();

        separator = fileSystem.getSeparator();

        Iterable<Path> rootDirectories = fileSystem.getRootDirectories();
        
        Iterator<Path> iterator = rootDirectories.iterator();
        Path directory = iterator.next();
        rootDirectory = directory.toString();
        //System.out.println(rootDirectory);
        //System.out.println(separator);

        // 현재 directory rootDirectory 로 최신화
        curDirectory = rootDirectory;
    }

    // file folder 명에 공백 있을 경우 "" 로 감싸줘야함!
    public void run() throws IOException {
        boolean isCmdRunning = true;

        while(isCmdRunning){
            InputVO input = mainView.getInput(curDirectory);

            command = input.getCommand();
            parameters = input.getParameters();

            switch(command){
                case "cd":
                    handleCD(parameters);
                    break;
                case "dir":
                    handleDIR(parameters);
                    break;
                case "copy":
                    handleCOPY(parameters);
                    break;
                case "move":
                    handleMOVE(parameters);
                    break;
                case "help":
                    handleHELP();
                    break;
                case "cls":
                    handleCLS();
                    break;
                case "exit":
                    isCmdRunning = false;
                    break;
                default:
                    break;
            }
        }
    }

    private void handleCD(String parameters) throws IOException {
        // 바뀔 directory 명이랑 예외가 발생했을 시에 따른 OutputVO 를 pair 객체로 받기
        // pair 객체로 return 하는 놈은 CD 밖에 없음. 얘만 예외임
        Map.Entry<String, OutputVO> cdResult = mainService.changeDirectory(curDirectory, parameters);

        // curDirectory 최신화
        String changedDirectory = cdResult.getKey();
        curDirectory = changedDirectory;
        
        // OutputVO view에 출력
        OutputVO exceptionMessageVO = cdResult.getValue();
        mainView.printReturnedResult(exceptionMessageVO);
    }

    private void handleDIR(String parameters) throws IOException {
        OutputVO output = mainService.listFiles(curDirectory, parameters);
        mainView.printReturnedResult(output);
    }

    private void handleCOPY(String parameters) throws IOException {
        OutputVO output = mainService.copyFile(curDirectory, parameters);
        mainView.printReturnedResult(output);
    }

    private void handleMOVE(String parameters) throws IOException {
        OutputVO output = mainService.moveFile(curDirectory, parameters);
        mainView.printReturnedResult(output);
    }

    private void handleHELP(){ mainView.showHelp(); }

    private void handleCLS(){
        mainView.clearPrompt();
    }
}