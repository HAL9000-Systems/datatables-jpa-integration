/**
 * 
 */
package com.hal9000.datatables_integration.query;

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
	private EntityManager entityManager;
	private TypedQuery<T> typedQuery;
	private final Class<T> persistentClass;
	
	@SuppressWarnings("unchecked")
	public DataTableQueryBuilder(EntityManager entityManager, DataTableRequest request) {
		this.request = request;
		this.entityManager = entityManager;
		this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	//TODO tiene sentido que siga siendo public?
	public DataTableQueryBuilder<T> addFilters() {
		
		if (this.request.getSearch().getValue() != null && !this.request.getSearch().getValue().equals("") && request.getColumns() != null && !request.getColumns().isEmpty()) {
			//Logica para armar el where
			List<Column> searchableColumns = request.getColumns().stream().filter(tableColumn -> tableColumn.getSearchable()).collect(Collectors.toList());
			
			if (searchableColumns != null && !searchableColumns.isEmpty()) {
				
				ArrayList<Predicate> restrictions = new ArrayList<Predicate>();
				searchableColumns.forEach(tableColumn -> restrictions.add(this.createColumnFilterCondition(tableColumn, this.request.getSearch().getValue())));
				this.getCriteriaQuery().where(this.criteriaBuilder.or((Predicate[]) restrictions.toArray()));
			}
		}
		
		return this;
	}
	//TODO hacer esto case insensitive
	private Predicate createColumnFilterCondition(Column column, String value) {
		
		String columnName = ("".equals(column.getName())) ? column.getData() : column.getName();
		
		if (columnName.contains(".")) {
			
			String propertyName = columnName.split("\\.")[0];
			String propertyAttrib = columnName.split("\\.")[1];
			String propertyNameAlias = propertyName + "_alias";
			
			this.getRoot().join(propertyName).alias(propertyNameAlias);
			
			columnName = propertyNameAlias + "." + propertyAttrib;
		}
		
		return this.getCriteriaBuilder().like(this.getRoot().get(columnName), "%" + value + "%");
	}
	//TODO tiene sentido que siga siendo public?
	public DataTableQueryBuilder<T> addOrders() {
		
		if(this.request.getOrder() != null && !this.request.getOrder().isEmpty()) {
			//Logica para armar el order by
			if (this.request.getColumns() != null && !this.request.getColumns().isEmpty() && this.request.getColumns().stream().anyMatch(tableColumn -> tableColumn.getOrderable())) {
				
				ArrayList<Order> orders = new ArrayList<Order>();
				
				this.request.getOrder().forEach(tableOrder -> orders.add(this.createOrderConditionOrderForColumn(this.request.getColumns().get(tableOrder.getColumn()), tableOrder)));
				this.getCriteriaQuery().orderBy(orders);
			}
		}
		
		return this;
	}
	
	private Order createOrderConditionOrderForColumn(Column column, TableOrder order) {
		
		if (column.getOrderable()) {
			
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
		
		return null;
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
