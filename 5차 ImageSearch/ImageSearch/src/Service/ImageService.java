package Service;

import Model.DAO.ImageDAO;
import Model.VO.ImageListVO;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

public class ImageService {

    public ArrayList<JLabel> GetKeywordImages(String keyWord){

        // 싱글톤 DAO 받기
        ImageDAO imageDAO = ImageDAO.GetInstance();
        // DAO 통해서 검색결과 받기
        ImageListVO imageListVO = imageDAO.GetImageURLs(keyWord);
        ArrayList<String> imageURLList = imageListVO.GetImageURLList();

        // 반환할 ArrayList 생성
        ArrayList<JLabel> retList = new ArrayList<>();

        URL url;
        Image curImage;

        for (int i = 0; i < 30; i++) {
            try {
                url = new URL(imageURLList.get(i));
                // URL 통해서 Image 가져오기
                curImage = ImageIO.read(url);
                curImage = curImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);

                JLabel curLabel = new JLabel(new ImageIcon(curImage));
                curLabel.setSize(800,50);
                curLabel.setBorder(new LineBorder(Color.BLACK, 5,false));
                retList.add(curLabel);

                System.out.println("[IMAGE SERVICE] DONE");
            } catch (Exception e) {
                System.out.println("[IMAGE SERVICE] FAILED");
            }
        }

        return retList;
    }
}
