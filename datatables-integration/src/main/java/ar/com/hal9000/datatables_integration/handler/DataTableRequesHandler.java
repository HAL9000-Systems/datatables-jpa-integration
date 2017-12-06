package ar.com.hal9000.datatables_integration.handler;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;

import ar.com.hal9000.datatables_integration.builder.DataTableQueryBuilder;
import ar.com.hal9000.datatables_integration.dto.request.DataTableRequest;
import ar.com.hal9000.datatables_integration.dto.response.DataTableResponse;

public abstract class DataTableRequesHandler<T> {
	
	protected EntityManager entityManager;
	protected Class<T> persistentClass;
	protected DataTableQueryBuilder<T> queryBuilder;
	
	@SuppressWarnings("unchecked")
	public DataTableRequesHandler() {
		
		this.persistentClass =(Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	@SuppressWarnings("unchecked")
	public DataTableRequesHandler(EntityManager entityManager) {
		
		this.entityManager = entityManager;
		this.persistentClass =(Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	public DataTableResponse<T> handleRequest(DataTableRequest request) {
		
		this.queryBuilder = new DataTableQueryBuilder<>(this.entityManager, request, persistentClass);
		
		DataTableResponse<T> response = new DataTableResponse<T>();
		
		response.setDraw(this.getDraw(request));
		response.setRecordsTotal(this.getTotalRowCount(request));
		response.setRecordsFiltered(this.getFilterRowCount(request));
		response.setData(this.getPageData(request));
		
		return response;
	}
	
	public Integer getDraw(DataTableRequest request) {
		return request.getDraw();
	}
	
	public Long getTotalRowCount(DataTableRequest request) {
		return this.queryBuilder.getTotalRowCount();
	}
	
	public Long getFilterRowCount(DataTableRequest request) {
		return this.queryBuilder.getFilterRowCount();
	}
	
	public List<T> getPageData(DataTableRequest request) {
		return this.queryBuilder.addFilters().addOrders().setPage().buildQuery().getResultList();
	}
}
