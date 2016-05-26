package org.sakaiproject.rubrics.api.rubric;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class RubricRow implements Serializable {

	private Long rowId;
	private Rubric rubric;
	private int sequence;
	private String rowText;
	private int rowWeight;
	private boolean removed;
	private Set<RubricCell> cellSet = new LinkedHashSet<RubricCell>();


	public RubricRow () {
	}
	
	public RubricRow(Rubric rubric) {
		this.rubric = rubric;
	}

//Getters and Setters
//Set: rowId, sequence, rowText, rowWeight, cellSet, 
	public Long getRowId() {
		return rowId;
	}

	public void setRowId(Long id) {
		this.rowId = id;
	}

	public Rubric getRubric () {
		return rubric;
	}
	
	public void setRubric (Rubric rubric) {
		this.rubric = rubric;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int order) {
		this.sequence = order;
	}

	public String getRowText() {
		return rowText;
	}

	public void setRowText(String rowText) {
		this.rowText = rowText;
	}

	public int getRowWeight(){
		return rowWeight;
	}

	public void setRowWeight(int weight) {
		this.rowWeight = weight;
	}
	
	public boolean getRemoved(){
		return removed;
	}
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
	public Set getCellSet() {
		return cellSet;
	}
	
	public void setCellSet(Set cellSet) {
		this.cellSet = cellSet;	
	}
	public String print(){
		StringBuffer sb= new StringBuffer();
		sb.append(rowId);
		sb.append(rowText);
		sb.append("cellsize: ");
		sb.append(cellSet.size());
		return sb.toString();
	}
}	
