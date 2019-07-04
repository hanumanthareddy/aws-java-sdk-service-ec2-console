package com.amazonaws.samples;

import java.util.HashMap;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.ModifyInstanceAttributeRequest;
import com.amazonaws.services.ec2.model.ModifyInstanceAttributeResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;


public class AwsConsoleApp {
    static AmazonEC2 ec2;
//    Loading credentials or profile
    private static void init() throws Exception {
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location, and is in valid format.",
                    e);
        }
        ec2 = AmazonEC2ClientBuilder.standard()
            .withCredentials(credentialsProvider)
            .withRegion("<enter your region>")
            .build();
    }

//  Generic Method to get an instance id using provided Public Ip address
    public static String getInstanceIdValue(String public_ip_address) {
	final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
  boolean done = false;
  String instance_id_value = null;
  HashMap<String, String> Desc_instance = new HashMap<String, String>();
  DescribeInstancesRequest request = new DescribeInstancesRequest();
  
  while(!done) {
      DescribeInstancesResult response = ec2.describeInstances(request);
      for(Reservation reservation : response.getReservations()) {
          for(Instance instance : reservation.getInstances()) {
          	Desc_instance.put(instance.getPublicIpAddress(), instance.getInstanceId());
          }
      }
      instance_id_value = Desc_instance.get(public_ip_address);
      request.setNextToken(response.getNextToken());

      if(response.getNextToken() == null) {
          done = true;
      }
  }
  return instance_id_value;
}
    
//  Start Instance
    public static void startInstance()
    {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
    	String instance_id = getInstanceIdValue("<provide your ip address>");
        StartInstancesRequest request = new StartInstancesRequest()
            .withInstanceIds(instance_id);
        ec2.startInstances(request);
        
        System.out.printf("Successfully started instance %s", instance_id);
    }
        
//  Stop Instance
    public static void stopInstance()
    {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
    	String instance_id = getInstanceIdValue("<provide your ip address>");
        StopInstancesRequest request = new StopInstancesRequest()
            .withInstanceIds(instance_id);
        ec2.stopInstances(request);
        
        System.out.printf("Successfully Stopped instance %s", instance_id);
    }    

//  Attach Security group to a instance
    public static void attachSecurityGroup() {
    	final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
    	String instance_id = getInstanceIdValue("<provide your ip address>");
    	ModifyInstanceAttributeRequest create_request = new ModifyInstanceAttributeRequest()
    			.withGroups("sg-<provide your security group id>")
    			.withInstanceId(instance_id);
    	
    	ModifyInstanceAttributeResult create_response =
        	    ec2.modifyInstanceAttribute(create_request);
    	
    	System.out.printf("Attached a security group is working %s %s", create_response, create_request);
    }
    
//  Main method    
    public static void main(String[] args) throws Exception {
        init();
        System.out.println("*********** Welcome to the AWS Console ***********");
// call the below required methods to perform the operations like start, stop and attach security groups to instance
//        startInstance();
//        stopInstance();
//        attachSecurityGroup();
    }
}
