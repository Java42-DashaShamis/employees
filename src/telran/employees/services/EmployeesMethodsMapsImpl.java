package telran.employees.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import telran.employees.dto.Employee;

public class EmployeesMethodsMapsImpl implements EmployeesMethods {
	private HashMap<Long, Employee> mapEmployees = new HashMap<>(); //key - id, value - employee
	private TreeMap<Integer, List<Employee>> employeesAge = new TreeMap<>();//key - age, value - list of empl
	private TreeMap<Integer, List<Employee>> employeesSalary = new TreeMap<>();//key - salary, value - list of empl
	private HashMap<String, List<Employee>> employeesDepartment = new HashMap<>();//key - department, value - list of empl
	
	@Override
	public ReturnCode addEmployee(Employee empl) {
		if(mapEmployees.containsKey(empl.id)) {
			return ReturnCode.EMPLOYEE_ALREADY_EXISTS;
		}
		Employee emplService = new Employee(empl.id, empl.name, empl.birthDate, empl.salary, empl.department); //create object
		mapEmployees.put(emplService.id, emplService);
		employeesAge.computeIfAbsent(getAge(emplService), k -> new LinkedList<Employee>()).add(emplService);
		employeesSalary.computeIfAbsent(emplService.salary, k -> new LinkedList<Employee>()).add(emplService);
		employeesDepartment.computeIfAbsent(emplService.department, k -> new LinkedList<Employee>()).add(emplService);
		return ReturnCode.OK;
	}

	private Integer getAge(Employee emplService) {
		return (int)ChronoUnit.YEARS.between(emplService.birthDate, LocalDate.now());
	}

	@Override
	public ReturnCode removeEmployee(long id) {
		if(!mapEmployees.containsKey(id)) {
			return ReturnCode.EMPLOYEE_NOT_FOUND;
		}
		Employee empl = mapEmployees.get(id);
		mapEmployees.remove(id);
		removeFromEmployeesAge(empl);
		removeFromEmployeesSalary(empl);
		removeFromEmployeesDepartment(empl);
		
		return ReturnCode.OK;
	}

	private void removeFromEmployeesAge(Employee empl) {
		List<Employee> list = employeesAge.get(getAge(empl));
		list.remove(empl);
		if(list.isEmpty()) {
			employeesAge.remove(getAge(empl));
		}
		
	}

	private void removeFromEmployeesSalary(Employee empl) {
		List<Employee> list = employeesSalary.get(empl.salary);
		list.remove(empl);
		if(list.isEmpty()) {
			employeesSalary.remove(empl.salary);
		}
	}

	private void removeFromEmployeesDepartment(Employee empl) {
		List<Employee> list = employeesDepartment.get(empl.department);
		list.remove(empl);
		if(list.isEmpty()) {
			employeesDepartment.remove(empl.department);
		}
	}

	@Override
	public Iterable<Employee> getAllEmployees() {
		List<Employee> employees = new LinkedList<Employee>(mapEmployees.values());
		return employees;
	}

	@Override
	public Employee getEmployee(long id) {
		Employee empl = mapEmployees.get(id);
		return empl==null ? null : new Employee(empl.id, empl.name, empl.birthDate, empl.salary, empl.department);
		
	}

	@Override
	public Iterable<Employee> getEmployeesByAge(int ageFrom, int ageTo) {
		if(ageFrom>=ageTo) {
			throw new IllegalArgumentException("Wrong Range");
		}
		List<Employee> employees = joinLists(employeesAge.subMap(ageFrom, true, ageTo, true));
		return employees.isEmpty() ? employees : getEmployeesFromList(employees);
	}

	private List<Employee> joinLists(NavigableMap<Integer, List<Employee>> subMap) {
		return subMap.values().stream().flatMap(list -> list.stream()).collect(Collectors.toList());
	}

	@Override
	public Iterable<Employee> getEmployeesBySalary(int salaryFrom, int salaryTo) {
		if(salaryFrom>=salaryTo) {
			throw new IllegalArgumentException("Wrong Range");
		}
		List<Employee> employees = joinLists(employeesSalary.subMap(salaryFrom, true, salaryTo, true));
		return employees.isEmpty() ? employees : getEmployeesFromList(employees);
	}

	@Override
	public Iterable<Employee> getEmployeesByDepartment(String department) {
		List<Employee>employees = employeesDepartment.getOrDefault(department, Collections.emptyList());
		return employees.isEmpty() ? employees : getEmployeesFromList(employees);
	}

	private Iterable<Employee> getEmployeesFromList(List<Employee> employees) {
		return employees.stream().map(e -> new Employee(e.id, e.name, e.birthDate, e.salary, e.department)).collect(Collectors.toList());
	}

	@Override
	public Iterable<Employee> getEmployeesByDepartmentAndSalary(String department, int salaryFrom, int salaryTo) {
		if(salaryFrom>=salaryTo) {
			throw new IllegalArgumentException("Wrong Range");
		}
		List<Employee> employees = new LinkedList<Employee>();
		getEmployeesByDepartment(department).forEach(e -> {if(e.salary>=salaryFrom && e.salary<=salaryTo) employees.add(e);});
		return employees.isEmpty() ? employees : getEmployeesFromList(employees);
	}

	@Override
	public ReturnCode updateSalary(long id, int newSalary) {
		if(!mapEmployees.containsKey(id)) {
			return ReturnCode.EMPLOYEE_NOT_FOUND;
		}
		Employee empl = mapEmployees.get(id);
		if(empl.salary == newSalary) {
			return ReturnCode.SALARY_NOT_UPDATE;
		}
		
		removeEmployee(empl.id);
		empl.salary = newSalary;
		addEmployee(empl);
		return  ReturnCode.OK;
	}
	
	@Override
	public ReturnCode updateDepartment(long id, String newDepartment) {
		if(!mapEmployees.containsKey(id)) {
			return ReturnCode.EMPLOYEE_NOT_FOUND;
		}
		Employee empl = mapEmployees.get(id);
		if(empl.department == newDepartment) {
			return ReturnCode.DEPARTMENT_NOT_UPDATED;
		}
		removeEmployee(empl.id);
		empl.department = newDepartment;
		addEmployee(empl);
		return  ReturnCode.OK;
	}

}
