package org.sakaiproject.rubrics.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.sakaiproject.rubrics.api.rubric.Rubric;
import org.sakaiproject.rubrics.api.rubric.RubricRow;
import org.sakaiproject.rubrics.api.rubric.RubricCell;
import org.sakaiproject.rubrics.api.rubric.RubricGrade;
import org.sakaiproject.rubrics.api.rubric.RubricMappedItem;
import org.sakaiproject.genericdao.api.search.Search;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.sakaiproject.genericdao.hibernate.HibernateCompleteGenericDao;

public class RURubricDaoImpl extends HibernateCompleteGenericDao implements RURubricDao {
	
	private static Log log = LogFactory.getLog(RURubricDaoImpl.class);
	
	public void init() {
		log.warn("RURubricDaoImpl intialized for Rubrics");
		 if (log.isDebugEnabled()) log.debug("init");
	}
	
	@Override
	public List<Rubric> getPredefinedRubrics(final String pUserId) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException ,SQLException {
					//Set<RURubric> rubricSet = new HashSet<RURubric>();
					Query query = session.getNamedQuery("getPredefinedRubrics");
					query.setParameter("userId", pUserId == null ? "" : pUserId);
					List<Rubric> rubricList = query.list();
		                // we need to remove duplicates but retain order, so put
		                // in a LinkedHashSet and then back into a list
		                if (rubricList != null) {
		                    Set<Rubric> rubricSet = new LinkedHashSet<Rubric>(rubricList);
		                    rubricList.clear();
		                    rubricList.addAll(rubricSet);
		                }

					return rubricList;
			}
		};
		return (List<Rubric>)getHibernateTemplate().execute(hc);
	}
	
	@Override
	public Rubric getRubricByItemId(final Long itemId, final String toolId) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException ,SQLException {
				Set<Rubric> rubricSet = new HashSet<Rubric>();
				Query query = session.getNamedQuery("getRubricByItemId");
				query.setParameter("itemId", itemId);
				query.setParameter("toolId", toolId);
				Rubric rubric = (Rubric) query.uniqueResult();
				return rubric;
			}
		};
		return (Rubric)getHibernateTemplate().execute(hc);
	}
	
	@Override
	public Rubric getRubricById(final Long rubricId) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException ,SQLException {
					Set<Rubric> rubricSet = new HashSet<Rubric>();
					Query query = session.getNamedQuery("getRubricsById");
					query.setParameter("rubricId", rubricId);
					Rubric rubric = (Rubric) query.uniqueResult();
					return rubric;
			}
		};
		return (Rubric)getHibernateTemplate().execute(hc);
		
	}
	
	@Override
	public List<RubricGrade> getRubricGradesBySubmission(final String assignSubId) {
		
			HibernateCallback hc = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException ,SQLException {
						Set<RubricGrade> rubricGrdSet = new HashSet<RubricGrade>();
						Query query = session.getNamedQuery("getGradedByRubricsSubmission");
						query.setParameter("assignmentSubmissionId", assignSubId);
						List<RubricGrade> rubricGrdList = query.list();
						if(rubricGrdList != null) {
							rubricGrdSet = new LinkedHashSet<RubricGrade>(rubricGrdList);
							rubricGrdList = new ArrayList<RubricGrade>(rubricGrdSet);
						}
						return rubricGrdList;
				}
			};
			return (List<RubricGrade>)getHibernateTemplate().execute(hc);
		}

	@Override
	public Long getRubricIdByItemId(final Long itemId, final String toolId) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException ,SQLException {
				Set<Rubric> rubricSet = new HashSet<Rubric>();
				Query query = session.getNamedQuery("getRubricIdByItemId");
				query.setParameter("itemId", itemId);
				query.setParameter("toolId", toolId);
				Long rubricId = (Long) query.uniqueResult();
				return rubricId;
			}
		};
		return (Long)getHibernateTemplate().execute(hc);
	}

	@Override
	public RubricMappedItem getMappedItemById(final Long itemId, final String toolId) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException ,SQLException {
				Set<Rubric> rubricSet = new HashSet<Rubric>();
				Query query = session.getNamedQuery("getMappedItemById");
				query.setParameter("itemId", itemId);
				query.setParameter("toolId", toolId);
				RubricMappedItem map = (RubricMappedItem) query.uniqueResult();
				return map;
			}
		};
		return (RubricMappedItem)getHibernateTemplate().execute(hc);
	}
	
	public void updateMappedItem(RubricMappedItem map) {
		getHibernateTemplate().update(map);
	}
	
	public void saveMappedItem(RubricMappedItem map) {
		getHibernateTemplate().save(map);
	}
}
