package org.sakaiproject.rubrics.api.rubric;

import java.io.Serializable;

/**
 * The sole purpose of this file is to be used in HQL queries to quickly find rubrics
 * associated to specific tool->item_ids.
 */

public class RubricMappedItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private MapId gId;
	private Long gRubricId;
	
	public RubricMappedItem() {
		this.gId = new MapId();
	}
	
	public RubricMappedItem(Long itemId, String toolId) {
		this();
		this.gId.setItemId(itemId);
		this.gId.setToolId(toolId);
	}
	
	public void setItemId(Long pItemId) {
		this.gId.setItemId(pItemId);
	}
	
	public void setToolId(String pToolId) {
		this.gId.setToolId(pToolId);
	}
	
	public MapId getId() { return this.gId; }
	
	public void setId(MapId pId) { this.gId = pId; }
	
	public void setRubricId(Long pRubricId) {
		this.gRubricId = pRubricId;
	}
	
	public Long getRubricId() {
		return this.gRubricId;
	}
	
}
