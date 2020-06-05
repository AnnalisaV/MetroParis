package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	private Map<Integer, Fermata> idMapFermate; 
	private MetroDAO dao; 
	private Graph<Fermata, DefaultEdge> graph; 
	
	public Model() {
		this.idMapFermate= new HashMap<>(); 
		this.dao= new MetroDAO(); 
		this.dao.getAllFermate(idMapFermate);
		
	}
	
	public void creaGrafo() {
		
		this.graph= new SimpleDirectedGraph<>(DefaultEdge.class); //bello pulito
		
		// aggiunta dei vertici
		Graphs.addAllVertices(this.graph, this.idMapFermate.values()); 
		
		//aggiunta di archi 
		// PRIMO METODO DOPPIO CICLO FOR (per pochi vertici) 
		/*for (Fermata f1 : this.graph.vertexSet()) {
			for (Fermata f2 : this.graph.vertexSet()) {
				int nConnessioni = this.dao.getConnessioniFermate(f1, f2); 
				if (nConnessioni >0) {
					// allora sono connessi 
					this.graph.addEdge(f1, f2); // gia' orientati 
				}
			}
		}*/
		
		//SECONDO METODO DOPPIO CICLO FOR CON SOTTOINSIEME 
		/*for (Fermata f : this.graph.vertexSet()) {
			//prendo i suoi vicini
			List<Fermata> lista= this.dao.getFermateConnesse(this.idMapFermate,f); 
			
			for (Fermata fc : lista) {
				this.graph.addEdge(f, fc); 
			}
		}
		*/
		//TERZO METODO DELLE COPPIE 
		List<CoppiaFermate> coppie= this.dao.getCoppieFermate(idMapFermate); 
		for (CoppiaFermate cc : coppie) {
			this.graph.addEdge(cc.getF1(), cc.getF2()); 
			
		}
	}
	
	public int nVertex() {
		return this.graph.vertexSet().size(); 
	}
	public int nArchi() {
		return this.graph.edgeSet().size(); 
	}
	
	public List<Fermata> getVertex(){
		List<Fermata> vertex= new ArrayList<>(this.graph.vertexSet()); 
		return vertex; 
	}
	
	
	//VISITA DEL GRAFO 
	
	//in ampiezza 
	public List<Fermata> visitaInAmpiezza(Fermata partenza){
		List<Fermata> lista= new ArrayList<>(); 
		
		BreadthFirstIterator<Fermata, DefaultEdge> bfIterator= new BreadthFirstIterator<Fermata, DefaultEdge>(this.graph,partenza ); 
	
		while(bfIterator.hasNext()) {
			lista.add(bfIterator.next()); 
		}
		
		return lista; 
	
	}
	
	//in profondita'
	public List<Fermata> visitaInProfondita(Fermata partenza){
		List<Fermata> lista= new ArrayList<>(); 
		
		DepthFirstIterator<Fermata, DefaultEdge> dfIterator= new DepthFirstIterator<Fermata, DefaultEdge>(this.graph,partenza ); 
	
		while(dfIterator.hasNext()) {
			lista.add(dfIterator.next()); 
		}
		
		return lista; 
	
	}
	
	//ALBERO DI VISITA
	public Map<Fermata, Fermata> alberoVisita(Fermata partenza){
		Map<Fermata, Fermata> albero= new HashMap<>(); //pulita
		albero.put(partenza,  null); 
		
		DepthFirstIterator<Fermata, DefaultEdge> dfIterator= new DepthFirstIterator<Fermata, DefaultEdge>(this.graph,partenza ); 
		dfIterator.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>() {
			
			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				DefaultEdge edge= e.getEdge(); 
				
				Fermata partenza=graph.getEdgeSource(edge); 
				Fermata arrivo=graph.getEdgeTarget(edge);
				
				if (albero.containsKey(partenza)) {
					//ho scoperto l'arrivo
					albero.put(arrivo, partenza); 
				}
				else {
					//ho scoperto la partenza
					albero.put(partenza,  arrivo);
				}
				
				
			}
			
			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//cmq va visitato
		while(dfIterator.hasNext()) {
			dfIterator.next(); 
		}
		return albero; 
	}
	
	//CAMMINO MINIMO 
	public List<Fermata> camminoMinimo(Fermata partenza, Fermata arrivo){
		
		//con Dijkstra, lo definisco
		DijkstraShortestPath<Fermata, DefaultEdge> dij= new DijkstraShortestPath<>(graph); 
		
		//struttura dati adeguata
		GraphPath<Fermata, DefaultEdge> cammino =dij.getPath(partenza, arrivo); 

    	return cammino.getVertexList(); 
		
		
	}
	
	
}
