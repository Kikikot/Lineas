package Lineas;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.util.ArrayList;
public class LineasDeColores extends JFrame implements ActionListener{//	Elementos
	private JPanel panel;
	private LineasControler controlador;
	
//	contenedores
	private ArrayList<JLabel> labels = new ArrayList<JLabel>();
	private ArrayList<JButton> botones = new ArrayList<JButton>();
	private ArrayList<JTextField> textos = new ArrayList<JTextField>();
	
//	Menu 1 - OFF line
	private JLabel lUser;
	private JTextField tUser;
	private JLabel lPass;
	private JPasswordField tPass;
	private JButton bConectar;
	private JButton bRegistrar;
	private JButton bSalir;
	
//	Menu 2 - ON line
	private JLabel lUserON;
	private JLabel lVictorias;
	private JLabel lNumVictorias;
	private JLabel lDerrotas;
	private JLabel lNumDerrotas;
	private JButton bJugar;
	private JButton bResultados;
	private JButton bDesconectar;
	
//	Menu 3 - Resultados
	private JButton bResMas;
	private JButton bResMejor;
	private JScrollPane scrollPane;
	private String[][] data = {{"-","-","-"}};
	private String[] cabeceras = {"Posición", "Jugador","Más Victorias"};
	private JTable tabla;
	private JButton bVolver;
	
//	Menu 4 - Partida
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LineasDeColores frame = new LineasDeColores();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LineasDeColores() {
		controlador = new LineasControler(panel);
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("Lineas");
		this.setSize(400, 600);
		this.setLocationRelativeTo(null);
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(panel);
		panel.setLayout(null);
		
		
// Elementos del menú Desconectado
		
		lUser = new JLabel("Usuario");
		lUser.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		lUser.setBounds(34, 50, 130, 50);
		panel.add(lUser);
		labels.add(lUser);
		
		tUser = new JTextField();
		tUser.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		tUser.setBounds(174, 50, 181, 50);
		tUser.setColumns(20);
		panel.add(tUser);
		textos.add(tUser);
		
		lPass = new JLabel("Contrase\u00F1a");
		lPass.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		lPass.setBounds(34, 150, 130, 50);
		panel.add(lPass);
		labels.add(lPass);
		
		tPass = new JPasswordField();
		tPass.setEchoChar('?');
		tPass.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		tPass.setColumns(20);
		tPass.setBounds(174, 150, 181, 50);
		panel.add(tPass);
		textos.add(tPass);
		
		bRegistrar = new JButton("Registrar");
		bRegistrar.addActionListener(this);
		bRegistrar.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		bRegistrar.setBounds(75, 250, 225, 50);
		panel.add(bRegistrar);
		botones.add(bRegistrar);
		
		bConectar = new JButton("Conectar");
		bConectar.addActionListener(this);
		bConectar.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		bConectar.setBounds(75, 350, 225, 50);
		panel.add(bConectar);
		botones.add(bConectar);
		
		bSalir = new JButton("Salir");
		bSalir.addActionListener(this);
		bSalir.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		bSalir.setBounds(75, 450, 225, 50);
		panel.add(bSalir);
		botones.add(bSalir);
		
		
// Elementos del menú Conectado
		
		lUserON = new JLabel("Mauricio");
		lUserON.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		lUserON.setHorizontalAlignment(SwingConstants.CENTER);
		lUserON.setBounds(475, 20, 225, 50);
		panel.add(lUserON);
		labels.add(lUserON);
		
		lVictorias = new JLabel("Victorias");
		lVictorias.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
		lVictorias.setHorizontalAlignment(SwingConstants.CENTER);
		lVictorias.setBounds(420, 70, 170, 25);
		panel.add(lVictorias);
		labels.add(lVictorias);
		
		lNumVictorias = new JLabel("XXX");
		lNumVictorias.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
		lNumVictorias.setHorizontalAlignment(SwingConstants.CENTER);
		lNumVictorias.setBounds(420, 100, 170, 25);
		panel.add(lNumVictorias);
		labels.add(lNumVictorias);
		
		lDerrotas = new JLabel("Derrotas");
		lDerrotas.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
		lDerrotas.setHorizontalAlignment(SwingConstants.CENTER);
		lDerrotas.setBounds(610, 70, 170, 25);
		panel.add(lDerrotas);
		labels.add(lDerrotas);
		
		lNumDerrotas = new JLabel("XXX");
		lNumDerrotas.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
		lNumDerrotas.setHorizontalAlignment(SwingConstants.CENTER);
		lNumDerrotas.setBounds(610, 100, 170, 25);
		panel.add(lNumDerrotas);
		labels.add(lNumDerrotas);
		
		bJugar = new JButton("Jugar Partida");
		bJugar.addActionListener(this);
		bJugar.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		bJugar.setBounds(475, 225, 225, 50);
		panel.add(bJugar);
		botones.add(bJugar);
		
		bResultados = new JButton("Ver Resultados");
		bResultados.addActionListener(this);
		bResultados.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		bResultados.setBounds(475, 325, 225, 50);
		panel.add(bResultados);
		botones.add(bResultados);
		
		bDesconectar = new JButton("Desconectar");
		bDesconectar.addActionListener(this);
		bDesconectar.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		bDesconectar.setBounds(475, 425, 225, 50);
		panel.add(bDesconectar);
		botones.add(bDesconectar);
		
		
// Elemento del menú Puntuaciones
		
		bResMas = new JButton("Más victorias");
		bResMas.addActionListener(this);
		bResMas.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
		bResMas.setBounds(20, 620, 170, 50);
		panel.add(bResMas);
		botones.add(bResMas);
		
		bResMejor = new JButton("Mejor promedio");
		bResMejor.addActionListener(this);
		bResMejor.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
		bResMejor.setBounds(210, 620, 170, 50);
		panel.add(bResMejor);
		botones.add(bResMejor);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 690, 360, 390);
		panel.add(scrollPane);
		tabla = new JTable(data, cabeceras);
		scrollPane.setViewportView(tabla);
		
		bVolver = new JButton("Volver");
		bVolver.addActionListener(this);
		bVolver.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		bVolver.setBounds(75, 1100, 225, 50);
		panel.add(bVolver);
		botones.add(bVolver);

		
// Elementos del menú Partida
		
	}
	
	/**
	 * Mueve todos los componentes del panel la cantidad de píxeles que se le indique por parámetros.
	 * @param a - Cantidad de píxeles en el eje de las X que se ha de mover.
	 * @param b - Cantidad de píxeles en el eje de las Y que se ha de mover.
	 */
	private void mover(int a, int b) {
		for (int i = 0; i<botones.size(); i++) {
			int x = botones.get(i).getX();
			int y = botones.get(i).getY();
			int lx = botones.get(i).getWidth();
			int ly = botones.get(i).getHeight();
			if (a==0 && b==0) botones.get(i).setVisible(false);
			else botones.get(i).setBounds(x-a, y-b, lx, ly);
		}
		
		for (int i = 0; i<textos.size(); i++) {
			int x = textos.get(i).getX();
			int y = textos.get(i).getY();
			int lx = textos.get(i).getWidth();
			int ly = textos.get(i).getHeight();
			if (a==0 && b==0) textos.get(i).setVisible(false);
			else textos.get(i).setBounds(x-a, y-b, lx, ly);
		}
		
		for (int i = 0; i<labels.size(); i++) {
			int x = labels.get(i).getX();
			int y = labels.get(i).getY();
			int lx = labels.get(i).getWidth();
			int ly = labels.get(i).getHeight();
			if (a==0 && b==0) labels.get(i).setVisible(false);
			else labels.get(i).setBounds(x-a, y-b, lx, ly);
		}
		
		int x = scrollPane.getX();
		int y = scrollPane.getY();
		int lx = scrollPane.getWidth();
		int ly = scrollPane.getHeight();
		if (a==0 && b==0) scrollPane.setVisible(false);
		else scrollPane.setBounds(x-a, y-b, lx, ly);
	}

	/**
	 * Listeners de cada uno de los botones.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bRegistrar) {
			pRegistrar();
		}
		if (e.getSource() == bConectar) {
			pConectar();
		}
		if (e.getSource() == bSalir) {
			pSalir();
		}
		if (e.getSource() == bJugar){
			pJugar();
		}
		if (e.getSource() == bResultados){
			pResultados();
		}
		if (e.getSource() == bDesconectar){
			pDesconectar();
		}
		if (e.getSource() == bResMas){
			pResMas();
		}
		if (e.getSource() == bResMejor){
			pResMejor();
		}
		if (e.getSource() == bVolver){
			pVolver();
		}
		
	}

	/**
	 * Procedimiento Registrar.
	 */
	private void pRegistrar() {
		this.controlador.registrarUser(this.tUser.getText(), this.tPass.getText());
	}
	
	/**
	 * Procedimiento Conectar.
	 */
	private void pConectar() {
		boolean ok = this.controlador.conectarUser(this.tUser.getText(), this.tPass.getText(), this.lUserON, this.lNumVictorias, this.lNumDerrotas);
		if (ok){
			setTitle("Lineas (" + this.tUser.getText().toUpperCase() + ")");
			this.tUser.setText("");
			this.tPass.setText("");
			mover(400, 0);
		}
	}
	
	/**
	 * Procedimiento Salir.
	 */
	private void pSalir() {
		this.controlador.salir();
	}
	
	/**
	 * Procedimiento Jugar.
	 */
	private void pJugar() {
		mover(0,0);
		this.controlador.jugarPartida(panel, this.lNumVictorias, this.lNumDerrotas);
		
	}
	
	/**
	 * Procedimiento Resultados.
	 */
	private void pResultados() {
		pResMas();
		mover(-400, 600);
	}

	/**
	 * Procedimiento Volver.
	 */
	private void pVolver() {
		mover(400, -600);
	}

	/**
	 * Procedimiento que muestra los jugadores con los mejores resultados  segun la cantidad de Victorias.
	 */
	private void pResMejor() {
		cabeceras[2] = "Victorias - Derrotas";
		String[][] data = this.controlador.MejoresPorCalidad();
		tabla = new JTable(data, cabeceras);
		scrollPane.setViewportView(tabla);
		bResMas.setEnabled(true);
		bResMejor.setEnabled(false);
	}

	/**
	 * Procedimiento que muestra los jugadores con los mejores resultados  segun la relación Victorias - Derrotas.
	 */
	private void pResMas() {
		cabeceras[2] = "Victorias Totales";
		String[][] data = this.controlador.MejoresPorCantidad();
		tabla = new JTable(data, cabeceras);
		scrollPane.setViewportView(tabla);
		bResMas.setEnabled(false);
		bResMejor.setEnabled(true);
	}

	/**
	 * Procedimiento Desconectar.
	 */
	private void pDesconectar() {
		this.controlador.desconectarUser();
		setTitle("Lineas");
		mover(-400, 0);
	}

}
