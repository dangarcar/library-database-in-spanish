package app;

import interfaz.Ventana;

/**
 * Clase principal del programa.<p>
 * Este es un programa para gestionar una biblioteca, en este caso la biblioteca es ficticia.<p>
 * Se puede gestionar pr�stamos, a�adir nuevos contenidos y perfiles y hacer b�squedas dentro de la BBDD.
 * @author Daniel Garc�a
 *
 */
public class App {
	//Donde est� guardada la BBDD Sqlite, c�mbiese para almacenar los datos en otro sitio
	public static final String url = "files/database.db";
	
	public static void main(String [] args){
		new Ventana();
	}
}
