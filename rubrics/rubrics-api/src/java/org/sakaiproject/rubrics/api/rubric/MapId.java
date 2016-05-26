package org.sakaiproject.rubrics.api.rubric;

import java.io.Serializable;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class MapId implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long gItemId;
	private String gToolId;
	
	public void setItemId(Long pItemId) {
		this.gItemId = pItemId;
	}
	
	public void setToolId(String pToolId) {
		this.gToolId = pToolId;
	}
	
	public Long getItemId() {
		return this.gItemId;
	}
	
	public String getToolId() {
		return this.gToolId;
	}
	
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder();
		hcb.append(gItemId);
		hcb.append(gToolId);
        return hcb.toHashCode();
	}
	
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o instanceof MapId) {
			MapId other = (MapId) o;
			return other.getItemId() == this.getItemId() 
					&& other.getToolId().equalsIgnoreCase(this.getToolId());
		}
		return false;
	}
	
}