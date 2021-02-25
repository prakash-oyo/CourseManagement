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

        Optional<User> idFound = userRepository.findById(id);

        if(!idFound.isPresent())
        {
            ApiResponse ret=new ApiResponse(idFound,Boolean.FALSE,"No Such User");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ret);
        }

            return ResponseEntity.ok(new ApiResponse(idFound.get(),Boolean.TRUE,"DONE"));

    }

    @RequestMapping(method = RequestMethod.POST, value = "/new/user")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody User noob) {

        User toParse = new User(noob.getName(), noob.getEmail(), noob.getNumber(), noob.getRole());

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

        String id=(String) userMap.get("id");
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

        if(toAddCourse.getCourseName().compareTo("invalid")==0||
                toAddCourse.maxCredit==-1)
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
    public ResponseEntity<StatusResponse> addSubjectToCourse(@RequestBody Map<String, Object> userMap) {

        String id= (String) userMap.get("id");

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

        String subject=(String) userMap.get("subjectName");
        String subjectCode= validatorSubject.returnSubCode(subject);
        Optional<Subject> subjectSelected;
        subjectSelected = subjectRepository.findById(subjectCode);

        if(!subjectSelected.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"No Such Subject"
                    )
            );


        String courseCode = validatorSubject.returnSubCode((String) userMap.get("courseName"));

        if (!courseRepository.findById(courseCode).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"No Such Course"
                    )
            );

        CourseProgram courseSelected = courseRepository.findById(courseCode).get();


        List<Subject> sub=courseSelected.availableSubjects;

        for( int index=0 ; index<sub.size() ; index++)
            if(sub.get(index).getSubjectCode().compareTo(subjectCode)==0)
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

        String id=(String) userMap.get("id");

        Optional<User> idFound;
        idFound=userRepository.findById(id);


        if(!idFound.isPresent() || idFound.get().getRole().compareTo("STUDENT") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Invalid Id Or Authorization Error"
                    )
            );

        String courseCode= validatorSubject.returnSubCode((String) userMap.get("courseName"));

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

        String id=(String) userMap.get("id");

        Optional<User> idFound;
        idFound = userRepository.findById(id);



        if(!idFound.isPresent() || idFound.get().getRole().compareTo("STUDENT") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                        Boolean.FALSE,"Invalid Id Or Authorization Error"
                )
        );

        String subjectCode= validatorSubject.returnSubCode((String) userMap.get("subjectName"));

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


        String courseCode= validatorSubject.returnSubCode(courseSelected.get().getCourseName());
        Optional<CourseProgram> course;
        course = courseRepository.findById(courseCode);
        CourseProgram currentCourse=course.get();
        Boolean isPresent=false;

        for(int sub = 0; sub< currentCourse.availableSubjects.size() &&!isPresent; sub++)
            if(subjectCode.compareTo(currentCourse.availableSubjects.get(sub).getSubjectCode())==0)
                isPresent=true;

        if(!isPresent)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Subject Not In "+currentCourse.getCourseName()
                    )
            );

        UserCourseProgram userCourse=courseSelected.get();

        int netCredit=0;

        for(int sub=0 ; sub<userCourse.addedSubject.size();sub++) {
            if (subjectCode.compareTo(userCourse.addedSubject.get(sub).getSubjectCode()) == 0)
                isPresent = false;

                netCredit += userCourse.addedSubject.get(sub).getCredit();

        }


        if(!isPresent)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Subject Already Added"
                    )
            );

        if(netCredit + credit <= currentCourse.getMaxCredit() ) {
            courseSelected.get().getAddedSubject().add(new Subject((String) userMap.get("subjectName"), credit));
            UserCourseProgram toAdd = courseSelected.get();
            userCourseRepository.deleteById(id);
            userCourseRepository.save(toAdd);

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
        String courseCode = validatorSubject.returnSubCode(courseName);

        if (!(courseRepository.findById(courseCode).isPresent()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                        Boolean.FALSE,"Course Does Not Exist"
                )
        );

        List<UserCourseProgram> tempCourse=userCourseRepository.findAll();

        for(int index=0 ; index<tempCourse.size() ; index++)
            if(tempCourse.get(index).getCourseName().compareTo(courseName) == 0)
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
        String subjectCode = validatorSubject.returnSubCode(subjectName);

        if (!(subjectRepository.findById(subjectCode).isPresent()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                            Boolean.FALSE,"Subject Not Found"
                    )
            );



        List<UserCourseProgram> temp=userCourseRepository.findAll();




        for(int index=0 ; index<temp.size() ; index++)
        {
            System.out.println(temp.get(index).getCourseName());

            List<Subject> subs = temp.get(index).getAddedSubject();

            for(int rIndex=0 ; rIndex<subs.size();rIndex++) {

                System.out.println(subs.get(rIndex).getSubjectName());

                if (subs.get(rIndex).getSubjectName().compareTo(subjectName) == 0)
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(
                                    Boolean.FALSE,"Subject Enrolled By Some Student"
                            )
                    );



            }
        }


        List<CourseProgram> listOfCourse=courseRepository.findAll();



        for(int index=0; index<listOfCourse.size() ; index++ )
        {
            List<Subject> cSub=listOfCourse.get(index).availableSubjects;


            for(int rIndex=0; rIndex<cSub.size() ;rIndex++)
                if(cSub.get(rIndex).getSubjectName().compareTo(subjectName)==0)
                    cSub.remove(rIndex);

            CourseProgram curr=listOfCourse.get(index);

            courseRepository.deleteById(listOfCourse.get(index).getCourseCode());
            courseRepository.save(curr);

        }



        subjectRepository.deleteById(subjectCode);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(
                        Boolean.TRUE,"Subject Deleted"
                )
        );


    }





}
