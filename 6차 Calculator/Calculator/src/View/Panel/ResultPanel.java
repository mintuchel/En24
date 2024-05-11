package View.Panel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ButtonUI;
import java.awt.*;

// curNumberLabel에 지금입력한 값이 들어갈때가 있고
// 지금까지 입력한 결과값이 들어갈때가 있음

public class ResultPanel extends JPanel {

    JButton logButton;
    JLabel smallLabel;
    JLabel bigLabel;

    public ResultPanel(){
        createComponents();
        initializeResultPanel();
    }

    private void createComponents(){
        createLogButton();
        createSmallLabel();
        createBigLabel();
    }

    private void initializeResultPanel(){

        GridBagLayout grid = new GridBagLayout();
        setLayout(grid);

        GridBagConstraints gbc = new GridBagConstraints();

        addLogButton(gbc);
        addSmallLabel(gbc);
        addBigLabel(gbc);
    }

    private void createLogButton(){
        ImageIcon imgIcon = new ImageIcon("src/Images/HistoryIcon.png");
        Image scaledImage = imgIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        logButton = new JButton(scaledIcon);
        logButton.setBorderPainted(true); // 버튼 테두리 설정
        logButton.setContentAreaFilled(false); // 버튼 영역 배경 표시 설정
        logButton.setFocusable(false);
    }

    private void createSmallLabel(){
        // 크기 줄어드는거 방지용 dummyString
        smallLabel = new JLabel(" ");
        smallLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        smallLabel.setBackground(Color.WHITE);

        smallLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        smallLabel.setVerticalAlignment(SwingConstants.BOTTOM);
    }

    private void createBigLabel(){
        bigLabel = new JLabel("0");
        bigLabel.setFont(new Font("Consolas", Font.BOLD, 36));
        bigLabel.setBackground(Color.WHITE);

        bigLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    private void addLogButton(GridBagConstraints gbc){

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 0.15;

        gbc.anchor = GridBagConstraints.LINE_END;

        add(logButton, gbc);
    }

    private void addSmallLabel(GridBagConstraints gbc){

        // grid 내에서 [x][y] 위치
        gbc.gridx = 0;
        gbc.gridy = 1;
        // 컴포넌트의 디폴트 크기에 대한 폭과 높이의 소속 배율
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        // 컴포넌트 각 영역의 크기 비율
        gbc.weightx = 1.0;
        gbc.weighty = 0.25;

        // 영역을 채우기 위한 속성 지정
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_END;

        add(smallLabel, gbc);
    }

    private void addBigLabel(GridBagConstraints gbc){

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.6;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_END;

        add(bigLabel, gbc);
    }

    public JLabel getSmallLabel(){
        return smallLabel;
    }

    public JLabel getBigLabel() {
        return bigLabel;
    }

    public JButton getLogButton(){
        return logButton;
    }
}
