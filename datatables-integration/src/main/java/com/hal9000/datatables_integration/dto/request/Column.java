/**
 * 
 */
package com.hal9000.datatables_integration.dto.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author gabriel
 *
 */
public class Column implements Serializable {

	/**
	 * 
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static final long serialVersionUID = 4511676956593928193L;
	
	private Integer index;
	@JsonProperty("data")
	private String data;
	@JsonProperty("name")
	private String name;
	@JsonProperty("searchable")
	private Boolean searchable;
	@JsonProperty("orderable")
	private Boolean orderable;
	@JsonProperty("search")
	private Search search;
	
	public Column() {
		
	}
	
	public Column(Integer index) {
		this.index = index;
	}
	
	public Column(String columnName, String data, boolean searchable, boolean orderable, Search searchValue) {
		this.name = columnName;
		this.searchable = searchable;
		this.orderable = orderable;
		this.search = searchValue;
		this.data = data;
	}
	
	public Search getSearch() {
		return this.search;
	}
	
	public void setSearchValue(String searchValue) {
		this.search.setValue(searchValue);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Boolean getSearchable() {
		return searchable;
	}

	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getOrderable() {
		return orderable;
	}

	public void setOrderable(Boolean orderable) {
		this.orderable = orderable;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
}
