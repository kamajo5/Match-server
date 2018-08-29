using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text.RegularExpressions;
using System.Windows;

namespace Calculation_server
{
    class Connecting
    {
        private string connectionString = "datasource=127.0.0.1;port=3306;username=root;password=;database=cloud_database;";
        private List<int> index = new List<int>();
        private List<string> input_data = new List<string>();
        private List<int> option_number = new List<int>();
        private string output_data;

        public void detect_operation()
        {
            index.Clear();
            input_data.Clear();
            option_number.Clear();
            string query = "Select ID, Input_data, Select_option from input_data where is_done = 0 ";
            MySqlConnection databaseConnection = new MySqlConnection(connectionString);
            MySqlCommand commandDatabase = new MySqlCommand(query, databaseConnection);
            MySqlDataReader reader;
            databaseConnection.Open();
            reader = commandDatabase.ExecuteReader();
            if (reader.HasRows)
            {
                while (reader.Read())
                {
                    index.Add(reader.GetInt16(0));
                    input_data.Add(reader.GetString(1));
                    option_number.Add(reader.GetInt16(2));
                }
            }
            databaseConnection.Close();
        }

        public void make_operation()
        {
            output_data = "";
            for (int i = 0; i < index.Count; i++)
            {

                switch (option_number[i])
                {
                    case 1:
                        try
                        {
                            output_data = ONP.Parse(input_data[i]).ToString();
                        }
                        catch
                        {
                            output_data = "NULL";
                        }
                        Update_database(output_data, index[i]);
                        output_data = "";
                        break;
                }
            }
        }

        private void Update_database(string out_data, int idx)
        {
            string query = "Update input_data set Output_data = " + out_data + ", Is_done = 1 where ID = " + idx;
            MySqlConnection databaseConnection = new MySqlConnection(connectionString);
            MySqlCommand commandDatabase = new MySqlCommand(query, databaseConnection);
            MySqlDataReader reader;
            databaseConnection.Open();
            reader = commandDatabase.ExecuteReader();
            databaseConnection.Close();
        }
    }
    public static class ONP
    {
        public static bool TryParse(string str)
        {
            try
            {
                Parse(str);
                return true;
            }
            catch (FormatException)
            {
                return false;
            }
        }

        public static double Parse(string str)
        {
            string[] func = { "sin", "cos", "ctan", "tan" };
            for (int i = 0; i < func.Length; i++)
            {
                Match matchFunc = Regex.Match(str, string.Format(@"{0}\(({1})\)", func[i], @"[1234567890\.\+\-\*\/^%]*"));
                if (matchFunc.Groups.Count > 1)
                {
                    string inner = matchFunc.Groups[0].Value.Substring(1 + func[i].Length, matchFunc.Groups[0].Value.Trim().Length - 2 - func[i].Length);
                    string left = str.Substring(0, matchFunc.Index);
                    string right = str.Substring(matchFunc.Index + matchFunc.Length);

                    switch (i)
                    {
                        case 0:
                            return Parse(left + Math.Sin(Parse(inner)) + right);

                        case 1:
                            return Parse(left + Math.Cos(Parse(inner)) + right);

                        case 2:
                            return Parse(left + Math.Tan(Parse(inner)) + right);

                        case 3:
                            return Parse(left + 1.0 / Math.Tan(Parse(inner)) + right);
                    }
                }
            }

            Match matchSk = Regex.Match(str, string.Format(@"\(({0})\)", @"[1234567890\.\+\-\*\/^%]*"));
            if (matchSk.Groups.Count > 1)
            {
                string inner = matchSk.Groups[0].Value.Substring(1, matchSk.Groups[0].Value.Trim().Length - 2);
                string left = str.Substring(0, matchSk.Index);
                string right = str.Substring(matchSk.Index + matchSk.Length);
                return Parse(left + Parse(inner) + right);
            }

            Match matchMulOp = Regex.Match(str, string.Format(@"({0})\s?({1})\s?({0})\s?", RegexNum, RegexMulOp));
            Match matchAddOp = Regex.Match(str, string.Format(@"({0})\s?({1})\s?({2})\s?", RegexNum, RegexAddOp, RegexNum));
            var match = (matchMulOp.Groups.Count > 1) ? matchMulOp : (matchAddOp.Groups.Count > 1) ? matchAddOp : null;
            if (match != null)
            {
                string left = str.Substring(0, match.Index);
                string right = str.Substring(match.Index + match.Length);
                string val = ParseAct(match).ToString(CultureInfo.InvariantCulture);
                return Parse(string.Format("{0}{1}{2}", left, val, right));
            }

            try
            {
                return double.Parse(str, CultureInfo.InvariantCulture);
            }
            catch (FormatException)
            {
                throw new FormatException(string.Format("Bad string '{0}'", str));
            }
        }

        private const string RegexNum = @"[-]?\d+\.?\d*";
        private const string RegexMulOp = @"[\*\/^%]";
        private const string RegexAddOp = @"[\+\-]";

        private static double ParseAct(Match match)
        {
            double a = double.Parse(match.Groups[1].Value, CultureInfo.InvariantCulture);
            double b = double.Parse(match.Groups[3].Value, CultureInfo.InvariantCulture);

            switch (match.Groups[2].Value)
            {
                case "+":
                    return a + b;

                case "-":
                    return a - b;

                case "*":
                    return a * b;

                case "/":
                    return a / b;

                case "^":
                    return Math.Pow(a, b);

                case "%":
                    return a % b;

                default:
                    throw new FormatException(string.Format("Bad string '{0}'", match.Value));
            }
        }
    }
}
