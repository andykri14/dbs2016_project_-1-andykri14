package Logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextPane;

public class Bikes {
	private Connection c;
	private Order o;
public Bikes(Connection con){//konstruktor pre bikies
		c=con;
}
public void setOrder(Order o){//seter pre Order
	this.o=o;
}

public void inicializeCombobx(JComboBox comboBox,JComboBox comboBox_1){//naplni komponenty comboBox a comboBox_1 informaciami s databazy 
	comboBox.removeAllItems();
	comboBox_1.removeAllItems();
	comboBox.addItem("all");
	comboBox_1.addItem("all");
	PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("select znackan from znacka order by 1");//sql prikaz,ktory vyberie vsetky mena znaciek a zoradi ich  
		 ResultSet rs = stmt.executeQuery();
		 while ( rs.next() ) {
			 String  s = rs.getString("znackan");//vlozenie vstetkych mien znaciek do comboBox-u
			 comboBox.addItem(s);
		     }
		 stmt=c.prepareStatement("select typn from typ order by 1");//sql prikaz,ktory vyberie vsetky mena znaciek a zoradi ich	 
		 rs = stmt.executeQuery();
		 while ( rs.next() ) {
			 String  s = rs.getString("typn");//vlozenie vstetkych mien typov do comboBox_1-u
			 comboBox_1.addItem(s);
		     }
		     rs.close();
		     stmt.close();	
	} catch (SQLException e) {
		e.printStackTrace();
	} 
}

public void setAverage(DefaultListModel itemName,String typ){
	 PreparedStatement stmt = null;
	 itemName.clear();
	try {
		 stmt=c.prepareStatement("select m.nazov from model m" //sql prikaz ktory vyberie vsetky take modely z vybratej katogorie
					+" inner join typ t on t.id_typ=m.id_typ"  //ktorych cena je nizsia ako priemerna cena modelu v tej kategorii
					+" where cena < (select avg(m.cena) from typ t"
					+" inner join model m on m.id_typ=t.id_typ"
					+" where t.typn= ?"
					+" group by t.typn) and t.typn= ?");
		 stmt.setString(1,typ);//obidva argumenty su typ modelu
		 stmt.setString(2,typ);
		ResultSet rs = stmt.executeQuery();
		 while ( rs.next() ) {
		        String  name = rs.getString("nazov");//zobrazenie modelov ktore splnaju vysie uvedenu podmienku na JList
		        itemName.addElement(name);
		     }
		     rs.close();
		     stmt.close();
		
	} catch (SQLException e) {
		e.printStackTrace();
	}
}

public void inicialize(DefaultListModel itemName,String znacka,String typ){//metoda kde sa deje filtrovanie modelu podla typu a znacky
	 Statement stmt = null;          
	 String sql;
	 itemName.clear();
	 if(znacka.compareTo("all")==0 && typ.compareTo("all")==0)//ak moze znacka aj typ modela lubovolna tak sa ukaze vsetko
		 sql=" ";
	 else
	 if(znacka.compareTo("all")==0 && typ.compareTo("all")!=0)//ak je znacka lubovolna a typ je presne urecny
		 sql=" where t.typn = '"+typ+"' ";
	 else
	 if(znacka.compareTo("all")!=0 && typ.compareTo("all")==0)//ak je typ lubovolnz a znacka je presne urecna
		 sql=" where z.znackan= '"+znacka+"' ";
	 else
		 sql=" where t.typn= '"+typ+"' and z.znackan= '"+znacka+"' ";//ak je typ aj znacka presne urcena
	try {
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT m.nazov FROM model m" //vyberie modely ktory splnaju poziadavky 
		+ " inner join znacka z on m.id_znacka=z.id_znacka"
		+ " inner join typ t on m.id_typ=t.id_typ"+sql+"order by 1");
		 while ( rs.next() ) {
		        String  name = rs.getString("nazov");//modely ktore splnaju podmienku zobrazi na JListe
		        itemName.addElement(name);
		     }
		     rs.close();
		     stmt.close();
		
	} catch (SQLException e) {
		e.printStackTrace();
	}
}

public void itemInfo(String s,JTable table){//zobrazi informacie o vybratom modely na JTable
	PreparedStatement stmt = null; 
	 try {
		 stmt=c.prepareStatement("SELECT m.nazov,z.znackan, t.typn, m.cena "+//vyberie nazov, znacku, typ, a cenu vybrateho modelu na JTable
			        "from (znacka z inner join model m on z.id_znacka= m.id_znacka) "+
			        "inner join typ t on t.id_typ=m.id_typ "+
					"where m.nazov=?");
		 stmt.setString(1,s);
		 ResultSet rs = stmt.executeQuery();
		 while ( rs.next() ) {
		        String  name = rs.getString("nazov");//navov modelu
		        String  name1 = rs.getString("znackan");//znacka modelu
		        String  name2 = rs.getString("typn");//typ modelu
		        String  name3 = rs.getString("cena");//cena modelu 
			    table.setValueAt(name1,1, 0);//vybrane atributy sa zobrazia na JTable
			    table.setValueAt(name2,1, 2);
			    table.setValueAt(name,1, 1);
			    table.setValueAt(name3,1, 3);
		     }
		     rs.close();
		     stmt.close();
		
	} catch (SQLException e) {
		e.printStackTrace();
	}
	
}

public void inicializel(String model,DefaultListModel itemName){//filter pre bike_code
	 PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("select b.bike_code from bike b"
		 +" inner join model m on m.id_model=b.id_model"
		 +" where bike_code not in (Select b.bike_code from bike b"//zobrazia len tie bike_code-y(cisla jednotlivych kusov modelu)
		 +" inner join polozka_ob ob on ob.id_bike=b.id_bike"//ktore nie su v case akualnej obiednavke reservovane pre inu obiednavku
		 +" inner join objednavka o on o.id_objednavka=ob.id_objednavka"//nestane sa, ze konkretny bike bude v dvoch objednavkach, ktorych casy sa krizuju
		 +" where  ((select date_from from objednavka where id_objednavka= ?) between o.date_from and (o.date_to-1)) or" //tu sa kontroluje ci je cas zaciatku pozicanie aktualnej objednavky medzi intervalom pozicanie  inej objednavky
		 +" ((select (date_to-1) from objednavka where id_objednavka= ?) between o.date_from and (o.date_to-1))) and m.nazov = ? order by 1");//tu sa kontroluje ci je cas konca pozicanie aktualnej objednavky medzi intervalom pozicanie  inej objednavky
		 stmt.setInt(1,o.getOrder());//vrati id aktualnej objednavky
		 stmt.setInt(2,o.getOrder());//vrati id aktualnej objednavky
		 stmt.setString(3,model);
		 ResultSet rs = stmt.executeQuery();
		 while ( rs.next() ) {
			    int code = rs.getInt("bike_code");
		        itemName.addElement(String.valueOf(code));//uloznie bike_code-u ktory splna poziadavky na JList
		     }
		     rs.close();
		     stmt.close();
		
	} catch (SQLException e) {
		e.printStackTrace();
	}
	 
}

public void changeNazov(String old,String newName,String cena,DefaultListModel itemName){//zmeni nazov a cenu vybrateho modela
	PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("UPDATE model set nazov = ?,cena = ? where nazov= ?");//udpate nazvu a ceny vybrateho  modela
		 stmt.setString(1,newName);//nove meno modela
		 stmt.setFloat(2,Float.parseFloat(cena));//nova cena modelu
		 stmt.setString(3,old);//stare meno modela
		 stmt.executeUpdate();
         stmt.close();
         c.commit();	
	} catch (SQLException e) {
		try{
		c.rollback();
		}catch (SQLException ex) {
			e.printStackTrace();
		}
		infoBox(e.getMessage(), e.getClass().getName());
		e.printStackTrace();
	} 
	 itemName.add(itemName.indexOf(old),newName);//vymena stareho nazvu modela za novy
	 itemName.removeElement(old);
}
public void changeZnacka(String nazov,String newZnacka){//zmeni znacku vybrateho modela
	int id;
	PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("SELECT id_znacka from znacka where znackan= ?");
		 stmt.setString(1,newZnacka);
		 ResultSet rs = stmt.executeQuery();
		if(rs.next()==false){//najprv skontroluje ci nova znacka modelu uz existuje, ak nie tak ju najprv prida do zaznamov znaciek
			 stmt=c.prepareStatement("INSERT INTO znacka (znackan) "  //pridanie nove znacky do znaciek
				                   + "VALUES (?) RETURNING id_znacka;");//vrati id novej znacky
			 stmt.setString(1,newZnacka);
			 rs = stmt.executeQuery();
			 rs.next();
			 id = rs.getInt("id_znacka");
		}  
		else
		id = rs.getInt("id_znacka");
	   rs.close();
	   stmt=c.prepareStatement("UPDATE model set id_znacka = ? where nazov= ?");//zmeni id znacky v modeli na id vybratej znacky
	   stmt.setInt(1,id);//id vybratej znacky
	   stmt.setString(2,nazov);//nazov modelu
	   stmt.executeUpdate();
       stmt.close();
       c.commit();		
	} catch (SQLException e) {
		try{
			c.rollback();
			}catch (SQLException ex) {
				e.printStackTrace();
			}
		e.printStackTrace();
	}	
}

public void changeTyp(String nazov,String newTyp){//zmeni typ vybrateho modela
	int id;
	PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("SELECT id_typ from typ where typn = ?");
		 stmt.setString(1,newTyp);
		 ResultSet rs = stmt.executeQuery();
		if(rs.next()==false){//najprv skontroluje ci novy typ modelu uz existuje, ak nie tak ho najprv prida do zaznamov typov
			 stmt=c.prepareStatement("INSERT INTO typ (typn) "//pridanie noveho typu do typov
		             + "VALUES (?) RETURNING id_typ;");//vrati id noveho typu
			 stmt.setString(1,newTyp);
			 rs = stmt.executeQuery();
			 rs.next();
			 id = rs.getInt("id_typ");
		}  
		else
		 id = rs.getInt("id_typ");
		rs.close();
		stmt=c.prepareStatement("UPDATE model set id_typ = ? where nazov = ?"); //zmeni id typu v modeli na id vybrateho typu
		stmt.setInt(1,id);//id vybrateho typu		
		stmt.setString(2,nazov);//nazov modelu		
		stmt.executeUpdate();
        stmt.close();
        c.commit();
		
	} catch (SQLException e) {
		try{
			c.rollback();
			}catch (SQLException ex) {
				e.printStackTrace();
			}
		e.printStackTrace();
	}	
}

public void delete(String name){//vymaze vybrati model byku a vstky jeho kusy
	 PreparedStatement stmt = null,stmt1 = null;
	 try {
		 stmt=c.prepareStatement("SELECT b.bike_code "+ //vyberie vsetky kusy vybrateho modelu
			       "from model m  inner join bike b on m.id_model= b.id_model "+
			       "where m.nazov= ? ");
		 stmt.setString(1,name);//nazov modelu
		ResultSet rs = stmt.executeQuery();
		while ( rs.next() ) {
			int code=rs.getInt("bike_code");	
			 stmt1=c.prepareStatement("DELETE from bike where bike_code= ? ");//vymaze vsetky kusy(bike_code) vybrateho modelu
			 stmt1.setInt(1,code);//bike_code
			stmt1.executeUpdate();				       
		}
		rs.close();
		stmt=c.prepareStatement("DELETE from model where nazov = ?");//nakoniec vymaze aj vybraty model s databazy
		stmt.setString(1,name);//nazov modelu
		stmt.executeUpdate();
        stmt.close();
        c.commit();
		
	} catch (SQLException e) {
		try{
			c.rollback();
			}catch (SQLException ex) {
				e.printStackTrace();
			}
		e.printStackTrace();
	} 
}

public void addNazov(String newName,String cena,DefaultListModel itemName){//prida novy zaznama do modelu
	PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("INSERT INTO model (nazov,cena) VALUES (?,?)");//
		 stmt.setString(1,newName);//nazov noveho modelu
		 stmt.setFloat(2,Float.parseFloat(cena));//cena noveho modelu
		 stmt.executeUpdate();
         stmt.close();
         c.commit();	
         itemName.addElement(newName);
	} catch (SQLException e) {
		try{
			c.rollback();
			}catch (SQLException ex) {
				e.printStackTrace();
			}
		e.printStackTrace();
		infoBox(e.getMessage(), e.getClass().getName());
	} 
}

public void addBike(String nazov,DefaultListModel itemName){//prida bike_cody k novemu modelu
	PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("SELECT id_model "//vyberie id modelu
			       +"from model where nazov = ?");
		 stmt.setString(1,nazov);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int id = rs.getInt("id_model");
		for(int i=0;i<itemName.size();i++){
			String s=(String)itemName.elementAt(i);
			stmt=c.prepareStatement("INSERT INTO bike (bike_code,id_model) "//vytvara zadane bike_cody k vybranemu modelu
				    +"VALUES(?,?)");
			stmt.setInt(1,Integer.parseInt(s));//bike_code zadany pouzivatelom
			stmt.setInt(2,id);//id model ku ktoremu novy bike_code ma patrit
			stmt.executeUpdate();
		}
       stmt.close();
       c.commit();
		
	} catch (SQLException e) {
		try{
			c.rollback();
			}catch (SQLException ex) {
				e.printStackTrace();
			}
		e.printStackTrace();
		infoBox(e.getMessage(), e.getClass().getName());
	} 
}

public void addBike_code(String nazov,String bike_code){//prida bike_code k uz existujucemu modelu
	PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("INSERT INTO bike (bike_code,id_model) "//vytvori novy bike
				    +"VALUES(?,(SELECT id_model from model where nazov= ?))");//najde id_modelu podla nazvu modelu
		 stmt.setInt(1,Integer.parseInt(bike_code));//zadany bike_cod
		 stmt.setString(2,nazov);//nazov modelu
		 stmt.executeUpdate();
         stmt.close();
         c.commit();	
	} catch (SQLException e) {
		try{
			c.rollback();
			}catch (SQLException ex) {
				e.printStackTrace();
			}
		infoBox(e.getMessage(), e.getClass().getName());
		e.printStackTrace();
	} 
}

public void removeBike(String bike_code){//odstrani vybreny kus daneho modelu 
	PreparedStatement stmt = null;
	 try {
	    stmt=c.prepareStatement("delete from bike where bike_code=?");//vymaze bike podla bike_codu
	    stmt.setInt(1,Integer.parseInt(bike_code));
		stmt.executeUpdate();
        stmt.close();
        c.commit();	
	 }catch (SQLException e) {
		 try{
				c.rollback();
				}catch (SQLException ex) {
					e.printStackTrace();
				}
		e.printStackTrace();
	 } 
}

public  void infoBox(String infoMessage, String titleBar){//chybova hlaska
    JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
}
}
