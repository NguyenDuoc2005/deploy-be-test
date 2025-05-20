package udpm.hn.server.core.admin.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.admin.staff.model.request.ADCreateStaffFDM;
import udpm.hn.server.core.admin.staff.model.request.ADUpdateStaffFDMRequest;
import udpm.hn.server.core.admin.student.model.request.ADCreateOrUpdateStudentRequest;
import udpm.hn.server.core.admin.student.model.request.ADCreateStudentFDMRequest;
import udpm.hn.server.core.admin.student.model.request.ADUpdateStudentFDMRequest;
import udpm.hn.server.core.admin.student.model.request.StudentRequest;
import udpm.hn.server.core.admin.student.service.StudentService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

import java.io.FileNotFoundException;

@Controller
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_ADMIN_STUDENT)
public class StudentRestController {

    private final StudentService studentService;

    @GetMapping("/get-all-student")
    public ResponseEntity<?> getAllStudent(StudentRequest request){
        return Helper.createResponseEntity(studentService.getAllStudnet(request));
    }

    @PostMapping("/add-student")
    public ResponseEntity<?> addStudent(@RequestBody ADCreateOrUpdateStudentRequest request){
        return Helper.createResponseEntity(studentService.addStudent(request));
    }

    @GetMapping("/fill-all-student")
    public ResponseEntity<?> fillAllStudent(){
        return Helper.createResponseEntity(studentService.findAllStudent());
    }

    @PutMapping("/update-student/{id}")
    public ResponseEntity<?> updateStudent(@RequestBody ADCreateOrUpdateStudentRequest request,@PathVariable String id){
        return Helper.createResponseEntity(studentService.updateStudent(request, id));
    }

    @GetMapping("/detail-student/{id}")
    public ResponseEntity<?> detailStudent(@PathVariable String id){
        return Helper.createResponseEntity(studentService.detailStudent(id));
    }

    @GetMapping("/detailStudent/department-major/{id}")
    public ResponseEntity<?> staffByDepartmentMajor(@PathVariable String id){
        return Helper.createResponseEntity(studentService.studentByDepartmentFacility(id));
    }

    @DeleteMapping("/student-facility/{id}")
    public ResponseEntity<?> deleteStaffFacility(@PathVariable String id){
        return Helper.createResponseEntity(studentService.deleteStudentByIdStudent(id));
    }

    @PostMapping("/major-department-facility")
    public ResponseEntity<?> createStaffFDM(ADCreateStudentFDMRequest request){
        return Helper.createResponseEntity(studentService.createStudentByFDM(request));
    }

    @PutMapping("/major-department-facility")
    public ResponseEntity<?> updateMajorDepartmentFacility(ADUpdateStudentFDMRequest request){
        return Helper.createResponseEntity(studentService.updateStudentByFDM(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable String id){
        return Helper.createResponseEntity(studentService.deleteStudent(id));
    }


    @GetMapping("/read/fileLog")
    public ResponseEntity<?> readCSV() {
        return Helper.createResponseEntity(studentService.readCSV());
    }

    @GetMapping("/history")
    public ResponseEntity<?> getChangeHistory(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size
    ) throws FileNotFoundException {
        return Helper.createResponseEntity(studentService.getLogsImportStudent(page, size));
    }

}
