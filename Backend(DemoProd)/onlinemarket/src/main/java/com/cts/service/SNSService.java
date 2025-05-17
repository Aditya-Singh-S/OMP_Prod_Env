package com.cts.service;

import java.util.List;

public interface SNSService {
	
	void subscribeUser(String email);
	
	void notifyOnAddProduct();
	
	void notifyAdminOnUnSubscription(String productName,String userEmail);
	void notifyUserOnUnSubscription(String nickName,String productName,String userEmail);
	
	void userEmailVerify(String email);
	
	void notifyUserOnUpdateProduct(List<String> userEmails);
	
	void notifyAdminOnUpdateProduct();
	
	void notifyReviewCreated(String userEmail, String productName, double rating, String review);
	
    void notifyReviewDeleted(String userEmail, String productName);
    
    void notifyOnSubscribing(String email, String nickName, String productName);
    
    void notifyOnUnSubscribing(String email, String nickName, String productName);
    
    void notifyonresetPassword(String email);

}
