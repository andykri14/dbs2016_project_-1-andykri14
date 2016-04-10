package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumnModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;

import Logic.Equipment;
import Logic.Order;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.JLabel;

public class resGui extends JFrame {

	private JPanel contentPane;
	final DefaultListModel itemName,itemName1;
	private resGui res;
	private JButton btnRemove,btnConfirm,btnBack,btnLoad,btnRemoveI;
	private Order o;
	private JTable table;
	private int mode;
	private JList list_1;
	private JLabel lblCena;
	private JTable table_1;
	private final Equipment eq;
	private JScrollPane scrollPane_1;
	public resGui(final Gui frame,final Order o,final Equipment eq) {//okno kde sa vybera existujuca objednavka alebo sa zobrazuju kosik
		setResizable(false);
		this.o=o;
		res=this;
		this.eq=eq;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 508, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		itemName = new DefaultListModel();
		final JList list = new JList(itemName);//list kde sa zobrazuju jednotlive biky v objednavke alebo neukoncene objednavky 
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	      list.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				 
					 String s=(String) list.getSelectedValue(); 
					 if(mode==1)//ak je mod 1 tak sa okno pouziva ako kosik, ak je 0 tak sa v okne vybera z nedokoncenych objednavok 
					 o.showItem(s,table);//zaobrazi informacie o jednotlivych bikok na table
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
		list.setBounds(24, 25, 136, 205);
		contentPane.add(list);
		
		JScrollPane scrollPane = new JScrollPane(list);//scrollbar pre list
		scrollPane.setBounds(24, 25, 136, 205);
		contentPane.add(scrollPane);
		
		 btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String s=(String)list.getSelectedValue();
				int index = list.getSelectedIndex();
			    if(index >= 0){ //odtrani iba ak je vybrati bike z listu
			    	itemName.removeElementAt(index);//odstrani vybrany bike z listu
			    	o.deleteOrderItem(s,lblCena);//odstrani vybraty bike z objednavky a upravi jej cenu
			    }
			}
		});
		btnRemove.setBounds(189, 23, 89, 23);
		contentPane.add(btnRemove);
		
		btnConfirm = new JButton("Confirm");//dokonci objednavku
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblCena.setText("");
				o.completeOrder();//zmeni stav objednavky v datavaze na dokonecnu
				frame.setEnabled(true);
				res.setVisible(false);
				res.setEnabled(false);
				itemName.removeAllElements();
			}
		});
		btnConfirm.setBounds(48, 238, 89, 23);
		contentPane.add(btnConfirm);
		
		btnBack = new JButton("Back");//navrat na hlavnu obrazovku
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblCena.setText("");
				itemName.removeAllElements();
				itemName1.removeAllElements();
				frame.setEnabled(true);//zobrazi sa hlavne okno, aktualne okno sa zatvori
				res.setVisible(false);
				res.setEnabled(false);
			}
		});
		btnBack.setBounds(233, 238, 89, 23);
		contentPane.add(btnBack);
		
		btnLoad = new JButton("Load");//nacita vybranu objednavku
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String data = (String) list.getSelectedValue();
				int index = list.getSelectedIndex();
			    if(index >= 0){//iba ak je vybrata konkretna objednavka
			    	o.setOrder(data);//nastavi vybratu objednavku ako aktualnu
			    	frame.sortItems();
					frame.setEnabled(true);//zobrazi sa hlavne okno, aktualne okno sa zatvori
					res.setVisible(false);
					res.setEnabled(false);
					itemName.removeAllElements();	
					frame.enableReserve();//povoli pridavanie bikov a prislusenstva da objednavky
			    }
			    else
			    	infoBox("Please select order or create new one","error");
			}
		});
		btnLoad.setBounds(48, 238, 89, 23);
		contentPane.add(btnLoad);
		
		table = new JTable(2,4);//tabulka, kde se budu vypisovat informacie o bikoch
		table.setValueAt("Brand",0, 0);//v prvom riadku su nazvy stlpcov
	    table.setValueAt("Model",0, 1);
	    table.setValueAt("Type",0, 2);
	    table.setValueAt("Price",0, 3);
	    table.setEnabled(false);//do tabulky sa neda pisat
		table.setBounds(189, 68, 293, 32);
		contentPane.add(table);
		
		lblCena = new JLabel(" ");//label kde sa zobrazi cena objednavky
		lblCena.setBounds(189, 189, 126, 14);
		contentPane.add(lblCena);
		
		table_1 = new JTable(2,2);//tabulka kde sa zobrazia informacie o prislusenstve
		table_1.setEnabled(false);//do tabulky sa neda pisat
		TableColumnModel colMdl = table_1.getColumnModel();//zmeni sirku stlpcou 
		colMdl.getColumn(0).setPreferredWidth(130);
		colMdl.getColumn(1).setPreferredWidth(70);
		table_1.setValueAt("Item",0, 0);//v prvom riadku su nazvy stlpcov
	    table_1.setValueAt("Price",0, 1);
		table_1.setBounds(189, 111, 150, 32);
		contentPane.add(table_1);
		itemName1 = new DefaultListModel();
		list_1 = new JList(itemName1);//v tomto liste je prislusenstvo objednavky
		list_1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	      list_1.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {//po kliknuti na prislusenstvo sa zobrazia jeho atributy v tabulke
				
					 String s=(String) list_1.getSelectedValue(); 
					 eq.showItem(s,table_1);//zobrazi atributy prislusenstva v tabulke
				
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
		list_1.setBounds(368, 111, 114, 119);
		contentPane.add(list_1);
		
		scrollPane_1 = new JScrollPane(list_1);//scroll pane pre list kde je prislusenstvo
		scrollPane_1.setBounds(368, 111, 114, 119);
		contentPane.add(scrollPane_1);
		
		btnRemoveI = new JButton("Remove Item");//tlacitko, ktore odstrani vybrane prislusenstvo z objednavky
		btnRemoveI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String s=(String)list_1.getSelectedValue();
				int index = list_1.getSelectedIndex();
			    if(index >= 0){ //skontroluje ci je vybraty prvok z listu 
			    	itemName1.removeElementAt(index);//odstrani vybrate prislusenstvo z listu
			    	eq.deleteOrderItem(s,lblCena);//odstrani vybrate prislusenstvo z objednavky
			    	frame.sortItems();//vymazane prislusenstvo z objednavky sa moze znova pouzit
			    }
			}
		});
		btnRemoveI.setBounds(368, 238, 114, 23);
		contentPane.add(btnRemoveI);
	}
	public void add(String s){
		itemName.addElement(s);
	}
	
	protected void changeGui(){//okno sa zmeni na vynratie existujucej objednavky
		mode=0;
		o.inicializeList(itemName);
		btnLoad.setEnabled(true);
		btnLoad.setVisible(true);
		btnConfirm.setEnabled(false);
		btnConfirm.setVisible(false);
		btnRemove.setEnabled(false);
		btnRemove.setVisible(false);
		table.setVisible(false);
		table_1.setVisible(false);
		list_1.setEnabled(false);
		list_1.setVisible(false);
		btnRemoveI.setEnabled(false);
		btnRemoveI.setVisible(false);
		scrollPane_1.setEnabled(false);
		scrollPane_1.setVisible(false);
	}
	
    protected void returnGui(){//okno sa vrati do povodneho stavu teda sa zobrazi ako kosik
    	mode=1;
    	table.setVisible(true);
    	btnLoad.setEnabled(false);
		btnLoad.setVisible(false);
		btnConfirm.setEnabled(true);
		btnConfirm.setVisible(true);
		btnRemove.setEnabled(true);
		btnRemove.setVisible(true);
		table_1.setVisible(true);
		list_1.setEnabled(true);
		list_1.setVisible(true);
		btnRemoveI.setEnabled(true);
		btnRemoveI.setVisible(true);
		scrollPane_1.setEnabled(true);
		scrollPane_1.setVisible(true);
		}
  protected void setList(){//nastavi list bikov
	  o.fillCart(itemName,lblCena);
  }
  protected void setList_1(){//nastavi list prislusenstva
	  eq.inicializeCart(itemName1);
  }
  public  void infoBox(String infoMessage, String titleBar)//vypis spravy
  {
      JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
  }
}
