package org.sakaiproject.rubrics.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.rubrics.impl.RURubricDaoImpl;
import org.sakaiproject.rubrics.api.rubric.ExternalLogic;
import org.sakaiproject.rubrics.api.rubric.Rubric;
import org.sakaiproject.rubrics.api.rubric.RubricCell;
import org.sakaiproject.rubrics.api.rubric.RubricGrade;
import org.sakaiproject.rubrics.api.rubric.RubricMappedItem;
import org.sakaiproject.rubrics.api.rubric.RURubricLogic;
import org.sakaiproject.rubrics.api.rubric.RubricRow;

import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;

public class RURubricLogicImpl implements RURubricLogic {
	
	private static Log log = LogFactory.getLog(RURubricLogicImpl.class);
	
	private ExternalLogic externalLogic;
    public void setExternalLogic(ExternalLogic externalLogic) {
    	log.warn("ExternalLogic Injected into RULubricLogic");
        this.externalLogic = externalLogic;
    }
	
	private RURubricDao rubricDao;
	public void setRubricDao(RURubricDao rubricDao){
		log.warn("RURubricDAO Injected into RULubricLogic");
		this.rubricDao = rubricDao;
	}
	
	public void init() {
		log.warn("RURubricLogicImpl initialized for Rubrics");
		 if (log.isDebugEnabled()) log.debug("init");
	}
	
	@Override
	public void saveRubric(Rubric rubric) throws Exception {
		
        Set<RubricRow> rowsToAdd = new HashSet();
		Set<RubricRow> rowsToDelete = new HashSet();
		Set<RubricCell> cellsToAdd= new HashSet();
		Set<RubricCell> cellsToDelete= new HashSet();

		String currentUserId = externalLogic.getCurrentUserId();
		
		boolean isNewRubric = true;
		Rubric existingRubric = null;
		Set<RubricCell> updateCells = new HashSet<RubricCell>();
		//System.out.print("\n** need to save rubric \n");
		printRubric(rubric);
		
		//determine if this is a new rubric
		if(rubric.getRubricId() != null) {
			existingRubric = getRubricById(rubric.getRubricId());
			if(existingRubric != null) {
				isNewRubric = false;
				//System.out.print("\n** this is an existing rubric \n");
				printRubric(existingRubric);	
			} else  {
				//throw new RubricNotFoundException(" No rubric exists with id: " + rubric.getRubricId() + " Rubric update failed");
				log.error("No rubric exists with id: " + rubric.getRubricId() + " Rubric update failed.");
			}
		}
		else
		{
			rubric.setCreatedBy(currentUserId);
			rubric.setCreatedDate(new Date());
		}

		if (!isNewRubric) {
			rubric.setModifiedBy(currentUserId);
			rubric.setModifiedDate(new Date());
			
			Set<RubricRow> rows = identDiffItemInFirstInput(existingRubric == null ? null : existingRubric.getRubricRowSet(), 
					rubric == null ? null : rubric.getRubricRowSet());
			if (rows != null)
				rowsToDelete.addAll(rows);
			if(rowsToDelete.size() > 0){
				for (RubricRow row: rowsToDelete) {
					Set<RubricCell> cells = row.getCellSet();
					cellsToDelete.addAll(cells);
				}	
			}		
			Set<RubricCell> cells = identifySameRowDifferentColumnInFirstInput(existingRubric == null ? null : existingRubric.getRubricRowSet(), 
					rubric == null ? null : rubric.getRubricRowSet(), true);
			if(cells != null && cells.size() > 0) {
				cellsToDelete.addAll(cells);
			}
			updateCells = identifySameRowColumnDiffCellTextInFirstInput(existingRubric == null ? null : existingRubric.getRubricRowSet(), 
					rubric == null ? null : rubric.getRubricRowSet());
		}
		Set<RubricRow> rows = identDiffItemInFirstInput(
					rubric == null ? null : rubric.getRubricRowSet(),
					existingRubric == null ? null : existingRubric.getRubricRowSet()
					);
		if(rows != null){
			rowsToAdd.addAll(rows);
		}
		if(rowsToAdd.size() > 0){
			for (RubricRow row : rowsToAdd) {
				cellsToAdd.addAll(row.getCellSet());
			}
		}
		Set<RubricCell> cells = identifySameRowDifferentColumnInFirstInput(rubric == null ? null : rubric.getRubricRowSet(),
					existingRubric == null ? null : existingRubric.getRubricRowSet(),false);
			
		if(rowsToAdd.size() > 0){
			for(RubricRow row:rowsToAdd){
					//System.out.print("\n add row : " + row.getRowText());
			}
		}
		
		if (cells != null && cells.size() > 0) {
			cellsToAdd.addAll(cells);
		}
	
		if(cellsToAdd.size() > 0) {
			for(RubricCell cell:cellsToAdd){
				//System.out.print("\n add cell : " + cell.getColumnText());
				//System.out.print("\n add cell row is : " + cell.getRow());
			}
		}
			// make sure the rubric has been set for the row and cell
			populateRubricForRowAndCellSets(rowsToAdd, cellsToAdd, rubric);
			
			
		try {
			if(rubric.getCreatedBy() == null){
				rubric.setCreatedBy(currentUserId);
			}
			Set<Rubric> rubricSet = new HashSet<Rubric>();
			rubricSet.add(rubric);
			List<Set> setsToSave = new ArrayList<Set>();
			
			if(rowsToAdd.size() > 0) {
				setsToSave.add(rowsToAdd);
			}
			
			if(cellsToAdd.size() > 0) {
				setsToSave.add(cellsToAdd);
			}
		
			if (setsToSave.size() >0 ) {
				Set[] toSave = new Set[setsToSave.size() + 1];
				toSave[0] = rubricSet;
				for (int i = 1; i <=setsToSave.size(); i++) {
					toSave[i] = setsToSave.get(i-1);
				}
				//System.out.print("\n call saveMixed\n");
				rubricDao.saveMixedSet(toSave);
				
			}else {
				rubricDao.save(rubric);
			
			}
			
			if (!isNewRubric) {
				if (rowsToDelete.size() > 0 ||
				cellsToDelete.size() > 0) {
					for(RubricRow row : rowsToDelete) {
						row.setRemoved(true);
						rubricDao.update(row);
					}
					rubricDao.deleteSet(cellsToDelete);	
				}
				if(updateCells != null && updateCells.size() > 0){
					for(RubricCell cell : updateCells){
					    // System.out.print(" \n\ncell update : " + cell.getCellText()  +""+cell.getColumnText());
						rubricDao.update(cell);
						
					}
						
				}
			}
	
		} catch (HibernateOptimisticLockingFailureException holfe ) {
			 if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while attempting to update assignment with id: " + rubric.getRubricId());
	            throw new Exception("An optimistic locking failure occurred while attempting to update assignment with id: " + rubric.getRubricId(), holfe);
	  
		}
	
	}
	
	@Override
	public List<Rubric> getPredefinedRubrics(final String pUserId) {
		List<Rubric> resultList = rubricDao.getPredefinedRubrics(pUserId);
		
		return resultList;
	}
	private void printRubric(Rubric existingRubric){
		if(existingRubric == null) {
			
			return;
		}
		Set<RubricRow> eRows = existingRubric.getRubricRowSet();
		if(eRows != null) {
			for(RubricRow row: eRows) {
				
				Set<RubricCell> cells = row.getCellSet();
				for(RubricCell cell:cells){
					
				}
			}
		}
	}
	private Set<RubricCell> getAllCells(Set<RubricRow> rows) {
		Set<RubricCell> cells = new HashSet();
		
		for(RubricRow row : rows) {
			cells.addAll(row.getCellSet());
		}
		return cells;
		
	}
	
	private Set<RubricRow> identDiffItemInFirstInput(Set<RubricRow> first, Set<RubricRow> second) {
		Set<RubricRow> diff = new HashSet();
		
		if(first == null) {
			return null;
		}
		if(second == null) {
			return first;
		}
		/*
		for(RURubricRow row : first){
			//System.out.print("\nrow in first:  " + row.getRowText());
			Set<RURubricCell> cells = row.getCellSet();
			for(RURubricCell cell:cells){
				//System.out.print("\n cell: "+ cell.getColumnText() + "\n");
			}
		}
		for(RURubricRow row : second) {
			//System.out.print("\nsecond : " + row.getRowText());
			Set<RURubricCell> cells = row.getCellSet();
			//System.out.print("\n cells on this row is: \n");
			for(RURubricCell cell:cells){
				//System.out.print("\n cell: "+ cell.getColumnText() + "\n");
			}
		}
		*/
		int i = 0;
		boolean found = false;
		RubricRow re = null;
		Iterator iter = first.iterator();
		    while (iter.hasNext()) {
		      re = (RubricRow)iter.next();
		      String reText = re.getRowText();

		      int reSeq = re.getSequence();
		      //System.out.print("\n reText : " + reText + " \n");

		      //System.out.print("\n reseq : " + reSeq + " \n");
		      Iterator iter1 = second.iterator();
		      i = 0;
		      found = false;
		      while (iter1.hasNext()) {
		    	  i++;
		    	 RubricRow ru = (RubricRow)iter1.next();
		    	  String ruText = ru.getRowText();
		    	  int ruSeq = ru.getSequence();
		    	  //System.out.print("\n rutext : " + ruText + "\n");
		    	  //System.out.print("\n ruSeq : " + ruSeq + "\n");
		    	  if(ruText != null && ruText.equals(reText) && reSeq == ruSeq) {
		    		  //System.out.print("\n ruText : " + ruText + "\n");
		    		 //System.out.print("\nfound the same row in second " + ruText + "\n" );
		    			  found = true;
		    			  break;
		    		  }
		    	 }
		      
		      	if((i == second.size()) && !found) {
		    	 //System.out.print("add one item to diff" + re.getRowText() + "\n");
		      		diff.add(re);
		      	}
		    }
		    
		    //System.out.print("diff size is : " + diff.size() + "\n");
		    if(diff.size() > 0){
		    	
		    	return diff; 
		    }else {
		    	return null;
		    }
	}
	private Set<RubricCell> identifySameRowColumnDiffCellTextInFirstInput(Set<RubricRow> first, Set<RubricRow> second){
		Set<RubricCell> diff = new HashSet();
		
		if(first == null || second == null) {
			return null;
		}
		RubricRow re = null;
		Iterator iter = first.iterator();
			while(iter.hasNext()){
				re = (RubricRow)iter.next();
				String reText = re.getRowText();
				int reSeq = re.getSequence();
				Iterator iter1 = second.iterator();
				while(iter1.hasNext()) {
					RubricRow ru = (RubricRow)iter1.next();
					String ruText = ru.getRowText();
					int ruSeq = ru.getSequence();
					if (reText != null && reText.equals(ruText) && reSeq == ruSeq) {
						//found the same row, get the cellSet of that row
						Set<RubricCell> firstCellSet = re.getCellSet();
						Iterator cellIter1 = firstCellSet.iterator();
			
						while(cellIter1.hasNext()){
							
							RubricCell ce = (RubricCell)cellIter1.next();
							Set<RubricCell> secondCellSet = ru.getCellSet();
							Iterator cellIter2 = secondCellSet.iterator();
							while(cellIter2.hasNext()){
								RubricCell cu = (RubricCell)cellIter2.next();
								if(ce.getColumnText() != null && ce.getColumnText().equals(cu.getColumnText())){
									if(ce.getCellText() != null && ! ce.getCellText().equals(cu.getCellText())){
										ce.setCellText(cu.getCellText());
										diff.add(ce);
									}
								}
							}
							
						}
					}
				}
			}
		if(diff.size() > 0)	
			return diff;
		else
			return null;
	}
	
	private Set<RubricCell> identifySameRowDifferentColumnInFirstInput(Set<RubricRow> first, Set<RubricRow> second, boolean forDelete) {
		Set<RubricCell> diff = new HashSet();
		
		if(first == null || second == null){
			return null;
		}
		int i = 0;
		RubricRow re = null;
		Iterator iter = first.iterator();
		    while (iter.hasNext()) {
		      re = (RubricRow)iter.next();
		      String reText = re.getRowText();
		      int reSeq = re.getSequence();
		      Iterator iter1 = second.iterator();
		      while (iter1.hasNext()) {
		    	 RubricRow ru = (RubricRow)iter1.next();
		    	  String ruText = ru.getRowText();
		    	  int ruSeq = ru.getSequence();
		    	  if(ruText != null && ruText.equals(reText) && reSeq == ruSeq) {
		    		  //find the same row, then compare the columns
		    		  Set<RubricCell> diffCell = diffcolumnsInFirst(re, ru);
		    		  if(diffCell.size() > 0 ){
		    		  	  //System.out.print("\nadd one cell to the cellset\n");
		    		  	  diff.addAll(diffCell);
		    		  }
		    		 break;
		    	  }  
		      }
		    }
		    
		return diff;
	}
	
	private Set<RubricCell> diffcolumnsInFirst(RubricRow first, RubricRow second){
		Set<RubricCell> diff = new HashSet();
		int i = 0;
		boolean found = false;
		RubricCell ce = null;
		Iterator iter = first.getCellSet().iterator();
		while (iter.hasNext()) {
		     ce = (RubricCell)iter.next();
		     String reText = ce.getColumnText();
		     Iterator iter1 = second.getCellSet().iterator();
		     i = 0 ;
		     while (iter1.hasNext()) {
		    	found = false;
		    	i++;
		    	RubricCell cu = (RubricCell)iter1.next();
		    	 String ruText = cu.getColumnText();
		    	 if(ruText != null && ruText.equals(reText)) {
		    			 found = true;
		    			 break;
		    	 }
		      }
		      if(i == second.getCellSet().size() && !found) {
		    	  /*
		    	  //System.out.print("in diffcolumnInFirst: the row id is " + ce.getRow().getRowId()+ "\n");
		    	  //System.out.print("\n ce.getColumnText()"+ "\n");
		    	  //System.out.print("\n second.getRowId() " + second.getRowId() + "\n");
		    	  */
		    	  ce.setRow(second);
		    	  diff.add(ce);
		      }
		    }
		    return diff; 
		
	}
	private void populateRubricForRowAndCellSets(Set<RubricRow> rowsAdd, Set<RubricCell> cellsAdd, Rubric rubric) {
		if (rowsAdd != null){
			//System.out.print("\n\n in populate, rubric id "+ rubric.getRubricId() + "\n");
			for(RubricRow row : rowsAdd) {
			row.setRubric(rubric);
			}
		}
		if(cellsAdd != null) {
			for(RubricCell cell: cellsAdd) {
				cell.setRubric(rubric);
			}
		}
	}
	
	@Override
	public Rubric getRubricById(Long rubricId){
		if (rubricId != null){
			Long id = new Long(rubricId);
			Rubric rubric = rubricDao.getRubricById(id);
			if(rubric == null)
				return null;
			Set<RubricRow> newRows = new LinkedHashSet<RubricRow>();
			Set<RubricRow> rows = rubric.getRubricRowSet();
			for(RubricRow row : rows){
				if (!row.getRemoved()){
					newRows.add(row);
				}
			}
			rubric.setRubricRowSet(newRows);
			return rubric;
		}else {
			return null;
		}
	}
	@Override
	public void saveRubricGradeSet(Set<RubricGrade> gradeSet, final String assignSubId) throws Exception {
		
		if(gradeSet != null){
			List<RubricGrade> grdList = rubricDao.getRubricGradesBySubmission(assignSubId);
			if (grdList != null && ! grdList.isEmpty()){
				for(RubricGrade grd:grdList){
					for(RubricGrade newGrd :gradeSet){
						if(newGrd.getRubricCellId().longValue() == grd.getRubricCellId().longValue()){
							grd.setPointsEarned(newGrd.getPointsEarned());
							if(newGrd.getComment() != null && grd.getComment() != null && newGrd.getComment().trim() != null && 
									!newGrd.getComment().trim().equals(grd.getComment().trim())){
								grd.setComment(newGrd.getComment().trim());
							}
						}
					}
					
					try{
						rubricDao.update(grd);
					}catch (HibernateOptimisticLockingFailureException holfe ) {
						if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while saving grade set " );
						throw new Exception("An optimistic locking failure occurred while saving grade set ", holfe);
					}
				}
			} else {
				try {
					rubricDao.saveSet(gradeSet);
				}catch (HibernateOptimisticLockingFailureException holfe ) {
					if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while saving grade set " );
					throw new Exception("An optimistic locking failure occurred while saving grade set ", holfe);
				}
			}
		}
	}
	@Override
	public List<RubricGrade> getRubricGradesBySubmission(final String assignSubId) {
		
		List<RubricGrade> rubricGrdList = null; 
		if(assignSubId != null && assignSubId.length() > 0) {
			 rubricGrdList = rubricDao.getRubricGradesBySubmission(assignSubId);
		}
		return rubricGrdList;
	}

	@Override
	public Rubric getRubricByItemId(Long itemId, String toolName) {
		Rubric rubric = null;
		if(itemId != null && toolName != null) {
			rubric = rubricDao.getRubricByItemId(itemId,toolName);
		}
		return rubric;
	}

	@Override
	public void saveRubricForItemId(Long pItemId, String pToolId, Long pRubricId) {
		Rubric rubric = null;
		try {
			if(pToolId == null || pItemId == null) throw new Exception("Tool or Item identifiers missing");
			rubric = this.getRubricById(pRubricId);
			if(rubric == null) throw new Exception("Rubric doesn not exist");
			RubricMappedItem map = rubricDao.getMappedItemById(pItemId, pToolId);
			if(map == null) { 
				map = new RubricMappedItem(pItemId, pToolId);
				map.setRubricId(pRubricId);
				rubricDao.saveMappedItem(map);
			} else {
				map.setRubricId(pRubricId); 
				rubricDao.updateMappedItem(map);
			}
		} catch (Exception e) {
			log.warn("Failed to associate rubric: "+pRubricId+" with item: "+pItemId+" from tool: "+pToolId+" with msg: " + e.getMessage());
		}
		
	}

	@Override
	public Long getRubricIdByItemId(Long itemId, String toolId) {
		Long rubricId = null;
		if(itemId != null && toolId != null) {
			rubricId = rubricDao.getRubricIdByItemId(itemId,toolId);
		}
		return rubricId;
	}

}
