package com.example.course.controller;

import com.example.course.model.*;
import com.example.course.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;



@RestController
public class Requests {

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

        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(user,Boolean.FALSE,"No Such User"));

        return ResponseEntity.ok(new ApiResponse(user.get(),Boolean.TRUE,"Successful"));

    }

    @RequestMapping(method = RequestMethod.POST, value = "/new/user")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody User body) {

        if (Validator.isValidName(body.getName()) && Validator.isValidEmail(body.getEmail()) && Validator.isValidNumber(body.getNumber()) && Validator.isValidRole(body.getRole()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Invalid Information Entered",Boolean.FALSE,"Try Again"));

        Optional<User> user = userRepository.findById(body.getEmail());
        if(user.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("User Already Exists",Boolean.FALSE,"Try Different Email"));
        User newUser = new User(body.getName(), body.getEmail(), body.getNumber(), body.getRole());
        userRepository.save(newUser);

        return ResponseEntity.ok(new ApiResponse(newUser,Boolean.TRUE,"USER CREATED!"));

    }

    @PostMapping("/new/subject")
    public ResponseEntity<StatusResponse> addSubject(@RequestBody Map<String, Object> userMap) {

        String id = (String) userMap.get("id");
        Optional<User> idFound = userRepository.findById(id);
        if (!idFound.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"No Such USER"));
        if (idFound.get().getRole().compareTo("ADMIN") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Authorization Error"));

        Subject toAddSubject = new Subject((String) userMap.get("subjectName"),(int) userMap.get("credit"));

        if (toAddSubject.getSubjectName().compareTo("invalid") == 0 || toAddSubject.getCredit() == -1)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Invalid Subject Details"));
        if(subjectRepository.findById(toAddSubject.getSubjectCode()).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Subject Already Added"));

        subjectRepository.save(toAddSubject);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(Boolean.TRUE,"Subject Added"));

    }

    @PostMapping("/new/course")
    public ResponseEntity<StatusResponse> addCourse(@RequestBody Map<String, Object> userMap)
    {
        String id = (String) userMap.get("id");
        Optional<User> idFound = userRepository.findById(id);
        if (!idFound.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"No Such USER"));
        if(idFound.get().getRole().compareTo("ADMIN") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Unauthorized Access"));

        CourseProgram toAddCourse = new CourseProgram((String) userMap.get("CourseName"),(int) userMap.get("maxCredit"));
        if(toAddCourse.getCourseName().compareTo("invalid") == 0 || toAddCourse.getMaxCredit() == -1)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Invalid Course Details Entered"));
        if(courseRepository.findById(toAddCourse.getCourseCode()).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Course Already Exist"));
        courseRepository.save(toAddCourse);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(Boolean.TRUE,"Course Successfully Created"));

    }

    @PostMapping("/addSubjectToCourse")
    public ResponseEntity < StatusResponse> addSubjectToCourse(@RequestBody Map<String, Object> userMap) {

        String id = (String) userMap.get("id");
        Optional<User> idFound = userRepository.findById(id);
        if (!idFound.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"No Such USER"));

        if(idFound.get().getRole().compareTo("ADMIN")!=0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Unauthorized Access"));

        String subject = (String) userMap.get("subjectName");
        String subjectCode = SubjectValidator.getSubjectCode(subject);
        Optional<Subject> subjectSelected = subjectRepository.findById(subjectCode);
        if(!subjectSelected.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"No Such Subject"));

        String courseCode = SubjectValidator.getSubjectCode((String) userMap.get("courseName"));
        if (!courseRepository.findById(courseCode).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"No Such Course"));

        CourseProgram courseSelected = courseRepository.findById(courseCode).get();
        List<Subject> subjects = courseSelected.availableSubjects;

        for( int index = 0 ; index < subjects.size() ; index++)
            if(subjects.get(index).getSubjectCode().compareTo(subjectCode) == 0)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Subject Already Added to "+courseSelected.getCourseName()));

        courseSelected.getAvailableSubjects().add(subjectSelected.get());
        courseRepository.save(courseSelected);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.TRUE,"Subject Added to "+courseSelected.getCourseName()));

    }

    @PostMapping("/enrollCourse")
    public ResponseEntity<StatusResponse> enrollCourse(@RequestBody Map<String, Object> userMap) {

        String id = (String) userMap.get("id");
        Optional<User> idFound = userRepository.findById(id);
        if(!idFound.isPresent() || idFound.get().getRole().compareTo("STUDENT") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Invalid Id Or Authorization Error"));

        String courseName = (String) userMap.get("courseName");
        String courseCode = SubjectValidator.getSubjectCode(courseName);
        if(!courseRepository.findById(courseCode).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Course Not Found"));

        if(userCourseRepository.findById(id).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"User Already Enrolled"));

        userCourseRepository.save(new UserCourseProgram(id,courseName));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.TRUE,"Enrolled to "+((String) userMap.get("courseName"))));

    }

    @PostMapping("/student/add/subject")
    public ResponseEntity<StatusResponse> addSubjectByStudent(@RequestBody Map<String, Object> userMap) {

        String id = (String) userMap.get("id");
        Optional<User> idFound = userRepository.findById(id);
        if(!idFound.isPresent() || idFound.get().getRole().compareTo("STUDENT") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Invalid Id Or Authorization Error"));

        String subjectCode = SubjectValidator.getSubjectCode((String) userMap.get("subjectName"));
        if(!subjectRepository.findById(subjectCode).isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Subject Not Found"));
        int credit = subjectRepository.findById(subjectCode).get().getCredit();
        Optional<UserCourseProgram> courseSelected = userCourseRepository.findById(id);

        if(!courseSelected.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Not Enrolled in any course"));
        String courseCode = SubjectValidator.getSubjectCode(courseSelected.get().getCourseName());
        Optional<CourseProgram> course = courseRepository.findById(courseCode);
        CourseProgram currentCourse = course.get();
        Boolean isSubjectPresent = false;

        for(int subjects = 0; subjects < currentCourse.availableSubjects.size() && !isSubjectPresent; subjects++)
            if(subjectCode.compareTo(currentCourse.availableSubjects.get(subjects).getSubjectCode()) == 0)
                isSubjectPresent = true;
        if(!isSubjectPresent)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Subject Not In "+currentCourse.getCourseName()));
        UserCourseProgram userCourse = courseSelected.get();
        int netCredit = 0;
        for(int subjects = 0; subjects < userCourse.enrollSubjects.size(); subjects++) {
            if (subjectCode.compareTo(userCourse.enrollSubjects.get(subjects).getSubjectCode()) == 0)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Subject Already Added"));

            netCredit += userCourse.enrollSubjects.get(subjects).getCredit();

        }

        if(netCredit + credit <= currentCourse.getMaxCredit() ) {
            courseSelected.get().getEnrollSubjects().add(new Subject((String) userMap.get("subjectName"), credit));
            UserCourseProgram toAddCourse = courseSelected.get();
            userCourseRepository.deleteById(id);
            userCourseRepository.save(toAddCourse);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.TRUE,"Subject Successfully Added"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Insufficient Credit"));

    }

    @DeleteMapping("delete/student")
    public ResponseEntity<StatusResponse> deleteStudent(@RequestBody Map<String, Object> userMap)
    {
        String studentId=(String) userMap.get("studentId");
        Optional<User> idFound = userRepository.findById(studentId);
        if(!idFound.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Student Does Not Exist"));
        if(idFound.get().getRole().compareTo("STUDENT") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Admin Can't Be Deleted"));
        String adminId=(String) userMap.get("adminId");
        idFound = userRepository.findById(adminId);
        if(!idFound.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Admin Does Not Exist"));
        if(idFound.get().getRole().compareTo("ADMIN") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Unauthorized Access"));
        if(userCourseRepository.findById(studentId).isPresent())
            userCourseRepository.deleteById(studentId);
        userRepository.deleteById(studentId);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(Boolean.TRUE,"Student Deleted"));

    }

    @DeleteMapping("delete/course")
    public ResponseEntity<StatusResponse> deleteCourse(@RequestBody Map<String, Object> userMap) {

        String adminId = (String) userMap.get("adminId");
        Optional<User> idFound = userRepository.findById(adminId);
        if (!idFound.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Admin Does Not Exist"));
        if (idFound.get().getRole().compareTo("ADMIN") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Unauthorized Access"));
        String courseName = (String) userMap.get("courseName");
        String courseCode = SubjectValidator.getSubjectCode(courseName);
        if (!(courseRepository.findById(courseCode).isPresent()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Course Does Not Exist"));
        List<UserCourseProgram> userCourses = userCourseRepository.findAll();
        for(int index=0 ; index < userCourses.size() ; index++)
            if(userCourses.get(index).getCourseName().compareTo(courseName) == 0)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Course enrolled by some student"));
        courseRepository.deleteById(courseCode);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.TRUE,"Course Deleted"));

    }

    @DeleteMapping("delete/subject")
    public ResponseEntity<StatusResponse> deleteSubject(@RequestBody Map<String, Object> userMap) {

        String adminId = (String) userMap.get("adminId");
        Optional<User> idFound = userRepository.findById(adminId);
        if (!idFound.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Insufficient Credit"));
        if (idFound.get().getRole().compareTo("ADMIN") != 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Unauthorized Access"));
        String subjectName = (String) userMap.get("subjectName");
        String subjectCode = SubjectValidator.getSubjectCode(subjectName);
        if (!(subjectRepository.findById(subjectCode).isPresent()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Subject Not Found"));
        List<UserCourseProgram> userCourses = userCourseRepository.findAll();

        for(int index=0 ; index < userCourses.size() ; index++)
        {
            List<Subject> subs = userCourses.get(index).getEnrollSubjects();
            for(int rIndex=0 ; rIndex < subs.size() ; rIndex++) {
                if (subs.get(rIndex).getSubjectName().compareTo(subjectName) == 0)
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse(Boolean.FALSE,"Subject Enrolled By Some Student"));
            }
        }

        List<CourseProgram> listOfCourse = courseRepository.findAll();
        for(int index = 0; index < listOfCourse.size() ; index++ )
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
        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(Boolean.TRUE,"Subject Deleted"));
    }
}
