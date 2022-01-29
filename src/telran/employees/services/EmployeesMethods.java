package telran.employees.services;
import telran.employees.dto.*;

public interface EmployeesMethods {
	ReturnCode addEmployee(Employee emp1);
	ReturnCode removeEmployee(long id);
	Iterable<Employee> getAllEmployees();
	Employee getEmployee(long id);
	Iterable<Employee> getEmployeesByAge(int ageFrom, int ageTo);
	Iterable<Employee> getEmployeesBySalary(int salaryFrom, int salaryTo);
	Iterable<Employee> getEmployeesByDepartment(String department);
	Iterable<Employee> getEmployeesByDepartmentAndSalary(String department, int salaryFrom, int salaryTo);
	ReturnCode updateSalary(long id, int newSalary);
	ReturnCode updateDepartment(long id, String newDepartment);
}
