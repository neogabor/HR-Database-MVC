/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HR_database_MVC;

import java.sql.SQLException;

/**
 *
 * @author user
 */
public class OwnException extends SQLException{
    public OwnException(){
        super();
    }
    
    public OwnException(String message){
        super("Madarász Gábor, "+message);
    }
}
