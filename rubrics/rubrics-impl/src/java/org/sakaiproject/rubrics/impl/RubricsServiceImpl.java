package org.sakaiproject.rubrics.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.rubrics.api.rubric.ExternalLogic;
import org.sakaiproject.rubrics.api.rubric.RURubricLogic;
import org.sakaiproject.rubrics.api.rubric.Rubric;
import org.sakaiproject.rubrics.api.rubric.RubricCell;
import org.sakaiproject.rubrics.api.rubric.RubricGrade;
import org.sakaiproject.rubrics.api.rubric.RubricRow;
import org.sakaiproject.rubrics.api.rubric.RubricsService;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;

public class RubricsServiceImpl implements RubricsService {

	
	private static Log log = LogFactory.getLog(RubricsServiceImpl.class);
	private ResourceLoader gResourceLoader;
	private static RubricsService mInstance;
	private HashMap<Long,String> gCommentsCells;
	
	
	/**
	 * Setting up autoddl
	 */
	
	protected boolean autoDdl = false;
	
	protected SqlService sqlService = null;
	
	public void setSqlService(SqlService service)
	{
		sqlService = service;
	}
	
	public void setAutoDdl(String value)
	{
		autoDdl = Boolean.valueOf(value).booleanValue();
	}
	
	
	public static RubricsService getInstance() {
		if(RubricsServiceImpl.mInstance == null)
			RubricsServiceImpl.mInstance = (RubricsService) ComponentManager.get(org.sakaiproject.rubrics.api.rubric.RubricsService.class);
		return RubricsServiceImpl.mInstance;
	}
	
	/* Bean initialization method */
	public void init() {
		gResourceLoader = new ResourceLoader("rubrics");
		log.warn("RubricsService initialized for Rubrics");
		gCommentsCells = new HashMap<Long,String>();
		RubricsServiceImpl.mInstance = this;
		Connection c = null;
		try
		{
			if (autoDdl)
			{
				sqlService.ddl(this.getClass().getClassLoader(), "sakai_rubrics_tool");
				
				/* We need to create a single index - I attempted to do so via sql in the main file
				 * but the autoddl wouldnt not propertly parse delimiter changes for store procedures
				 * (needed to check existing indices before creating new ones)
				 * So below is a quick workaround.
				 */
				c = sqlService.borrowConnection();
				PreparedStatement ps = c.prepareStatement("select COUNT(*) from INFORMATION_SCHEMA.STATISTICS where TABLE_NAME=? and index_name=?");
				ps.setString(1, "rubric_grade_t");
				ps.setString(2, "rubric_submission_index");
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					if(rs.getInt(1) == 0) {
						sqlService.ddl(this.getClass().getClassLoader(), "sakai_rubrics_indices");
					}
				}
			}
		}
		catch (Exception e)
		{
			log.warn("Rubrics autoddl did not completely finished: " + e.getMessage());
		} finally {
			if(c != null)
				sqlService.returnConnection(c);
		}
	}
	
	/* Logic bean - this interface shells logic+dao */
	private RURubricLogic gRubricLogicImpl;
	public void setRubricsLogic(RURubricLogic arg0) {
		gRubricLogicImpl = arg0;
	}
	
	/* External logic */
	private ExternalLogic externalLogic;
	public void setExternalLogic(ExternalLogic pel) {
		this.externalLogic = pel;
	}
	
	/* Bundle loader */
	public String getMessage(String pKey) {
		String message = null;
		if(gResourceLoader != null)
			message =  this.gResourceLoader.getString(pKey);
		return message;
	}
	
    public List<Rubric> getPredefinedRubrics(final String pUserId) {
		return this.gRubricLogicImpl.getPredefinedRubrics(pUserId);
	}
    
	public void saveRubric(Rubric pRubric) throws Exception {
		this.gRubricLogicImpl.saveRubric(pRubric);
	}
	
	public Rubric getRubricById(Long pId) {
		return this.gRubricLogicImpl.getRubricById(pId);
	}
	
	
	public void saveRubricGradeSet(Set<RubricGrade> rubricGradeSet, String submissionId) throws Exception {
		this.gRubricLogicImpl.saveRubricGradeSet(rubricGradeSet, submissionId);
	}
	
	public List<RubricGrade> getRubricGradesBySubmission(String submissionId) {
		return this.gRubricLogicImpl.getRubricGradesBySubmission(submissionId);
	}
	
	/* UTILS */
	
	public String getPredefinedRubricsString(String pUserId) {
		if(pUserId == null) pUserId =  externalLogic.getCurrentUserId();
		List<Rubric> rubricList = this.getPredefinedRubrics(pUserId);
		String prefefinedRubrics = "";
    	String rubric_delimeters[]= {RUBRIC_DELIMETERS1, RUBRIC_DELIMETERS2};
    	for (Rubric rubric : rubricList) {
    		prefefinedRubrics+="["+rubric.getRubricId().toString()+"][title]"+rubric_delimeters[1]+ rubric.getTitle()+rubric_delimeters[0];
    		prefefinedRubrics+="["+rubric.getRubricId().toString()+"][description]"+rubric_delimeters[1]+ rubric.getDescription()+rubric_delimeters[0];
    		prefefinedRubrics+="["+rubric.getRubricId().toString()+"][icon]"+rubric_delimeters[1]+rubric.getIcon()+rubric_delimeters[0];
    		List<RubricRow> rubricRows = new ArrayList<RubricRow>(rubric.getRubricRowSet());
    		RubricRow row=rubricRows.get(0);
    		List <RubricCell> cellSet = new ArrayList <RubricCell>(row.getCellSet());
    		ArrayList<String> rubric_columns = new ArrayList<String>((int)cellSet.size()+1);
			for(RubricCell cell : cellSet){
				prefefinedRubrics+="["+rubric.getRubricId().toString()+"]["+cell.getColumnSequence()+"][0]"+rubric_delimeters[1]+ cell.getColumnText()+rubric_delimeters[0];
				rubric_columns.add(cell.getColumnText());
			}
    		for(RubricRow currentRow : rubricRows){
    			prefefinedRubrics+="["+rubric.getRubricId().toString()+"][0]["+currentRow.getSequence()+"]"+rubric_delimeters[1]+ currentRow.getRowText()+rubric_delimeters[0];
				cellSet = new ArrayList <RubricCell>(currentRow.getCellSet());
				for(RubricCell cell : cellSet){
					long RowId = cell.getRow().getRowId();
					prefefinedRubrics+="["+rubric.getRubricId().toString()+"]["+(cell.getColumnSequence())+"]["+currentRow.getSequence()+"]"+rubric_delimeters[1]+ cell.getCellText()+rubric_delimeters[0];
				}
    		}
    	}
    	return prefefinedRubrics;
	}
	
	/**
	 * Dispatcher for view submission list options
	 */
	public String buildRubricHTMLTable(Rubric rubric, boolean pWithFeedback, double pPoints) {
		boolean calcPoints = false;
		double totalPoints = 0;
		try {
			totalPoints = pPoints;
			calcPoints = totalPoints > 0;
		} catch (Exception e) { log.warn("Failed at parsing total points"); }
		boolean hasCommentColumn = false;
		boolean hasGradingRow = false , isDataRow = false;
		int totalCols = 1, totalRows = 1;
		double[] colWeights, rowWeights;
		String commentsCells = "";
		String rubricTable = "";
		List<RubricRow> rubricRows = new ArrayList<RubricRow>(rubric.getRubricRowSet());
		// grading weights data
		{
			totalCols += rubricRows.get(0).getCellSet().size(); 
			totalRows = rubricRows.size();
			colWeights = new double[totalCols];
			rowWeights = new double[totalRows];
		}
		int row = 0;
		List<RubricCell> headerCells = new ArrayList<RubricCell>(rubricRows.get(0).getCellSet());
		int cell = 0; 
		rubricTable += "<tr>";
		// set the icon, if any
		if(rubric.getIcon() == null) {
			rubricTable += "<th></th>";
		} else {
			rubricTable += 	"<th><div><img src=\""+
					rubric.getIcon()+
					"\" width=\"150px\" height=\"150px\" style=\"display: block;margin-left: auto;margin-right: auto;\" alt=\"rubric icon\"></div></th>";
		}
		// build the header row
		int col = 0;
		for(RubricCell c : headerCells) {
			if(c.getCellType().equals(RUBRIC_COMMENTCELL)) {
				hasCommentColumn = true;
				if(!pWithFeedback) {
					continue;
				}
			}
			if(!c.getColumnText().isEmpty()) rubricTable += "<th style=\"	min-width: 125px;\"><span>"+c.getColumnText()+"</span></th>";
			if(isValue(c.getCellText())) {
				colWeights[col] = getValue(c.getCellText());
				col += 1;
			} else if (c.getCellText().equals(RUBRIC_GRADING_ROW_TEXT)) {
				hasGradingRow = true;
			}
		}
		rubricTable += "</tr>";
		
		/*
		 * I see no other option but to fill up the valus arrays before creating the html table, if not, weights will only be found as the last td in the row.
		 */
		for(int i  = 1; i < rubricRows.size(); i++) {
			RubricRow curRow = rubricRows.get(i);
			for(RubricCell c : new ArrayList<RubricCell>(curRow.getCellSet())) {
				if(isValue(c.getCellText())) {
					rowWeights[i-1] = getValue(c.getCellText());
				}
			}
		}
		
		// build the table
		for(RubricRow currentRow : rubricRows) {
			hasGradingRow = currentRow.getRowText().equals(RUBRIC_GRADING_ROW_TEXT);
			isDataRow = !hasGradingRow;
			String additionalStyle = "position: relative;";
			String optClass = hasGradingRow ? "class=\"gradingRow\"" : "class=\"dataRow\"";
			rubricTable += "<tr "+optClass+">";
			List<RubricCell> cells = new ArrayList<RubricCell>(currentRow.getCellSet());
			cell = 0;
			rubricTable += "<td><b>"+currentRow.getRowText()+"</b></td>";
			for (RubricCell currentCell : cells) {
				if(hasGradingRow && (currentCell.getColumnText().isEmpty() || currentCell.getCellText().equalsIgnoreCase("[[COMMENTS]]"))) continue;
				if(cell == 0) additionalStyle += "text-align: center;";
				if(currentCell.getCellType().equals(RUBRIC_COMMENTCELL) && !hasGradingRow) {
					commentsCells += currentCell.getCellId().toString() + ",";
					if(!pWithFeedback) { // < subject to change depending on how JQuery handles comments cells - need to do some research
						continue;
					}
				} else {
					if(isValue(currentCell.getCellText())) {
						additionalStyle += "font-weight: bold; text-align: center; background-color: rgba(160, 201, 120, 0.2);";
					} else if (hasGradingRow) {
						additionalStyle = "font-weight: bold; text-align: center; background-color: rgba(247, 198, 176, 0.4);";
					}
					String elId = isDataRow ? (row-1)+":"+cell : "";
					rubricTable += 	"<td id=\""+elId+"\" style=\""+additionalStyle+"\">";
				}
				rubricTable += "<span style=\"position: relative;\" class=\"cellId-"+currentCell.getCellId()+"\">"+currentCell.getCellText()+"</span>";
				if(calcPoints && isDataRow) {
					rubricTable += "<div id='cellPoints' style='display: none; text-align: center; background-color: rgba(195, 219, 243, 0.9); position: absolute; bottom: 0px; right: 0px; padding: 2px; border-radius: 1px;'>";
					try {
						if(!isValue(currentCell.getCellText())) {
							double cellPts = totalPoints * (rowWeights[row-1]/100) * (colWeights[cell]/100);
							rubricTable += cellPts;
						}
					} catch (Exception e) {  }
					finally { rubricTable += "</div></td>"; }
				}
				cell +=1;
			}
			rubricTable += "</tr>";
			row +=1;
		}
		// basically cache the comments cells when creating the rubric, hence this can't exist without the other.
		this.gCommentsCells.put(rubric.getRubricId(), commentsCells);
		return (!rubricTable.isEmpty() ? 
				"<div style=\"display: none;\" id=\"rubricDiv\"><br><br><table id=\"rubricTable2\" border=\"2\">"
					+ rubricTable
						+ "</table><br><br></div>"
				: rubricTable);
	}
	
	private boolean isValue(String pVal) {
		if(pVal.isEmpty()) return false;
		pVal = pVal.trim();
		String tmp = "";
		for(int i = 0; i < pVal.length(); ++i) {
			if(Character.isLetter(pVal.charAt(i))) return false;
			else tmp += Character.isDigit(pVal.charAt(i)) || pVal.charAt(i) == '.'  ? pVal.charAt(i) : "" ;
		}
		try {
			Float.parseFloat(tmp);
			if(tmp.length() >= pVal.length() - 2) // check if all the digits NOT included in tmp are no more than 2; for % and . 
				return true;
			else return true;
		} catch (Exception e) { return false; }
	}
	
	/*
	 * Returns double value of e.g. 99.5% as 99.50 if valid. 0 otherwise. 
	 */
	private double getValue(String pVal) {
		pVal = pVal.trim();
		String tmp = "";
		for(int i = 0; i < pVal.length(); ++i) {
			tmp += Character.isDigit(pVal.charAt(i)) || pVal.charAt(i) == '.'  ? pVal.charAt(i) : "" ;
		}
		try {
			return Float.parseFloat(tmp);
		} catch (Exception e) {
			return 0.0;
		}
	}
	
	public void processRubricGradingData(String pRubricData, long pRubricId,long pId) throws Exception {
		String rubricGradingData = pRubricData, submissionId = null;
		if(rubricGradingData != null && !rubricGradingData.isEmpty()){
			submissionId = String.valueOf(pId);
			String rubricGradeDataArray[];
			ArrayList selectedCells = new ArrayList();
			HashMap<Long, String> commentCells = new HashMap<Long, String>();
			rubricGradeDataArray=rubricGradingData.split("\\|\\|");
			if(rubricGradeDataArray.length > 0) {
				for(String selectedCell: rubricGradeDataArray){
					if(selectedCell.length()==0) continue;
					int equalsIndex = selectedCell.indexOf('=');
					if(equalsIndex!=-1){
						commentCells.put(Long.parseLong(selectedCell.substring(0, equalsIndex)) , selectedCell.substring(equalsIndex+1));
					}else{
						log.warn("parseLong(selectedCell)" + Long.parseLong(selectedCell));
						selectedCells.add(Long.parseLong(selectedCell));
					}
				}
				Set<RubricGrade> rubricGrdSet = new HashSet<RubricGrade>();
				Rubric rubric = this.getRubricById(pRubricId);
				Set<RubricRow> rowSet = rubric.getRubricRowSet();
				for (RubricRow row : rowSet) {
					Set<RubricCell> cellSet = row.getCellSet();
					for(RubricCell cell: cellSet){
						RubricGrade rubricGrd = new RubricGrade();
						rubricGrd.setRubricCellId(cell.getCellId());
						Double points;
						if(cell.getCellType()!= RUBRIC_COMMENTCELL && selectedCells.size() > 0 && selectedCells.contains(cell.getCellId())) 
							points=1.0;
						else if(cell.getCellType().equals(RUBRIC_COMMENTCELL))
						{
							points=0.0;
							if(commentCells.containsKey(cell.getCellId())){
								rubricGrd.setComment(commentCells.get(cell.getCellId()));
							}
						}
						else points=0.0;
						rubricGrd.setPointsEarned(points);
						rubricGrdSet.add(rubricGrd);
						rubricGrd.setAssignmentSubmissionId(submissionId);
					}
				}
				this.saveRubricGradeSet(rubricGrdSet, submissionId);
			}
		}
	}
	
	public void buildRubricFromData(Rubric rubric, String pData)
    {
    	final String DELIMETER1 = " ;; ", DELIMETER2 = " == ", rubricTemplate = "t3mpl@te:";
	
		rubric.setDataSet(pData);
		
		String[] records = pData.split(DELIMETER1), recordSplit, coordinateSplit;
		int column, rowumn, rowIndex;
		boolean isRubricTemplate = false, titleFound = false , descriptionFound = false, commentCell=false, myRubric = false;
		
		HashMap<Integer, String> rowTitles = new HashMap<Integer, String>();
		HashMap<Integer, String> colTitles = new HashMap<Integer, String>();
		ArrayList<Integer> uniqueRowsAL = new ArrayList<Integer>();				//for each cell in a row, only one Row needs to be created.
		ArrayList<RubricRow> rowAL = new ArrayList<RubricRow>();
		ArrayList<Set<RubricCell>> cellAL = new ArrayList<Set<RubricCell>>();
		
		//First Pass: title, description, and rubric row/column titles are set
		//2nd Pass  : build rows and cells. Rows and cells need titles from 1st pass.
		for(int rubricPass = 0 ; rubricPass < 2 ; rubricPass++)
		{
			titleFound=false; descriptionFound=false;
			for (String record : records)
			{
				boolean emptyCell = false;
				recordSplit = record.split(DELIMETER2);
				if(recordSplit.length != 2) 
					emptyCell = true;
				if(!titleFound && recordSplit[0].indexOf("title") != -1)
				{
					if(rubricPass == 0){	
						if(!emptyCell)
						{
							if(recordSplit[1].indexOf(rubricTemplate)!=-1)
							{
								recordSplit[1]=recordSplit[1].substring(rubricTemplate.length());
								isRubricTemplate = true;
							}
							rubric.setTitle(recordSplit[1]);
						}
						else
							rubric.setTitle("untitled");
					}
					titleFound = true;
					continue;
				}
				else if(!descriptionFound && recordSplit[0].indexOf("description") != -1)
				{
					if(rubricPass == 0){
						if(!emptyCell)	
							rubric.setDescription(recordSplit[1]);
						else
							rubric.setDescription("");
					}
					descriptionFound = true;
					continue;
				}
				else if(recordSplit[0].indexOf("templates") != -1)
				{
					if(rubricPass == 0){
						if(!emptyCell)	
						{
							// some useless statement
						}
					}
					continue;
				}
				else if(recordSplit[0].indexOf("icon") != -1)
				{
					if(rubricPass == 0 && !emptyCell){	
						rubric.setIcon(recordSplit[1]);
					}
					continue;
				}
				else if(recordSplit[0].indexOf("coreKey") != -1)
				{
					if(rubricPass == 0 && !emptyCell){	
						rubric.setTitleKey(recordSplit[1]);
					}
					continue;
				} else if (recordSplit[0].indexOf("rubric_type") != -1) {
					// might very well be added eventually to the bean but it is not truly necessary, yet this check ought to stay here
					continue;
				} else if(recordSplit[0].indexOf("add_to_my_rubrics") != -1) {
					rubric.setTemplate(true);
					myRubric = true;
					continue;
				}
				else
				{
					try {
						coordinateSplit = recordSplit[0].split("\\]\\[");
						column = Integer.parseInt(coordinateSplit[0].substring(1));
						rowumn = Integer.parseInt(coordinateSplit[1].substring(0, coordinateSplit[1].length()-1));
					} catch(Exception e) { continue; }
					
					if(rubricPass == 0)
					{
						if(column == 0  ^  rowumn == 0 )
						{
							if(rowumn == 0)
								if(!emptyCell)
									colTitles.put(column, recordSplit[1]);
								else colTitles.put(column, "");
							if(column == 0)
								if(!emptyCell)
									rowTitles.put(rowumn, recordSplit[1]);
								else rowTitles.put(rowumn, "");
						}
					}
					else
					{
						if(column == 0  ||  rowumn == 0 ) continue;
						commentCell=false;
						if(!emptyCell && recordSplit[1].equals("[[COMMENTS]]")){
							commentCell=true;
						}
						if(!uniqueRowsAL.contains((Integer)rowumn))
						{
							uniqueRowsAL.add((Integer)rowumn);
							rowAL.add(new RubricRow(rubric));
							cellAL.add(new HashSet<RubricCell>()); //each row, has a cell set
						}
						
						rowIndex = uniqueRowsAL.indexOf(rowumn);
						rowAL.get(rowIndex).setSequence(rowumn);
						rowAL.get(rowIndex).setRowText(rowTitles.get(rowumn));
						RubricCell cell = new RubricCell(rubric,rowAL.get(rowIndex));
						cell.setColumnSequence(column);
						cell.setCellText((emptyCell)?"":recordSplit[1]);
						cell.setColumnText(colTitles.get(column));
						if(commentCell)cell.setCellType(RUBRIC_COMMENTCELL);else cell.setCellType(RUBRIC_NOTCOMMENTCELL);
						cellAL.get(rowIndex).add(cell);
					}
				}
			}
		}
	
		for(RubricRow curRow: rowAL){
			curRow.setCellSet(cellAL.get(rowAL.indexOf(curRow)));
		}
		
		Set<RubricRow> rowSet = new HashSet<RubricRow>();
		for(RubricRow curRow: rowAL){rowSet.add(curRow);}
		rubric.setRubricRowSet(rowSet);
		
		if(!titleFound){log.warn("RUBRIC: The javascript did not send [title] tag.");rubric.setTitle("untitled");}
		if(!descriptionFound){log.warn("RUBRIC: The javascript did not send [description] tag");rubric.setDescription("");}
		if(myRubric) rubric.setTitle(rubric.getTitle() + " - My Rubric");
		if(!rubric.isTemplate()) rubric.setTemplate(isRubricTemplate);
		rubric.setCreatedBy(externalLogic.getCurrentUserId());
		Date date = new Date();
		rubric.setCreatedDate(date);
    }
    
	@Override
    public void processRubric(Long rubricId, Rubric pRubric, String pData) {
    	if (rubricId != null) {
    		pRubric.setRubricId(rubricId);
    	}
    	pRubric.setDataSet(pData);
		try {
			this.saveRubric(pRubric);
		} catch(Exception e) {
			log.warn("Rubric coudlnt be saved - "+e.getMessage());
    	} 
    }

	@Override
	public Rubric getRubricByItemId(Long itemId, String toolId) {
		if(itemId != null && toolId != null) {
			return gRubricLogicImpl.getRubricByItemId(itemId, toolId);
		}
		return null;
	}

	@Override
	public void saveRubricForItemId(Long pItemId, String pToolId, Long pRubricId) {
		gRubricLogicImpl.saveRubricForItemId(pItemId, pToolId, pRubricId);
	}

	@Override
	public long saveNewRubric(String pData) throws Exception {
		if(pData == null) throw new Exception("SaveNewRubricWithItem: Invalid parameter, pData is missing");
		Rubric r = new Rubric();
		this.buildRubricFromData(r, pData);
		this.saveRubric(r);
		return r.getRubricId();
	}

	@Override
	public long saveNewRubricWithItem(String pData, Long pItemId, String pToolId) throws Exception {
		if(pData == null || pItemId == null || pToolId == null) throw new Exception("SaveNewRubricWithItem: Invalid parameters, some are null");
		Long rubId = this.saveNewRubric(pData);
		this.saveRubricForItemId(pItemId, pToolId, rubId);
		return rubId;
	}

	@Override
	public Long getRubricIdByItemId(Long itemId, String toolId) {
		if(itemId != null && toolId != null) {
			return gRubricLogicImpl.getRubricIdByItemId(itemId, toolId);
		}
		return null;
	}
    
	@Override
	public String getRubricGradingDataString(String submissionId){
        String selectedCellsString = "";
    	if (submissionId != null && submissionId.length() > 0){
        	ArrayList<String> selectedCells = new ArrayList<String>();          
        	selectedCellsString = "";
        	List<RubricGrade> rubricGradeList = this.getRubricGradesBySubmission(submissionId);
        	if (rubricGradeList != null){
        		for (RubricGrade grd : rubricGradeList) {
        			if(grd.getPointsEarned()!=0)
        				selectedCells.add(""+grd.getRubricCellId());
        			else if(grd.getComment()!=null)
        				selectedCells.add(""+grd.getRubricCellId()+"="+grd.getComment());
        		}
        	}
        	for(Object cellId : selectedCells){
        		selectedCellsString+=cellId+"||";
        	}
        }
    	return selectedCellsString;
    }

	@Override
	public String getCommentsCells(Long rubricId) {
		String val = "";
		if(this.gCommentsCells.containsKey(rubricId)) {
			val = this.gCommentsCells.get(rubricId);
		}
		return val;
	}
	
	/* UTILS END */
	
}