package com.ashish.elasticsearch_project.sec02;

import com.ashish.elasticsearch_project.AbstractTest;
import com.ashish.elasticsearch_project.sec02.entity.Employee;
import com.ashish.elasticsearch_project.sec02.repository.EmployeeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import java.util.List;
import java.util.stream.IntStream;

public class CrudOperationTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(CrudOperationTest.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void crud(){

        Employee employee = createEmployee(1,"king",33);

        this.employeeRepository.save(employee);
        this.printAll();

        //find by id
        employee =this.employeeRepository.findById(1).orElseThrow();
        Assertions.assertEquals(33,employee.getAge());
        Assertions.assertEquals("king",employee.getName());

        //update and save
        employee.setAge(30);
        this.employeeRepository.save(employee);
        employee =this.employeeRepository.findById(1).orElseThrow();
        this.printAll();
        Assertions.assertEquals(30,employee.getAge());

        //delete
        this.employeeRepository.deleteById(1);
        this.printAll();
        Assertions.assertTrue(this.employeeRepository.findById(1).isEmpty());
    }

    @Test
    public void bulkCrud(){
        List<Employee> list = IntStream.rangeClosed(1,10)
                .mapToObj(i -> createEmployee(i, "name-" +i,30+i))
                .toList();

        this.employeeRepository.saveAll(list);
        printAll();

        //Check the count
        Assertions.assertEquals(this.employeeRepository.count(),10);

        //find by ids
        List<Integer> ids = List.of(2,5,7);
        Iterable iterable= this.employeeRepository.findAllById(ids);
        list = Streamable.of(iterable).toList();
        printAll();
        Assertions.assertEquals(3,list.size());

        //update and save
        list.forEach(e -> e.setAge(e.getAge() +10)); //mutable object
        this.employeeRepository.saveAll(list);

        //verify the updates
        printAll();
        this.employeeRepository.findAllById(ids)
                .forEach(e -> Assertions.assertEquals(e.getId() + 40, e.getAge()));

        //delete
        this.employeeRepository.deleteAllById(ids);
        printAll();
        Assertions.assertEquals(this.employeeRepository.count(),7);
    }


    private Employee createEmployee(int id, String name, int age) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setAge(age);
        return employee;
    }

    private void printAll(){
        this.employeeRepository.findAll()
                .forEach(e -> log.info("employee: {}",e));
    }

}
