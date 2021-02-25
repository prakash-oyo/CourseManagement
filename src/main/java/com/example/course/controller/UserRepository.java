package com.example.course.controller;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface UserRepository extends JpaRepository<User, String> {

    List<User> findByEmail(String email);
    List<User> findAll();

}


