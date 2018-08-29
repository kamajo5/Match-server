using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace Rejestrator
{
    class Connecting
    {
        private string connectionString = "datasource=127.0.0.1;port=3306;username=root;password=;database=cloud_database;";
        private List<int> new_user = new List<int>();
        private List<string> new_email = new List<string>();

        public void load_new_user()
        {
            new_user.Clear();
            new_email.Clear();
            string query = "Select Id,Email FROM user Where Activation = 0 AND Token is NULL";
            MySqlConnection databaseConnection = new MySqlConnection(connectionString);
            MySqlCommand commandDatabase = new MySqlCommand(query, databaseConnection);
            MySqlDataReader reader;
            databaseConnection.Open();
            reader = commandDatabase.ExecuteReader();
            if (reader.HasRows)
            {
                while (reader.Read())
                {
                    new_user.Add(reader.GetInt16(0));
                    new_email.Add(reader.GetString(1));
                }
            }
            databaseConnection.Close();
        }

        public void Generuj_tokeny()
        {
            int code = 0;
            if (new_user.Count != 0)
            {
                for (int i = 0; i < new_user.Count; i++)
                {
                    code = token();
                    string query = "Update user set token = " + code + " where token is null AND ID = " + new_user[i];
                    MySqlConnection databaseConnection = new MySqlConnection(connectionString);
                    MySqlCommand commandDatabase = new MySqlCommand(query, databaseConnection);
                    //commandDatabase.CommandTimeout = 60;
                    MySqlDataReader reader;
                    databaseConnection.Open();
                    reader = commandDatabase.ExecuteReader();
                    databaseConnection.Close();
                    Send_Email message = new Send_Email();
                    message.Send(new_email[i],code);
                }
            }
        }
        private int token()
        {
            int tok = 0;
            Random rnd = new Random();
            tok = rnd.Next(10000, 99999);
            return tok;
        }
    }
}
