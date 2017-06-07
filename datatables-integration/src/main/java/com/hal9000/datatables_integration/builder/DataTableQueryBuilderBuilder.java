package com.hal9000.datatables_integration.builder;

import java.lang.reflect.Type;

import javax.persistence.EntityManager;

import com.hal9000.datatables_integration.dto.request.DataTableRequest;

public final class DataTableQueryBuilderBuilder {
	
	private EntityManager entityManager;
	private Class<?> persistentClass;
	private DataTableQueryBuilder<?> queryBuilder;
	private DataTableRequest request;
	private Type type;
	
	private DataTableQueryBuilderBuilder() {
		
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Object getPersistentClass() {
		return persistentClass;
	}

	public void setPersistentClass(Object persistentClass) {
		this.persistentClass = persistentClass.getClass();
		this.type = persistentClass.getClass().getGenericSuperclass();
	}
	
	public DataTableQueryBuilder<?> build() {
		return new DataTableQueryBuilder<>(entityManager.getCriteriaBuilder(), request, persistentClass);
	}
}
