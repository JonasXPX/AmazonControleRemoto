package me.jonasxpx;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.InstanceAttributeName;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.ModifyInstanceAttributeRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

public class AmazonMain {
	
	private AWSCredentials credentials = null;
	private AmazonEC2 ec2 = null;

	public AmazonMain(Region region){
		register(region);
	}
	
	private void register(Region region){
		credentials = new ProfileCredentialsProvider().getCredentials();
		ec2 = new AmazonEC2Client(credentials);
		ec2.setRegion(region);
	}
	
	public AmazonEC2 getAmazonClient(){
		return ec2;
	}
	
	public void stopInstance(String instanceId){
		ec2.stopInstances(new StopInstancesRequest().withInstanceIds(instanceId));
		System.out.println(instanceId + " Stopped!...");
	}
	public void startInstance(String instanceId){
		while(true){
			String a = ec2.describeInstances(new DescribeInstancesRequest().withInstanceIds(instanceId)).getReservations().get(0).getInstances().get(0).getState().getName();
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
	
	public void chargeInstanceType(String instanceId,InstanceType type){
		ModifyInstanceAttributeRequest miar = new ModifyInstanceAttributeRequest(instanceId, InstanceAttributeName.InstanceType);
		miar.setInstanceType(type.name());
		ec2.modifyInstanceAttribute(miar);
	}
	
	public static void main(String[] args) {
		AmazonMain main = new AmazonMain(Region.getRegion(Regions.US_EAST_1));
		if(args.length == 0){
			System.out.println("Use <stop,start> <instanceId>");
			return;
		}
		if(args.length == 2){
			if(args[0].equalsIgnoreCase("stop")){
				main.stopInstance(args[1]);
			}
			if(args[0].equalsIgnoreCase("start")){
				main.startInstance(args[1]);
			}
		}
	}
	
	
}
