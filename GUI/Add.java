package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;

import Logic.Order;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Add extends JFrame {

	private JPanel contentPane;
	private JTextField textField,textField_1; 
	private JButton btnAdd;//button ktory potvrdi zadane informacie
    private Add ad;
    private Order o;
	
	public Add(final Gui frame,Order o) {
		ad=this;
		this.o=o;
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 168);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();//texfield do ktoreho sa napise datum od kedy sa pozicaju bycikle
		textField.setBounds(158, 26, 202, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();//textfield do ktoreho sa napise pocet dni na ako dlho sa pozicia bycikel
		textField_1.setBounds(158, 57, 202, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblId = new JLabel("Lend items from(date)");
		lblId.setBounds(10, 29, 138, 14);
		contentPane.add(lblId);
		
		JLabel lblProduct = new JLabel("how many days");
		lblProduct.setBounds(10, 60, 92, 14);
		contentPane.add(lblProduct);
		
		JButton btnBack = new JButton("Back");//tlacidlo ktore vrati predchadzajucu obrazovku a zotvori sucasnu
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				frame.setEnabled(true);
				ad.setVisible(false);
				ad.setEnabled(false);
			}
		});
		btnBack.setBounds(345, 99, 89, 23);
		contentPane.add(btnBack);
		
		btnAdd = new JButton("Ok");//tlacidlo ktore prida novu objednavku
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getContentPane().add(frame);
				
			}
		});
		btnAdd.setBounds(157, 99, 89, 23);
		contentPane.add(btnAdd);
	}
	private void add(Gui frame){
		String s=o.getActualDate("yyyy-MM-dd");//vrati akualny datum
		String s1=textField.getText();
		String s2=textField_1.getText();
		try{
		int i=Integer.parseInt(s2);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		try{
		Date date = format.parse(s);//konveruje string na datum
		Date date1 = format.parse(s1);
        if((date.before(date1) || s.compareTo(s1)==0)&&(i>0 && i<=30)){//kontroluje aby zadane data boli spravne
        	date=addDays(date1,i);
        	Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        	String s3 = formatter.format(date);
        	frame.setEnabled(true);
			ad.setVisible(false);
			ad.setEnabled(false);
        	o.addOrder(s1,s3);//metoda kde sa prida nova objednavka
        	frame.enableReserve();//spristupni tlacidla na pridanie biku do objednavky
		}
        else
         infoBox("Invalid date, actual date >= lend from \n Maximal period of lending is 30 days ","error");
        	
		}catch(ParseException e){
			infoBox("Wrong date format!","error");
		}
		}catch(Exception e){
			infoBox("Number of days must be a number!","error");
		}
	}
	
	protected void inicialize(){
		String s="yyyy-MM-dd";
		String s1="yyyy-MM";
		textField.setText(o.getActualDate(s));
	}
	
	public Date addDays(Date date, int days)//date je datum a days je pocet dni o ktory sa zvysi
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //zvys datum o urceny pocet dni
        return cal.getTime();
    }
	
	public  void infoBox(String infoMessage, String titleBar)//pomocny infobx na vypis errorov a warningov
	  {
	      JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
	  }
}
