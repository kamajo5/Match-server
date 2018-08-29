using System;
using System.ComponentModel;
using System.Globalization;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Forms;

namespace Calculation_server
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
            InitializeComponent();
            isActive = false;
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
                con.detect_operation();
                con.make_operation();
            }
        }

        private void Button_Click_1(object sender, RoutedEventArgs e)
        {
            dzialanie.Content = "Stop ";
            if ((null != threed) && threed.IsBusy)
            {
                isActive = false;
                threed.CancelAsync();
            }
        }


    }
}
