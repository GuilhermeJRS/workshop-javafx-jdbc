package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection conn;

	public SellerDaoJDBC(Connection conn) {
		super();
		this.conn = conn;
	}

	@Override
	public Seller findById(Integer id) {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT seller.*, department.Name AS DeptName " 
									+ "FROM seller "
									+ "INNER JOIN department " 
									+ "ON department.Id = seller.DepartmentId " 
									+ "WHERE seller.Id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();

			if (rs.next()) {

				Department dep = instantiateDepartment(rs);
				Seller seller = instantiateSeller(rs, dep);

				return seller;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findAll() {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT seller.*, department.Name AS DeptName " 
									+ "FROM seller "
									+ "INNER JOIN department " 
									+ "ON department.Id = seller.DepartmentId " 
									+ "ORDER BY Name");

			rs = st.executeQuery();

			Department dep = null;
			
			List<Seller> listSeller = new ArrayList<>();
			Map<Integer, Department> mapDep = new HashMap<>();

			while (rs.next()) { 
				dep = mapDep.get(rs.getInt("DepartmentId"));
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					mapDep.put(rs.getInt("DepartmentId"), dep);
				}
				listSeller.add(instantiateSeller(rs, dep));
			}

			return listSeller;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT seller.*, department.Name AS DeptName " 
									+ "FROM seller "
									+ "INNER JOIN department " 
									+ "ON department.Id = seller.DepartmentId " 
									+ "WHERE department.Id = ? "
									+ "ORDER BY Name");

			st.setInt(1, department.getId());
			rs = st.executeQuery();

			Department dep = null;
			
			List<Seller> listSeller = new ArrayList<>();
			Map<Integer, Department> mapDep = new HashMap<>();

			while (rs.next()) { 
				dep = mapDep.get(rs.getInt("DepartmentId"));
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					mapDep.put(rs.getInt("DepartmentId"), dep);
				}
				listSeller.add(instantiateSeller(rs, dep));
			}

			return listSeller;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public void insert(Seller seller) {

		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("INSERT INTO seller " 
									+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
									+ "VALUES (?, ?, ?, ?, ?)", 
									Statement.RETURN_GENERATED_KEYS); 
			st.setString(1, seller.getName());
			st.setString(2, seller.getEmail());
			st.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			st.setDouble(4, seller.getBaseSalary());
			st.setInt(5, seller.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0 ) { 
				rs = st.getGeneratedKeys();	
				if(rs.next()) {
					seller.setId(rs.getInt(1));								
				}
			}
			else {
				throw new DbException("Unexpected Erro! No rows affected");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public void update(Seller seller) {

		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("UPDATE seller " 
									+ "SET name = ?, "
					                + "email = ?, "
									+ "birthDate = ?, "
					                + "baseSalary = ?, "
									+ "departmentId = ? "
									+ "WHERE id = ?;"); 
									
			st.setString(1, seller.getName());
			st.setString(2, seller.getEmail());
			st.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			st.setDouble(4, seller.getBaseSalary());
			st.setInt(5, seller.getDepartment().getId());
			st.setInt(6, seller.getId());

			st.executeUpdate();
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {

		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("DELETE FROM seller " 
									+ "WHERE id = ?;"); 						
			st.setInt(1, id);
			
			st.executeUpdate();
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		return new Seller(rs.getInt("Id"), rs.getString("Name"), rs.getString("Email"), rs.getDate("BirthDate"),
				rs.getDouble("BaseSalary"), dep);
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		return new Department(rs.getInt("DepartmentId"), rs.getString("deptName"));
	}
}
