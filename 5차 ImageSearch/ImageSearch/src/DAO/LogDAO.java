package DAO;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

// JAVA > JDBC API > JDBC DRIVER > DBMS(각종 DB들)
// JDBC API는 DBMS(실제 DB들)에 상관없이 사용할 수 있는 API를 제공함
// 얘내는 JDK에 이미 포함이 되어있기 때문에 따로 다운로드하거나 설치할 필요가 없음

// 여기서 ConnectionPool 못씀??
// 매번 JDBC 드라이버에서 Connection 을 불러오는 대신 Connection Pool 사용
// 커넥션 풀에서 미리 준비되어 있는 Connection 객체를 받아 쓰기만 하면 됨
public class LogDAO {

    public static LogDAO instance;

    // 얘는 return을 DB에 적용된 row 개수로 해줌
    // 이걸로 성공했는지 판단가능
    // 얘내는 ? 로 매개변수 지정 가능
    private PreparedStatement preparedStatement;

    // 얘내는 매개변수 없는 sql 문
    // select 같은거에 사용
    private Statement statement;

    // select 한 rows 를 말그대로 set 형식으로 받음
    // 얘내 method 사용해서 받은 row를 조회가능
    private ResultSet resultSet;

    // 1. JDBC 드라이버 : DBMS(사용하는 실제 DB)에 맞는 드라이버를 runtime 때 로딩할 때 필요함
    // "com.mysql.cj.jdbc.Driver" == 최신 버전 (MySQLConnector/J 버전)
    // "com.mysql.jdbc.Driver" == 구 버전
    // 나는 8.036 버전이라 cj 꺼를 써야함
    // 쉽게 말하면 runtime 에 자기가 쓸 DB에 맞는 JDBC 드라이버 로딩하기 위해 필요한 JDBC DRIVER URL임
    // 얘를 Class.forName 할때 인자로 넘기면 jdbcURL에 맞는 드라이버를 자동으로 등록해줌
    // 이 jdbcDriverURL 가지고 Class.forName 쓰면 알아서 JDBC 드라이버를 등록해주는거임
    private String jdbcDriverURL = "com.mysql.cj.jdbc.Driver";

    // 2. URL : DBMS(사용하는 실제 DB)와 연결하기 위한 URL
    // 사용하는 DB에 따라서 형식이 다르기 때문에 DB에 따라서 입력값이 달라짐
    // 얘는 위 jdbcDriverURL을 통해 얻은 JDBCDriver의 인자로 넘겨주면 실제 DB Connection 반환해줌
    private String jdbcURL = "jdbc:mysql://localhost/ensharp";

    // singleton 으로 해놔 conn 변수가 딱 한번만 초기화되게 하자
    private LogDAO(){

    }

    public static LogDAO GetInstance(){
        if(instance==null){
            instance = new LogDAO();
        }
        return instance;
    }

    //=========================== SINGLETON =============================//

    // 매번 JDBC DRIVER 에서 Connection 객체를 생성해서 반환함
    // conn.Close() 하면 매번 다시 JDBC DRIVER 에서 새로운 Connection 객체를 받아와야함
    // 1. 그냥 열어놓기
    // 2. 매번 Connection 객체 받아오기

    private Connection GetConnection(){
        Connection newConnectionObject = null;

        try{
            // 1. dynamically loads JDBC driver class from certain URL
            // JDBC driver 는 default 로 로딩되는게 아니라 runtime 에 로딩되서 그럼
            // triggers the static initializer block of the JDBC driver class
            // 해당 JDBC driver class를 static 으로 알아서 올려줌
            //Class.forName(jdbcDriverURL);
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("[SUCCESS] GOT DRIVERMANAGER IN RUNTIME");

            // 2. Driver Manager에게 새로운 Connection 객체를 달라고 요청
            // 위 코드로 runtime에 등록된 DriverManager 가지고 getConnection 해달라는 거임
            // 위에 코드때문에 DriverManager 사용할 수 있는거
            // 저거로 사용할 Driver 를 미리 등록했기 때문
            newConnectionObject = DriverManager.getConnection(jdbcURL,"root","1234");

            System.out.println("[SUCCESS] GOT NEW CONNECTION FROM DRIVERMANAGER");
        }catch(Exception e){
            System.out.println("[FAIL] GET CONNECTION");
        }

        return newConnectionObject;
    }

    //=========================== ADD CRUD =============================//

    public void AddLog(String keyWord){

        // AddLog 할때 사용할 Connection 객체를 참조할 Connection 참조변수
        // 매번 GetConnection 으로 받아줘야함
        Connection conn = null;

        try{
            conn = GetConnection();

            statement = conn.createStatement();

            String addLogQuery = "INSERT INTO imagesearchlogdb VALUES (?)";

            preparedStatement = conn.prepareStatement(addLogQuery);
            preparedStatement.setString(1, keyWord);
            int success = preparedStatement.executeUpdate();

            if (success > 0) System.out.println("[SUCCESS] ADD LOG");
            else System.out.println("[FAIL] ADD LOG");

            // 자원반환
            preparedStatement.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //=========================== SELECT CRUD =============================//

    public ArrayList<String> GetAllLogs(){
        Connection conn = null;
        ArrayList<String> logList = new ArrayList<>();

        try{
            // 새로운 Connection 객체 받기
            conn = GetConnection();

            statement = conn.createStatement();

            String selectLogQuery = "SELECT log FROM imagesearchlogdb";

            // select 문으로 조회된 row를 다 받기
            resultSet = statement.executeQuery(selectLogQuery);

            while (resultSet.next()) {
                String curLog = resultSet.getString("log");
                System.out.println("[LOG] "+curLog);
            }

            // 자원반환
            conn.close();
            statement.close();
            resultSet.close();

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return logList;
    }
}
