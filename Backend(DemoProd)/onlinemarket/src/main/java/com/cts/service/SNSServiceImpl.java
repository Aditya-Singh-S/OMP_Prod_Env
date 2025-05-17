package com.cts.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import com.cts.repository.UserRepository;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;

@Service
public class SNSServiceImpl implements SNSService {
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	SnsClient snsClient;
	
	@Value("${aws.snsTopicARN}")
	String TOPIC_ARN;

	
	@Override
	public void subscribeUser(String email) {
		
		String filterPolicy = String.format("""
				{
					"recipient" : ["%s","USER"]
				}
				""", email);
		
		Map<String, String> attributes = new HashMap<>();
		attributes.put("FilterPolicy", filterPolicy);
		
		System.out.println("Inside subscribeUser");
		SubscribeRequest subscribeRequest = SubscribeRequest.builder().protocol("email")
				.endpoint(email).topicArn(TOPIC_ARN).attributes(attributes).build();
		
		snsClient.subscribe(subscribeRequest);

	}


	@Override
	public void notifyOnAddProduct() {
		
		String subject = "New Product Added";
		String message = String.format("""
                Hello Everyone,

                New Product has been added into our website.
                Please have a look!!
                """);
		
		Map<String, MessageAttributeValue> attributes = Map.of(
	            "recipient", MessageAttributeValue.builder()
	                .dataType("String")
	                .stringValue("USER").build());
		
		PublishRequest publishRequest = PublishRequest.builder()
				.subject(subject)
				.message(message)
				.messageAttributes(attributes)
				.topicArn(TOPIC_ARN).build();
		
		snsClient.publish(publishRequest);
		
	}
	
	// Scrum-80 : Email to admin and users when admin update user preferences
		// and this method sends email to the admin
		@Override
		public void notifyAdminOnUnSubscription(String productName,String userEmail) {
			String subject = "Product Subscription Removed!";
			String message = "Hey Admin, you have removed Subscription of the product " + productName + " for " + userEmail;
			
			List<String> adminEmails = userRepo.getAdminList();
			
			for(String email: adminEmails) {
				
				Map<String, MessageAttributeValue> attributes = Map.of(
			            "recipient", MessageAttributeValue.builder()
			                .dataType("String")
			                .stringValue(email).build());
				
				PublishRequest publishRequest = PublishRequest.builder()
		        		.messageAttributes(attributes)
		        		.topicArn(TOPIC_ARN)
		                .subject(subject)
		                .message(message)
		                .build();
		 
		        snsClient.publish(publishRequest);
			}
			
		}
		
		// Scrum-80 : Email to admin and users when admin update user preferences
		// and this method sends email to the user
		@Override
		public void notifyUserOnUnSubscription(String nickName,String productName,String userEmail) {
			Map<String, MessageAttributeValue> attributes = Map.of(
		             "recipient", MessageAttributeValue.builder()
		                 .dataType("String")
		                 .stringValue(userEmail).build());
		 
		  String subject = "Product Subscription Removed!";
		  String message = "Hey " + nickName +", Admin have removed your Subscription for the product " + productName;
		 
		        PublishRequest publishRequest = PublishRequest.builder()
		          .messageAttributes(attributes)
		          .topicArn(TOPIC_ARN)
		                .subject(subject)
		                .message(message)
		                .build();
		 
		        snsClient.publish(publishRequest);
		}
	
	
	public void userEmailVerify(String email)
	{
		String message = "Hey Please reset your passowrd using the below link  http://online-marketplace-bucket.s3-website-us-east-1.amazonaws.com/forgot-page";
		
		
		System.out.println("Forget Password");
		Map<String, MessageAttributeValue> attributes = Map.of(
	            "recipient", MessageAttributeValue.builder()
	                .dataType("String")
	                .stringValue(email).build());
		
		PublishRequest publishRequest = PublishRequest.builder().message(message)
				.messageAttributes(attributes)
				.topicArn(TOPIC_ARN).build();
		
		snsClient.publish(publishRequest);
	}
	



	@Override
	public void notifyUserOnUpdateProduct(List<String> userEmails) {
		// TODO Auto-generated method stub

		String subject = "Product Updated";
		//String message = "Hey User!! The product you have subscribed has been updated by Admin.";
		
		for(String email: userEmails) {
			
			String message = String.format("""
	                Hello %s,

	                The product you have subscribed has been updated by Admin.
	                Please have a look!!
	                """, email);
			
			Map<String, MessageAttributeValue> attributes = Map.of(
		            "recipient", MessageAttributeValue.builder()
		                .dataType("String")
		                .stringValue(email).build());
			
			PublishRequest requests = PublishRequest.builder()
					.subject(subject)
					.message(message)
					.topicArn(TOPIC_ARN)
					.messageAttributes(attributes)
					.build();
			
			snsClient.publish(requests);
		}
		
	}


	@Override
	public void notifyAdminOnUpdateProduct() {
		
List<String> adminEmails = userRepo.getAdminList();
		
		String message = "Attention ADMIN Group!! Someone just updated the product!!";
		
		for(String email: adminEmails) {
			
			
			
			Map<String, MessageAttributeValue> attribute = Map.of(
		            "recipient", MessageAttributeValue.builder()
		                .dataType("String")
		                .stringValue(email).build());
			
			PublishRequest request = PublishRequest.builder().message(message)
					.topicArn(TOPIC_ARN)
					.messageAttributes(attribute)
					.build();
			
			snsClient.publish(request);
			//System.out.println(email);
		}
		
	}


	@Override
	public void notifyReviewCreated(String userEmail, String productName, double rating, String review) {
		// TODO Auto-generated method stub
		
		Map<String, MessageAttributeValue> attributes = Map.of(
	            "recipient", MessageAttributeValue.builder()
	                .dataType("String")
	                .stringValue(userEmail).build());
		
		
		String subject = "New Review Posted!";
        String message = String.format("""
                Hello %s,

                Thank you for posting a review for the product: %s.

                Your review details are:
                Rating: %.1f stars
                Review: %s

                We appreciate your feedback!
                """, userEmail, productName, rating, review);

        PublishRequest publishRequest = PublishRequest.builder()
        		
        		.topicArn(TOPIC_ARN)
                .subject(subject)
                .message(message)
                .messageAttributes(attributes)
                .build();

        snsClient.publish(publishRequest);
		
	}


	@Override
	public void notifyReviewDeleted(String userEmail, String productName) {
		
		Map<String, MessageAttributeValue> attributes = Map.of(
	            "recipient", MessageAttributeValue.builder()
	                .dataType("String")
	                .stringValue(userEmail).build());
		
		String subject = "Review Deleted";
        String message = String.format("""
                Hello %s,
 
                Your review for the product: %s has been successfully deleted.
                
                If you did not initiate this deletion, please contact our support team.
                """, userEmail, productName);
 
        PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(TOPIC_ARN)
                .subject(subject)
                .message(message)
                .messageAttributes(attributes)
                .build();
 
        snsClient.publish(publishRequest);
    }

	@Override
	public void notifyOnSubscribing(String email, String nickName, String productName) {
		// TODO Auto-generated method stub
		String subject = "Product Subscribed";
		String message = String.format("""
                Hello %s,

                Thank you for subscribing to Product %s.

                If you did not initiate this subscription, please contact our support team.
                """, nickName, productName);
		
		Map<String, MessageAttributeValue> attributes = Map.of(
	            "recipient", MessageAttributeValue.builder()
	                .dataType("String")
	                .stringValue(email).build());
		
		PublishRequest request = PublishRequest.builder()
				.subject(subject)
				.message(message)
				.topicArn(TOPIC_ARN)
				.messageAttributes(attributes)
				.build();
		
		snsClient.publish(request);
	}


	@Override
	public void notifyOnUnSubscribing(String email, String nickName, String productName) {
		// TODO Auto-generated method stub
		String subject = "Product Unsubscribed";
		String message = String.format("""
                Hello %s,

                You have unsubscribed to Product %s.

                If you did not initiate this deletion, please contact our support team.
                """, nickName, productName);
		
		Map<String, MessageAttributeValue> attributes = Map.of(
	            "recipient", MessageAttributeValue.builder()
	                .dataType("String")
	                .stringValue(email).build());
		
		PublishRequest request = PublishRequest.builder()
				.subject(subject)
				.message(message)
				.topicArn(TOPIC_ARN)
				.messageAttributes(attributes)
				.build();
		
		snsClient.publish(request);
	}


	@Override
	public void notifyonresetPassword(String email) {
		// TODO Auto-generated method stub
		String message = "Hi User!, Your password has been reset successfully.";

	    Map<String,MessageAttributeValue> attributes=Map.of
				("recipient",MessageAttributeValue.builder()
			  .dataType("String")
			  .stringValue(email).build());
	    		
	    PublishRequest publishRequest = PublishRequest.builder()
	            .message(message)
	            .subject("Password Reset Successful") 
	            .topicArn(TOPIC_ARN)
	            .messageAttributes(attributes)
	            .build();



	    snsClient.publish(publishRequest);
		
	}
		
	}


