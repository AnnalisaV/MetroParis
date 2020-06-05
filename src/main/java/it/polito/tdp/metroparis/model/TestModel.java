package it.polito.tdp.metroparis.model;



public class TestModel {

	public static void main(String[] args) {


		Model m = new Model(); 
		
		m.creaGrafo();
		System.out.println("Vertici, archi : "+m.nVertex()+" "+m.nArchi()+"\n"); 
		
		//visita 
		for(Fermata fe : m.visitaInProfondita(m.getVertex().get(1))) {
			System.out.println(fe.toString()+"\n"); 
			
		}
		
		//albero
		for (Fermata fff : m.alberoVisita(m.getVertex().get(0)).keySet()) {
			System.out.println(fff.toString()+"\n"); 
		}
		
			//cammino minimo 
		for (Fermata f : m.camminoMinimo(m.getVertex().get(0), m.getVertex().get(5))) {
			System.out.println(f.toString()+"\n"); 
			
		}
		
		

	}
}

