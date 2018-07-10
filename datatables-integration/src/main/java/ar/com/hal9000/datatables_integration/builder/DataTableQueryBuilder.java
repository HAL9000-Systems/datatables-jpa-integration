/**
 * 
 */
package ar.com.hal9000.datatables_integration.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ar.com.hal9000.datatables_integration.dto.request.Column;
import ar.com.hal9000.datatables_integration.dto.request.DataTableRequest;
import ar.com.hal9000.datatables_integration.dto.request.TableOrder;

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
	
	public DataTableQueryBuilder(EntityManager entityManager, DataTableRequest request, Class<T> persistentClass) {
		this.request = request;
		this.entityManager = entityManager;
		this.criteriaBuilder = this.entityManager.getCriteriaBuilder();
		this.persistentClass = persistentClass;
	}
	
	private DataTableQueryBuilder<T> addFilters() {
		
		if (request.getColumns() != null && !request.getColumns().isEmpty()) {
			
			List<Column> searchableColumns = request.getColumns().stream().filter(tableColumn -> tableColumn.getSearchable()).collect(Collectors.toList());
			ArrayList<Predicate> whereConditions = this.createFilters(searchableColumns, this.getRoot());
			
			if (whereConditions != null && !whereConditions.isEmpty())
				this.getCriteriaQuery().where(this.criteriaBuilder.or(whereConditions.toArray(new Predicate[whereConditions.size()])));
		}
		
		return this;
	}
	
	private ArrayList<Predicate> createFilters(List<Column> searchableColumns, Root<T> root) {
		
		ArrayList<Predicate> restrictions = new ArrayList<Predicate>();
		
		if (searchableColumns != null && !searchableColumns.isEmpty()) {

			String globalSearchValue = null;
			
			if (request.getSearch().getValue() != null && !request.getSearch().getValue().equals(""))
				globalSearchValue = request.getSearch().getValue();
			
			//Iterator<Column> i = searchableColumns.iterator();
			
			//while (i.hasNext()) {
			for (Column tableColumn : searchableColumns) {
				
				//Column tableColumn = i.next();
				String searchValue = null;
				
				if (globalSearchValue != null)
					searchValue = globalSearchValue;
				else
					searchValue = tableColumn.getSearch().getValue();
				
				if (searchValue != null && !searchValue.equals("")) {
					
					Predicate predicate = this.createColumnFilterCondition(root, tableColumn, searchValue);
				
					if (predicate != null)
						restrictions.add(predicate);
				}
			}
		}
		
		return restrictions;
	}
	
	private Predicate createColumnFilterCondition(Root<T> root, Column column, String value) {
		
		Predicate predicate = null;
		
		if (value != null && !value.isEmpty()) {
			
			String columnName = !column.getData().matches("\\d+") ? column.getData() : column.getName();
			
			if (columnName != null && !columnName.isEmpty()) {
				
				Expression<String> expression = null;
				
				if (columnName.contains(".")) {
					
					Join<?, ?> join = null;
					String[] parts = columnName.split("\\.");
					
					for (int i = 0; i < parts.length; i++) {
						
						if (i < parts.length - 1)
							join = (join == null) ? root.join(parts[i], JoinType.LEFT) : join.join(parts[i], JoinType.LEFT);
						else if (join != null)
							expression = join.get(parts[i]);
					}
				}
				else {
					expression = root.get(columnName);
				}
				
				predicate = this.getCriteriaBuilder().like(this.getCriteriaBuilder().lower(expression.as(String.class)), "%" + value.toLowerCase() + "%");
			}
		}
		
		return predicate;
	}
	
	private DataTableQueryBuilder<T> addOrders() {
		
		if(this.request.getOrder() != null && !this.request.getOrder().isEmpty()) {
			//Logica para armar el order by
			if (this.request.getColumns() != null && !this.request.getColumns().isEmpty() && this.request.getColumns().stream().anyMatch(tableColumn -> tableColumn.getOrderable())) {
				
				ArrayList<Order> orders = new ArrayList<Order>();
				
				this.request.getOrder().forEach(tableOrder -> {
					
					Order order = this.createColumnOrderCondition(this.request.getColumns().get(tableOrder.getColumn()), tableOrder);
					
					if (order != null)
						orders.add(order);
				});
				
				this.getCriteriaQuery().orderBy(orders);
			}
		}
		
		return this;
	}
	
	private Order createColumnOrderCondition(Column column, TableOrder order) {
		
		Order orderBy = null; 
		String columnName = !column.getData().matches("\\d+") ? column.getData() : column.getName();
		
		if (columnName != null && !columnName.isEmpty()) {
			
			Expression<String> expression = null;
			
			if (columnName.contains(".")) {
				
				Join<?, ?> join = null;
				String[] parts = columnName.split("\\.");
				
				for (int i = 0; i < parts.length; i++) {
					
					if (i < parts.length - 1)
						join = (join == null) ? root.join(parts[i], JoinType.LEFT) : join.join(parts[i], JoinType.LEFT);
					else if (join != null)
						expression = join.get(parts[i]);
				}
			}
			else {
				expression = root.get(columnName);
			}
			
			if ("ASC".equalsIgnoreCase(order.getDir()))
				orderBy = this.getCriteriaBuilder().asc(expression);
			else
				orderBy = this.getCriteriaBuilder().desc(expression);
		}
		
		return orderBy;
	}
	
	@SuppressWarnings("unused")
	private DataTableQueryBuilder<T> addGroupBy() {
		
		if(this.request.getOrder() != null && !this.request.getOrder().isEmpty()) {
			//Logica para armar el order by
			if (this.request.getColumns() != null && !this.request.getColumns().isEmpty()) {
				
				ArrayList<Expression<?>> groupByConditions = new ArrayList<>();
				
				this.request.getOrder().forEach(tableOrder -> {
					
					Expression<?> groupBy = this.addGroupByForColumn(this.request.getColumns().get(tableOrder.getColumn()));
					
					if (groupBy != null)
						groupByConditions.add(groupBy);
				});
				
				this.getCriteriaQuery().groupBy(groupByConditions);
			}
		}
		
		return this;
	}
	
	private Expression<?> addGroupByForColumn(Column column) {
		
		Expression<?> groupBy = null; 
		String columnName = !column.getData().matches("\\d+") ? column.getData() : column.getName();
		
		if (columnName != null && !columnName.isEmpty()) {
			
			if (columnName.contains(".")) {
				
				Join<?, ?> join = null;
				String[] parts = columnName.split("\\.");
				
				for (int i = 0; i < parts.length; i++) {
					
					if (i < parts.length - 1)
						join = (join == null) ? root.join(parts[i], JoinType.LEFT) : join.join(parts[i], JoinType.LEFT);
					else if (join != null)
						groupBy = join.get(parts[i]);
				}
			}
			else {
				groupBy = root.get(columnName);
			}
		}
		
		return groupBy;
	}
	
	private DataTableQueryBuilder<T> setPage() {
		
		this.getTypedQuery().setFirstResult(this.request.getStart() != null ? this.request.getStart() : 0).setMaxResults(this.request.getLength() != null ? this.request.getLength() : 100);
		return this;
	}
	
	public Long getTotalRowCount() {
		
		CriteriaQuery<Long> query = this.getCriteriaBuilder().createQuery(Long.class);
		Root<T> root = query.from(this.persistentClass);
		query.select(this.getCriteriaBuilder().count(root));
		return this.entityManager.createQuery(query).getSingleResult();
	}
	
	
	public Long getFilterRowCount() {
		
		CriteriaQuery<Long> query = this.getCriteriaBuilder().createQuery(Long.class);
		Root<T> root = query.from(this.persistentClass);
		query.select(this.getCriteriaBuilder().countDistinct(root));
		
		if (request.getColumns() != null && !request.getColumns().isEmpty()) {
			
			List<Column> searchableColumns = request.getColumns().stream().filter(tableColumn -> tableColumn.getSearchable()).collect(Collectors.toList());
			ArrayList<Predicate> whereConditions = this.createFilters(searchableColumns, root);
			
			if (whereConditions != null && !whereConditions.isEmpty())
				query.where(this.criteriaBuilder.or(whereConditions.toArray(new Predicate[whereConditions.size()])));
		}
		
		return this.entityManager.createQuery(query).getSingleResult();
	}
	
	public TypedQuery<T> buildQuery() {
		this.getCriteriaQuery().select(this.getRoot());
		this.addFilters().addOrders().setPage();
		return this.getTypedQuery();
	}
	
	private CriteriaBuilder getCriteriaBuilder() {
		
		if (this.criteriaBuilder == null)
			this.criteriaBuilder = this.entityManager.getCriteriaBuilder();
		
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
