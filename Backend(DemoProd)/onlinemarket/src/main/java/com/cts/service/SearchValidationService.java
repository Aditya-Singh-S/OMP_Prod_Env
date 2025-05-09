package com.cts.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.cts.exception.InvalidInputException;
import com.cts.exception.InvalidProductException;
import com.cts.exception.InvalidSearchCriteriaException;

@Service
public class SearchValidationService {

    public void validateProductName(String productName) throws InvalidSearchCriteriaException {
        if (!StringUtils.hasText(productName)) {
            throw new InvalidSearchCriteriaException("Product name is required");
        }
        
        if (!productName.matches("[a-zA-Z._0-9\\s]+")) {
            throw new InvalidSearchCriteriaException("Product name cannot contain special characters.");
        }
    }

    public void validateSubsCount(int count) throws InvalidSearchCriteriaException{
        if (count < 0) {
            throw new InvalidSearchCriteriaException("Subscribers count cannot be negative.");
        }
        if(count>=99999) {
        	throw new InvalidSearchCriteriaException("Invalid subscribers count.");
        }
        
        //MethodArgumentTypeMismatchException is added in Global Exception
        
    }

    public void validateRating(double rating) throws InvalidSearchCriteriaException {
        if (rating < 0.0 || rating > 5.0) {
            throw new InvalidSearchCriteriaException("Invalid Rating. Rating must be between 0.0 and 5.0.");
        }  
        //MethodArgumentTypeMismatchException is added in Global Exception
    }

    // New validation method for name, count, and rating
    public void validateSearchCriteria(String name, int count, double rating) throws InvalidSearchCriteriaException{
    
            validateProductName(name);
            validateSubsCount(count);
            validateRating(rating);
        
    }
    
    public void validateSearchCriteria(int count, double rating) throws InvalidSearchCriteriaException{
            validateSubsCount(count);
            validateRating(rating);
    }

    public void validateSearchCriteria(String name, int count) throws InvalidSearchCriteriaException{
    	validateProductName(name);
    	validateSubsCount(count);
    }

    public void validateSearchCriteria(String name, double rating) throws InvalidSearchCriteriaException{
       
            validateProductName(name);
            validateRating(rating);
        
    }
}