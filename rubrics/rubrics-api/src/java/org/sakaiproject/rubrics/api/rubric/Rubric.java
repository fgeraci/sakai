package org.sakaiproject.rubrics.api.rubric;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import org.sakaiproject.rubrics.api.rubric.RubricRow;

public class Rubric implements Serializable{
	private Long rubricId;
	private int version;
	private String title;
	private String titleKey; //referred to as coreKey within html template
	private boolean template;
	private String description;
	private String createdBy;
	private Date createdDate;
	private String modifiedBy;
	private Date modifiedDate; 
	private String icon;
	private boolean removed;
	private String dataSet;
	private Set<RubricRow> rubricRowSet = new LinkedHashSet<RubricRow>();
	
	

	public Rubric() {
	}
	public Long getRubricId() {
		return rubricId;
	}
	public void setRubricId(Long id) {
		this.rubricId = id;
	}
	public int getVersion () {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getTitle () {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitleKey() {
		return titleKey;
	}
	public void setTitleKey(String key) {
		this.titleKey = key;
	}
	public boolean isTemplate() {
		return template;
	}
	public void setTemplate (boolean template) {
		this.template = template;
	}
	public String getDescription () {
		return description;
	}
	public void setDescription (String description) {
		this.description = description;
	}	
	public String getCreatedBy () {
		return createdBy;
	}
	public void setCreatedBy (String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedDate () {	
		return createdDate;
	}
	public void setCreatedDate (Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getModifiedBy () {
			return modifiedBy;
	}
	public void setModifiedBy (String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Date getModifiedDate () {
			return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
			this.modifiedDate = modifiedDate;
	}
	public String getIcon () {
		return icon;
	}
	public void setIcon (String icon) {
		this.icon = icon;
	}
	public boolean isRemoved () {
			return removed;
	}
	public void setRemoved (boolean removed) {
			this.removed = removed;
	}
	public Set getRubricRowSet() {
		return rubricRowSet;
	}
	public void setRubricRowSet(Set rubricRowSet){
		this.rubricRowSet = rubricRowSet;
	}
	public String getDataSet() {
		return dataSet;
	}
	public void setDataSet(String dataSet){
		this.dataSet = dataSet;
	}
	public String print(){
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("ID: "+rubricId);
		strbuf.append("Title: "+title);
		strbuf.append("Creation Date: "+createdDate.toString());
		RubricRow [] array = rubricRowSet.toArray(new RubricRow[rubricRowSet.size()]);
		StringBuffer sb = new StringBuffer();
		for(RubricRow row : array){
			
			sb.append("Row: "+row.print());
		}
		strbuf.append(sb.toString());
		return strbuf.toString();
		
	}
}
