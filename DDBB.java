package Lineas;
import java.sql.*;
import java.util.ArrayList;


public class DDBB {
	
	private String ip;
	private int tipo;
	private String base;
	private String user;
	private String pass;
	private Connection con = null;
	private ResultSet res = null;
	
	public DDBB (int elTipo, String laIp, String laBase, String elUser, String laPass){
		this.ip = laIp;
		this.tipo = elTipo;
		this.base = laBase;
		this.user = elUser;
		this.pass = laPass;
		
		this.conect();
	}

	public void conect() {
		try {
			// 1 -- MySQL
			// 2 -- Oracle
			
			if (this.tipo == 1){
				Class.forName("com.mysql.jdbc.Driver");
				this.con = DriverManager.getConnection("jdbc:mysql://"+this.ip+"/"+this.base, this.user, this.pass);
				//this.con = DriverManager.getConnection("jdbc:mysql://ec2-54-243-48-227.compute-1.amazonaws.com/"+this.base, this.user, this.pass);
			}else if(this.tipo == 2){
				Class.forName("oracle.jdbc.driver.OracleDriver");
				this.con = DriverManager.getConnection("jdbc:oracle:thin:@"+this.ip+"/"+this.base, this.user, this.pass);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void close(){
		if (this.con != null){
			try {
				this.con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (this.res != null){
			try {
				this.res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public String[][] select(String[] columnas, String[] tablas, String condiciones){
		String select = this.montaSelect(columnas, tablas, condiciones);
		String [][] resultado = new String[0][0];
		try {
			Statement orden = this.con.createStatement();
			this.res=orden.executeQuery(select);
			
			ArrayList<String[]> preResultado =  this.leerResultado(columnas.length);
			resultado = this.montarResultado(columnas.length, preResultado.size(), preResultado);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return resultado;
	}

	private String[][] montarResultado(int x, int y, ArrayList<String[]> preResultado) {
		String[][] resultado = new String[y][x];
		for (int i=0; i<x; i++){
			for (int j=0; j<y; j++){
				resultado[j][i] = preResultado.get(j)[i];
			}
		}
		return resultado;
	}

	private ArrayList<String[]> leerResultado(int x) throws SQLException {
		ArrayList<String[]> resultado =  new ArrayList<String[]>();
		
		while (this.res.next()){
			String [] temp = new String[x];
			for (int i=1; i<=x; i++){
				temp[i-1] = this.res.getString(i);
			}
			resultado.add(temp);
		}
		
		return resultado;
	}

	private String montaSelect(String[] columnas, String[] tablas, String condiciones) {
		String select = "select";
		
		for (int i=1; i<=columnas.length; i++){
			select = select + " " + columnas[i-1];
			if (i!=columnas.length) select = select + ",";
		}
		
		select = select + " from";
		
		for (int i=1; i<=tablas.length; i++){
			select = select + " " + tablas[i-1];
			if (i!=tablas.length) select = select + ",";
		}

		if (condiciones.equals("") || condiciones!=null){
			select = select + " " + condiciones;
		}
		return select;
	}
	
	public void ordenar(String comando){
		try {
			Statement orden = this.con.createStatement();
			orden.executeUpdate(comando);
		} catch (SQLException e) {
		}
	}
	
}
