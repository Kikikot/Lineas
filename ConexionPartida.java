package Lineas;
public class ConexionPartida {
	
	private String user;
	private DDBB base;
	private int valorListo = 99;
	private int valorRendido = 98;
	
	private int partida;
	
	private String ultimo;
	private int x;
	private int y;
	
	/**
	 * Constructor del gestor de la conexi�n a la base de datos en referencia a las partidas.
	 * @param laBase - Es labase dedatos en laque se ha de trabajar.
	 * @param elUsuario - Es el usuario con el que se realiza la conexi�n.
	 */
	public ConexionPartida (DDBB laBase, String elUsuario) {
		base = laBase;
		user = elUsuario;
	}
	
	/**
	 * Env�a las coordenadas de una linea pulsada a la base de datos en la partida que se haya asignado antes.
	 * @param x - Es la coordenada en el eje de las X's
	 * @param y - Es la coordenada en el eje de las Y's
	 */
	public void juega(int x, int y) {
		base.ordenar("update partidas_lineas set ultimo_user = '" + user + "', x = " + x + ", y = " + y + " where partida = " + partida);
		this.actualizaUser();
	}
	
	/**
	 * Realiza una consulta sobre la base de datos para leer el estado actual de la partida (que debe haber sido asignada previamente).
	 * @return - Si el �ltimo usuario que ha realizado una acci�n sobre la base de datos es el usuario que maneja esta sesi�n.
	 */
	public boolean ultimaJugadaPropia() {
		String[] columnas = {"ultimo_user", "x", "y"};
		String[] tablas = {"partidas_lineas"};
		String condiciones = "where partida = " + partida;
		String[][] ultimaJugada = base.select(columnas, tablas, condiciones);
		ultimo = ultimaJugada[0][0];
		x = Integer.parseInt(ultimaJugada[0][1]);
		y = Integer.parseInt(ultimaJugada[0][2]);
		return ultimo.equals(user);
	}
	
	/**
	 * Devuelve si tras la ultima consulta a la partida se ha le�do en las coordenadas la condici�n de 'listo para empezar'.
	 * @return - True si es el valor de 'listo para empezar' o false si no es as�.
	 */
	public boolean listoParaEmpezar() {
		return x == valorListo;
	}

	/**
	 * Devuelve el valor de la coordenada en el eje de las X que hab�a tras la �ltima consulta que se realiz� sobre la partida
	 * @return - El valor de las X tras la �ltima consulta.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Devuelve el valor de la coordenada en el eje de las Y que hab�a tras la �ltima consulta que se realiz� sobre la partida
	 * @return - El valor de las Y tras la �ltima consulta.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Asigna al usuario una partida de las que est�n en espera de user_2, o una nueva si es que no hay ninguna en espera.
	 */
	public void iniciaPartida() {
		String[] columnas = {"ifnull(min(partida),-1)"};
		String[] tablas = {"partidas_lineas"};
		String condiciones = "where user_2 is null";
		partida = Integer.parseInt(base.select(columnas, tablas, condiciones)[0][0]);
		if (partida == -1){
			columnas[0] = "ifnull(max(partida), 0)";
			partida = Integer.parseInt(this.base.select(columnas, tablas, "")[0][0])+1;
			this.base.ordenar("insert into partidas_lineas values('" + user + "', null, " + partida + ", '" + user + "', " + valorListo + ", " + valorListo +")");
		}else{
			this.base.ordenar("update partidas_lineas set user_2 = '" + user + "', ultimo_user = '" + user + "' where partida = " + partida);
		}
		this.actualizaUser();
	}

	/**
	 * Devuelve si tras la ultima consulta a la partida se ha le�do en las coordenadas la condici�n de 'rendido'.
	 * @return - True si es el valor de 'listo para empezar' o false si no es as�.
	 */
	public boolean rendido() {
		return x == valorRendido;
	}
	
	/**
	 * Realiza una consulta a la base de datos �nica y exclusivamente para comprobar si en las coordenadas se ha
	 * insertado la condici�n de rendido.
	 * @return - Si en la base de datos se ha modificado las coordenada a condici�n de 'rendido'.
	 */
	public boolean rendidoEsperando(){
		String[] columnas = {"x"};
		String[] tablas = {"partidas_lineas"};
		String condiciones = "where partida = " + partida;
		int estado = Integer.parseInt(base.select(columnas, tablas, condiciones)[0][0]);
		return estado == valorRendido;
	}

	/**
	 * Introduce en las coordenadas de la partida la condici�n de 'rendido', y a�ade una derrota al usuario.
	 */
	public void rendir() {
		this.base.ordenar("update partidas_lineas set ULTIMO_USER = '" + user + "', x = " + this.valorRendido + ", y = " + this.valorRendido + " where partida = " + partida);
		this.base.ordenar("update usuarios_lineas set derrotas = derrotas+1 where usuario = '" + user + "'");
		this.actualizaUser();
	}

	/**
	 * Se encarga de actualizar la �ltima acci�n al usuario, para que este no pase a estar desactivado.
	 */
	public void actualizaUser() {
		this.base.ordenar("update usuarios_lineas set ultima_accion = sysdate() where usuario = '" + user + "'");
	}

	/**
	 * A�ade una victoria al usuario en la base de datos.
	 */
	public void ganar() {
		this.base.ordenar("update usuarios_lineas set ultima_accion = sysdate(), victorias = victorias+1 where usuario = '" + user + "'");
	}

	/**
	 * A�ade una derrota al usuario en la base de datos.
	 */
	public void perder() {
		this.base.ordenar("update usuarios_lineas set ultima_accion = sysdate(), derrotas = derrotas+1 where usuario = '" + user + "'");
	}

	/**
	 * Borra la partida de la base de datos.
	 */
	public void borrarPartida() {
		this.base.ordenar("delete from partidas_lineas where partida = " + partida + " and (user_1 = '" + user + "' or user_2 = '" + user + "')");
	}
	
}
