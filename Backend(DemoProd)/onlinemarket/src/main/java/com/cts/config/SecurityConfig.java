

package com.cts.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

import java.net.http.HttpHeaders;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        //.authorizeHttpRequests(auth -> auth)
        
                .authorizeHttpRequests((authz) -> authz
                		
                						.requestMatchers(HttpMethod.OPTIONS,"OMP/**").permitAll()
                						
                                        .requestMatchers(
                                        		 "/OMP/admin/**"
                                        		,"OMP/viewUsersSubscribedToProduct"
                                        		,"OMP/getAllUser").hasAuthority("ADMIN")
                                        
                                        .requestMatchers(
                                        		"/OMP/login"
                                        		,"OMP/register"
                                        		,"OMP/reset-password"
                                        		,"OMP/generate-reset-link"
                                        		,"OMP/verify-email"
                                        		,"OMP/viewAllProducts"
                                        		,"/OMP/viewProductDetails/{productId}"
                                        		,"/OMP/product/**"
                                        		,"OMP/topSubscribedProduct"
                                        		,"OMP/topRatedProducts"
                                        		,"OMP/searchProductByName"
                                        		,"OMP/searchProductBySubsCount"
                                        		,"OMP/searchProductByRating"
                                        		,"OMP/searchProductBySubsCountAndRating"
                                        		,"OMP/searchProductByNameSubsRating"
                                        		,"OMP/searchProductByNameAndRating"
                                        		,"OMP/searchProductByNameAndSubsCount"
                                        		,"/OMP/reviews/getSpecificProductReviews"
                                        		,"/OMP/reviews/highestRatedReview"
                                        		
                                        		,"OMP/getUserIdByEmail"
                                        		).permitAll()
                                        
                                        
                   	                     .requestMatchers(
                   	                    		"OMP/user/image/{userId}",
                   	                    		"OMP/myDetails/{userId}",
                   	                    		 "OMP/addSubscription"
                   	                    		 ,"/OMP/removeSubscription"
                   	                    		 ,"OMP/updateUser/{userId}"
                                        		 ,"OMP/reviews/user/{userId}"
//                                        		 ,"OMP/reviews/**"
                                        		 ,"OMP/reviews/updateReview"
                   	                    		 ,"OMP/viewSubscriptionList"
                   	                    		 ,"OMP/getProductSubscriptionList"
                   	                    		,"OMP/getUserEmailById",
                   	                    		"OMP/reviews/all/user/{userId}",
                   	                    		"OMP/product/imageByName/{name}"
                   	                    		).hasAnyAuthority("USER","ADMIN")
                                        
                                        
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
//                .httpBasic(withDefaults())
//                .authenticationProvider(authenticationProvider())
//                .csrf((csrf) -> csrf.disable());
//
        return http.build();
    }
}
