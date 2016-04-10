package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JButton;

import Logic.Login;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;

public class RegisterGui extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JPasswordField passwordField;
	private JLabel lblEmail;
	private JLabel lblPasword;
	private JButton btnBack,btnConfirm;
	private RegisterGui reg;
	private Login lo;
	
	/**
	 * Create the frame.
	 */
	public RegisterGui(final LoginGui log,Login l) {//okno v ktorom sa novy uzivatel zaregistruje
		setResizable(false);
		reg=this;
		lo=l;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 256);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();//text field kde sa zada uzivatelske meno pri registracii
		textField.setBounds(112, 79, 200, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();//text field kde sa zada email pouzivatle pri registracii
		textField_1.setBounds(112, 40, 200, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		passwordField = new JPasswordField();//password field kde zada uzivatel svoje heslo do aplikacie
		passwordField.setBounds(112, 117, 200, 20);
		contentPane.add(passwordField);
		
		JLabel lblLogin = new JLabel("Login ");//informativny label
		lblLogin.setBounds(36, 40, 72, 20);
		contentPane.add(lblLogin);
		
		lblEmail = new JLabel("E-mail");//informativny label
		lblEmail.setBounds(36, 76, 66, 26);
		contentPane.add(lblEmail);
		
		lblPasword = new JLabel("Pasword");//informativny label
		lblPasword.setBounds(36, 114, 66, 26);
		contentPane.add(lblPasword);
		
		btnConfirm = new JButton("Confirm");//tlacitko na zaregistrovanie
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(textField_1.getText().compareTo("")!=0 && textField.getText().compareTo("")!=0 && String.valueOf(passwordField.getPassword()).compareTo("")!=0){//skontroluje ci su vsetky policka vyplnene
				lo.registration(textField_1.getText(),textField.getText(),String.valueOf(passwordField.getPassword()));//metoda ktora ulozi noveho pouzivatela do databazy
				log.setEnabled(true);
				reg.setVisible(false);//navrat na prihlasovaciu obrazovku
				reg.setEnabled(false);
				}
				else
					infoBox("All lines must be filt!","warning");
			}
		});
		btnConfirm.setBounds(154, 148, 89, 23);
		contentPane.add(btnConfirm);
		
		btnBack = new JButton("Back");//tlacitko na navrat  do prihlasovacej obrazovky
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.setEnabled(true);//prihlasovacia obrazovka
				reg.setVisible(false);
				reg.setEnabled(false);
			}
		});
		btnBack.setBounds(335, 183, 89, 23);
		contentPane.add(btnBack);
	}
	
	public  void infoBox(String infoMessage, String titleBar){//chybova hlaska
	    JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
	}
	
}
