/**
 * Atenção, este arquivo é propriedade de www.endcraft.com.br e sua copia esta proibida.
 * Este aquivo é parte de um estudo e para possível certificação da amazon.
 * 
 * Mais informações aws.amazon.com/java
 * Data de criação 23/07/2015
 * @author JonasPC
 */
package me.jonasxpx;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.ModifyInstanceAttributeRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

public class AmazonMain {
	
	private AWSCredentials credentials = null;
	private AmazonEC2 ec2 = null;
	
	/**
	 * Chamada do metodo constuctor.
	 * @param region Região a ser definida
	 */
	public AmazonMain(Region region){
		register(region);
	}
	
	/**
	 * Metodo privado chamado no metodo contrutor para iniciar as credencias(Secret key)
	 * E setar a region principal de trabalho
	 * @param region 
	 */
	private void register(Region region){
		credentials = new ProfileCredentialsProvider().getCredentials();
		ec2 = new AmazonEC2Client(credentials);
		ec2.setRegion(region);
	}
	
	public AmazonEC2 getAmazonClient(){
		return ec2;
	}
	
	/**
	 * Para uma instancia.
	 * @param instanceId ID da instacia.
	 */
	public void stopInstance(String instanceId){
		ec2.stopInstances(new StopInstancesRequest().withInstanceIds(instanceId));
		System.out.println(instanceId + " Stopped!...");
	}
	
	/**
	 * Inicia um instancia ou Aguarda uma instacia ser parada para iniciar.
	 * @param instanceId Id da instancia
	 */
	public void startInstance(String instanceId){
		String a = null;
		while(true){
			a = ec2.describeInstances(new DescribeInstancesRequest().withInstanceIds(instanceId)).getReservations().get(0).getInstances().get(0).getState().getName();
			System.out.println("Checando se esta padarado.... "+ a);
			if(a.equalsIgnoreCase("stopped")){
				ec2.startInstances(new StartInstancesRequest().withInstanceIds(instanceId));
				System.out.println(instanceId + " Started!...");
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		
	}
	
	/**
	 * Muda o Tipo da instancia.
	 * @param instanceId Id da instancia
	 * @param type Tipo da instancia
	 * @param ebs True para instancias acima da C3 e False Para instancias abaixo da C3
	 */
	public void chargeInstanceType(String instanceId,InstanceType type, boolean ebs){
		String a = null;
		ModifyInstanceAttributeRequest miar = null;
		while(true){
			a = ec2.describeInstances(new DescribeInstancesRequest().withInstanceIds(instanceId)).getReservations().get(0).getInstances().get(0).getState().getName();
			System.out.println("Checando se esta padarado.... "+ a);
			if(a.equalsIgnoreCase("stopped")){
				miar = new ModifyInstanceAttributeRequest();
				miar.withInstanceId(instanceId);
				miar.withInstanceType(type.nome);
				ec2.modifyInstanceAttribute(miar);
				miar = new ModifyInstanceAttributeRequest();
				miar.withInstanceId(instanceId);
				miar.withEbsOptimized(ebs);
				ec2.modifyInstanceAttribute(miar);
				System.out.println("Alterado para " + type.name());
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
	
	
	/**
	 * Inicia do sistema com os argumentos.
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length == 0){
			System.out.println("Use <stop,start> <instanceId>");
			System.out.println("Use <change> <type (t2medium,c4large,c4xlarge)> <instanceId>");
			return;
		}
		AmazonMain main = new AmazonMain(Region.getRegion(Regions.US_EAST_1));
		if(args.length == 2){
			if(args[0].equalsIgnoreCase("stop")){
				main.stopInstance(args[1]);
				return;
			}
			if(args[0].equalsIgnoreCase("start")){
				main.startInstance(args[1]);
				return;
			}
		}
		if(args.length == 3)
			if(args[0].equalsIgnoreCase("change")){
				switch(args[1]){
				case "t2medium":
					main.chargeInstanceType(args[2], InstanceType.T2Medium, false);
					break;
				case "c4large":
					main.chargeInstanceType(args[2], InstanceType.C4Large, true);
					break;
				case "c4xlarge":
					main.chargeInstanceType(args[2], InstanceType.C4xLarge, true);
					break;
				}
				return;
			}
		System.out.println("Use <stop,start> <instanceId>");
		System.out.println("Use <change> <type (t2medium,c4large,c4xlarge)> <instanceId>");
		
	}
	
	
}
