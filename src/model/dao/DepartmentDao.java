package model.dao;

import java.util.List;

import model.entities.Department;

public interface DepartmentDao { 

	Department findById(Integer id);
	List <Department> findAll(); 
	void insert(Department department);
	void update(Department department);
	void deleteById(Integer id);

}
