﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Library
{
    class UserController
    {
        private string curUserID;

        UserView userView;
        MemberModel memberModel;
        BookModel bookModel;

        public UserController()
        {
            memberModel = MemberModel.GetInstance();
            bookModel = BookModel.GetInstance();

            userView = new UserView();
        }

        //===================== SINGELTON ========================//

        // UserFrontController에서 LOGIN 후 UserController.run이 실행되기 전에
        // 매번 이 함수가 호출되어 curUserID를 초기화해줌
        public void InitializeUserController(string curUserID)
        {
            this.curUserID = curUserID;
        }

        public void Run()
        {
            UserMenuState selectedMenu;

            List<string> dataFromView;
            List<BookDTO> retList;
            MiniDTO miniDTO;

            bool isUserModeRunning = true;

            while (isUserModeRunning)
            {
                selectedMenu = userView.UserMenuForm(memberModel.GetMember(curUserID).GetName());

                switch (selectedMenu)
                {
                    case UserMenuState.GOBACK:
                        isUserModeRunning = false;
                        break;

                    case UserMenuState.PRINTALLBOOK:

                        retList = bookModel.GetAllBooks();
                        userView.PrintAllBooksForm(retList);
                        break;

                    case UserMenuState.FIND:
                       
                        // 찾을 책에 대한 정보를 userView에서 받아오기
                        dataFromView = userView.FindBookForm();
                        // bookModel로 전달해서 매칭된 List<BookDTO> 받아오기
                        retList = bookModel.FindBook(dataFromView);
                        // 다시 userView에 전달해서 매칭된 책들 모두 출력해서 보여주기
                        userView.PrintSelectedBooksForm(retList);
                        break;

                    case UserMenuState.BORROW:
                        
                        // BORROW할 책에 대한 정보를 userView에서 받아오기
                        dataFromView = userView.BorrowBookForm();
                        // 책 정보를 MiniDTO에 담아주기
                        miniDTO = new MiniDTO(dataFromView);
                        // bookModel에 누가 빌린거 적용해주기
                        bookModel.UpdateBorrowed(miniDTO);
                        // 성공하면 memberModel로 가서 저장해주기
                        memberModel.UpdateBorrowed(curUserID, miniDTO);
                        break;

                    case UserMenuState.CHECKBORROW:

                        List<int> curUserBorrowedBookIDs = memberModel.GetMemberBorrowedBooks(curUserID);
                        List<BookDTO> curUserBorrowedBooks = new List<BookDTO>();

                        for(int i = 0; i < curUserBorrowedBookIDs.Count; i++)
                        {
                            int curId = curUserBorrowedBookIDs[i];
                            curUserBorrowedBooks.Add(bookModel.GetBookDTO(curId));
                        }
                        userView.CheckBorrowedForm(curUserBorrowedBooks);
                        break;
                        
                    case UserMenuState.RETURN:

                        // RETURN할 책에 대한 정보를 userView에서 받아오기
                        dataFromView = userView.ReturnBookForm();
                        // 책 정보를 MiniDTO에 담아주기
                        miniDTO = new MiniDTO(dataFromView);

                        // 만약 진짜로 빌린거면
                        if (memberModel.CheckIfUserBorrowed(curUserID, miniDTO))
                        {
                            // bookModel에 누가 반납한거 적용해주기
                            bookModel.UpdateReturned(miniDTO);
                            // 성공하면 memberModel로 가서 저장해주기
                            memberModel.UpdateReturned(curUserID, miniDTO);
                        }
                        break;
                    
                    case UserMenuState.CHECKRETURN:

                        List<int> curUserReturnedBookIDs = memberModel.GetMemberReturnedBooks(curUserID);
                        List<BookDTO> curUserReturnedBooks = new List<BookDTO>();

                        for (int i = 0; i < curUserReturnedBookIDs.Count; i++)
                        {
                            int curId = curUserReturnedBookIDs[i];
                            curUserReturnedBooks.Add(bookModel.GetBookDTO(curId));
                        }
                        userView.CheckReturnedForm(curUserReturnedBooks);
                        break;
                    
                    case UserMenuState.UPDATEINFO:

                        // PW NAME AGE PHONENUM 입력받은거 가져오기
                        List<string> updatedUserInfo = userView.UpdateMyInfoForm();
                        
                        MemberDTO updatedMember = new MemberDTO(updatedUserInfo);
                        // ID는 controller에서 따로 세팅
                        updatedMember.SetId(curUserID);
                        memberModel.UpdateMember(updatedMember);
                        break;
                    
                    case UserMenuState.DELETEMYSELF:

                        userView.DeleteMyselfForm();
                        break;
                    
                    case UserMenuState.NAVERSEARCH:
                        break;
                    
                    case UserMenuState.REQUESTED:
                        break;
                }
            }
        }
    }
}
