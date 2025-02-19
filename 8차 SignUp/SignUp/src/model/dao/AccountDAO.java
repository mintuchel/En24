package model.dao;

import constant.Querys;
import model.AppConfig;
import model.DBConnector;
import model.dto.AccountDTO;
import model.dto.LoginDTO;
import model.dto.ValueDTO;
import model.dto.UpdateInfoDTO;

import java.sql.*;

public class AccountDAO {

    public static AccountDAO instance;

    private DBConnector dbConnector;

    private PreparedStatement preparedStatement; // 동적쿼리문. ? 있는것들
    private ResultSet resultSet; // 결과 반환받는 자료형
    
    // singleton 으로 해놔 conn 변수가 딱 한번만 초기화되게 하자
    private AccountDAO(){

        dbConnector = new DBConnector();

        try {
            // dynamically loads JDBC driver class from certain URL
            // JDBC driver 는 default 로 로딩되는게 아니라 runtime 에 로딩되서 그럼
            // triggers the static initializer block of the JDBC driver class
            // 해당 JDBC driver class를 static 으로 알아서 올려줌
            Class.forName(AppConfig.JDBC_DRIVER_URL);

            System.out.println("[SUCCESS] GOT DRIVERMANAGER IN RUNTIME");
        }catch(Exception e){
            System.out.println("[FAILED]");
        }
    }

    public static AccountDAO GetInstance(){
        if(instance==null){
            instance = new AccountDAO();
        }
        return instance;
    }

    //=========================== SINGLETON =============================//

    //============================= GET ===============================//'

    public boolean checkIfIDExists(ValueDTO valueDTO) {
        Connection conn = null;
        boolean exists = false;
        
        try {
            // 새로운 Connection 객체 받기
            conn = dbConnector.getConnection();

            String query = Querys.checkIfIDExists;
            preparedStatement = conn.prepareStatement(query);

            preparedStatement.setString(1, valueDTO.getValue());
            
            resultSet = preparedStatement.executeQuery();
            
            if(resultSet.next()){
                exists = resultSet.getBoolean(1);
            }

            resultSet.close();
            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return exists;
    }

    public boolean checkIfPhoneNumExists(ValueDTO valueDTO){
        Connection conn = null;

        boolean exists = false;
        try{
            conn = dbConnector.getConnection();

            String query = Querys.checkIfPhoneNumExists;

            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, valueDTO.getValue());

            resultSet = preparedStatement.executeQuery();

            // resultSet 내 cursor 옮기기
            if(resultSet.next()){
                exists = resultSet.getBoolean(1);
            }

            resultSet.close();
            preparedStatement.close();
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }

        return exists;
    }

    public boolean checkIfValidLogin(LoginDTO loginDTO){

        String id = loginDTO.getId();
        String pw = loginDTO.getPw();

        Connection conn = null;
        boolean isValid = false;

        try{
            conn = dbConnector.getConnection();

            String query = Querys.checkIfValidLogin;

            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, pw);

            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                isValid = resultSet.getBoolean(1);
            }

            resultSet.close();
            preparedStatement.close();
            conn.close();

        }catch(SQLException e){
            e.printStackTrace();
        }

        return isValid;
    }

    public ValueDTO getPWOfCertainID(ValueDTO valueDTO){

        Connection conn = null;

        String curID = valueDTO.getValue();
        String pw = "";

        try{
            conn = dbConnector.getConnection();

            preparedStatement = conn.prepareStatement(Querys.getPWOfCertainID);
            preparedStatement.setString(1, curID);

            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                pw = resultSet.getString("userpw");
            }

            resultSet.close();
            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ValueDTO(pw);
    }

    // 여기 하고 있었음
    public UpdateInfoDTO getUpdatableData(String userId){

        Connection conn = null;

        String curPw = "";
        String curEmail = "";
        String curAddress = "";
        String curZipcode= "";
        
        try{
            conn = dbConnector.getConnection();

            preparedStatement = conn.prepareStatement(Querys.getUpdatableUserInfos);
            preparedStatement.setString(1, userId);

            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                curPw = resultSet.getString("userpw");
                curEmail = resultSet.getString("useremail");
                curAddress = resultSet.getString("userzipcode");
                curZipcode = resultSet.getString("useraddress");
            }

            resultSet.close();
            preparedStatement.close();
            conn.close();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return new UpdateInfoDTO(curPw, curEmail, curZipcode, curAddress);
    }
    //============================= ADD ===============================//

    public void Add(AccountDTO accountDTO){
        Connection conn = null;

        try{
            conn = dbConnector.getConnection();

            preparedStatement = conn.prepareStatement(Querys.addAccount);

            preparedStatement.setString(1, accountDTO.getUserId());
            preparedStatement.setString(2, accountDTO.getUserPw());
            preparedStatement.setString(3, accountDTO.getUserName());
            preparedStatement.setString(4, accountDTO.getUserPhoneNum());
            preparedStatement.setString(5, accountDTO.getUserBirth());
            preparedStatement.setString(6, accountDTO.getUserEmail());
            preparedStatement.setString(7, accountDTO.getUserZipCode());
            preparedStatement.setString(8, accountDTO.getUserAddress());

            int success = preparedStatement.executeUpdate();

            if(success > 0) System.out.println("[SUCCESS] ADD ACCOUNT");
            else System.out.println("[FAIL] ADD ACCOUNT");

            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //=========================== DELETE =============================//

    public void Delete(ValueDTO valueDTO){
        Connection conn = null;

        String deletingID = valueDTO.getValue();

        try{
            conn = dbConnector.getConnection();

            preparedStatement = conn.prepareStatement(Querys.deleteAccount);
            preparedStatement.setString(1, deletingID);

            int success = preparedStatement.executeUpdate();

            if(success>0) System.out.println("[SUCCESS] DELETE ACCOUNT");
            else System.out.println("[FAIL] DELETE ACCOUNT");

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
