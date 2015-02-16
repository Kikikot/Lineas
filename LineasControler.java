package Lineas;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class LineasControler {
	
	private DDBB base;
	private String usuario = "";
	private int segundosDeEspera = 300;
	private ControlerPartida elControladorDeLaPartida;
	
	/**
	 * Constructor del controlador de la gestión de usuarios y consulta de estadísticas.
	 * @param panel - Es el panel sobre el cual se está trabajando.
	 * @param ip - Es la ip donde se encuentra la base de datos.
	 */
	public LineasControler(JPanel panel){
		this.base = new DDBB (1, "23c76216-6666-4be4-a243-a3de0033796a.mysql.sequelizer.com",
				"db23c7621666664be4a243a3de0033796a", "dnnntponvgdhkoyn",
				"tA5XdMLXo7tWU5CsqJgAyAotyneh38BHhmxeMDdD2QsVqTV3SWaVADg2mKxXHD4p");
		this.elControladorDeLaPartida = new ControlerPartida();
		this.elControladorDeLaPartida.start();
	}

	/**
	 * Checkea si realmente se han introducido valores de usuario y contraseña y confirma la conexión del usuario, si su últiama conexión es de hace más de 5 minutos.
	 * @param user - Es el nimbre de usuario.
	 * @param pass - Es la clave del usuario.
	 * @param lUserON - Es la etiqueta que va a mostrar el nombre de usuario.
	 * @param lOK - Es la etiqueta que muestra las Victorias del usuario.
	 * @param lKO - Es la etiqueta que muestra las Derrotas del usuario.
	 * @return
	 */
	public boolean conectarUser(String user, String pass, JLabel lUserON, JLabel lOK, JLabel lKO) {
		boolean ok = false;
		if (okCabeceras(user, pass)){
			ok = intentaConectar(user.toUpperCase(), pass.toUpperCase());
		}
		if (ok){
			this.usuario = user.toUpperCase();
			lUserON.setText(this.usuario);
			muestraDatosUser(this.usuario, lOK, lKO);
		}
		return ok;
	}

	/**
	 * Actualiza las etiquetas de Victorias y Derrotas con los valores que tiene el usuario en la base de datos.
	 * @param user - Usuario a consultar.
	 * @param lOK - Es la etiqueta que muestra las Victorias del usuario.
	 * @param lKO - Es la etiqueta que muestra las Derrotas del usuario.
	 */
	private void muestraDatosUser(String user, JLabel lOK, JLabel lKO) {
		String[] columnas = {"victorias"};
		String[] tablas = {"usuarios_lineas"};
		String condiciones = "where usuario = '"+user+"'";
		lOK.setText(this.base.select(columnas, tablas, condiciones)[0][0]);
		columnas[0] = "derrotas";
		lKO.setText(this.base.select(columnas, tablas, condiciones)[0][0]);
	}

	/**
	 * Confirma la conexión del usuario si este hace menos de 5 minutos que se conectó.
	 * @param user - Es el usuario que se quiere conectar
	 * @param pass - Es la contraseña conla que se quiere conectar.
	 * @return - True si los datos son correctos y hace más de 5 minutos de la última conexión, false por lo contrario.
	 */
	private boolean intentaConectar(String user, String pass) {
		boolean ok = false;
		if (existeCorrespondencia(user, pass, true)){
			if (ultimaAccionReciente(user)){
				JOptionPane.showMessageDialog( null, "El usuario está conectado en estos momentos.\n"
						+ "Cierra la otra sesión para poder conectarte, o espera\n"
						+ "5 minutos (si es que has salido sin ''Desconectar'')");
			}else{
				ok = true;
				this.base.ordenar("update usuarios_lineas set ultima_accion = sysdate() where usuario = '"+user+"'");
			}
		}else{
			JOptionPane.showMessageDialog( null, "El usuario o la contraseña no existen.");
		}
		return ok;
	}

	/**
	 * Comprueva si el usuario (y contraseña si se quiere) existen en la base de datos.
	 * @param user - Usuario
	 * @param pass - Contraseña (puede ser null si 'con_clave' es false)
	 * @param con_clave - Confirmar tambien la existencia del usuario con la contraseña proporcionada.
	 * @return - Si ya existe el jugador.
	 */
	private boolean existeCorrespondencia(String user, String pass, boolean con_clave) {
		String[] columnas = {"count(*)"};
		String[] tablas = {"usuarios_lineas"};
		String condicion = "where usuario = '"+user+"'";
		if (con_clave) condicion = condicion + " and pass like '"+pass+"'";
		int n = Integer.parseInt(this.base.select(columnas, tablas, condicion)[0][0]);
		boolean hayCorrespondencia = true;
		if (n==0) hayCorrespondencia = false;
		return hayCorrespondencia;
	}

	/**
	 * Consulta si el tiempo desde la última acción es menor a 5 minutos.
	 * @param user - Usuario
	 * @return - True si hace menos de 300 segundos. False por lo contrario.
	 */
	private boolean ultimaAccionReciente(String user) {
		// Calcular segundos desde ultima_accion
		// select (sysdate - ultima_accion)*60*60*24 from usuarios_lineas
		String[] columnas = {"(sysdate() - ultima_accion)*60*60*24"};
		String[] tablas = {"usuarios_lineas"};
		float segundos =Float.parseFloat(this.base.select(columnas, tablas, "where usuario = '"+user+"'")[0][0]);
		
		boolean reciente = false;
		if (segundos<this.segundosDeEspera) reciente = true;
		return reciente;
	}

	/**
	 * Checkea si el usuario y la contraseña son Strings dentro de los límites aceptados.
	 * @param user - Usuario
	 * @param pass - Contraseña
	 * @return Si todo está OK, true. False por lo contrario.
	 */
	private boolean okCabeceras(String user, String pass) {
		boolean ok = false;
		if (user.equals("")){
			JOptionPane.showMessageDialog( null, "No has introducido ningún nombre de usuario.");
		}else if (pass.equals("")){
			JOptionPane.showMessageDialog( null, "No has introducido ninguna contraseña.");
		}else{
			if(user.length()<21 && pass.length()<21){
				ok = true;
			}else{
				JOptionPane.showMessageDialog( null, "Ups...\n"
						+ "Tanto 'Usuario' como 'Contraseña' no pueden tener más de 20 carácteres.\n"
						+ "¡Inténtalo de nuevo!");
			}
		}
		return ok;
	}

	/**
	 * Actualiza el campo de última acción del usuario a hace un día.
	 */
	public void desconectarUser() {
		this.base.ordenar("update usuarios_lineas set ultima_accion = (sysdate() - 1) where usuario = '"+this.usuario+"'");
	}

	public void registrarUser(String user, String pass) {
		if (okCabeceras(user, pass)) {
			if (existeCorrespondencia(user.toUpperCase(), pass.toUpperCase(), false)){
				JOptionPane.showMessageDialog( null, "El usuario ya existe.");
			}else{
				base.ordenar("insert into usuarios_lineas values ('"+ user.toUpperCase() +"', '"+ pass.toUpperCase() + "', sysdate()-1, 0, 0)");
				JOptionPane.showMessageDialog( null, "Enhorabuena, " + user.toUpperCase() + "!!\n"
						+ "Ya eres un jugador oficial de 'LINEAS'.");
			}
		}
	}

	/**
	 * Devuelve los 30 mejores jugadores por cantidad de Victorias.
	 * @return - String[][] con los 30 mejores jugadores y tres columnas (Posición, Nombre, Cantidad de Victorias)
	 */
	public String[][] MejoresPorCantidad() {
		
		this.base.ordenar("update usuarios_lineas set ultima_accion = sysdate() where usuario = '"+this.usuario+"'");
		this.base.ordenar("SET @i = 0;");
		String[] columnas = {"(@i := @i + 1)", "usuario","victorias"};
		String[] tablas = {"usuarios_lineas"};
		String condiciones = "order by victorias desc limit 30";
		String [][] datos = base.select(columnas, tablas, condiciones);
		return datos;
	}

	/**
	 * Devuelve los 30 mejores jugadores por relación Victorias - Derrotas.
	 * @return - String[][] con los 30 mejores jugadores y tres columnas (Posición, Nombre, Cantidad de Victorias)
	 */
	public String[][] MejoresPorCalidad() {
		this.base.ordenar("update usuarios_lineas set ultima_accion = sysdate() where usuario = '"+this.usuario+"'");
		this.base.ordenar("SET @i = 0;");
		String[] columnas = {"(@i := @i + 1)", "usuario","victorias-derrotas"};
		String[] tablas = {"usuarios_lineas"};
		String condiciones = "order by victorias-derrotas desc limit 30";
		String [][] datos = base.select(columnas, tablas, condiciones);
		return datos;
	}
	
	/**
	 * Llama al segundo hilo y le hace ejecutar la partida.
	 * @param panel - panel donde se va a mostrar la partida.
	 * @param lVictorias - Etiqueta de Victorias del usuario.
	 * @param lDerrotas - Etiqueta de Derrotas del usuario.
	 */
	public void jugarPartida(JPanel panel, JLabel lVictorias, JLabel lDerrotas) {
		this.elControladorDeLaPartida.activar(usuario, base, panel, lVictorias, lDerrotas);
	}

	/**
	 * Salir.
	 */
	public void salir() {
		this.base.close();
		System.exit(0);
	}
	
}
