package me.jonasxpx;

public enum InstanceType {

	T2Medium("t2.medium"),
	C4Large("c4.large"),
	C4xLarge("c4.xlarge");
	
	public String nome;
	
	
	private InstanceType(String nome){
		this.nome = nome;
	}
}
