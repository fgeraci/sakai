package org.sakaiproject.rubrics.api.rubric;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public interface RubricsService {
	
	/* Rubric Constants */
	static final int NO_RUBRIC_ATTACHED = 0;
    static final int PREDEFINED_RUBRIC = 1;
    static final int NEW_RUBRIC = 2;
    static final String RUBRIC_DELIMETERS1 = " ;; ";
    static final String RUBRIC_DELIMETERS2 = " == ";
	static final String RUBRIC_COMMENTCELL = "1";
    static final String RUBRIC_NOTCOMMENTCELL = "2";
    static final String RUBRIC_CUSTOMCELL = "3";
    
    /* Context Constants */
    /** Rubric's context constants **/
	static final String RUBRICS_LIST = "rubrics_list";
	static final String ADD_NO_RUBRIC = "select_addNoRubric";
	static final String ADD_NEW_RUBRIC = "select_addNewRubric";
	static final String ADD_PREDEFINED_RUBRIC = "select_addPredefinedRubric";
	static final String INPUT_RUBRIC_ID = "rubric_chosen";
	static final String RUBRIC_DATA = "rubric_data";
	static final String RUBRIC_AVAILABLE = "rubric_available";
	static final String EXISTING_RUBRIC = "existingRubricData";
	static final String RUBRIC_HTML_TABLE = "rubricHTMLTable";
	static final String RUBRIC_TITLE = "rubric_title";
	static final String RUBRIC_COMMENTS_CELL = "rubric_comments_cell";
	static final String RUBRIC_GRADE_DATA="rubricGradeData";
	static final String RUBRIC_GRADING_ROW_TEXT = "% Grade Per Category";
	static final String RUBRIC_ITEM_ID_MAP = "itemByMappedRubric"; // interface - needed in consumer item
	
	static final String PREDEFINED = "predefined";
	static final String NEW = "new";
	
	void init();
	String getMessage(String pKey);
	List<Rubric> getPredefinedRubrics(String pUserId);
	void saveRubric(Rubric pRubric) throws Exception;
	Rubric getRubricById(Long pId);
	void saveRubricGradeSet(Set<RubricGrade> rubricGradeSet, String submissionId) throws Exception;
	List<RubricGrade> getRubricGradesBySubmission(String submissionId);
	String getPredefinedRubricsString(final String pUserId);
	String buildRubricHTMLTable(Rubric rubric, boolean pWithFeedback, double pPoints);
	void processRubricGradingData(String pRubricData, long pRubricId,long pId) throws Exception;
	void buildRubricFromData(Rubric rubric, String pData);
	void processRubric(Long rubricId, Rubric pRubric, String pData);
	Rubric getRubricByItemId(Long itemId, String toolId);
	public void saveRubricForItemId(Long pItemId, String pToolId, Long pRubricId);
	public String getRubricGradingDataString(String submissionId);
	public String getCommentsCells(Long rubricId);
	
	/* Simplifying the interface for consumers */
	
	/**
	 * Internally creates the rubric, saves it and return the new rubric Id.
	 * @param pData
	 * @return rubric id
	 * @throws exception
	 */
	long saveNewRubric(String pData) throws Exception;
	
	/**
	 * Internally calls saveNewRubric, and then saveRubricForItemId to map them.
	 * @param pData
	 * @param pItemId
	 * @param pToolId
	 * @return rubric Id
	 * @throws exception
	 */
	long saveNewRubricWithItem(String pData, Long pItemId, String pToolId) throws Exception;
	
	public Long getRubricIdByItemId(Long itemId, String toolId);
}
