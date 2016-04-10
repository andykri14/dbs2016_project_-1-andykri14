package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import javax.swing.DefaultListModel;
import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import Logic.*;

import javax.swing.JLabel;
import javax.swing.JCheckBox;
public class Gui extends JFrame {

	private JPanel contentPane,topPanel;
	private	JMenuBar	menuBar;
	private	JMenu		menuStart,menuAcount;
	private	JMenuItem	menuNew,menuCart,menuNewOr,menuLoad,menuPaswd,menuLogOut,menuDel,menuAkt;
	private JButton btnBack,btnRemove,btnAddSerial,btnAdd,btnReserve,btnSaveChange,btnShow,btnAdd_1;
	private final DefaultListModel itemName1,itemName,itemName2;
	private Gui frame;
	private Add ad;
	private LoginGui log;
	private RegisterGui reg;
	private resGui res;
	private JList list;
	private JComboBox comboBox,comboBox_1;
	private JScrollPane scrollPane_1;
	private JPopupMenu popup;
	private JCheckBox checkbox;
	private Connect con;
	private Bikes b;
	private Order o;
	private Equipment eq;
	int gui=0;
	private JTable table,table_1;
	private JList list_1,list_2;
	private JScrollPane scrollPane;
	private JTextField textField_1;
	private int freeze=0;
	
	public static void main(String[] args) {//main funkcia , ktora spusti aplikaciu
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui frame = new Gui();
					frame.setVisible(false);
					frame.setEnabled(false);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Gui() {
		con=new Connect();
		Connection c=con.create_connection();//vytvori connecion do databazy
		b=new Bikes(c);
		setResizable(false);//okno nemoze menit velkost
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 850, 473);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnShow = new JButton("Search");//tlacitko po ktoreho stlaceni sa vytriedia bicykle
		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s=(String)comboBox.getSelectedItem();//vyberie polozku zo znaciek
				String s1=(String)comboBox_1.getSelectedItem();//vyberie polozku z typov
				if(checkbox.isSelected()==true){// skontroluje ci je checkbox true ak hej tak vypise len tie modely vybratej znacky ktorych cena je mensia ako priemer
					b.setAverage(itemName,s1);
				}
				else //ak je checkbox false tak sa vytriedi modely ktore su vybratej znacky a typu
				b.inicialize(itemName,s,s1);
			}
		});
		btnShow.setBounds(10, 149, 102, 23);
		contentPane.add(btnShow);
		
	    comboBox = new JComboBox();//combobox kde su nazvy znaciek
	    comboBox.addItem("All");
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String s = (String)comboBox.getSelectedItem();	
				
			}
		});
		comboBox.setBounds(10, 70, 102, 23);
		contentPane.add(comboBox);
		
		itemName = new DefaultListModel();//vytvori novy list model
		b.inicialize(itemName,"all","all");//inicializuje list modelov tak  ze ukazuje vsetky modely 
		list = new JList(itemName);//priradenie list modelu k listu
		  list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	      list.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {//ked sa klikne na model , tak za zobrazia vsetky dostupne jednotlive biky daneho modelu
				String data="";
				if(list.getSelectedValue()!=null)//iba ak je vybrata nejaka polozka
				data = (String) list.getSelectedValue(); 
				itemName1.removeAllElements();
				b.itemInfo(data,table);
				b.inicializel(data,itemName1);//najde a vypise bike_cody pre dany model
				if(data.compareTo("")!=0){
				if(arg0.getButton() == MouseEvent.BUTTON1)//lave tlacidlo
			    {
					
			    }
				if(arg0.getButton() == MouseEvent.BUTTON3)//prave tlacidlo, ak sa klikne pravym tlacidlom nad oznacenov polozkov tak sa zobrazi kontexove menu
			    {
					ShowPopup(arg0);
			    }
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {	
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
	      });
	    list.setVisibleRowCount(18);       
		list.setBounds(126, 141, 22, -70);
		
		popup = new JPopupMenu();
		menuDel= new JMenuItem("Delete");//vyskakovacie okno po ktorom kliknuti na sa vymaze vybrata polozka
		
		menuDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int index = list.getSelectedIndex();
				String s=(String) list.getSelectedValue();
			    if(index >= 0){ //odstrani iba ak je vybratý konkrétny element
			    	itemName.removeElementAt(index);//odstrani element s listu
			    	b.delete(s);//odstrani element s databazy
			    }
			}
		});
	    popup.add(menuDel);
	    menuAkt= new JMenuItem("Edit");
		contentPane.add(list);
		scrollPane_1 = new JScrollPane(list);//prida na lit modelov scroll pane aby sa dal list scrollovat ked je v nom viac poloziek
		scrollPane_1.setBounds(126, 70, 89, 324);
		contentPane.add(scrollPane_1);
		
		btnReserve = new JButton("Reserve");
		btnReserve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String selected = (String)list_1.getSelectedValue();
				int index = list_1.getSelectedIndex();
			    if(index >= 0){ //prida novy bike do objednavky ak je nejaky vybraty
			    	itemName1.removeElementAt(index);//odstrani bike s listu abi nebol dvakrat ten isty bike v jdnej objednavke
			    	  o.addOrderItem(selected);//prida bike do objednavky v databaze
			    }
			}
		});
		btnReserve.setVisible(false);
		btnReserve.setEnabled(false);
		btnReserve.setBounds(243, 370, 89, 23);
		contentPane.add(btnReserve);
		
		comboBox_1 = new JComboBox();//combobox kde su nazvy typov bicyklov
		comboBox_1.setBounds(10, 110, 102, 23);
		contentPane.add(comboBox_1);
		comboBox_1.addItem("all");//prida prvok all
		
		
		table = new JTable(2,4);//tabulka kde su zobrazene jednotlive zaznamy bicyklov
		table.setValueAt("Brand",0, 0);//pomenovanie stlpcov
	    table.setValueAt("Model",0, 1);
	    table.setValueAt("Type",0, 2);
	    table.setValueAt("Price",0, 3);
	    
		table.setBounds(243, 74, 377, 32);
		contentPane.add(table);
		itemName1 = new DefaultListModel();//list model pre list kde su zobrazene bik_cody
		list_1 = new JList(itemName1);//list pre bike_cody
		list_1.setBounds(243, 105, 126, 224);
		contentPane.add(list_1);
		
		scrollPane = new JScrollPane(list_1);//scrollovaci panel pre list s bike codmi 
		scrollPane.setBounds(242, 134, 127, 225);
		contentPane.add(scrollPane);
		
		btnSaveChange = new JButton("Save change");//tlacidlo ktore ulozi zmeny po zmeneni onkretneho zaznamu
		btnSaveChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {//po jeho stlaceni sa zoberu hodnoty zadane v tabulke
				String data = (String) list.getSelectedValue();
				String s=(String) table.getValueAt(1, 1);
				String s1=(String) table.getValueAt(1, 0);
				String s2=(String) table.getValueAt(1, 2);
				String s3=(String) table.getValueAt(1, 3);
				try{
					Float.parseFloat(s3);//kontroluje ci je v 4 stlpci cislo vatsie ako 0
				if(s.compareTo("")!=0 && s1.compareTo("")!=0 && s2.compareTo("")!=0 && Float.parseFloat(s3)>0){//kontroluje aby bunky neboli prazdne
				if(s1.compareTo("all")!=0 && s2.compareTo("all")!=0){// kontroluje ci zadany nazov nie je all, ten je vyhradeny pre specialnu moznost
				b.changeNazov(data, s, s3,itemName);//zmeni cenu a nazov
				b.changeZnacka(data, s1);//zmeni znacku
				b.changeTyp(data, s2);//zmeni typ
				b.inicializeCombobx(comboBox,comboBox_1);
				}
				else
					infoBox("Type and brand cant be named all !\nItem not changed!","error");//errorove hlasky
			   }
				else
					infoBox("Type, brand and model cells can not be empty, \nprice must by bigger than 0","error");//errorove hlasky
				}catch(Exception e){
					infoBox("Value in price cell must be number","error");//errorove hlasky
				}
			}
		});
		btnSaveChange.setBounds(243, 371, 126, 23);
		contentPane.add(btnSaveChange);
		
		btnAdd = new JButton("Add Model");//tlacitko po stlaceni ktoreho sa prida novy model a jeho bike_cody
		btnAdd.setVisible(false);
		btnAdd.setEnabled(false);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String s=(String) table.getValueAt(1, 1);//zoberie hodnoty s tabulky a vytvori z nich novy model 
				String s1=(String) table.getValueAt(1, 0);
				String s2=(String) table.getValueAt(1, 2);
				String s3=(String) table.getValueAt(1, 3);
				try{
					Float.parseFloat(s3);//kontrola ci je v 4. stlpci cislo
				if(s.compareTo("")!=0 && s1.compareTo("")!=0 && s2.compareTo("")!=0&& Float.parseFloat(s3)>0){//kontroluje ci niesu bunky tabulky prazdne
					if(s1.compareTo("all")!=0 && s2.compareTo("all")!=0){// kontroluje ci zadany nazov nie je all, ten je vyhradeny pre specialnu moznost
				b.addNazov(s, s3,itemName);//vytvori novy model
				b.changeZnacka(s,s1);//priradi znacku modelu
				b.changeTyp(s,s2);//priradi typ modelu
				b.addBike(s,itemName1);//prida bike_cody k modlu 
				addReturn();
				b.inicializeCombobx(comboBox,comboBox_1);
					}
				 }
				else
					infoBox("Type, brand and model cells can not be empty","error");
				}catch(Exception e){
					infoBox("Value in price cell must be number","error");
				}
			}
		});
		btnAdd.setBounds(462, 370, 111, 23);
		contentPane.add(btnAdd);
		
		btnAddSerial = new JButton("Add serial");//tlacitko ktere prida bike_code novemu alebo staremu modelu
		btnAddSerial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s=textField_1.getText();
				try{
				if(Integer.parseInt(s)>=100000 && Integer.parseInt(s)<=999999){//kontroluje ci bike code splna zadane vlasnosti
				itemName1.addElement(s);//pridaho nalist
				if(freeze==0){//rozlisuje ci sa pridava existujucemu modelu alebo novemu 
					String data = (String) list.getSelectedValue();
					b.addBike_code(data,s);//prida bike_code do databazy
				}
				}
				else
					infoBox("Serila must be number \n between 100000 an 999999","Warnig!");
				}
				catch(Exception ex){
					infoBox("Serila must be number \n between 100000 an 999999","Warnig!");
				}
			}
		});
		btnAddSerial.setBounds(379, 132, 102, 23);
		contentPane.add(btnAddSerial);
		
		btnRemove = new JButton("Remove");//tlacitko ktore odstrani bike_code
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String data = (String) list_1.getSelectedValue();
				int index = list_1.getSelectedIndex();
			    if(index >= 0){
				itemName1.removeElement(data);//odstrani bike code z listu
				if(freeze==0){
					b.removeBike(data);//odstrani bike_code z databazy
				}
			    }
			}
		});
		btnRemove.setBounds(379, 166, 102, 23);
		contentPane.add(btnRemove);
		
		textField_1 = new JTextField();//textfiled do ktoreho sa zadava bike_code ktory sa ma pridat
		textField_1.setBounds(505, 133, 102, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		btnBack = new JButton("Back");//tlacitko pre navrat pri zadavani noveho modelu
		btnBack.setEnabled(false);
		btnBack.setVisible(false);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 addReturn();//vrati komponenty do povodneho stavu 
			}
		});
		btnBack.setBounds(363, 370, 89, 23);
		contentPane.add(btnBack);
		
		JLabel lblSerials = new JLabel("Serials:");// pomocny label
		lblSerials.setBounds(243, 115, 79, 18);
		contentPane.add(lblSerials);
		frame=this;
		
		log = new LoginGui(frame,c,b);//inicializacia premennych pre ostatne triedy
		log.setVisible(true);
		o=new Order(c,log.getlog());
		ad = new Add(frame,o);
		ad.setVisible(false);
		ad.setEnabled(false);
		b.setOrder(o);
		eq=new Equipment(c,o);
		res = new resGui(frame,o,eq);
		res.setVisible(false);
		res.setEnabled(false);
		
		menuBar = new JMenuBar();//vytvorenie hornej listy kde budu jednotlive polozky
		setJMenuBar( menuBar );
		menuStart = new JMenu( "Start" );//vytvorenie polozky start
		menuBar.add( menuStart );  //po jej odkliknuti sa zobrazia ostatne polozky
		menuNew = CreateMenuItem(menuStart,"Add item");//moznost pre pridanie noveho modelu
		menuNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addFreeze();//zmeni gui a pripravi ho na zadavanie noveho modelu
			}
		});
		
		
		menuCart = CreateMenuItem(menuStart,"Cart");//polozka pre kosik
		menuCart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {// po jej kliknuti sa otvori kosik
				res.returnGui();
				res.setVisible(true);
				res.setEnabled(true);
				frame.setEnabled(false);	
				res.setList();
				res.setList_1();
			}
		});
		menuCart.setVisible(false);
		menuCart.setEnabled(false);
		menuNewOr = CreateMenuItem( menuStart,"New Order");//moznost pre vytvorenie novej objednavky
		menuNewOr.setEnabled(false);
		menuNewOr.setVisible(false);
		menuNewOr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {//otvori sa okno na pridanie novej objednavky
				ad.inicialize();
				ad.setVisible(true);
				ad.setEnabled(true);
				frame.setEnabled(false);
			}
		});
		
		menuLoad = CreateMenuItem( menuStart,"Load Order");//moznost pre nacitanie existujucej objednavky
		menuLoad.setEnabled(false);
		menuLoad.setVisible(false);
		menuLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {//otvori sa okno kde sa vyberie existujuca objednaka
				
				res.changeGui();
				res.setVisible(true);
				res.setEnabled(true);
				frame.setEnabled(false);	
			}
		});
		
		menuAcount = new JMenu("Account");//vytvorenie polozky start
		menuBar.add(menuAcount);
		menuPaswd = CreateMenuItem( menuAcount,"Change paswd");//moznost pre zmenu hesla
		menuPaswd.addActionListener(new ActionListener() {// po kliknuti sa otvori okno kde kdes sa zmeni heslo
			public void actionPerformed(ActionEvent arg0) {
				log.setbacround(1);
				log.setVisible(true);
				log.setEnabled(true);
				frame.setEnabled(false);	
			}
		});
		menuLogOut = CreateMenuItem( menuAcount,"Log Out");//moznost pre odhlasenie
		menuLogOut.addActionListener(new ActionListener() {//po kliknuti sa pouzivtel odhlasi a otvori sa okno kde sa da prihlasit
			public void actionPerformed(ActionEvent arg0) {
				o.setOrder(0);
				log.getlog().logout();//odhlai pouzivatela
				ReturnGui();
				log.setbacround(0);
				log.setVisible(true);
				log.setEnabled(true);
				frame.setVisible(false);
				frame.setEnabled(false);
			}
		});
		b.inicializeCombobx(comboBox,comboBox_1);//inicializuje comboboxy
		
		checkbox= new JCheckBox("models with price less then average price");//check box pre vyber statistiky
		checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(checkbox.isSelected()==true){//ak check box true tak sa gui prisposobi na vypocitanie statistiky
					comboBox.setEnabled(false);
					comboBox_1.removeItemAt(0);
				}
				else{//gui sa vrati do povodneho stavu
					comboBox.setEnabled(true);
					comboBox_1.insertItemAt("all", 0);
				}
			}
		});
		checkbox.setBounds(10, 40, 322, 23);
		contentPane.add(checkbox);
		itemName2 = new DefaultListModel();
		list_2 = new JList(itemName2);//list kde sa zobrazi prislusenstvo 
		list_2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	      list_2.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
					 String s=(String) list_2.getSelectedValue(); //ked sa na prislusenstvo klikne tak sa zobrazia jej udeje v tabulke
					 eq.showItem(s,table_1);
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {	
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
	      });
		list_2.setBounds(673, 135, 125, 227);
		contentPane.add(list_2);
		
		JLabel lblEquipment = new JLabel("Equipment:");// pomocnu label
		lblEquipment.setBounds(673, 114, 87, 14);
		contentPane.add(lblEquipment);
		
		btnAdd_1 = new JButton("Add Item");//tlacidlo na pridanie prislusenstva do objednavky
		btnAdd_1.setEnabled(false);
		btnAdd_1.setVisible(false);
		btnAdd_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selected = (String)list_2.getSelectedValue();
				int index = list_2.getSelectedIndex();
			    if(index >= 0){ //iba ak je vybraty nejaky prvok v  liste
			    	itemName2.removeElementAt(index);//odstrani prislusenstvo z listu aby sa nestalo ze to iste prislusenstvo bude dva-krat v tej istej objednavke
			    	eq.addItem(selected);//prida vybrate prislusenstvo do objednavky
			    }
			}
		});
		btnAdd_1.setBounds(702, 370, 89, 23);
		contentPane.add(btnAdd_1);
		
		JScrollPane scrollPane_2 = new JScrollPane(list_2);//scroll pane pre list s prislusenstvom
		scrollPane_2.setBounds(673, 134, 125, 227);
		contentPane.add(scrollPane_2);
		
		table_1 = new JTable(2,2);//tabulka kde budu zobrazene informacie o prislusenstve 
		table_1.setEnabled(false);//nemoze sa do nej pisat
		table_1.setValueAt("Item",0, 0);
	    table_1.setValueAt("Price",0, 1);
		table_1.setBounds(505, 194, 158, 32);
		contentPane.add(table_1);
	}
	
	private void ShowPopup(MouseEvent e) {//zabezpeci vyskocenie kontexoveho menu po kliknuty misi
		popup.show(e.getComponent(),e.getX(), e.getY());
	    }
	
	public JMenuItem CreateMenuItem( JMenu menu, String sText)//vytvori pozku menu
	{
		JMenuItem menuItem;
		menuItem = new JMenuItem();
		menuItem.setText( sText );
		menu.add( menuItem );
		return menuItem;
	}
	
	public void UserGUI(){//prisposobi gui pre pouzivatela
		menuNewOr.setEnabled(true);
		menuNewOr.setVisible(true);
		menuLoad.setVisible(true);
		menuLoad.setEnabled(true);
		btnRemove.setVisible(false);
		btnRemove.setEnabled(false);
		btnAddSerial.setVisible(false);
		btnAddSerial.setEnabled(false);
		btnSaveChange.setEnabled(false);
		btnSaveChange.setVisible(false);
		table.setEnabled(false);
		menuCart.setVisible(true);
		menuCart.setEnabled(true);
		menuDel.setVisible(false);
		menuDel.setEnabled(false);	
		menuNew.setVisible(false);
		menuNew.setEnabled(false);
		menuAkt.setVisible(false);
		menuAkt.setEnabled(false);
		btnReserve.setVisible(true);
		btnAdd_1.setVisible(true);
		textField_1.setEnabled(false);
		textField_1.setVisible(false);
	}	
	
	private void ReturnGui(){//vrati gui do povodneho stavu, povodny stav je pre administratora/zamestnanca
		menuNewOr.setEnabled(false);
		menuNewOr.setVisible(false);
		menuLoad.setVisible(false);
		menuLoad.setEnabled(false);
		btnRemove.setVisible(true);
		btnRemove.setEnabled(true);
		btnAddSerial.setVisible(true);
		btnAddSerial.setEnabled(true);
		btnSaveChange.setEnabled(true);
		btnSaveChange.setVisible(true);
		table.setEnabled(true);
		menuCart.setVisible(false);
		menuCart.setEnabled(false);
	    btnAdd_1.setEnabled(false);
		btnAdd_1.setVisible(false);
		btnReserve.setVisible(false);
		btnReserve.setEnabled(false);
		menuAkt.setVisible(true);
		menuAkt.setEnabled(true);
		menuDel.setVisible(true);
		menuDel.setEnabled(true);
		menuNew.setVisible(true);
		menuNew.setEnabled(true);
		textField_1.setEnabled(true);
		textField_1.setVisible(true);
	}
	
	private void addFreeze(){//zmeni gui pre pridanie modelu
		freeze=1;
		btnBack.setEnabled(true);
		btnBack.setVisible(true);
		table.setValueAt("",1, 0);
	    table.setValueAt("",1, 1);
	    table.setValueAt("",1, 2);
	    table.setValueAt("",1, 3);
	    list.setEnabled(false);
	    list.setVisible(false);
		btnAdd.setVisible(true);
		btnAdd.setEnabled(true);
		btnSaveChange.setEnabled(false);
		btnSaveChange.setVisible(false);
	    itemName1.removeAllElements();
	}
	
private void addReturn(){//vrati gui do povodneho stavu po pridani modelu
	 freeze=0;
	btnBack.setEnabled(false);
	btnBack.setVisible(false);
	btnAdd.setVisible(false);
	btnAdd.setEnabled(false);
	table.setValueAt(" ",1, 0);
    table.setValueAt(" ",1, 1);
    table.setValueAt(" ",1, 2);
    table.setValueAt(" ",1, 3);
    list.setEnabled(true);
    list.setVisible(true);
    itemName1.removeAllElements();
    btnSaveChange.setEnabled(true);
	btnSaveChange.setVisible(true);
	}
public  void infoBox(String infoMessage, String titleBar)
{
    JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
}

protected void enableReserve(){
	btnReserve.setEnabled(true);
	btnAdd_1.setEnabled(true);
}
protected void sortItems(){
	eq.sortItems(itemName2);
	//else
		
}

protected void innicializeItems(){
	eq.inicializeItems(itemName2);
}

}
