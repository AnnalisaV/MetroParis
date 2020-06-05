package it.polito.tdp.metroparis.model;

public class CoppiaFermate {

	private Fermata f1; 
	private Fermata f2;
	
	
	/**
	 * @param f1
	 * @param f2
	 */
	public CoppiaFermate(Fermata f1, Fermata f2) {
		super();
		this.f1 = f1;
		this.f2 = f2;
	}


	public Fermata getF1() {
		return f1;
	}


	public void setF1(Fermata f1) {
		this.f1 = f1;
	}


	public Fermata getF2() {
		return f2;
	}


	public void setF2(Fermata f2) {
		this.f2 = f2;
	} 
	
	
}
