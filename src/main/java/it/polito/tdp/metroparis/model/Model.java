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
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	                       //non pesato
	private Graph<Fermata, DefaultEdge> graph; 
	private List<Fermata> fermate; // tutte le fermate 
	private Map<Integer, Fermata> fermateIdMap; 
	
	public Model() {
		this.graph= new SimpleDirectedGraph<> (DefaultEdge.class); 
		
		MetroDAO dao= new MetroDAO(); 
		
		//creazione vertici
		this.fermate= dao.getAllFermate(); 
		this.fermateIdMap= new HashMap<>(); 
		for (Fermata f : this.fermate) {
			fermateIdMap.put(f.getIdFermata(), f); 
		}
		
		//aggiungo le fermate al grafo
		Graphs.addAllVertices(this.graph, this.fermate); // si poteva fare anche con for sulle fermate e aggiungendone una alla volta al graph
		
		System.out.println(this.graph); 
		
		//creazione archi
		//PRIMO MODO (meno efficiente) -> per ogni coppia di vertici puo' esserci o meno l'arco di connessione
		/*for(Fermata fp : this.fermate) {
			for (Fermata fa : this.fermate) {
				if (dao.fermateConnesse(fp, fa)) {
					this.graph.addEdge(fp, fa); 
				}
			}
		}
		
		//System.out.println(this.graph); // abbastanza lento (circa 5min, complessita n^2)
		                                // ma se il grafo e' abbastanza piccolo va bene 
		System.out.format("Grafo caricato %d vertici %d archi", this.graph.vertexSet().size(), this.graph.edgeSet().size()); // senza stampare per sapere quanti ce ne sono
		
		*/
		//SECONDO METODO -> query piu' complicata : per ogni stazione di partenza, voglio tutte quelle di arrivo
		                   // complessita' n*gradomedioVertici( n^2*densitaGrafo)
		// -> da un vertice trova tutti i connessi
		/*for (Fermata fp: this.fermate) {
			List<Fermata> connesse = dao.fermateSuccessive(fp, fermateIdMap);
			
			for(Fermata f : connesse) {
				this.graph.addEdge(fp, f); // itera meno volte (qui circa due volte, non 300)
			}
			
		}
		System.out.format("Grafo caricato %d vertici %d archi\n", this.graph.vertexSet().size(), this.graph.edgeSet().size()); 
		*/
		
		//TERZO METODO -> chiedere al db l'elenco degli archi 
		//(qui e' facile perche' il db ha gia' una tabella con l'informazione, 
		// a volte e' piu' difficile e diventa inefficiente sul db a livello sql)
		List<CoppiaFermate> coppie= dao.coppieFermate(fermateIdMap);
		for (CoppiaFermate c : coppie) {
			graph.addEdge(c.getPartenza(), c.getArrivo()); 
		}
		
		System.out.format("Grafo caricato %d vertici %d archi\n", this.graph.vertexSet().size(), this.graph.edgeSet().size()); 
	}
	
	/**
	 * Visitare in ampiezza(BREADTH FIRST)  il grafo a partire da una certa {@code Fermata}
	 * @param source {@code Fermata} da cui partire per visitare il grafo
	 * @return insieme di Feramate incontrate
	 */
	public List<Fermata> visitaAmpiezza(Fermata source) {
		
		List<Fermata> visita= new ArrayList<>(); 
		//definisco l'iteratore (gli passo il grafo da visitare ed il vertice di partenza)
		// se non do il vertice di partenza parte da uno a caso
		BreadthFirstIterator<Fermata, DefaultEdge> bfv= new BreadthFirstIterator<Fermata, DefaultEdge>(graph, source); 
		//appena creato sara' posizionato sul primo elemento
		// esistono elementi successivi?
		while(bfv.hasNext()) {
			visita.add(bfv.next()); //aggiungo il prox finche' esiste un successivo
			
		}// quando arriva al fondo non c'e' piu' nulla da visitare
		
		return visita; 
	}
	
	/**
	 * Visita in profondita' (DEPTH FIRST)
	 * @param source
	 * @return
	 */
	//Come quello in ampiezza ma con iteratore opportuno
    public List<Fermata> visitaProfondita(Fermata source) {
		
		List<Fermata> visita= new ArrayList<>(); 
		//iteratore definito con interfaccia 
		GraphIterator/*DepthFirstIterator*/<Fermata, DefaultEdge> dfv= new DepthFirstIterator<Fermata, DefaultEdge>(graph, source); 
		//appena creato sara' posizionato sul primo elemento
		// esistono elementi successivi?
		while(dfv.hasNext()) {
			visita.add(dfv.next()); //aggiungo il prox finche' esiste un successivo
			
		}// quando arriva al fondo non c'e' piu' nulla da visitare
		
		return visita; 
	}
	
    /**
     * Costruzione dell'albero di visita a partire da un certo vertice {@code Fermata}
     * @param source la {@code Fermata} di partenza 
     * @return l'albero
     */
    public Map<Fermata,Fermata> alberoVisita(Fermata source) {
    	
    	Map<Fermata, Fermata> albero= new HashMap<>(); 
    	//aggiungo subito la sorgente che non sara' scoperta da nessuno MAI
    	albero.put(source, null); 
    	// creo l'iterator per visitare il grafo
    	BreadthFirstIterator<Fermata, DefaultEdge> bfv= new BreadthFirstIterator<Fermata, DefaultEdge>(graph, source); 
    	//aggiungo all'iteratore la Listener (come classe anonima)
    	bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>() {
			
    		//non mi serve, non lo implemento
			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
			}
			
			//non mi serve
			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {
			
			}
			
			//MI SERVE 
			@Override                   //un arco
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				//la visita sta considerando un arco
				// questo arco ha scoperto un nuovo vertice
				// sì, provenendo da dove?
				DefaultEdge edge= e.getEdge(); //prendo l'arco attraversato : orientato o no?
				             //(a,b) ho scoperto a partendo da b oppure b partendo da a? Dipende da chi conosco : chi conosco gia' e' il padre
				Fermata a=graph.getEdgeSource(edge); 
				Fermata b= graph.getEdgeTarget(edge); 
				//conosco a? Se sì allora sta gia' nella Map
				if(albero.containsKey(a) && !albero.containsKey(b)) {
					//allora b e' quello scoperto
					albero.put(b, a); 
				}
				//allora a e' quello nuovo, conoscevo b
				albero.put(a, b); 
			}
			
			//non mi serve 
			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
			
			}
			
			//non mi serve 
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				
			}
		}/*fine Listener*/);
    	
    	while(bfv.hasNext()) {
    		bfv.next(); // estrae e ignora ma l'importante e' che attraversi tutto
    	}
    	
    	return albero; 
    	
    	
    }
    
    /**
     * Ricerca del cammino minimo con Dijkstra 
     * a partire da nua certa {@code Fermata}
     */
    public List<Fermata>camminiMinimi(Fermata partenza, Fermata arrivo) {
    	
    	DijkstraShortestPath<Fermata, DefaultEdge> dij= new DijkstraShortestPath<>(graph); 
    
    	//estraggo la struttura dati
    	GraphPath<Fermata, DefaultEdge> cammino=dij.getPath(partenza, arrivo); 
    	return cammino.getVertexList(); 
    
    }
    
	//TestModel interno alla classe Model per non fare un'altra classe
	public static void main(String arg[]) {
		Model m= new Model(); 
		
	List<Fermata> visitaAmpiezza= m.visitaAmpiezza(m.fermate.get(0)); 
		
	System.out.println(visitaAmpiezza+"\n"); 
	
	List<Fermata> visitaProfondita= m.visitaProfondita(m.fermate.get(0)); 
	
	System.out.println(visitaProfondita+"\n"); 
	
	
	Map<Fermata, Fermata> albero= m.alberoVisita(m.fermate.get(0)); 
	
	for (Fermata f : albero.keySet()) {
		System.out.format("%s <- %s", f, albero.get(f)+"\n"); 
	}
	
	System.out.println("\nCammino minimo\n"); 
	List<Fermata> cammino= m.camminiMinimi(m.fermate.get(0), m.fermate.get(1)); 
	System.out.println(cammino); 
	
	}
	
}
