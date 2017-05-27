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
@JsonIgnoreProperties(ignoreUnknown = true)
public class TableOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6087828198485516364L;
	
	@JsonProperty("column")
	private Integer column;
	private Integer index;
	@JsonProperty("dir")
	private String dir;
	
	public TableOrder() {
		
	}
	
	public TableOrder(Integer index) {
		this.index = index;
	}
	
	public TableOrder(Integer column, String dir) {
		this.column = column;
		this.dir = dir;
	}
	
	public Integer getColumn() {
		return column;
	}
	
	public void setColumn(Integer column) {
		this.column = column;
	}
	
	public String getDir() {
		return dir;
	}
	
	public void setDir(String dir) {
		this.dir = dir;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
}
