package com.cts.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cts.dto.ResponseDTO;
import com.cts.dto.UserDTO;
import com.cts.entity.User;
import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

	User findByEmail(String email);
	
    boolean existsByEmail(String email);
    
    @Query("SELECT u.email FROM User u WHERE u.email = :email")
    String searchByEmail(@Param("email") String email);
    
    List<User> findByIsActive(boolean isActive);
	
}
