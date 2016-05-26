package org.sakaiproject.rubrics.impl;

import java.util.List;

import org.sakaiproject.rubrics.api.rubric.Rubric;
import org.sakaiproject.rubrics.api.rubric.RubricGrade;
import org.sakaiproject.rubrics.api.rubric.RubricMappedItem;
import org.sakaiproject.genericdao.api.GeneralGenericDao;

public interface RURubricDao extends GeneralGenericDao  {
		
		public List<Rubric> getPredefinedRubrics(final String pUserId);
		
		public Rubric getRubricByItemId(final Long itemId, final String toolId);
		
		public Rubric getRubricById(final Long rubricId);
		
		public List<RubricGrade> getRubricGradesBySubmission(final String assignSubId);
		
		public Long getRubricIdByItemId(final Long itemId, final String toolId);
		
		public RubricMappedItem getMappedItemById(Long itemId, String toolId);
		
		public void updateMappedItem(RubricMappedItem map);
		
		public void saveMappedItem(RubricMappedItem map);
}
