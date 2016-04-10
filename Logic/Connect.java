package Logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
public class Connect {
    
public Connection create_connection(){//metoda kde sa vytvori pripojenie k databaze
	Connection c = null;
     try {
        Class.forName("org.postgresql.Driver");
        c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/projekt","postgres", "1234");//pripojenie k postresql databaze s meno projekt 
        c.setAutoCommit(false);//nastavenie autocommit na false, aby sa dali manualalne pouzivat trasakcie
        }	catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
         }
     return c;
}

}
