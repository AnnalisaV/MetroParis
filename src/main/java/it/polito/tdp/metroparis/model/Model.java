package it.polito.tdp.metroparis.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

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
		for (Fermata fp: this.fermate) {
			List<Fermata> connesse = dao.fermateSuccessive(fp, fermateIdMap);
			
			for(Fermata f : connesse) {
				this.graph.addEdge(fp, f); // itera meno volte (qui circa due volte, non 300)
			}
			
		}
		System.out.format("Grafo caricato %d vertici %d archi\n", this.graph.vertexSet().size(), this.graph.edgeSet().size()); 
		
		
		//TERZO METODO -> chiedere al db l'elenco degli archi 
		//(qui e' facile perche' il db ha gia' una tabella con l'informazione, 
		// a volte e' piu' difficile e diventa inefficiente sul db a livello sql)
		List<CoppiaFermate> coppie= dao.coppieFermate(fermateIdMap);
		for (CoppiaFermate c : coppie) {
			graph.addEdge(c.getPartenza(), c.getArrivo()); 
		}
		
		System.out.format("Grafo caricato %d vertici %d archi\n", this.graph.vertexSet().size(), this.graph.edgeSet().size()); 
	}
	
	//TestModel interno alla classe Model per non fare un'altra classe
	public static void main(String arg[]) {
		Model m= new Model(); 
		
	}
	
}
