package org.sakaiproject.rubrics.api.rubric;

import java.io.Serializable;

public class RubricCell implements Serializable {

	private Long cellId;
	private RubricRow row;
	private Rubric rubric;
	private String columnText;
	private int columnWeight;
	private String cellText;
	private String cellType;
	private int columnSequence;

	public RubricCell() {
	}
	public RubricCell(RubricRow row){
		this.row = row;
	}
	public RubricCell(Rubric rubric, RubricRow row){
		this.row = row;
		this.rubric = rubric;
	}
	
	public Long getCellId() {
		return cellId;
	}

	public void setCellId(Long id) {
		this.cellId = id;
	}

	public RubricRow getRow() {
		return row;
	}

	public void setRow(RubricRow row) {
		this.row = row;
	}

	public Rubric getRubric() {
		return rubric;
	}

	public void setRubric(Rubric rubric) {
		this.rubric = rubric;
	}
	public String getColumnText() {
		return columnText;
	}
	public void setColumnText(String columnText) {
		this.columnText = columnText;
	}
	
	public int getColumnWeight() {
		return columnWeight;
	}
	
	public void setColumnWeight(int columnWeight) {
		this.columnWeight = columnWeight;
	}
	
	public String getCellText() {
		return cellText;
	} 	
	public void setCellText(String cellText){
		this.cellText = cellText;
	} 

	public String getCellType() {
		return cellType;
	} 	
	public void setCellType(String cellType){
		this.cellType = cellType;
	} 
	public void setColumnSequence(int sequence){
		this.columnSequence = sequence;
	} 
	public int getColumnSequence() {
		return columnSequence;
	} 	

	
}
	 	
