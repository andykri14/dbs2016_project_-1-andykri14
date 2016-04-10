package Logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JOptionPane;

public class Login {
private	Connection c;
private int act=0,type=0;
public Login(Connection c){
	this.c=c;
}
public int log(String name,String paswd)	{//metoda ktora vykonava prihlasenie 
	int id=0;
	//Connection c;
	 Statement stmt = null;
	 int ID=0;
	 try {
	 stmt = c.createStatement();
     ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS;" );//vybera meno,heslo, id a typ pouzivate s users
     while ( rs.next() ) {
        String  dname = rs.getString("NAME");//vyberie meno 
        String  dpaswd = rs.getString("PASWD");//vyberie heslo
        if((name.compareTo(dname)==0) && (paswd.compareTo(dpaswd)==0)){//ak sa meno zhoduje zo zadanym menom a heslo zo zadanym heslom
        	ID=1; 
        	type=rs.getInt("TYPE");//tak sa nastavy typ pouzivtela (typ moze byt 0-admistrator a 1-normalny pouzivatel )
        	 act=rs.getInt("idus");//a nastavi sa act na id daneho pouzivatela
        }
     }
     rs.close();
     stmt.close();
      //c.commit();
	 }catch ( Exception e ) {
         System.err.println( e.getClass().getName()+": "+ e.getMessage() );
       }
	
	return ID;
}

public void registration(String name,String email,String paswd){//zaregitruje noveho pouzivatela
	PreparedStatement stmt;
	try {	
		stmt = c.prepareStatement("INSERT INTO USERS (NAME,EMAIL,PASWD,TYPE) "
				+ "VALUES (?,?,?,?)");//vytvori noveho pouzivatela zo zadanych udajov(meno,heslo,email), typ pouzivatela je 1
		stmt.setString(1,name);
		stmt.setString(2,email);
		stmt.setString(3,paswd);
		stmt.setInt(4,1);
		stmt.executeUpdate();
        stmt.close();
        c.commit();
	}catch ( Exception e ) {
		try{
			c.rollback();
			}catch (SQLException ex) {
				e.printStackTrace();
			}
         //System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		infoBox(e.getMessage(), e.getClass().getName());
    }
}
public void logout(){//odlasi pouzivatela
	act=0;//zmeni aktualneho pouzivatela na 0
	System.out.print(act);
}
public void changepaswd(String paswd){//metoda na zmenu hesla
	PreparedStatement stmt = null;
	try {	
		stmt=c.prepareStatement("UPDATE USERS set PASWD = ? where idus= ?");//zmeni stare heslo na nove heslo zadane uzivatelom
		stmt.setString(1,paswd);//nove heslo
		stmt.setInt(2,act);//id prihlaseneho uzivatela
		stmt.executeUpdate();
        stmt.close();
        c.commit();
	}catch ( Exception e ) {
		try{
			c.rollback();
			}catch (SQLException ex) {
				e.printStackTrace();
			}
         System.err.println( e.getClass().getName()+": "+ e.getMessage() );
    }
}

public boolean check(String paswd){//kontrola zadaneho hesla
	Statement stmt = null;
	 try {
	 stmt = c.createStatement();
     ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS;" );
    
    while ( rs.next() ) {
       int  id = rs.getInt("idus");
       
       if(id==act && paswd.compareTo(rs.getString("PASWD"))==0){//porovnava ci ma aktualny user spravne heslo
    	   return true;
       }
    }
    rs.close();
    stmt.close();
     c.commit();
	 }catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
        System.exit(0);
      }
	return false;
}

public void checkOrders(){//automaticky zrusi objednavku ked ju pouzivel nedokonci do datumu ked sa zacina zapozicanie vybratych veci
	PreparedStatement stmt = null;
	 
	 try {
		 stmt=c.prepareStatement("update objednavka set stav=1"
				 +" where id_user=? and date_to < current_date and stav=0"
				 +" returning id_objednavka");
		 stmt.setInt(1, getId_user());
		 ResultSet rs = stmt.executeQuery();
		 while ( rs.next() ){
			    int id = rs.getInt("id_objednavka");
			    infoBox("Your order number "+id+" was canceled becasue\n Your order start today and was not completed", "warning");//informacne sprava
		     }
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

public int getId_user(){//vrati id actualneho pouzivatela
	return act;
}

public int getType(){//vrati typ actualneho pouzivatela
	return type;
}

public  void infoBox(String infoMessage, String titleBar){//chybova hlaska
    JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
}
}
