package SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.JOptionPane;

import perfiles.Admin;
import perfiles.Perfil;
import perfiles.excepciones.ExcepcionDNIPerfil;
import perfiles.excepciones.ExcepcionPerfil;

public class PerfilSQL {
	/**
	 * Crea un objeto Perfil a partir de una clave, en este caso el DNI<br>
	 * Es v�lida para los admins
	 * @param dni El DNI de la persona
	 * @return El objeto del tipo Perfil
	 */
	public static Perfil getPerfil(int dni) {
		Perfil perfil = null;
		ResultSet resultado = null;
		ConectorSQL conector = null;
		
		String nombre;
		String apellidos;
		LocalDate fechaDeNacimiento = null;
		int DNI;
		String direccionCasa;
		String correoElectronico;
		boolean admin;
		
		String peticionSQL = "SELECT Nombre,Apellidos,FechaDeNacimiento,DNI,DireccionCasa,CorreoElectronico,Admin FROM Perfiles WHERE DNI = "+dni+";";
		
		try {
			//Creo un objeto con el que conectarme a la base de datos y me conecto a la base de datos
			conector = new ConectorSQL();
			conector.conectar();
			
			//Hago al peticion SQL a la base de datos y almaceno el ResultSet
			resultado = conector.seleccionar(peticionSQL);
			
			//Saco las variables a partir de el ResultSet SQL
			nombre = resultado.getString("Nombre");
			apellidos = resultado.getString("Apellidos");
			try{
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				fechaDeNacimiento = LocalDate.parse(resultado.getString("FechaDeNacimiento"),formatter);
			} catch(DateTimeParseException e) {
				System.out.println("�Eres admin?");
			}
			DNI = resultado.getInt("DNI");
			direccionCasa = resultado.getString("DireccionCasa");
			correoElectronico = resultado.getString("CorreoElectronico");
			admin = resultado.getBoolean("Admin");
			
			if(admin == true) {
				perfil = new Admin(nombre,apellidos,fechaDeNacimiento,DNI,direccionCasa,correoElectronico);
			}
			else {
				perfil = new Perfil(nombre,apellidos,fechaDeNacimiento,DNI,direccionCasa,correoElectronico);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ExcepcionPerfil e) {
			System.out.println("Error con el perfil que coincide con el siguiente DNI: "+dni);
			e.printStackTrace();
		}  finally {
			//Si la conexion a la base de datos existe, la cierro
			if(conector != null) {
				conector.cerrar();
			}
		}
		
		return perfil;
	}
	
	/**
	 * Esta funci�n escribe en la base de datos el perfil si es correcto<br>
	 * No es v�lida para los admins
	 * @param perfil El objeto Perfil a ser pasado
	 * @throws ExcepcionPerfil 
	 */
	public static void writePerfil(Perfil perfil) throws ExcepcionPerfil {
		ConectorSQL conector = null;
		PreparedStatement st;
		
		if (perfil != null) {
			try{
				//Me conecto a la base de datos
				conector = new ConectorSQL();
				Connection connect = conector.conectar();
				
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String peticionSQL = "INSERT INTO Perfiles(Nombre,Apellidos,FechaDeNacimiento,DNI,DireccionCasa,CorreoElectronico) VALUES(?,?,?,?,?,?);";
			
				st = connect.prepareStatement(peticionSQL);
				st.setString(1, perfil.getNombre());
				st.setString(2, perfil.getApellido());
				st.setString(3, (perfil.getFechaNacimiento() != null)? perfil.getFechaNacimiento().format(formatter):null);
				st.setInt(4, perfil.getDNI());
				st.setString(5, perfil.getDireccionDeCasa());
				st.setString(6, perfil.getCorreoElectronico());
				st.executeUpdate();
				st.clearParameters();
				
				System.out.println("Se ha guardado el perfil en la base de datos");
			
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				//Si la conexi�n a la base de datos existe, la cierro
				if(conector != null) {
					conector.cerrar();
				}
			}
		} else {
			throw new ExcepcionPerfil("El perfil seleccionado no es v�lido",perfil);
		}
	}
	
	/**
	 * Este m�todo escribe en la base de datos el admin pasado por par�metros si es correcto<br>
	 * esta funci�n no es v�lida para los perfiles de usuario normales
	 * @param perfil El objeto de tipo admin a ser pasado
	 * @throws ExcepcionPerfil 
	 */
	public static void writeAdmin(Admin perfil) throws ExcepcionPerfil {
		ConectorSQL conector = null;
		PreparedStatement st;
		
		if (perfil != null) {
			try{
				//Me conecto a la base de datos
				conector = new ConectorSQL();
				Connection connect = conector.conectar();
				
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String peticionSQL = "INSERT INTO Perfiles(Nombre,Apellidos,FechaDeNacimiento,DNI,DireccionCasa,CorreoElectronico,Admin) VALUES(?,?,?,?,?,?,1);";
			
				st = connect.prepareStatement(peticionSQL);
				st.setString(1, perfil.getNombre());
				st.setString(2, perfil.getApellido());
				st.setString(3, (perfil.getFechaNacimiento() != null)? perfil.getFechaNacimiento().format(formatter):null);
				st.setInt(4, perfil.getDNI());
				st.setString(5, perfil.getDireccionDeCasa());
				st.setString(6, perfil.getCorreoElectronico());
				st.executeUpdate();
				st.clearParameters();
				
				System.out.println("Se ha guardado el administrador en la base de datos");
			
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				//Si la conexi�n a la base de datos existe, la cierro
				if(conector != null) {
					conector.cerrar();
				}
			}
		} else {
			throw new ExcepcionPerfil("El admin seleccionado no es v�lido",perfil);
		}
	}
	
	/**
	 * Esta funci�n da permisos de administrador a un perfil normal,<br>
	 * pone como administrador en la base datos a ese perfil
	 * @param perfil El perfil a ser convertido
	 * @return El perfil convertido en admin
	 * @throws ExcepcionPerfil 
	 */
	public static Admin perfilToAdmin(Perfil perfil) throws ExcepcionPerfil {
		Admin admin = null;
		ConectorSQL conector = null;
		PreparedStatement st;
		
		if (perfil != null) {
			try{
				//Me conecto a la base de datos
				conector = new ConectorSQL();
				Connection connect = conector.conectar();
				
				String peticionSQL = "UPDATE Perfiles SET Admin = 1 WHERE DNI = (?);"; 
			
				st = connect.prepareStatement(peticionSQL);
				st.setInt(1, perfil.getDNI());
				st.executeUpdate();
				st.clearParameters();
				
				admin = new Admin(perfil.getNombre(),perfil.getApellido(),perfil.getFechaNacimiento(),perfil.getDNI(),perfil.getDireccionDeCasa(),perfil.getCorreoElectronico());
				
				System.out.println("El perfil: "+perfil.getDNI()+" ha pasado a ser administrador");
			
			} catch (SQLException e) {
				e.printStackTrace();
			} catch(ExcepcionPerfil e){
				JOptionPane.showMessageDialog(null, "El perfil no se ha podido convertir en admin");
			} finally {
				//Si la conexi�n a la base de datos existe, la cierro
				if(conector != null) {
					conector.cerrar();
				}
			}
		} else {
			throw new ExcepcionPerfil("El perfil seleccionado no es v�lido",perfil);
		}
		return admin;
	}
	
	/**
	 * Esta funci�n devuelve un perfil creado a partir del administrador que se pasa por par�metro,<br>
	 * quit�ndole todos los privilegios y modificando su estado en la base de datos
	 * @param admin El administrador a quitar privilegios
	 * @return Un objeto Perfil con los datos del administrador pasado por par�metro
	 * @throws ExcepcionPerfil
	 */
	public static Perfil adminToPerfil(Admin admin) throws ExcepcionPerfil{
		Perfil perfil = null;
		ConectorSQL conector = null;
		PreparedStatement st;
		
		if (admin != null) {
			try{
				//Me conecto a la base de datos
				conector = new ConectorSQL();
				Connection connect = conector.conectar();
				
				String peticionSQL = "UPDATE Perfiles SET Admin = 0 WHERE DNI = (?);"; 
			
				st = connect.prepareStatement(peticionSQL);
				st.setInt(1, admin.getDNI());
				st.executeUpdate();
				st.clearParameters();
				
				perfil = new Perfil(admin.getNombre(),admin.getApellido(),admin.getFechaNacimiento(),admin.getDNI(),admin.getDireccionDeCasa(),admin.getCorreoElectronico());
				
				System.out.println("El admin: "+admin.getDNI()+" ha pasado a ser un usuario normal");
			
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ExcepcionPerfil e) {
				JOptionPane.showMessageDialog(null, "El admin no se ha podido convertir en perfil");
			} finally {
				//Si la conexi�n a la base de datos existe, la cierro
				if(conector != null) {
					conector.cerrar();
				}
			}
		} else {
			throw new ExcepcionPerfil("El perfil seleccionado no es v�lido",perfil);
		}
		return perfil;
	}
	
	/**
	 * Esta funci�n borra el perfil indicado de la base de adtos si el perfil es correcto y ya existe en ella<br>
	 * Es tambi�n v�lida para los admins
	 * @param perfil
	 * @throws ExcepcionPerfil 
	 */
	public static void deletePerfil(Perfil perfil) throws ExcepcionPerfil {
		ConectorSQL conector = null;
		PreparedStatement st;
		
		if (perfil != null) {
			try{
				//Me conecto a la base de datos
				conector = new ConectorSQL();
				Connection connect = conector.conectar();
				
				String peticionSQL = "DELETE FROM Perfiles WHERE DNI = (?);"; 
			
				st = connect.prepareStatement(peticionSQL);
				st.setInt(1, perfil.getDNI());
				st.executeUpdate();
				st.clearParameters();
				
				System.out.println("Se ha borrado el perfil de la base de datos");
			
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				//Si la conexi�n a la base de datos existe, la cierro
				if(conector != null) {
					conector.cerrar();
				}
			}
		} else {
			throw new ExcepcionPerfil("El perfil seleccionado no es v�lido",perfil);
		}
	}
}
