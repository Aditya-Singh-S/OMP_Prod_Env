package com.cts.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AWSSNSConfig {
	
	@Value("${aws.snsAccessKey}")
	String ACCESS_KEY;
	

	@Value("${aws.snsSecretKey}")
	String SECRET_KEY;

	@Bean
	public SnsClient snsClient() {
		AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
		
		return SnsClient.builder().region(Region.US_EAST_1)
				.credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
				.build();
	}
	
}
