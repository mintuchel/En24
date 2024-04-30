﻿using System;
using System.Collections.Generic;
using MySql.Data.MySqlClient;

namespace Library
{
    // 여기서 조건 따지지말기
    // 조건은 모두 Service에서 따지고 온다
    // 여기에 조건이 있으면 안됨
    class HistoryDAO
    {
        private string connectionString;
        private MySqlConnection connection;
        private MySqlCommand command;

        private static HistoryDAO instance;

        private HistoryDAO()
        {
            connectionString = "Server=localhost;Database=ensharp;Uid=root;Pwd=1234;";
            connection = new MySqlConnection(connectionString);
            // 해당 DB에 사용할 command 객체 생성
            command = connection.CreateCommand();

            InitializeHistoryDB();
        }

        public static HistoryDAO GetInstance()
        {
            if (instance == null)
            {
                instance = new HistoryDAO();
            }
            return instance;
        }

        private void InitializeHistoryDB()
        {
            connection.Open();

            command.CommandText = Querys.historyDBInitializeQuery;
            command.ExecuteNonQuery();

            connection.Close();
        }

        //================= SIMPLE GET CHECK FUNCTIONS ==================//

        // historyDB 참조해서 user가 진짜 빌린 책인지 확인
        public bool CheckIfUserBorrowed(string curUserID, string returningBookID)
        {
            List<int> curUserBorrowedBookList = new List<int>();

            connection.Open();
            command.Parameters.Clear();

            command.CommandText = Querys.checkIfUserBorrowedQuery;
            command.Parameters.AddWithValue("@borrowerID", curUserID);
            command.Parameters.AddWithValue("@bookID", returningBookID);
            bool exists = Convert.ToBoolean(command.ExecuteScalar());

            connection.Close();

            return exists;
        }

        // 해당 책을 빌린 사람이 있는지 확인
        // BookService에서 DELETE BOOK 할때 조건문으로 쓰임
        public bool CheckIfBookIsBorrowed(int bookID)
        {
            connection.Open();
            command.Parameters.Clear();

            command.CommandText = Querys.checkIfBookIsBorrowedQuery;
            command.Parameters.AddWithValue("@bookID", bookID);
            bool exists = Convert.ToBoolean(command.ExecuteScalar());

            connection.Close();

            return exists;
        }

        // 현재 USER가 BORROW한 BOOK들의 ID에 대한 정보 반환 -> BOOKID LIST로
        public List<int> GetMemberBorrowedBooks(string curUserID)
        {
            List<int> curUserBorrowedBookList = new List<int>();

            connection.Open();
            command.Parameters.Clear();

            command.CommandText = Querys.getCertainMemberBorrowHistory;
            command.Parameters.AddWithValue("@borrowerID", curUserID);

            MySqlDataReader reader = command.ExecuteReader();

            while (reader.Read())
            {
                curUserBorrowedBookList.Add(int.Parse(reader["book_id"].ToString()));
            }

            reader.Close();
            connection.Close();

            return curUserBorrowedBookList;
        }

        // 현재 USER가 RETURN한 BOOK들의 ID에 대한 정보 반환 -> BOOKID LIST로
        public List<int> GetMemberReturnedBooks(string curUserID)
        {
            List<int> curUserReturnedBookList = new List<int>();

            connection.Open();
            command.Parameters.Clear();

            command.CommandText = Querys.getCertainMemberReturnHistory;
            command.Parameters.AddWithValue("@borrowerID", curUserID);

            MySqlDataReader reader = command.ExecuteReader();

            while (reader.Read())
            {
                curUserReturnedBookList.Add(int.Parse(reader["book_id"].ToString()));
            }

            reader.Close();
            connection.Close();

            return curUserReturnedBookList;
        }


        //================ BORROW && RETURN CRUD =================//

        // 여기서 직접 historyDB에 접근하는 쿼리문 적어주기
        // 일단 memberService부터 손보고 시작하자
        public bool AddBorrowHistory(string curUserID, string bookID)
        {
            connection.Open();
            command.Parameters.Clear();

            command.CommandText = Querys.addBorrowHistoryQuery;
            command.Parameters.AddWithValue("@borrowerID", curUserID);
            command.Parameters.AddWithValue("@bookID", bookID);
            command.ExecuteNonQuery();
            
            connection.Close();

            return true;
        }

        // 여기서 직접 historyDB에 쿼리문으로 접근해서 returned boolean 값 바꿔주기
        // 여기도 service부터 하고 시작
        public bool AddReturnHistory(string curUserID, string bookID)
        {
            // 특정 member가 특정 book을 빌리고 반납하는걸 반복했을때 중복튜플이 생기는걸 방지하기 위한 추가 코드
            if (CheckIfReturnHistoryAlreadyExists(curUserID, bookID))
            {
                connection.Open();
                command.Parameters.Clear();

                command.CommandText = Querys.deleteBorrowHistoryQuery;
                command.Parameters.AddWithValue("@borrowerID", curUserID);
                command.Parameters.AddWithValue("@bookID", bookID);
                command.ExecuteNonQuery();

                connection.Close();

                return true;
            }

            connection.Open();
            command.Parameters.Clear();

            command.CommandText = Querys.updateToReturnHistoryQuery;
            command.Parameters.AddWithValue("@borrowerID", curUserID);
            command.Parameters.AddWithValue("@bookID", bookID);
            command.ExecuteNonQuery();

            connection.Close();

            return true;
        }
      
        public bool DeleteMemberHistory(string curUserID)
        {
            connection.Open();
            command.Parameters.Clear();

            command.CommandText = Querys.deleteMemberHistoryQuery;
            command.Parameters.AddWithValue("@deletingMemberID", curUserID);
            command.ExecuteNonQuery();

            connection.Close();

            return true;
        }

        private bool CheckIfReturnHistoryAlreadyExists(string curUserID, string bookID)
        {
            connection.Open();
            command.Parameters.Clear();

            command.CommandText = Querys.checkIfReturnHistoryAlreadyExistsQuery;
            command.Parameters.AddWithValue("@borrowerID", curUserID);
            command.Parameters.AddWithValue("@bookID", bookID);

            bool exists = Convert.ToBoolean(command.ExecuteScalar());

            connection.Close();

            return exists;
        }
    }
}
