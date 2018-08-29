using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace Rejestrator
{
    class Send_Email
    {
        public void Send(String Destination,int token)
        {
            using (SmtpClient client = new SmtpClient())
            {
                var credential = new NetworkCredential
                {
                    UserName = "kamil.jonaszko@gmail.com",
                    Password = "radioeska11"
                };
                client.Credentials = credential;
                client.Host = "smtp.gmail.com";
                client.Port = 587;
                client.EnableSsl = true;

                var message = new MailMessage();

                message.To.Add(new MailAddress(Destination));
                message.From = new MailAddress("kamil.jonaszko@gmail.com");
                message.Subject = "Kod aktywacyjny do rejestracji";
                
                message.Body = token.ToString();
                message.IsBodyHtml = true;
                client.Send(message);

            }
        }
    }
}
