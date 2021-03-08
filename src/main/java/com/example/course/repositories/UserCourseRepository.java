package com.example.course.repositories;



import com.example.course.model.UserCourseProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;



@Repository
public interface UserCourseRepository extends JpaRepository<UserCourseProgram,String> {

    List<UserCourseProgram> findByEmail(String email);

}
