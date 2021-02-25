package model.dao;

import java.util.List;

import model.entities.Department;
import model.entities.Seller;

public interface SellerDao {
	
	Seller findById(Integer id);
	List <Seller> findAll();
	List <Seller> findByDepartment(Department department);
	void insert(Seller obj);
	void update(Seller obj);
	void deleteById(Integer id);

}
