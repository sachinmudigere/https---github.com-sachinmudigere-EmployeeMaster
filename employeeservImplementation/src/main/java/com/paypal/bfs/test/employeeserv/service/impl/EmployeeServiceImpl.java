package com.paypal.bfs.test.employeeserv.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.bfs.test.employeeserv.api.model.Address;
import com.paypal.bfs.test.employeeserv.api.model.Employee;
import com.paypal.bfs.test.employeeserv.dao.AddressRepository;
import com.paypal.bfs.test.employeeserv.dao.EmployeeRepository;
import com.paypal.bfs.test.employeeserv.domain.AddressEntity;
import com.paypal.bfs.test.employeeserv.domain.EmployeeEntity;
import com.paypal.bfs.test.employeeserv.service.EmployeeService;
import com.paypal.bfs.test.employeeserv.utils.EntityUtil;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	EmployeeRepository empRepository;
	
	@Autowired
	AddressRepository addrRepository;

	SimpleDateFormat dtFormat = new SimpleDateFormat("dd/MM/yyyy");

	public Employee getEmployee(String employeeId) {
		
		Employee employee = null;
		
		try {
			EmployeeEntity empEntity = empRepository.getOne(Integer.parseInt(employeeId));
			employee = new EntityUtil<EmployeeEntity, Employee>().copyProperties(empEntity,  new Employee());
			employee.setDateOfBirth(dtFormat.format(empEntity.getDateOfBirth()));
			List<AddressEntity> addressEntities = addrRepository.findByEmployeeId(empEntity.getId());
			List<Address> addresses = new ArrayList<Address>();
			addressEntities.forEach(a -> {
				Address address = new EntityUtil<AddressEntity, Address>().copyProperties(a, new Address());
				addresses.add(address);
			});
			employee.setAddress(addresses);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return employee;
	}
	
	public Employee createEmployee(@Valid Employee employee) {
		try {
			EmployeeEntity empEntity = new EntityUtil<Employee, EmployeeEntity>().copyProperties(employee, new EmployeeEntity());
			empEntity.setDateOfBirth(dtFormat.parse(employee.getDateOfBirth()));
			final EmployeeEntity emp  = empRepository.save(empEntity);
			employee.setId(emp.getId());
			employee.getAddress().forEach(a -> {
				AddressEntity addrEntity = new EntityUtil<Address, AddressEntity>().copyProperties(a, new AddressEntity());
				addrEntity.setEmployee(emp);
				addrRepository.save(addrEntity);
			});
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return employee;
	}
}
