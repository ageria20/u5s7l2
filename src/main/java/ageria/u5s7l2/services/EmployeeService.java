package ageria.u5s7l2.services;


import ageria.u5s7l2.dto.EmployeeDTO;
import ageria.u5s7l2.entities.Employee;
import ageria.u5s7l2.exception.BadRequestException;
import ageria.u5s7l2.exception.NotFoundException;
import ageria.u5s7l2.repositories.EmployeeRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;


    @Autowired
    Cloudinary cloudinaryUploader;

    @Autowired
    PasswordEncoder bcrypt;

    // 1. GET
    public Page<Employee> getAllEmployee(int pages, int size, String sortBy) {
        Pageable pageable = PageRequest.of(pages, size, Sort.by(sortBy));
        return this.employeeRepository.findAll(pageable);
    }

    // 1.1 GET by id
    public Employee findById(Long id) {
        return this.employeeRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    // 2. POST
    public Employee saveEmployee(EmployeeDTO body) {
        if (employeeRepository.existsByEmail(body.email())) {
            throw new BadRequestException("Employee with this email already exists");
        }

        Employee newEmployee = new Employee(
                body.username(),
                body.name(),
                body.surname(),
                body.email(),
                bcrypt.encode(body.password()),
                "https://ui-avatars.com/api/?name=" + body.name() + "+" + body.surname());
        return this.employeeRepository.save(newEmployee);

    }

    public Employee findByIdAndUpdate(Long id, Employee body) {
        // controllo se l'email e' gia in uso
        this.employeeRepository.findByEmail(body.getEmail()).ifPresent(
                user -> {
                    throw new BadRequestException("The employee with this email already exists");
                }
        );
        Employee employeeFromDb = this.findById(id);
        employeeFromDb.setUsername(body.getUsername());
        employeeFromDb.setName(body.getName());
        employeeFromDb.setSurname(body.getSurname());
        employeeFromDb.setEmail(body.getEmail());
        employeeFromDb.setAvatar("https://ui-avatars.com/api/?name=" + body.getName() + body.getSurname());
        return employeeFromDb;
    }

    public void findByIdAndDelete(Long id) {
        Employee employeeToDelete = this.findById(id);
        this.employeeRepository.delete(employeeToDelete);
    }

    public void uploadImage(MultipartFile avatar, Long id) {
        try {
            Employee employeeFromDb = this.findById(id);
            String url = (String) cloudinaryUploader.uploader().upload(avatar.getBytes(), ObjectUtils.emptyMap()).get("url");
            employeeFromDb.setAvatar(url);
            this.employeeRepository.save(employeeFromDb);
        } catch (IOException ex) {
            throw new MaxUploadSizeExceededException(avatar.getSize());
        }
    }

    public Employee findFromEmail(String email) {
        return this.employeeRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("EMPLOYEE NOT FOUND"));
    }


}
