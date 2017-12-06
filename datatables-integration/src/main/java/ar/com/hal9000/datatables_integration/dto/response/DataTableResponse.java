/**
 * 
 */
package ar.com.hal9000.datatables_integration.dto.response;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author gabriel
 *
 */
public class DataTableResponse<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2091946552363529011L;
	
	@JsonView(View.class)
	private int draw;
	@JsonView(View.class)
	private long recordsTotal = 0L;
	@JsonView(View.class)
	private long recordsFiltered = 0L;
	@JsonView(View.class)
	private List<T> data = Collections.emptyList();
	@JsonView(View.class)
	private String error;
	
	public interface View {
		
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public long getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public long getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
