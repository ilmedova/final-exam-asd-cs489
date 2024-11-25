package edu.miu.cse.vsms.service.impl;

import edu.miu.cse.vsms.dto.request.EmployeeRequestDto;
import edu.miu.cse.vsms.dto.response.EmployeeResponseDto;
import edu.miu.cse.vsms.dto.response.VehicleServiceResponseDto;
import edu.miu.cse.vsms.exception.ResourceNotFoundException;
import edu.miu.cse.vsms.model.Employee;
import edu.miu.cse.vsms.repository.EmployeeRepository;
import edu.miu.cse.vsms.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeResponseDto addEmployee(EmployeeRequestDto request) {
        Employee savedEmployee = employeeRepository.save(new Employee(request.name(), request.email(), request.phone(), request.hireDate()));
        return Optional.of(mapToResponseDto(savedEmployee)).get();
    }

    @Override
    public List<EmployeeResponseDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeResponseDto> employeeResponseDtos = employees.stream().map(this::mapToResponseDto).toList();
        return Optional.of(employeeResponseDtos).get();
    }

    @Override
    public EmployeeResponseDto getEmployeeById(Long id) throws ResourceNotFoundException {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            return mapToResponseDto(employee.get());
        }
        else{
            throw new ResourceNotFoundException("Employee not found");
        }
    }

    @Override
    public EmployeeResponseDto partiallyUpdateEmployee(Long id, Map<String, Object> updates) {
        // Fetch the employee by ID or throw an exception if not found
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id " + id));

        // Apply each update based on the key
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    // Write your code here
                    employee.setName((String) value);
                    break;
                case "email":
                    // Write your code here
                    employee.setEmail((String) value);
                    break;
                case "phone":
                    // Write your code here
                    employee.setPhone((String) value);
                    break;
                case "hireDate":
                    // Write your code here
                    employee.setHireDate((LocalDate) value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field: " + key);
            }
        });
        // Write your code here
        employeeRepository.save(employee);
        return Optional.of(mapToResponseDto(employee)).get();
    }

    private EmployeeResponseDto mapToResponseDto(Employee employee) {
        List<VehicleServiceResponseDto> serviceDtos = employee.getVServices().stream()
                .map(service -> new VehicleServiceResponseDto(
                        service.getId(),
                        service.getServiceName(),
                        service.getCost(),
                        service.getVehicleType()
                )).toList();

        return new EmployeeResponseDto(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getHireDate(),
                serviceDtos
        );
    }
}
