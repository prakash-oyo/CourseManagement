package com.example.course.controller;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,String> {

    List<Subject> findBySubjectName(String subjectName);
    List<Subject> findAll();

}
