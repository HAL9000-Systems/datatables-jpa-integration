/**
 * 
 */
package com.hal9000.datatables_integration.builder;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.hal9000.datatables_integration.dto.request.Column;
import com.hal9000.datatables_integration.dto.request.DataTableRequest;
import com.hal9000.datatables_integration.dto.request.TableOrder;

/**
 * @author gabriel
 *
 */
public class DataTableQueryBuilder<T> {
	
	private DataTableRequest request;
	private CriteriaQuery<T> criteriaQuery;
	private Root<T> root;
	private CriteriaBuilder criteriaBuilder;
	private TypedQuery<T> typedQuery;
	private Class<T> persistentClass;
	private EntityManager entityManager;
	
	private DataTableQueryBuilder() {
		
	}
	
	
	public DataTableQueryBuilder(CriteriaBuilder criteriaBuilder, DataTableRequest request, Class<T> persistentClass) {
		this.request = request;
		this.criteriaBuilder = criteriaBuilder;
		this.persistentClass = persistentClass;
	}
	
	@SuppressWarnings("unchecked")
	public DataTableQueryBuilder(EntityManager entityManager, DataTableRequest request) {
		this.request = request;
		this.entityManager = entityManager;
		//this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.persistentClass = (Class<T>) ((ParameterizedType) (new DataTableQueryBuilder<T>(){}).getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	//TODO tiene sentido que siga siendo public?
	public DataTableQueryBuilder<T> addFilters() {
		
		if (request.getColumns() != null && !request.getColumns().isEmpty()) {
			
			List<Column> searchableColumns = request.getColumns().stream().filter(tableColumn -> tableColumn.getSearchable()).collect(Collectors.toList());
			
			if (this.request.getSearch().getValue() != null && !this.request.getSearch().getValue().equals("")) {
			
				if (searchableColumns != null && !searchableColumns.isEmpty()) {
					
					//Logica para armar el where comun para todas las columnas
					ArrayList<Predicate> restrictions = new ArrayList<Predicate>();
					searchableColumns.forEach(tableColumn -> restrictions.add(this.createColumnFilterCondition(tableColumn, this.request.getSearch().getValue())));
					this.getCriteriaQuery().where(this.criteriaBuilder.or((Predicate[]) restrictions.toArray()));
				}
				else {
					
					//Logica para armar el where individual por columna
					ArrayList<Predicate> restrictions = new ArrayList<Predicate>();
					searchableColumns.forEach(tableColumn -> restrictions.add(this.createColumnFilterCondition(tableColumn, tableColumn.getSearch().getValue())));
					this.getCriteriaQuery().where(this.criteriaBuilder.or((Predicate[]) restrictions.toArray()));
				}
			}
		}
		
		return this;
	}
	
	private Predicate createColumnFilterCondition(Column column, String value) {
		
		if (value != null && !value.isEmpty()) {
			
			String columnName = ("".equals(column.getName())) ? column.getData() : column.getName();
			
			if (columnName.contains(".")) {
				
				String propertyName = columnName.split("\\.")[0];
				String propertyAttrib = columnName.split("\\.")[1];
				String propertyNameAlias = propertyName + "_alias";
				
				this.getRoot().join(propertyName).alias(propertyNameAlias);
				
				columnName = propertyNameAlias + "." + propertyAttrib;
			}
			
			return this.getCriteriaBuilder().like(this.getCriteriaBuilder().lower(this.getCriteriaBuilder().toString(this.getRoot().get(columnName))), "%" + value.toLowerCase() + "%");
		}
		
		return null;
	}
	//TODO tiene sentido que siga siendo public?
	public DataTableQueryBuilder<T> addOrders() {
		
		if(this.request.getOrder() != null && !this.request.getOrder().isEmpty()) {
			//Logica para armar el order by
			if (this.request.getColumns() != null && !this.request.getColumns().isEmpty() && this.request.getColumns().stream().anyMatch(tableColumn -> tableColumn.getOrderable())) {
				
				ArrayList<Order> orders = new ArrayList<Order>();
				
				this.request.getOrder().forEach(tableOrder -> orders.add(this.createColumnFilterCondition(this.request.getColumns().get(tableOrder.getColumn()), tableOrder)));
				this.getCriteriaQuery().orderBy(orders);
			}
		}
		
		return this;
	}
	
	private Order createColumnFilterCondition(Column column, TableOrder order) {
		
		String columnName = ("".equals(column.getName())) ? column.getData() : column.getName();
		
		if (columnName.contains(".")) {
			
			String propertyName = columnName.split("\\.")[0];
			String propertyAttrib = columnName.split("\\.")[1];
			String propertyNameAlias = propertyName + "_alias";
			
			this.getRoot().join(propertyName).alias(propertyNameAlias);
			
			columnName = propertyNameAlias + "." + propertyAttrib;
		}
		
		if ("ASC".equalsIgnoreCase(order.getDir()))
			return this.getCriteriaBuilder().asc(this.getRoot().get(columnName));
		else
			return this.getCriteriaBuilder().desc(this.getRoot().get(columnName));
	}
	//TODO tiene sentido que siga siendo public?
	public DataTableQueryBuilder<T> setPage() {
		
		this.getTypedQuery().setFirstResult(this.request.getStart() != null ? this.request.getStart() : 0).setMaxResults(this.request.getLength() != null ? this.request.getLength() : 100);
		return this;
	}
	
	public TypedQuery<T> buildQuery() {
		this.getCriteriaQuery().select(this.getRoot());
		this.addFilters().addOrders().setPage();
		return this.getTypedQuery();
	}
	
	private CriteriaBuilder getCriteriaBuilder() {
		
		if (this.criteriaBuilder == null)
			this.criteriaBuilder = entityManager.getCriteriaBuilder();
		
		return this.criteriaBuilder;
	}
	
	
	private TypedQuery<T> getTypedQuery() {
		
		if (this.typedQuery == null)
			this.typedQuery = this.entityManager.createQuery(this.getCriteriaQuery());
		
		return this.typedQuery;
	}
	
	private CriteriaQuery<T> getCriteriaQuery() {
		
		if (this.criteriaQuery == null)
			this.criteriaQuery = this.getCriteriaBuilder().createQuery(this.persistentClass);
		
		return this.criteriaQuery;
	}
	
	private Root<T> getRoot() {
		
		if (this.root == null)
			this.root = this.getCriteriaQuery().from(this.persistentClass);
		
		return this.root;
	}
	
	public DataTableRequest getRequest() {
		return request;
	}
	
	public void setRequest(DataTableRequest request) {
		this.request = request;
	}
}
