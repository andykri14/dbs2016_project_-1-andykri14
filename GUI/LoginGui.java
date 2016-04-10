package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;

import Logic.*
;public class LoginGui extends JFrame {

	private JPanel contentPaneL;
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField,passwordField_1,passwordField_2;
	private LoginGui log;
	private JTextField textField_1;
	private RegisterGui reg;
	private JButton btnRegister;
	Connection c;
	Login l;
	Bikes b;
	
	public LoginGui(final Gui frame,Connection c,Bikes b) {
		setResizable(false);
		this.b=b;//a contentPaneL sluzi na prihlasenie do programu
		this.c=c;
		l=new Login(c);
		log=this;
		reg = new RegisterGui(log,l);
		reg.setVisible(false);
		reg.setEnabled(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 336, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		contentPaneL = new JPanel();
		contentPaneL.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPaneL);
		contentPaneL.setLayout(null);
		
		textField_1 = new JTextField();//text field do ktoreho sa zadava stare heslo pri zmene na nove
		textField_1.setBounds(111, 11, 116, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		passwordField_1 = new JPasswordField();//password field do ktoreho sa zada nove heslo
		passwordField_1.setBounds(111, 42, 116, 20);
		contentPane.add(passwordField_1);
		
		passwordField_2 = new JPasswordField();//password field do ktoreho sa zada nove heslo druhy-krat na overenie
		passwordField_2.setBounds(111, 73, 116, 20);
		contentPane.add(passwordField_2);
		
		JLabel lblPasword_1 = new JLabel("Pasword");//pocny label pre zmenu hesla
		lblPasword_1.setBounds(10, 14, 91, 14);
		contentPane.add(lblPasword_1);
		
		JLabel lblNewPasword = new JLabel("New Pasword");//pocny label pre zmenu hesla
		lblNewPasword.setBounds(10, 45, 91, 14);
		contentPane.add(lblNewPasword);
		
		JLabel lblNewPasword_1 = new JLabel("New Pasword");//pocny label pre zmenu hesla
		lblNewPasword_1.setBounds(10, 76, 91, 14);
		contentPane.add(lblNewPasword_1);
		
		JButton btnChange = new JButton("Change");//tlacitko ktoreho stacenim sa skontroliju udaje a zmeni sa heslo
		btnChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String a1=String.valueOf(passwordField_1.getPassword()),a2=String.valueOf(passwordField_2.getPassword());//ulozi hodnoty passwordfieldov do premennych
				if(a1.compareTo("")!=0 && a2.compareTo("")!=0){//skontroluje aby nove heslo nebolo prazdne
				if(a1.compareTo(a2)==0 && l.check(textField_1.getText())){//skontroluje ci stare heslo odpoveda staremu heslu aktualneho pouzivatela a skontroluje ci sa nove heslo a jeho overenie rovnaju 
				l.changepaswd(a1);//zmeni heslo v databaze
				frame.setEnabled(true);
				log.setVisible(false);//navrat na hlavne okno programu
				log.setEnabled(false);
				}
				}
				else
					infoBox("New Pasword fileds can not be empty","Warnig");
			}
		});
		btnChange.setBounds(111, 127, 81, 23);
		contentPane.add(btnChange);
		
		JButton btnBack1 = new JButton("Back");//tlacidlo ktore vrati naspat hlavne okno programu a zatvori aktualne okno
		btnBack1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setEnabled(true);
				log.setVisible(false);
				log.setEnabled(false);
			}
		});
		btnBack1.setBounds(238, 127, 72, 23);
		contentPane.add(btnBack1);
		
		textField = new JTextField();//texfiled do ktoreho sa zadava meno pri prihlasovani
		textField.setBounds(104, 34, 124, 20);
		contentPaneL.add(textField);
		textField.setColumns(10);
		
		JLabel lblName = new JLabel("Name");
		lblName.setBounds(48, 37, 46, 14);
		contentPaneL.add(lblName);
		
		passwordField = new JPasswordField();//passwoed filed do ktoreho sa zadava heslo pri prihlasovani
		passwordField.setBounds(104, 65, 124, 20);
		contentPaneL.add(passwordField);
		
		JLabel lblPasword = new JLabel("Pasword");
		lblPasword.setBounds(48, 68, 46, 14);
		contentPaneL.add(lblPasword);
		
		JButton btnLog = new JButton("Log in");//tlacitko ktore prihlasi do systemu
		btnLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(l.log(textField.getText(),String.valueOf(passwordField.getPassword()))==1){//verifikuje prihlasovacie udaje 
					frame.innicializeItems();//inicializuje listy v hlavnom okne
					l.checkOrders();//inicializuje objednavky prihlaseneho pouzivatela
					frame.setVisible(true);//zatvoriprihlasovaciu obrazovku a otvri hlavnu obrazovku programu
					frame.setEnabled(true);
					log.setVisible(false);
					log.setEnabled(false);
					if(l.getType()==1)//nastavi typ pouzivatela
					frame.UserGUI();
				}
		
			}
		});
		btnLog.setBounds(114, 96, 103, 23);
		contentPaneL.add(btnLog);
		
		btnRegister = new JButton("Register");// po kliknuti sa ovtvori registracne okno
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				log.setEnabled(false);
				reg.setVisible(true);
				reg.setEnabled(true);
			}
		});
		btnRegister.setBounds(231, 127, 89, 23);
		contentPaneL.add(btnRegister);
	}
	public void setbacround(int i){//zmeni content pane na druhy - 0 zmeni sa na contentPaneL 1 - zmeni sa contetPane
		if(i==0){
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setContentPane(contentPaneL);
		}
		else{
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			setContentPane(contentPane);
		}
	}
	
	protected Login getlog(){//geter pre login 
		return l;
	}
	
	public  void infoBox(String infoMessage, String titleBar)
	{
	    JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
	}
}
