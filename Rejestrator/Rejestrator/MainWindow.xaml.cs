using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Rejestrator
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        bool isActive;
        private BackgroundWorker threed = null;
        public MainWindow()
        {
            isActive = false;
            InitializeComponent();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            isActive = true;
            if (null == threed)
            {
                threed = new BackgroundWorker();
                threed.DoWork += new DoWorkEventHandler(threed_DoWork);
                threed.WorkerSupportsCancellation = true;
                threed.WorkerReportsProgress = true;
            }
            threed.RunWorkerAsync();
            dzialanie.Content = "Running";
        }

        private void threed_DoWork(object sender, DoWorkEventArgs e)
        {
            while (isActive)
            {
                GC.Collect();
                Connecting con = new Connecting();
                con.load_new_user();
                con.Generuj_tokeny();
                Thread.Sleep(100);
            }
        }

        private void Button_Click_1(object sender, RoutedEventArgs e)
        {
            dzialanie.Content = "Stop server";
            if ((null != threed) && threed.IsBusy)
            {
                isActive = false;
                threed.CancelAsync();            
            }
        }
    }
}
