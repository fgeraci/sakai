package org.sakaiproject.rubrics.api.cover;

import java.util.List;
import java.util.Set;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.rubrics.api.rubric.Rubric;
import org.sakaiproject.rubrics.api.rubric.RubricGrade;

public class RubricsService {
	
	private static org.sakaiproject.rubrics.api.rubric.RubricsService mInstance;
	
	public static org.sakaiproject.rubrics.api.rubric.RubricsService getInstance() {
		if(RubricsService.mInstance == null) {
			RubricsService.mInstance = (org.sakaiproject.rubrics.api.rubric.RubricsService) 
					ComponentManager.get("org.sakaiproject.rubrics.api.rubric.RubricsService");
		}
		return RubricsService.mInstance;
	}
	
	protected static void setInstance(org.sakaiproject.rubrics.api.rubric.RubricsService service) {
		RubricsService.mInstance = service;
	}
	
	public static String getMessage(String pKey) {
		return RubricsService.mInstance.getMessage(pKey);
	}
	public static List<Rubric> getPredefinedRubrics(final String pUserId) {
		return RubricsService.mInstance.getPredefinedRubrics(pUserId);
	}
	public static void saveRubric(Rubric pRubric) throws Exception{
		RubricsService.mInstance.saveRubric(pRubric);
	}
	public static Rubric getRubricById(Long pId){
		return RubricsService.mInstance.getRubricById(pId);
	}
	public static void saveRubricGradeSet(Set<RubricGrade> rubricGradeSet, String submissionId) throws Exception{
		RubricsService.mInstance.saveRubricGradeSet(rubricGradeSet, submissionId);
	}
	public static List<RubricGrade> getRubricGradesBySubmission(String submissionId){
		return RubricsService.mInstance.getRubricGradesBySubmission(submissionId);
	}
	public static String getPredefinedRubricsString(final String pUserId){
		return RubricsService.mInstance.getPredefinedRubricsString(pUserId);
	}
	public static String buildRubricHTMLTable(Rubric rubric, boolean pWithFeedback, double pPoints){
		return RubricsService.mInstance.buildRubricHTMLTable(rubric, pWithFeedback, pPoints);
	}
	public static void processRubricGradingData(String pRubricData, long pRubricId,long pId) throws Exception{
		RubricsService.mInstance.processRubricGradingData(pRubricData, pRubricId, pId);
	}
	public static void buildRubricFromData(Rubric rubric, String pData){
		RubricsService.mInstance.buildRubricFromData(rubric, pData);
	}
	public static void processRubric(Long rubricId, Rubric pRubric, String pData){
		RubricsService.mInstance.processRubric(rubricId, pRubric, pData);
	}
	
	public static String getRubricGradingDataString(String submissionId) {
		return RubricsService.mInstance.getRubricGradingDataString(submissionId);
	}

}
