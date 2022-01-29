package telran.employees.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import telran.employees.dto.Employee;

class EmployeesMethodsTest {
	private static final long ID1 = 123;
	private static final String NAME1 = "Alex";
	private static final LocalDate BIRTHDATE1 = LocalDate.of(1988, 1, 1);//34
	private static final int SALARY1 = 10000;
	private static final String DEPARTMENT1 = "department1";
	private static final long ID2 = 124;
	private static final long ID3 = 125;
	private static final long ID4 = 126;
	private static final long ID5 = 127;
	private static final long ID6 = 128;
	private static final long ID7 = 234;
	private static final LocalDate BIRTHDATE2 = LocalDate.of(1995, 1, 1);//27
	private static final LocalDate BIRTHDATE3 = LocalDate.of(1997, 1, 1);//25
	private static final LocalDate BIRTHDATE4 = LocalDate.of(1970, 1, 1);//52
	private static final LocalDate BIRTHDATE5 = LocalDate.of(1971, 1, 1);//51
	private static final LocalDate BIRTHDATE6 = LocalDate.of(1980, 1, 1);//42
	private static final String DEPARTMENT2 = "department2";
	private static final int SALARY2 = 5000;
	private static final int SALARY3 = 15000;
	EmployeesMethods employees;
	Employee empl1 = new Employee(ID1, NAME1, BIRTHDATE1, SALARY1, DEPARTMENT1);
	Employee empl2 = new Employee(ID2, NAME1, BIRTHDATE2, SALARY1, DEPARTMENT1);
	Employee empl3 = new Employee(ID3, NAME1, BIRTHDATE3, SALARY2, DEPARTMENT1);
	Employee empl4 = new Employee(ID4, NAME1, BIRTHDATE4, SALARY2, DEPARTMENT2);
	Employee empl5 = new Employee(ID5, NAME1, BIRTHDATE5, SALARY3, DEPARTMENT2);
	Employee empl6 = new Employee(ID6, NAME1, BIRTHDATE6, SALARY3, DEPARTMENT2);
	
	List<Employee> employeesList = Arrays.asList(empl1, empl2, empl3, empl4, empl5, empl6);
	@BeforeEach
	void setUp() throws Exception {
		employees = new EmployeesMethodsMapsImpl();
		employeesList.forEach(employees::addEmployee);
	}

	@Test
	void testAddEmployee() {
		assertEquals(ReturnCode.EMPLOYEE_ALREADY_EXISTS, employees.addEmployee(empl1));
		Employee emplAdded = new Employee(ID1+100, NAME1, BIRTHDATE1, SALARY1, DEPARTMENT1);
		assertEquals(ReturnCode.OK, employees.addEmployee(emplAdded));
		List<Employee>expected = Arrays.asList(empl1, empl2, empl3, empl4, empl5, empl6, emplAdded);
		employees.getAllEmployees().forEach(e -> assertTrue(expected.contains(e)));
		List<Employee> listEmployees = (List)employees.getAllEmployees();  
		assertEquals(expected.size(), listEmployees.size());
	}

	@Test
	void testRemoveEmployee() {
		assertEquals(ReturnCode.EMPLOYEE_NOT_FOUND, employees.removeEmployee(ID7));
		assertEquals(ReturnCode.OK, employees.removeEmployee(ID2));
		assertFalse(ReturnCode.OK.equals(employees.removeEmployee(ID2)) );
		employees.removeEmployee(empl1.id);
		List<Employee>expected1 = Arrays.asList(empl3, empl4, empl5, empl6);
		List<Employee> listEmployees1 = (List)employees.getAllEmployees();  
		assertEquals(expected1.size(), listEmployees1.size());
		employees.removeEmployee(empl3.id);
		employees.removeEmployee(empl4.id);
		employees.removeEmployee(empl5.id);
		employees.removeEmployee(empl6.id);
		List<Employee>expected2 = Collections.EMPTY_LIST;
		assertEquals(expected2, employees.getAllEmployees());
	}

	@Test
	void testGetAllEmployees() {
		List<Employee>expected = Arrays.asList(empl1, empl2, empl3, empl4, empl5, empl6);
		employees.getAllEmployees().forEach(e -> assertTrue(expected.contains(e)));
		List<Employee> listEmployees = (List)employees.getAllEmployees();  
		assertEquals(expected.size(), listEmployees.size());
	}

	@Test
	void testGetEmployee() {
		assertEquals(empl2, employees.getEmployee(ID2));
		assertEquals(empl2.name, employees.getEmployee(ID2).name);
		assertEquals(empl2.salary, employees.getEmployee(ID2).salary);
		assertEquals(empl2.birthDate, employees.getEmployee(ID2).birthDate);
		assertEquals(empl2.department, employees.getEmployee(ID2).department);
		assertFalse(empl3.birthDate.equals(employees.getEmployee(ID2).birthDate));
	}

	@Test
	void testGetEmployeesByAge() {
		int maxAge1 = getAge(BIRTHDATE6);
		int minAge1 = getAge(BIRTHDATE2);
		List<Employee> listEmployees1 = (List)employees.getEmployeesByAge(minAge1,maxAge1);
		List<Employee>expected1 = Arrays.asList(empl1, empl2, empl6);
		assertEquals(expected1.size(), listEmployees1.size());
		assertTrue(listEmployees1.containsAll(expected1));
		int maxAge2 = getAge(BIRTHDATE6)+2;
		int minAge2 = getAge(BIRTHDATE2)-1;
		List<Employee> listEmployees2 = (List)employees.getEmployeesByAge(minAge2,maxAge2);
		List<Employee>expected2 = Arrays.asList(empl1, empl2, empl6);
		assertEquals(expected2.size(), listEmployees2.size());
		assertTrue(listEmployees2.containsAll(expected2));
		boolean flag = false;
		try {
			employees.getEmployeesByAge(maxAge2,minAge2);
		} catch (IllegalArgumentException e) {
			flag = true;
		}
		assertTrue(flag);
		List<Employee> listEmployees3 = (List)employees.getEmployeesByAge(getAge(BIRTHDATE4)+10,getAge(BIRTHDATE4)+15);
		List<Employee>expected3 = Collections.EMPTY_LIST;
		assertEquals(expected3, listEmployees3);
	}
	private Integer getAge(LocalDate date) {
		return (int)ChronoUnit.YEARS.between(date, LocalDate.now());
	}

	@Test
	void testGetEmployeesBySalary() {
		List<Employee> listEmployees = (List)employees.getEmployeesBySalary(SALARY2,SALARY1);
		List<Employee>expected = Arrays.asList(empl1, empl2, empl3, empl4);
		assertEquals(expected.size(), listEmployees.size());
		assertTrue(listEmployees.containsAll(expected));
		boolean flag = false;
		try {
			employees.getEmployeesBySalary(SALARY1,SALARY2);
		} catch (IllegalArgumentException e) {
			flag = true;
		}
		assertTrue(flag);
		List<Employee> listEmployees1 = (List)employees.getEmployeesBySalary(SALARY2-3000,SALARY2-1000);
		List<Employee>expected1 = Collections.EMPTY_LIST;
		assertEquals(expected1, listEmployees1);
	}

	@Test
	void testGetEmployeesByDepartment() {
		List<Employee>expected = Arrays.asList(empl1, empl2, empl3);
		assertIterableEquals(expected, employees.getEmployeesByDepartment(DEPARTMENT1));
	}

	@Test
	void testGetEmployeesByDepartmentAndSalary() {
		List<Employee> listEmployees = (List)employees.getEmployeesByDepartmentAndSalary(DEPARTMENT1,SALARY2,SALARY1);
		List<Employee>expected = Arrays.asList(empl1, empl2, empl3);
		assertEquals(expected.size(), listEmployees.size());
		assertTrue(listEmployees.containsAll(expected));
		boolean flag = false;
		try {
			employees.getEmployeesByDepartmentAndSalary(DEPARTMENT1,SALARY1,SALARY2);
		} catch (IllegalArgumentException e) {
			flag = true;
		}
		assertTrue(flag);
		List<Employee> listEmployees1 = (List)employees.getEmployeesByDepartmentAndSalary("dfghj",SALARY2,SALARY1);
		List<Employee>expected1 = Collections.EMPTY_LIST;
		assertEquals(expected1, listEmployees1);
		List<Employee> listEmployees2 = (List)employees.getEmployeesByDepartmentAndSalary(DEPARTMENT1,SALARY2-3000,SALARY2-1000);
		assertEquals(expected1, listEmployees2);
	}

	@Test
	void testUpdateSalary() {
		Employee expected = new Employee(ID1, NAME1, BIRTHDATE1, SALARY1+2000, DEPARTMENT1);
		assertTrue(expected.salary != employees.getEmployee(ID1).salary);
		assertEquals(ReturnCode.EMPLOYEE_NOT_FOUND, employees.updateSalary(ID7, 12000));
		assertEquals(ReturnCode.OK, employees.updateSalary(ID1, 12000));
		assertEquals(expected.salary, employees.getEmployee(ID1).salary);
		assertFalse(expected.salary+1000 == employees.getEmployee(ID1).salary);
		employees.updateSalary(ID2, 12000);
		List<Employee> listEmployeesExp = new LinkedList<Employee>();
		assertIterableEquals((List)employees.getEmployeesBySalary(SALARY1,SALARY1+1000), listEmployeesExp);
	}
	
	@Test
	void testUpdateDepartment() {
		Employee expected = new Employee(ID1, NAME1, BIRTHDATE1, SALARY1, DEPARTMENT2);
		assertFalse(expected.department.equals(employees.getEmployee(ID1).department));
		assertEquals(ReturnCode.EMPLOYEE_NOT_FOUND, employees.updateDepartment(ID7, DEPARTMENT2));
		assertEquals(ReturnCode.OK, employees.updateDepartment(ID1, DEPARTMENT2));
		assertTrue(expected.department.equals(employees.getEmployee(ID1).department));
		assertFalse(DEPARTMENT1.equals(employees.getEmployee(ID1).department));
		employees.updateDepartment(ID2, DEPARTMENT2);
		employees.updateDepartment(ID3, DEPARTMENT2);
		List<Employee> listEmployeesExp = new LinkedList<Employee>();
		assertIterableEquals((List)employees.getEmployeesByDepartment(DEPARTMENT1), listEmployeesExp);
		
	}

}
