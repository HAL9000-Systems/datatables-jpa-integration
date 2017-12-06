/**
 * 
 */
package ar.com.hal9000.datatables_integration.dto.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author gabriel
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Search implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6158902154422187666L;
	
	@JsonProperty("value")
	private String value;
	@JsonProperty("regex")
	private Boolean regex;
	
	public Search() {
		
	}
	
	public Search(Boolean regex) {
		this.regex = regex;
	}
	
	public Search(String value, Boolean regex) {
		this.value = value;
		this.regex = regex;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public Boolean getRegex() {
		return regex;
	}
	
	public void setRegex(Boolean regex) {
		this.regex = regex;
	}
}
