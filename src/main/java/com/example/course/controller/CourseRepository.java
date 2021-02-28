package com.example.course.controller;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends  JpaRepository<CourseProgram,String>{
    List<User> findByCourseName(String courseName);

}
