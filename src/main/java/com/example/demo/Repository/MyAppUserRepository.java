package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.MyAppUser;

public interface MyAppUserRepository extends JpaRepository<MyAppUser, Long> {
	Optional<MyAppUser> findByUsername(String username);

	Optional<MyAppUser> findByEmail(String email);

	Optional<MyAppUser> findByPhone(String phone);
}
