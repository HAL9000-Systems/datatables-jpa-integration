/**
 * 
 */
package ar.com.hal9000.datatables_integration.dto.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author gabriel
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataTableRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3822922638956307286L;
	
	@JsonProperty("draw")
	private Integer draw = 1;
	@JsonProperty("start")
	private Integer start = 0;
	@JsonProperty("length")
	private Integer length = 10;
	@JsonProperty("search")
	private Search search = new Search();
	@JsonProperty("order")
	private List<TableOrder> order = new ArrayList<TableOrder>();
	@JsonProperty("columns")
	private List<Column> columns = new ArrayList<Column>();
	
	public Map<String, Column> getColumnsAsMap() {
		
		Map<String, Column> map = new HashMap<String, Column>();
		
		for (Column column : columns) {
			map.put(column.getData(), column);
		}
		
		return map;
	}
	
	public Column getColumn(String columnName) {
		
		if (columnName == null) {
			return null;
		}
		
		for (Column column : columns) {
			
			if (columnName.equals(column.getData())) {
				return column;
			}
		}
		
		return null;
	}
	
	public void addColumn(String columnName, boolean searchable, boolean orderable, String searchValue) {
		
		this.columns.add(new Column(columnName, "", searchable, orderable, new Search(searchValue, false)));
	}
	
	public void addOrder(String columnName, boolean ascending) {
		
		if (columnName == null) {
			return;
		}
		
		for (int i = 0; i < columns.size(); i++) {
			
			if (!columnName.equals(columns.get(i).getData())) {
				continue;
			}
			
			order.add(new TableOrder(i, ascending ? "asc" : "desc"));
		}
	}

	public Integer getDraw() {
		return draw;
	}

	public void setDraw(Integer draw) {
		this.draw = draw;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public List<TableOrder> getOrder() {
		return order;
	}

	public void setOrder(List<TableOrder> order) {
		this.order = order;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
}
