package it.polito.tdp.metroparis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metroparis.model.CoppiaFermate;
import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Linea;

public class MetroDAO {

	public void getAllFermate(Map<Integer, Fermata> idMap) {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		//List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(!idMap.containsKey(rs.getInt("id_fermata"))) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				idMap.put(f.getIdFermata(), f);
			}
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		//return fermate;
	}

	public List<Linea> getAllLinee() {
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea ORDER BY nome ASC";

		List<Linea> linee = new ArrayList<Linea>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Linea f = new Linea(rs.getInt("id_linea"), rs.getString("nome"), rs.getDouble("velocita"),
						rs.getDouble("intervallo"));
				linee.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return linee;
	}

	/**
	 * Date due {@code Fermata}, restituisce il numero di connessioni
	 * @param f1 {@code Fermata} uno 
	 * @param f2 {@code Fermata} due 
	 * * @return numero di connessioni fra le due 
	 */
	public int getConnessioniFermate(Fermata f1, Fermata f2) {
		
		String sql="SELECT count(id_connessione) as n " + 
				"FROM connessione " + 
				"WHERE id_stazP=? AND id_stazA=? "; 
		int numero=0; 
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, f1.getIdFermata());
			st.setInt(2, f2.getIdFermata());
			ResultSet rs = st.executeQuery();

			if(rs.next()) {
				numero= rs.getInt("n"); 
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return numero;
		
	}
	
	/**
	 * Data una {@code Fermata}, restituisce l'elenco di connesse a lei
	 */
      public List<Fermata> getFermateConnesse(Map<Integer, Fermata> idMap, Fermata partenza){
    	  String sql="SELECT distinct id_stazA " + 
    	  		"FROM connessione " + 
    	  		"WHERE id_stazP=? " ; 
    	  
    	  List<Fermata> lista= new ArrayList<>(); 
    	  

  		try {
  			Connection conn = DBConnect.getConnection();
  			PreparedStatement st = conn.prepareStatement(sql);
  			st.setInt(1, partenza.getIdFermata());
  			
  			ResultSet rs = st.executeQuery();

  			while(rs.next()) {
  				Fermata f = idMap.get(rs.getInt("id_stazA")); 
  				lista.add(f); 
  			}

  			st.close();
  			conn.close();

  		} catch (SQLException e) {
  			e.printStackTrace();
  			throw new RuntimeException("Errore di connessione al Database.");
  		}

  		return lista;
  		
      }
      
      /**
       * Coppie di {@code Fermata} connesse 
       */
      
      public List<CoppiaFermate> getCoppieFermate(Map<Integer, Fermata> idMap){
    	  
    	  String sql="SELECT distinct id_stazP, id_stazA " + 
    	  		"FROM connessione "; 
    	  //pesato con il numero di linee che connettono le fermate
    	  /*SELECT distinct id_stazP, id_stazA, COUNT(DISTINCT id_linea)
    	  FROM connessione 
    	  GROUP BY id_stazP, id_stazA*/
    	  
    	  List<CoppiaFermate> lista= new ArrayList<>(); 
    	  

    		try {
    			Connection conn = DBConnect.getConnection();
    			PreparedStatement st = conn.prepareStatement(sql);
    			ResultSet rs = st.executeQuery();

    			while(rs.next()) {
    				Fermata partenza= idMap.get(rs.getInt("id_stazP")); 
    				Fermata arrivo= idMap.get(rs.getInt("id_stazA")); 
    				
    				CoppiaFermate c = new CoppiaFermate(partenza, arrivo); 
    				lista.add(c); 
    			}

    			st.close();
    			conn.close();

    		} catch (SQLException e) {
    			e.printStackTrace();
    			throw new RuntimeException("Errore di connessione al Database.");
    		}

    		return lista;
    		
    	  
      }

}
