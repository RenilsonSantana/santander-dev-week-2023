package me.dio.domain.repository;

import org.springframework.stereotype.Repository;

import me.dio.domain.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	boolean existsByAccountNumber(String number);
	boolean existsByCardNumber(String number);
}
