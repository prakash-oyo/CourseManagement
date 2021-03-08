package com.example.course.repositories;

import com.example.course.model.CourseProgram;
import com.example.course.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;



@Repository
public interface CourseRepository extends  JpaRepository<CourseProgram,String>{
    List<User> findByCourseName(String courseName);

}
