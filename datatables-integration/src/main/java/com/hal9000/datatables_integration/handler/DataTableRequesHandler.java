package com.hal9000.datatables_integration.handler;

//import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;

import com.hal9000.datatables_integration.builder.DataTableQueryBuilder;
import com.hal9000.datatables_integration.dto.request.DataTableRequest;
import com.hal9000.datatables_integration.dto.response.DataTableResponse;

public class DataTableRequesHandler<T> {
	
	private EntityManager entityManager;
	private Class<T> persistentClass;
	private DataTableQueryBuilder<T> queryBuilder;
	
	public DataTableRequesHandler(EntityManager entityManager, Class<T> persistentClass) {
		
		this.entityManager = entityManager;
		this.persistentClass = persistentClass;
	}
	
	public DataTableResponse<T> handleRequest(DataTableRequest request) {
		
		this.queryBuilder = new DataTableQueryBuilder<>(this.entityManager, request, persistentClass);
		
		DataTableResponse<T> response = new DataTableResponse<T>();
		
		response.setDraw(request.getDraw());
		response.setRecordsTotal(this.queryBuilder.getTotalRowCount());;
		
		this.queryBuilder.addFilters().addOrders();
		
		response.setRecordsFiltered(this.queryBuilder.getFilterRowCount());
		
		this.queryBuilder.setPage();
		response.setData(this.queryBuilder.buildQuery().getResultList());
		
		return response;
	}
}
