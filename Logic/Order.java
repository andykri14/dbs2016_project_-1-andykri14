package Logic;

import java.awt.Label;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JTable;

public class Order {
    private int order=0;
    private Login l;
	private Connection c;
public Order(Connection con, Login l){//konstruktor pre order
		 this.l=l;
	     c=con;
			
}
public void setOrder(int i){//seter pre order
		order=i;
}

public int getOrder(){//geter pre order
	return order;
}	

public String getActualDate(String s){//vrati aktualny cas vo zvolenom formate	
	String timeStamp = new SimpleDateFormat(s).format(Calendar.getInstance().getTime());
	return timeStamp;
}

public void addOrder(String dateFrom, String dateTo){//metoda kde sa vytvori nova objednavka pre zakaznika
	PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("INSERT INTO objednavka "
				+ "(id_user,date_from,date_to,stav,cena_objednavky)"
				+ " VALUES (?,?,?,0,0) RETURNING id_objednavka;");//vrati id vytvorenej objednavky
		stmt.setInt(1,l.getId_user());//id aktualneho usera
		stmt.setDate(2,Date.valueOf(dateFrom));//datum kedy sa zacina rezervacia poloziek
		stmt.setDate(3,Date.valueOf(dateTo));//datum kedy sa konci pozicie poloziek
		ResultSet rs =stmt.executeQuery();
		rs.next();
		order = rs.getInt("id_objednavka");//nastavy actualnu objednavku na id prave vytvorenej objednavky
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

public void inicializeList(DefaultListModel itemName){//vytvori zoznam nedokoncenych objednavok pre usera
	PreparedStatement stmt = null;
	 try {
		//stmt = c.createStatement();
		stmt=c.prepareStatement("select id_objednavka from objednavka where id_user= ? and stav=0 order by 1");//vyberu sa neukoncene objednavky pre prihlaseneho usera
		stmt.setInt(1,l.getId_user());//id aktualne usera
		ResultSet rs = stmt.executeQuery();
		 while ( rs.next() ) {
			    int id = rs.getInt("id_objednavka");//id objednavok ktore splnaju podmienky
		        itemName.addElement(String.valueOf(id));//vlozi id-cka do JListu
		     }
		     rs.close();
		     stmt.close();
	} catch (SQLException e) {
		e.printStackTrace();
	}
}

 public void setOrder(String actOrder){//seter pre order s argumentom String
	 order=Integer.valueOf(actOrder);
 }
 
 public void addOrderItem(String bike_code){//metoda kde sa prida bike do objednavky
	 PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("INSERT INTO polozka_ob (id_bike,id_objednavka)"//vytvori sa nova polozka_objednavky
		 + " VALUES ((select id_bike  from bike where bike_code= ?),?)");
		 stmt.setInt(1,Integer.parseInt(bike_code));//pripocita sa cena pridaneho modelu krat pocet dni vypozicky
		 stmt.setInt(2,order);//id aktualnej objednavky
		stmt.executeUpdate();
		stmt=c.prepareStatement("UPDATE objednavka set cena_objednavky =((select sum(p.cena) from prislusenstvo p"
        +" inner join zoznam_prislusenstva z on p.id_prislusenstvo=z.id_prislusenstvo"
        +" inner join objednavka o on o.id_objednavka=z.id_objednavka"
        +" where o.id_objednavka= ?)+ (select sum(m.cena) from model m"
		+" inner join bike b on b.id_model=m.id_model"
		+" inner join polozka_ob ob on ob.id_bike= b.id_bike"
		+" inner join objednavka o on o.id_objednavka=ob.id_objednavka"
		+" where o.id_objednavka=?))*(select (date_to - date_from) from objednavka where id_objednavka=?)"
		+" where id_objednavka=?");
		stmt.setInt(1,order);//id aktualnej objednavky
		stmt.setInt(2,order);
		stmt.setInt(3,order);
		stmt.setInt(4,order);
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
 
 public void deleteOrderItem(String bike_code,JLabel l){//metoda v ktorej sa vymaze vyvbraty bike s objednavky
	 PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("delete from polozka_ob where id_objednavka =  ?"//vymaze sa vybrati bike z danej objednavky na zaklade bike_codu
					+" and id_bike=(select id_bike from bike where bike_code= ?)");
		 stmt.setInt(1,order);//id aktualnej objednavky
		 stmt.setInt(2,Integer.parseInt(bike_code));
		 stmt.executeUpdate();
		
		 stmt=c.prepareStatement("UPDATE objednavka set cena_objednavky = cena_objednavky-(select (date_to - date_from) from objednavka where id_objednavka=?)*(select m.cena from model m" 
					+" inner join bike b on b.id_model=m.id_model"//odpocita z ceny objednavky cenu modelu ktory sa vyhodi krat pocet dni zapozicania
					+" where bike_code= ?) where id_objednavka= ? returning cena_objednavky");//vrati cenu objednavky
		 stmt.setInt(1,order);
		 stmt.setInt(2,Integer.parseInt(bike_code));
		 stmt.setInt(3,order);
		 ResultSet rs = stmt.executeQuery();
		 rs.next();
		 float cena=rs.getFloat("cena_objednavky");
		 l.setText("Total sum: "+cena);//vypise zmenenu cenu objednavky na label
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
 
 
 public void fillCart(DefaultListModel itemName,JLabel l){//priradi do jlistu vsetky polozky objednavky
	 PreparedStatement stmt = null;
	 if(order!=0){
	 try {
		 stmt=c.prepareStatement("select b.bike_code from bike b"//vyberie bike_code ktore su v danej objednake
					+" inner join polozka_ob p on p.id_bike=b.id_bike"
					+" where p.id_objednavka=? order by 1");
				 stmt.setInt(1,order);//id aktualnej objednavky
		 ResultSet rs = stmt.executeQuery();
		 while ( rs.next() ) {
			 int  name = rs.getInt("bike_code");//vyberie bike_cody ,ktore splnaju poziadavku
		     itemName.addElement(String.valueOf(name));//prida vybrate bike_code do listu
		     }
		 stmt=c.prepareStatement("select cena_objednavky from objednavka where id_objednavka =?");//vyberie cenu aktualnej objednavky
			stmt.setInt(1,order);//id aktualnej bjednavky
			 rs = stmt.executeQuery();
			 rs.next();
			 float cena=rs.getFloat("cena_objednavky");
			 l.setText("Total sum: "+cena);//vypise cenu na label
		     rs.close();
		     stmt.close();
		
	} catch (SQLException e) {
		e.printStackTrace();
	} 
	}
 }
 
 public void completeOrder(){//metoda kde sa dokonci objednavka
	 PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("UPDATE objednavka set stav = 1 where id_objednavka = ?");//zmeni stav vybranej objednavky na 1(dokoncene objednavka), nedokoncena objednavka ma stav 0
		 stmt.setInt(1,order);//id aktualnej objednavky
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
 
 public void showItem(String code,JTable table){//ukaze popis biku v kosiku
	 PreparedStatement stmt = null;
	 try {
		 stmt=c.prepareStatement("Select z.znackan,m.nazov,t.typn,m.cena from bike b"//vyberie znacku, nazov modelu, typ a cenu vybraneho biku
         + " inner join model m on m.id_model=b.id_model"
         + " inner join znacka z on z.id_znacka=m.id_znacka"
         + " inner join typ t on t.id_typ=m.id_typ"
         + " where b.bike_code= ?");
		 stmt.setInt(1,Integer.parseInt(code));
		 ResultSet rs= stmt.executeQuery();
		 rs.next();
		 String znacka=rs.getString("znackan");
		 String model=rs.getString("nazov");
		 String typ=rs.getString("typn");
		 String cena=rs.getString("cena");
		 table.setValueAt(znacka,1, 0);//pridanie znacky do tabulky
		 table.setValueAt(typ,1, 2);//pridanie typu do tabulky
		 table.setValueAt(model,1, 1);//pridanie modelu do tabulky
		 table.setValueAt(cena,1, 3);  //pridanie ceny do tabulky
         stmt.close();
         c.commit();	
	} catch (SQLException e) {
		e.printStackTrace();
	}  
 }
}
