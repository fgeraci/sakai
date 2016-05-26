package org.sakaiproject.rubrics.api.rubric;

import java.util.List;
import java.util.Set;

public interface RURubricLogic {
	public List<Rubric> getPredefinedRubrics(final String pUserId);
	public Rubric getRubricByItemId(Long itemId, String toolId);
	public void saveRubric(Rubric rubric) throws Exception;
	public void saveRubricForItemId(Long pItemId, String pToolId, Long pRubricId);
	public Rubric getRubricById(Long rubricId);
	public void saveRubricGradeSet(Set<RubricGrade> rubricGrade, final String assignSubId) throws Exception;
	public List<RubricGrade> getRubricGradesBySubmission(final String assignSubId);
	public Long getRubricIdByItemId(final Long itemId, final String toolId);
}
