package Lineas;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class ControlerPartida extends Thread implements ActionListener {
	
	private JLabel misVictorias;
	private JLabel misDerrotas;
	private boolean FIN = false;
	
	private JPanel p;
	private ConexionPartida conexion;
	
	private JButton bStart;
	private JButton bExit;
	
	private int n = 5, x = 60, y = 10, ms = 150, ml = 18, tiempoDeJugada = 15;
	private JButton[][] tablero;
	
	private JLabel tu;
	private JLabel el;
	private JLabel miMarcador;
	private JLabel suMarcador;
	
	private JLabel reloj;
	
	private boolean turnoPropio = true;
	private boolean jugando = false;
	
	/**
	 * Constructor del segundo hilo del programa.
	 */
	public ControlerPartida (){
		super();
	}
	
	/**
	 * Inicializa la Partida, mostrando el tablero. Hay que montar la partida sobre el segundo hilo.
	 * Además, se activa el modo partida en el segundo hilo.
	 * @param usuario - Es el usuario que está jugando la partida.
	 * @param laBase - Es la base de datos dnde están todos los datos referentes al usuario y a las partidas.
	 * @param panel - Es el panel en el que se ha de mostrar el tablero.
	 * @param lVictorias - Es la etiqueta de Victorias del menu principal de la conexión del usuario (pequeña chapucilla)
	 * @param lDerrotas - Es la etiqueta de Derrotas del menu principal de la conexión del usuario (pequeña chapucilla)
	 */
	public void activar (String usuario, DDBB laBase, JPanel panel, JLabel lVictorias, JLabel lDerrotas){
		
		misVictorias = lVictorias;
		misDerrotas = lDerrotas;
		
		turnoPropio = true;
		jugando = false;
		p = panel;
		
		tu = new JLabel("Tú");
		el = new JLabel("Él");
		
		tu.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		tu.setHorizontalAlignment(SwingConstants.CENTER);
		tu.setBounds(0, 25, 100, 50);
		tu.setForeground(Color.BLUE);
		p.add(tu);
		
		miMarcador = new JLabel("0");
		miMarcador.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		miMarcador.setHorizontalAlignment(SwingConstants.CENTER);
		miMarcador.setBounds(0, 75, 100, 50);
		miMarcador.setForeground(Color.BLUE);
		p.add(miMarcador);
		
		el.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		el.setHorizontalAlignment(SwingConstants.CENTER);
		el.setBounds(300, 25, 100, 50);
		el.setForeground(Color.RED);
		p.add(el);
		
		suMarcador = new JLabel("0");
		suMarcador.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		suMarcador.setHorizontalAlignment(SwingConstants.CENTER);
		suMarcador.setBounds(300, 75, 100, 50);
		suMarcador.setForeground(Color.RED);
		p.add(suMarcador);
		
		bExit = new JButton("Salir");
		bExit.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		bExit.setBounds(85, 525, 225, 40);
		bExit.addActionListener(this);
		p.add(bExit);
		
		bStart = new JButton("Buscar");
		bStart.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		bStart.setBounds(100, 20, 195, 40);
		bStart.addActionListener(this);
		p.add(bStart);
		
		conexion = new ConexionPartida(laBase, usuario);
		
		reloj = new JLabel(tiempoDeJugada+"");
		reloj.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		reloj.setHorizontalAlignment(SwingConstants.CENTER);
		reloj.setBounds(100, 80, 195, 50);
		reloj.setForeground(Color.RED);
		p.add(reloj);
		
		pintaTablero();
		
	}
	
	/**
	 * Es el código que se va a estar ejecutando continuamente en el segundo hilo. Se encarga de controlar
	 * el tiempo, y el ritmo de las consultas a la base de ato.
	 */
	public void run (){
		while (!FIN) {
			if (jugando){
				moverReloj();
				if (!turnoPropio){
					if (!conexion.ultimaJugadaPropia()){
						if (conexion.listoParaEmpezar()) cambiaTurno(); // Esto solo será después de buscar la partida en el usuario 1
						else{
							if (!conexion.rendido()){
								lineaPulsada(tablero[conexion.getX()][conexion.getY()]);
							} else finOponenteRendido();
						}
						
					}
				}
				if (tiempoAcabado()){
					finalPorTiempo();
				}
			}
		}
	}
	
	/**
	 * Chekea si el tiempo del turno se ha acabado.
	 * @return - True si el reloj ha llegado a cero, o False si aun no.
	 */
	private boolean tiempoAcabado() {
		return this.reloj.getText().equals("0");
	}

	/**
	 * Ejecución del final de la partida por tiempo. Analiza las diferentes condiciones que se pueden dar, y actúa en consecuencia.
	 */
	private void finalPorTiempo() {
		if(turnoPropio){
			JOptionPane.showMessageDialog( null, "No has realizado ningún movimiento en "+ tiempoDeJugada +" segundos.\n"
					+ "Has perdido esta partida por tiempo.");
			terminarPartida();
			aumentaPuntos(misDerrotas);
			conexion.perder();
		}else{
			if (conexion.listoParaEmpezar()){
				JOptionPane.showMessageDialog( null, "En estos momentos no hay nadie para jugar.\n"
						+ "Intentalo de nuevo más tarde.");
				terminarPartida();
			} else {
				JOptionPane.showMessageDialog( null, "¡¡Has ganado la partida!!\n"
						+ "Tu oponente no ha realizado ningún movimiento en "+ tiempoDeJugada +" segundos.");
				terminarPartida();
				aumentaPuntos(misVictorias);
				conexion.ganar();
			}
		}
		conexion.borrarPartida();
	}

	/**
	 * Ejecución del final de la partida por rendición del oponente. Modifica estadisticas del jugador, y borra la partida.
	 */
	private void finOponenteRendido() {
		JOptionPane.showMessageDialog(null, "Tu oponente se ha rendido. Enhorabuena!!");
		terminarPartida();
		conexion.borrarPartida();
		conexion.ganar();
		aumentaPuntos(misVictorias);
	}

	/**
	 * Desactiva la partida en el segundo hilo. Después de ejecutar este método, ya no se controla la base de datos,
	 * ni se realizan acciones de jugadas.
	 */
	private void terminarPartida() {
		this.jugando = false;
	}

	/**
	 * Pausa el la ejecución del hilo durante un segundo, y actualiza el reloj.
	 */
	private void moverReloj() {
		try {
			Thread.sleep (1000);
		} catch (Exception e) {
		}
		reloj.setText(Integer.parseInt(reloj.getText())-1+"");
	}

	/**
	 * Busca una partida para el jugador, e inicia la partida. Deja al jugador en
	 * pausa hasta que otro usuario haya realizado una acción o se conecte para jugar con él.
	 */
	public void empezar(){
		this.bStart.setText("");
		this.bStart.setEnabled(false);
		cambiaTurno();
		conexion.iniciaPartida();
		this.jugando = true;
	}

	/**
	 * Activa o desactiva las lineas que aun no hayan sido pulsadas.
	 * @param nuevoEstado - Si estrue, las activa. Si es false, las desactiva.
	 */
	private void setTableroEnable(boolean nuevoEstado) {
 		for (int i=0; i<n*2+1; i++){
			for (int j=0; j<n*2+1; j++){
				if (tablero[i][j].getHeight() != tablero[i][j].getWidth()){
					if (!pulsado(i,j)){
						tablero[i][j].setEnabled(nuevoEstado);
					}
				}
			}
 		}
	}

	/**
	 * Ejecuta la acción de pulsar una línea pintandola de azul si es el turno del usuario, o de rojo si es el del oponente,
	 * y analiza si la partida ha finalizado.
	 * @param linea - Es la línea pulsada.
	 */
	public void lineaPulsada(JButton linea) {
		int Y = trataHorizontal(linea.getX());
		int X = trataVertical(linea.getY());
		
		if (!pulsado(X,Y)) {
			if (turnoPropio) linea.setBackground(Color.BLUE);
			else linea.setBackground(Color.RED);
			
			if (turnoPropio) conexion.juega(X,Y);
			
			tablero[X][Y].setEnabled(false);
			
			if(cierraCaja(X,Y)){
				if (finalDePartidaPorVictoria()){
					this.terminarPartida();
					if (turnoPropio) {
						JOptionPane.showMessageDialog( null, "¡¡Enhorabuena!!\n"
								+ "Has ganado la partida.");
						aumentaPuntos(misVictorias);
						conexion.ganar();
					} else {
						JOptionPane.showMessageDialog( null, "Lo siento...\n"
								+ "Has perdido esta partida.");
						aumentaPuntos(misDerrotas);
						conexion.perder();
						conexion.borrarPartida();
					}
				}else{
					if (turnoPropio) {
						setReloj(tiempoDeJugada);
					}else{
						setReloj(tiempoDeJugada+1);
					}
				}
			}else{
				cambiaTurno();
			}
		}
	}
	
	/**
	 * Comprueba si se cumple la condición de victoria, haber derrado más de la mitad de los cuadros.
	 * @return
	 */
	private boolean finalDePartidaPorVictoria() {
		if (Integer.parseInt(this.miMarcador.getText()) >= ((n*n)/2)+1 || Integer.parseInt(this.suMarcador.getText()) >= ((n*n)/2)+1) return true;
		else return false;
	}

	/**
	 * Cambia de turno, y modifica el estado del tablero para mostrarloal usuario. Si el nuevo turno es del usuario,
	 * activa el teclado y muestra un panel azul. Si es del oponente, desactiva el tablero, y el panel se pone rojo.
	 * Además, resetea el segundero.
	 */
	private void cambiaTurno() {
		turnoPropio = !turnoPropio;
		if (turnoPropio){
			bStart.setBackground(Color.BLUE);
			setTableroEnable(true);
			reloj.setForeground(Color.BLUE);
			setReloj(tiempoDeJugada);
		}else{
			bStart.setBackground(Color.RED);
			setTableroEnable(false);
			reloj.setForeground(Color.RED);
			setReloj(tiempoDeJugada+2);
		}
	}

	/**
	 * Fija los segundos del reloj.
	 * @param segundos - La cantidad de segundos que hay que mostrar en el segundero.
	 */
	private void setReloj(int segundos) {
		reloj.setText(segundos+"");
	}
	
	/**
	 * Comprueba si la linea indicada en las coordenada confirma almenos un cierre de caja con las de su entorno.
	 * @param X - Eje de las X de la línea que se quiere analizar.
	 * @param Y - Eje de las Y de la línea que se quiere analizar.
	 * @return - True si cierra alguna caja. False si no cierra ninguna.
	 */
	private boolean cierraCaja(int X, int Y) {
		boolean ok = false;
		
		if (X%2==0){
//			Horizontal
			boolean arriba=cierraArriba(X,Y);
			boolean abajo=cierraAbajo(X,Y);
			ok = arriba || abajo;
		}else{
//			Vertical
			boolean derecha=cierraDerecha(X,Y);
			boolean izquierda=cierraIzquierda(X,Y);
			ok = derecha || izquierda;
		}
		return ok;
	}

	/**
	 * Comprueba si la linea indicada en las coordenada confirma un cierre de caja a su izquierda.
	 * @param X - Eje de las X de la línea que se quiere analizar.
	 * @param Y - Eje de las Y de la línea que se quiere analizar.
	 * @return - True si cierra caja. False si no cierra.
	 */
	private boolean cierraIzquierda(int X, int Y) {
		boolean ok = false;
		if (X-1 >= 0 && X+1 < 11 && Y-2 >= 0){
			if (pulsado(X-1,Y-1) && pulsado(X+1,Y-1) && pulsado(X,Y-2)){
				ok = true;
				pintaCuadro(X,Y-1);
			}
		}
		return ok;
	}

	/**
	 * Confirma si la linea indicada ha sido pulsada con anterioridad o no.
	 * @param i - Eje de las X de la línea que se quiere analizar.
	 * @param j - Eje de las Y de la línea que se quiere analizar.
	 * @return - True había sido pulsada. False si no había sido pulsada.
	 */
	private boolean pulsado(int i, int j) {
		return tablero[i][j].getBackground()!=Color.LIGHT_GRAY;
	}

	/**
	 * Comprueba si la linea indicada en las coordenada confirma un cierre de caja a su derecha.
	 * @param X - Eje de las X de la línea que se quiere analizar.
	 * @param Y - Eje de las Y de la línea que se quiere analizar.
	 * @return - True si cierra caja. False si no cierra.
	 */
	private boolean cierraDerecha(int X, int Y) {
		boolean ok = false;
		if (X-1 >= 0 && X+1 < 11 && Y+2 < 11){
			if (pulsado(X-1,Y+1) && pulsado(X+1,Y+1) && pulsado(X,Y+2)){
				ok = true;
				pintaCuadro(X,Y+1);
			}
		}
		return ok;
	}

	/**
	 * Comprueba si la linea indicada en las coordenada confirma un cierre de caja por debajo de ella.
	 * @param X - Eje de las X de la línea que se quiere analizar.
	 * @param Y - Eje de las Y de la línea que se quiere analizar.
	 * @return - True si cierra caja. False si no cierra.
	 */
	private boolean cierraAbajo(int X, int Y) {
		boolean ok = false;
		if (X+2 < 11 && Y-1 >= 0 && Y+1 < 11){
			if (pulsado(X+2,Y) && pulsado(X+1,Y-1) && pulsado(X+1,Y+1)){
				ok = true;
				pintaCuadro(X+1,Y);
			}
		}
		return ok;
	}

	/**
	 * Comprueba si la linea indicada en las coordenada confirma un cierre de caja sobre ella.
	 * @param X - Eje de las X de la línea que se quiere analizar.
	 * @param Y - Eje de las Y de la línea que se quiere analizar.
	 * @return - True si cierra caja. False si no cierra.
	 */
	private boolean cierraArriba(int X, int Y) {
		boolean ok = false;
		if (X-2 >= 0 && Y-1 >= 0 && Y+1 < 11){
			if (pulsado(X-2,Y) && pulsado(X-1,Y-1) && pulsado(X-1,Y+1)){
				ok = true;
				pintaCuadro(X-1,Y);
			}
		}
		return ok;
	}
	
	/**
	 * Pinta el cuadro de CIAN si la ha cerrado el usuario, o de MAGENTA si la ha cerrado el oponente.
	 * @param X - Eje de las X del cuadro.
	 * @param Y - Eje de las Y del cuadro.
	 */
	private void pintaCuadro(int X, int Y) {
		if(turnoPropio){
			tablero[X][Y].setBackground(Color.CYAN);
			aumentaPuntos(miMarcador);
		}
		else{
			tablero[X][Y].setBackground(Color.MAGENTA);
			aumentaPuntos(suMarcador);
		}
	}

	/**
	 * Aumenta en uno la cantidad indicada por la etiqueta que se pase por parámetros.
	 * @param num - Etiqueta que se desea aumentar.
	 */
	private void aumentaPuntos(JLabel num) {
		num.setText(""+(Integer.parseInt(num.getText())+1));
	}

	/**
	 * Calcula la coordenada de las Y en el tablero a partir de la coordenada de las Y en el panel
	 * @param Y - Coordenada de las Y en el panel
	 * @return - Coordenada de las Y en el tablero.
	 */
	private int trataVertical(int Y) {
		if ((Y-ms)%(x+y)==0) return 2*((Y-ms)/(x+y));
		else return 2*((Y-ms)/(x+y))+1;
	}

	/**
	 * Calcula la coordenada de las X en el tablero a partir de la coordenada de las X en el panel
	 * @param X - Coordenada de las X en el panel
	 * @return - Coordenada de las X en el tablero.
	 */
	private int trataHorizontal(int X) {
		if ((X-ml)%(x+y)==0) return 2*((X-ml)/(x+y));
		else return 2*((X-ml)/(x+y))+1;
	}

	/**
	 * Construye las lineas, cuadros y vertices del tablero en función de las dimensiones por defecto que se tienen.
	 */
	public void pintaTablero() {
		tablero = new JButton[2*n+1][2*n+1];
		for (int i=0; i<n*2+1; i++){
			for (int j=0; j<n*2+1; j++){
				JButton bTemporal = new JButton();
				bTemporal.setEnabled(false);
				if (j%2==0){
					if (i%2==0){
						bTemporal.setBounds(ml+(j/2)*(y+x), ms+(y+x)*(i/2), y, y);
						bTemporal.setBackground(Color.BLACK);
					}else{
						bTemporal.setBounds(ml+(j/2)*(y+x), ms+(y+x)*(i/2)+y, y, x);
						bTemporal.addActionListener(this);
						bTemporal.setBackground(Color.LIGHT_GRAY);
					}
				}else{
					if (i%2==0){
						bTemporal.setBounds(ml+(j/2)*(y+x)+y, ms+(y+x)*(i/2), x, y);
						bTemporal.addActionListener(this);
						bTemporal.setBackground(Color.LIGHT_GRAY);
					}else{
						bTemporal.setBounds(ml+(j/2)*(y+x)+y, ms+(y+x)*(i/2)+y, x, x);
					}
				}
				tablero[i][j] = bTemporal;
				p.add(bTemporal);
			}
		}
	}

	/**
	 * Listeners de los botones y las lineas.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bStart) {
			empezar();
		}else if (e.getSource() == bExit) {
			salir();
		}else{
			if (!conexion.rendidoEsperando()){
				JButton linea = (JButton) e.getSource();
				lineaPulsada(linea);
				try {
					Thread.sleep (1000);
				} catch (Exception ex) {
				}
			}else{
				finOponenteRendido();
			}
		}
	}
	
	/**
	 * Lo que se ejecta cuando se pulsa sobre el botón salir.
	 * Si aun no se ha acabado la partida, informa al usuario de que perderá la partica, y da la opción de
	 * elegir entre rendirse o cancelar. Si al final, se ha decidido salir, se ejecuta el procedimiento de salir.
	 */
	private void salir() {
		boolean salir = true;
		if(jugando){
			if (!conexion.listoParaEmpezar()){
				int n = JOptionPane.showOptionDialog(bExit, "Si sales cuando la partida aún está en marcha, pierdes la partida.\n"
						+ "¿Que quieres hacer?", "¿Renuncias?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[] { "Renunciar", "Seguir jugando"}, "Seguir jugando");
				
				if (n==1){
					salir = false;
				}else{
					conexion.rendir();
					this.terminarPartida();
					aumentaPuntos(misDerrotas);
				}
			}
		}
		if (salir) pSalirPartida();
		
	}

	/**
	 * Borra el tablero del panel, y muestra todo lo que esté oculto en el panel.
	 */
	private void pSalirPartida() {
		reloj.setText(tiempoDeJugada+"");
		conexion.actualizaUser();
		int n = p.getComponentCount();
		for (int i=0; i<n; i++){
			if (p.getComponent(i).isVisible()){
				p.getComponent(i).setVisible(false);
				p.remove(i);
				i--;
				n--;
			} else p.getComponent(i).setVisible(true);
		}
	}

}
