package com.example.course.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class RestEndpoints {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;



    @GetMapping("/user/{id}")
    public ResponseEntity <ApiResponse> searchUser(@PathVariable String id) {

        Optional<User> userFound = userRepository.findById(id);

        if(!userFound.isPresent())
        {
            ApiResponse ret = new ApiResponse(userFound,Boolean.FALSE,"No Such User");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ret);
        }

        return ResponseEntity.ok(new ApiResponse(userFound.get(),Boolean.TRUE,"DONE"));

    }

    @RequestMapping(method = RequestMethod.POST, value = "/new/user")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody User newUser) {

        User toParse = new User(newUser.getName(), newUser.getEmail(), newUser.getNumber(), newUser.getRole());

        if (toParse.getName().compareTo("invalid") == 0 ||
                toParse.getEmail().compareTo("invalid") == 0 ||
                toParse.getNumber().compareTo("invalid") == 0 ||
                toParse.getRole().compareTo("invalid") == 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Invalid Information Entered",Boolean.FALSE,"Try Again"));


        if (toParse.getEmail().length() <= 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(
                    "Invalid Email",Boolean.FALSE,"TRY AGAIN"
            ));

        Optional<User> foundUser = userRepository.findById(toParse.getEmail());

        if(foundUser.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(
                    "User Already Exists",Boolean.FALSE,"Try Different Email"));

        userRepository.save(toParse);

        return ResponseEntity.ok(new ApiResponse(toParse,Boolean.TRUE,"USER CREATED!"));

    }

    @PostMapping("/new/subject")
    public ResponseEntity<StatusResponse> addSubject(@RequestBody Map<String, Object> userMap) {

        String id = (String) userMap.get("id");

        Optional<User> idFound;
        idFound = userRepository.findById(id);
        
        if (!idFound.isPresent()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"No Such USER"
                    )
            );
        }



        if (idFound.get().getRole().compareTo("ADMIN") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Authorization Error"
                    )
            );


        ObjectMapper mapper = new ObjectMapper();
        Subject tempSubject = mapper.convertValue(userMap, Subject.class);
        Subject toAddSubject = new Subject(tempSubject.getSubjectName(), tempSubject.getCredit());

        if (toAddSubject.getSubjectName().compareTo("invalid") == 0 ||
                toAddSubject.getCredit() == -1)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Invalid Subject Details"
                    )
            );

        if(subjectRepository.findById(toAddSubject.getSubjectCode()).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Subject Already Added"
                    )
            );

        subjectRepository.save(toAddSubject);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(
                        Boolean.TRUE,"Subject Added"
                )
        );

    }



    @RequestMapping(method = RequestMethod.POST,value = "/new/course")
    public ResponseEntity<StatusResponse> addCourse(@RequestBody Map<String, Object> userMap)
    {

        String id = (String) userMap.get("id");
        Optional<User> idFound;

        idFound = userRepository.findById(id);
        if (!idFound.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"No Such USER"
                    )
            );


        if(idFound.get().getRole().compareTo("ADMIN")!=0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                    Boolean.FALSE,"Unauthorized Access"
            ));

        CourseProgram toAddCourse=new CourseProgram((String) userMap.get("CourseName"),
                (int) userMap.get("maxCredit"));

        if(toAddCourse.getCourseName().compareTo("invalid") == 0||
                toAddCourse.maxCredit == -1)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Invalid Course Details Entered"
                    )
            );


        if(courseRepository.findById(toAddCourse.getCourseCode()).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Course Already Exist"
                    )
            );


        courseRepository.save(toAddCourse);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(
                        Boolean.TRUE,"Course Successfully Created"
                )
        );

    }


    @PostMapping("/addsubjecttocourse")
    public ResponseEntity < StatusResponse> addSubjectToCourse(@RequestBody Map<String, Object> userMap) {

        String id = (String) userMap.get("id");

        Optional<User> idFound;
        idFound = userRepository.findById(id);
        if (!idFound.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"No Such USER"
                    )
            );

        if(idFound.get().getRole().compareTo("ADMIN")!=0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Unauthorized Access"
                    )
            );

        String subject = (String) userMap.get("subjectName");
        String subjectCode = SubjectValidator.getSubjectCode(subject);
        Optional<Subject> subjectSelected;
        subjectSelected = subjectRepository.findById(subjectCode);

        if(!subjectSelected.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"No Such Subject"
                    )
            );


        String courseCode = SubjectValidator.getSubjectCode((String) userMap.get("courseName"));

        if (!courseRepository.findById(courseCode).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"No Such Course"
                    )
            );

        CourseProgram courseSelected = courseRepository.findById(courseCode).get();


        List<Subject> sub = courseSelected.availableSubjects;

        for( int index = 0 ; index < sub.size() ; index++)
            if(sub.get(index).getSubjectCode().compareTo(subjectCode) == 0)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                                Boolean.FALSE,"Subject Already Added to "+courseSelected.getCourseName()
                        )
                );

        courseSelected.getAvailableSubjects().add(subjectSelected.get());

        courseRepository.save(courseSelected);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                        Boolean.TRUE,"Subject Added to "+courseSelected.getCourseName()
                )
        );

    }


    @PostMapping("/enroll")
    public ResponseEntity<StatusResponse> enrollCourse(@RequestBody Map<String, Object> userMap) {

        String id = (String) userMap.get("id");

        Optional<User> idFound;
        idFound = userRepository.findById(id);


        if(!idFound.isPresent() || idFound.get().getRole().compareTo("STUDENT") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Invalid Id Or Authorization Error"
                    )
            );

        String courseCode = SubjectValidator.getSubjectCode((String) userMap.get("courseName"));

        if(!courseRepository.findById(courseCode).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Course Not Found"
                    )
            );

        if(userCourseRepository.findById(id).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"User Already Enrolled"
                    )
            );

        userCourseRepository.save(new UserCourseProgram(id,courseCode));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                        Boolean.TRUE,"Enrolled to "+((String) userMap.get("courseName"))
                )
        );

    }

    @PostMapping("/student/add/subject")
    public ResponseEntity<StatusResponse> addSubjectByStudent(@RequestBody Map<String, Object> userMap) {

        String id = (String) userMap.get("id");

        Optional<User> idFound;
        idFound = userRepository.findById(id);



        if(!idFound.isPresent() || idFound.get().getRole().compareTo("STUDENT") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Invalid Id Or Authorization Error"
                    )
            );

        String subjectCode = SubjectValidator.getSubjectCode((String) userMap.get("subjectName"));

        if(!subjectRepository.findById(subjectCode).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Subject Not Found"
                    )
            );


        int credit = subjectRepository.findById(subjectCode).get().getCredit();

        Optional<UserCourseProgram> courseSelected;
        courseSelected = userCourseRepository.findById(id);

        if(!courseSelected.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Not Enrolled in any course"
                    )
            );


        String courseCode = SubjectValidator.getSubjectCode(courseSelected.get().getCourseName());
        Optional<CourseProgram> course;
        course = courseRepository.findById(courseCode);
        CourseProgram currentCourse=course.get();
        Boolean isSubjectPresent=false;

        for(int sub = 0; sub < currentCourse.availableSubjects.size() && !isSubjectPresent; sub++)
            if(subjectCode.compareTo(currentCourse.availableSubjects.get(sub).getSubjectCode()) == 0)
                isSubjectPresent = true;

        if(!isSubjectPresent)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Subject Not In "+currentCourse.getCourseName()
                    )
            );

        UserCourseProgram userCourse = courseSelected.get();

        int netCredit = 0;


        for(int sub=0 ; sub < userCourse.addedSubject.size(); sub++) {
            if (subjectCode.compareTo(userCourse.addedSubject.get(sub).getSubjectCode()) == 0)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                                Boolean.FALSE,"Subject Already Added"
                        )
                );

            netCredit += userCourse.addedSubject.get(sub).getCredit();

        }

        if(netCredit + credit <= currentCourse.getMaxCredit() ) {
            courseSelected.get().getAddedSubject().add(new Subject((String) userMap.get("subjectName"), credit));
            UserCourseProgram toAddCourse = courseSelected.get();
            userCourseRepository.deleteById(id);
            userCourseRepository.save(toAddCourse);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.TRUE,"Subject Successfully Added"
                    )
            );

        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                        Boolean.FALSE,"Insufficient Credit"
                )
        );

    }


    @DeleteMapping("del/student")
    public ResponseEntity<StatusResponse> deleteStudent(@RequestBody Map<String, Object> userMap)
    {
        String studentId=(String) userMap.get("studentId");

        Optional<User> idFound;
        idFound = userRepository.findById(studentId);

        if(!idFound.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Student Does Not Exist"
                    )
            );

        if(idFound.get().getRole().compareTo("STUDENT") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Admin Can't Be Deleted"
                    )
            );

        String adminId=(String) userMap.get("adminId");

        idFound = userRepository.findById(adminId);


        if(!idFound.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Admin Does Not Exist"
                    )
            );

        if(idFound.get().getRole().compareTo("ADMIN") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Unauthorized Access"
                    )
            );


        if(userCourseRepository.findById(studentId).isPresent())
            userCourseRepository.deleteById(studentId);
        userRepository.deleteById(studentId);


        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(
                        Boolean.TRUE,"Student Deleted"
                )
        );


    }

    @DeleteMapping("del/course")
    public ResponseEntity<StatusResponse> deleteCourse(@RequestBody Map<String, Object> userMap) {

        String adminId = (String) userMap.get("adminId");

        Optional<User> idFound;
        idFound = userRepository.findById(adminId);

        if (!idFound.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Admin Does Not Exist"
                    )
            );

        if (idFound.get().getRole().compareTo("ADMIN") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Unauthorized Access"
                    )
            );


        String courseName = (String) userMap.get("courseName");
        String courseCode = SubjectValidator.getSubjectCode(courseName);

        if (!(courseRepository.findById(courseCode).isPresent()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Course Does Not Exist"
                    )
            );

        List<UserCourseProgram> userCourses = userCourseRepository.findAll();

        for(int index=0 ; index < userCourses.size() ; index++)
            if(userCourses.get(index).getCourseName().compareTo(courseName) == 0)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                                Boolean.FALSE,"Course enrolled by some student"
                        )
                );


        courseRepository.deleteById(courseCode);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                        Boolean.TRUE,"Course Deleted"
                )
        );

    }

    @DeleteMapping("del/subject")
    public ResponseEntity<StatusResponse> deleteSubject(@RequestBody Map<String, Object> userMap) {

        String adminId = (String) userMap.get("adminId");

        Optional<User> idFound;
        idFound = userRepository.findById(adminId);

        if (!idFound.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Insufficient Credit"
                    )
            );

        if (idFound.get().getRole().compareTo("ADMIN") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Unauthorized Access"
                    )
            );


        String subjectName = (String) userMap.get("subjectName");
        String subjectCode = SubjectValidator.getSubjectCode(subjectName);

        if (!(subjectRepository.findById(subjectCode).isPresent()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Subject Not Found"
                    )
            );


        List<UserCourseProgram> userCourses = userCourseRepository.findAll();


        for(int index=0 ; index<userCourses.size() ; index++)
        {
            List<Subject> subs = userCourses.get(index).getAddedSubject();

            for(int rIndex=0 ; rIndex<subs.size();rIndex++) {

                if (subs.get(rIndex).getSubjectName().compareTo(subjectName) == 0)
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                                    Boolean.FALSE,"Subject Enrolled By Some Student"
                            )
                    );

            }
        }


        List<CourseProgram> listOfCourse = courseRepository.findAll();



        for(int index = 0; index<listOfCourse.size() ; index++ )
        {
            List<Subject> currentSubject = listOfCourse.get(index).getAvailableSubjects();


            for(int rIndex = 0; rIndex < currentSubject.size() ; rIndex++)
                if(currentSubject.get(rIndex).getSubjectName().compareTo(subjectName) == 0)
                    currentSubject.remove(rIndex);

            CourseProgram currentCourse = listOfCourse.get(index);

            courseRepository.deleteById(listOfCourse.get(index).getCourseCode());
            courseRepository.save(currentCourse);

        }

        subjectRepository.deleteById(subjectCode);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(
                        Boolean.TRUE,"Subject Deleted"
                )
        );

    }

}
