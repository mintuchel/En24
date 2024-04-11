﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Library
{
    // USER가 BORROW 또는 RETURN 할때 쓰는 MiniDTO
    // controller에서 만들어서 model로 보내줌
    class MiniDTO
    {
        private string bookID;
        private string quantity;

        public MiniDTO(string bookID, string quantity)
        {
            this.bookID = bookID;
            this.quantity = quantity;
        }

        public MiniDTO(List<string> dataFromView)
        {
            this.bookID = dataFromView[0];
            this.quantity = dataFromView[1];
        }

        public string GetBookID() { return bookID; }
        public string GetBookNum() { return quantity; }

    }
}
