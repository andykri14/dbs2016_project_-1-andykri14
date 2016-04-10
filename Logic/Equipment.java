package Logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JTable;

public class Equipment {//trieda kde su sql operacie pre prislusenstvo a zoznam_prislusenstva
private Connection c;
private Order o;

public Equipment(Connection con,Order o){//konstruktor pre triedu Equipment
	c=con;
	this.o=o;
	
}	
public void inicializeItems(DefaultListModel itemName){// metoda kde sa ulozia vsetky prislusenstva do JListu
	PreparedStatement stmt = null;
	 try {
		 itemName.clear();
		 stmt=c.prepareStatement("select nazov from prislusenstvo order by 1");//vyberie prislusenstvo a zoradi ho
		 ResultSet rs = stmt.executeQuery();
		 while ( rs.next() ) {
			 String  s = rs.getString("nazov");//nazov prislusenstva
			 itemName.addElement(s);//prida ho do JListu
		     }
		     rs.close();
		     stmt.close();
		
	} catch (SQLException e) {
		e.printStackTrace();
	} 
}

public void sortItems(DefaultListModel itemName){//vytriedi prislusenstvo 
 PreparedStatement stmt = null;
	 try {
		 itemName.clear();
		 stmt=c.prepareStatement("select p.nazov from prislusenstvo p"
				         +" where p.nazov not in (select p.nazov from prislusenstvo p"//zobrazi sa len to prislusenstvo
						 +" inner join zoznam_prislusenstva z on p.id_prislusenstvo=z.id_prislusenstvo"//ktore nie je v case akualnej obiednavke reservovane pre inu obiednavku
						 +" inner join objednavka o on o.id_objednavka=z.id_objednavka"//nestane sa, ze konkretne prislusenstvo bude v dvoch objednavkach, ktorych casy sa krizuju
						 +" where  ((select date_from from objednavka where id_objednavka= ?) between o.date_from and (o.date_to-1)) or"//tu sa kontroluje ci je cas zaciatku pozicanie aktualnej objednavky medzi intervalom pozicanie  inej objednavky
						 +" ((select (date_to-1) from objednavka where id_objednavka= ?) between o.date_from and (o.date_to-1))) order by 1");//tu sa kontroluje ci je cas konca pozicanie aktualnej objednavky medzi intervalom pozicanie  inej objednavky
		 stmt.setInt(1,o.getOrder());//id aktualnej objednavky
		 stmt.setInt(2,o.getOrder());
		 ResultSet rs = stmt.executeQuery();
		 while ( rs.next() ) {
			    String s = rs.getString("nazov");
		        itemName.addElement(s);//priradavanie prislusenstva ktore splna podmienky do JListu
		     }
		     rs.close();
		     stmt.close();
		
	} catch (SQLException e) {
		e.printStackTrace();
	}
}

public void addItem(String nazov){//prida vybrate prislusenstvo do objednavky
	PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("insert into zoznam_prislusenstva (id_prislusenstvo,id_objednavka)"
				 +" values ((select id_prislusenstvo from prislusenstvo where nazov = ?),?)");//vyberie id prislusenstva podla jeho nazvu
		 stmt.setString(1,nazov);
		 stmt.setInt(2,o.getOrder());
		 stmt.executeUpdate();
		 
		 stmt=c.prepareStatement("update objednavka set cena_objednavky=(select cena_objednavky" //zvysi cenu objednavky do ktorej sa pridalo prislusenstvo
				 +" from objednavka where id_objednavka=?)+"// o cenu vybrateho prislusesntva krat pocet dni na ako dloho bude zapozicane 
				 +" ((select cena from prislusenstvo where nazov=?)*(select (date_to - date_from)"
				 +" from objednavka where id_objednavka=?)) where id_objednavka=?");
		 stmt.setInt(1,o.getOrder());//id aktualnej objednavky
		 stmt.setString(2,nazov);//nazov prislusenstva
		 stmt.setInt(3,o.getOrder());
		 stmt.setInt(4,o.getOrder());
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

public void showItem(String nazov,JTable table){//vypise udaje o vybratom prislusenstve 
	PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("select cena from prislusenstvo where nazov = ?");//najde cenu prislusenstva podla nazvu
		 stmt.setString(1,nazov);//nazov prislusestva
		 ResultSet rs= stmt.executeQuery();
		 rs.next();
		 String cena=rs.getString("cena");
		 table.setValueAt(nazov,1, 0);//jednotlive atributy vypise do JTable
		 table.setValueAt(cena,1, 1);
         rs.close();
		 stmt.close();
	} catch (SQLException e) {
		e.printStackTrace();
	}
}

public void inicializeCart(DefaultListModel itemName){//vypise prislusestnsvo, ktore je v danej objednavke
	PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("select p.nazov from prislusenstvo p"//vyberie prislusenstvo podla toho ci je vaktualnej objednavku
				 +" inner join zoznam_prislusenstva z on p.id_prislusenstvo=z.id_prislusenstvo"
				 +" where z.id_objednavka= ?");
		 stmt.setInt(1,o.getOrder());//id aktualnej objednavky
		 ResultSet rs= stmt.executeQuery();
		 while( rs.next()){
		 String nazov=rs.getString("nazov");
		 itemName.addElement(nazov);
		 }
        rs.close();
		stmt.close();
	} catch (SQLException e) {
		e.printStackTrace();
	}
}

public void deleteOrderItem(String s,JLabel l){//vymaze vybrate prislusenstvo s objednavky
	PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("delete from zoznam_prislusenstva"//vymaze prislusenstvo z objednavky 
		 +" where id_objednavka= ? and id_prislusenstvo=(select id_prislusenstvo from prislusenstvo where nazov = ?)");//podla jeho nazvu
		 stmt.setInt(1,o.getOrder());//id aktualnej obiednavky
		 stmt.setString(2,s);//nazov prislusenstva
		 stmt.executeUpdate();
		 
		 stmt=c.prepareStatement("update objednavka set cena_objednavky=(select cena_objednavky"//upravi cenu biednavky tak, 
				 +" from objednavka where id_objednavka=?)-"//ze odpocita cenu odstraneneho prislusenstva krat pocet dni na ako dlho malo byt zapozicane
				 +" ((select cena from prislusenstvo where nazov=?)*(select (date_to - date_from)"
				 +" from objednavka where id_objednavka=?)) where id_objednavka=? returning cena_objednavky");
		 stmt.setInt(1,o.getOrder());//id aktualnej obiednavky
		 stmt.setString(2,s);//nazov prislusenstva
		 stmt.setInt(3,o.getOrder());
		 stmt.setInt(4,o.getOrder());
		 ResultSet rs=stmt.executeQuery();
		 rs.next();
		 String cena=rs.getString("cena_objednavky");
		 l.setText("Total sum: "+cena);
		 rs.close();
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
}
