package contenido;

import contenido.excepciones.ExcepcionAno;
import contenido.excepciones.ExcepcionDuracion;
import contenido.excepciones.ExcepcionSoporte;

public class Audio extends Contenido{
	private static final long serialVersionUID = -549498529487274208L;
	private double duracion;
	private final int ID;
	
	/**
	 * 
	 * @param titulo
	 * @param autor
	 * @param descripcion
	 * @param ano
	 * @param idioma
	 * @param prestable
	 * @param soporte
	 * @param duracionEnMinutos
	 * @throws ExcepcionAno
	 * @throws ExcepcionDuracion
	 * @throws ExcepcionSoporte
	 */
	public Audio(String titulo, String autor, String descripcion, int ano, String idioma, boolean prestable, Soporte soporte, int diasDePrestamo, double duracionEnMinutos) 
			throws ExcepcionAno, ExcepcionDuracion, ExcepcionSoporte {
		super(titulo, autor, descripcion, ano, idioma, prestable, soporte,diasDePrestamo);
		if (!(soporte.isMultimedia())) {
			throw new ExcepcionSoporte("El soporte seleccionado no es compatible con audio",this,soporte);
		}
		setDuracion(duracionEnMinutos);
		this.ID = getIdentifier();
	}
	
	public double getDuracion() { return duracion; }
	
	public void setDuracion(double duracion) throws ExcepcionDuracion {
		if (duracion < 0) {
			throw new ExcepcionDuracion("La duracion debe ser un numero positivo",this,duracion);
		}
		this.duracion = duracion;
	}
	
	public int getID() { return ID; }
	private int getIdentifier() {
		return (int) (getTitulo().hashCode()+Math.sqrt(duracion));
	}
	
}
