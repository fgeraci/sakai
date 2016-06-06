/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/msgcntr/trunk/messageforums-app/src/java/org/sakaiproject/tool/messageforums/ui/DiscussionForumBean.java $
 * $Id: DiscussionForumBean.java 9227 2006-05-15 15:02:42Z cwen@iupui.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.tool.messageforums.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sakaiproject.api.app.messageforums.Attachment;
import org.sakaiproject.api.app.messageforums.DiscussionForum;
import org.sakaiproject.api.app.messageforums.ui.DiscussionForumManager;
import org.sakaiproject.api.app.messageforums.ui.UIPermissionsManager;

import org.sakaiproject.time.cover.TimeService;
import org.sakaiproject.util.ResourceLoader;

/**
 * @author <a href="mailto:rshastri@iupui.edu">Rashmi Shastri</a>
 */
public class DiscussionForumBean
{
  private static final Logger LOG = LoggerFactory.getLogger(DiscussionForumBean.class);
  private DiscussionForum forum;
  private boolean markForDeletion;
  private boolean markForDuplication;
  private UIPermissionsManager uiPermissionsManager;
  private DiscussionForumManager forumManager;
  private boolean readFullDesciption; 
  private ArrayList contributorsList = new ArrayList();
  private ArrayList accessorList = new ArrayList();
  private String gradeAssign;
  private Boolean nonePermission = null;
  
  private Boolean newTopic = null;
  private Boolean changeSettings = null;
  private ArrayList decoAttachList = null;
  private Boolean hasExtendedDescription = null;
  private String locked;
  private String hide;
  private String timedLock;
  
  private SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  
  private static final String MESSAGECENTER_BUNDLE = "org.sakaiproject.api.app.messagecenter.bundle.Messages";
  private static final ResourceLoader rb = new ResourceLoader(MESSAGECENTER_BUNDLE);    
   
  private String postFirst = null;
   
  /**
   * List of decorated topics
   */
  private List topics = new ArrayList();

  public DiscussionForumBean(DiscussionForum forum, UIPermissionsManager uiPermissionsManager, DiscussionForumManager forumManager)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("DiscussionForumBean(DiscussionForum "+forum+", UIPermissionsManager"  +uiPermissionsManager+ ")");
    }
    this.forum = forum;
    this.uiPermissionsManager=uiPermissionsManager;
    this.forumManager=forumManager; 
  }

  /**
   * @return
   */
  public DiscussionForum getForum()
  {
    LOG.debug("getForum()");
    return forum;
  }

  /**
   * @return Returns count of topics in the forum
   */
  public int getTopicCount()
  {
    LOG.debug("getTopics()");
    return (topics == null) ? 0 : topics.size();
  }

  /**
   * @return List of SelectItem
   */
  public List getTopicSelectItems()
  {
     List f = getTopics();
     int num = (f == null) ? 0 : f.size();

     List retSort = new ArrayList();
     for(int i = 1; i <= num; i++) {
        Integer index = Integer.valueOf(i);
        retSort.add(new SelectItem(index, index.toString()));
     }
     
     return retSort;
  }

  /**
   * @return Returns the decorated topic.
   */
  public List getTopics()
  {
    LOG.debug("getTopics()");
    return topics;
  }

  public void addTopic(DiscussionTopicBean decoTopic)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("addTopic(DiscussionTopicBean"+ decoTopic+")");
    }
    if (!topics.contains(decoTopic))
    {
      topics.add(decoTopic);
    }
  }

  /**
   * @return Returns the locked as String.
   */
  public Boolean getLocked()
  {
    return forum.getLocked() == null ? Boolean.FALSE : Boolean.valueOf(forum.getLocked());
  }

  /**
   * @param String locked
   *          The locked to set.
   */
  public void setLocked(String locked)
  {
    forum.setLocked(locked == null ? Boolean.FALSE : Boolean.valueOf(locked));
  }
  
  /**
   * @return Returns the hide as String.
   */
  public Boolean getHideForum()
  {
	  return forum.getHide() == null ? Boolean.FALSE : Boolean.valueOf(forum.getHide());
  }

  /**
   * @param String hide
   *          The hide to set.
   */
  public void setHideForum(String hide)
  {
	  forum.setHide(hide == null ? Boolean.FALSE : Boolean.valueOf(hide));
  }
  
  /**
   * @return Returns the lock as String.
   */
  public Boolean getTimedLock()
  {
	  return forum.getTimedLock() == null ? Boolean.FALSE : forum.getTimedLock();
  }

  /**
   * @param String hide
   *          The locked to set.
   */
  public void setTimedLock(Boolean timedLock)
  {
	  boolean tl = timedLock == null ? Boolean.FALSE : Boolean.valueOf(timedLock);
	  forum.setTimedLock(tl);
  }
  
  /**
   * @return Returns the locked as boolean
   */
  public Boolean getForumLocked()
  {
    return forum.getLocked() == null ? Boolean.FALSE : forum.getLocked();
  }

  /**
   * @param Boolean locked
   *          The locked to set.
   */
  public void setForumLocked(Boolean locked)
  {
    boolean l = (locked == null) ? Boolean.FALSE : Boolean.valueOf(locked);
    forum.setLocked(l);
  }
  
  /**
   * @return Returns the hide as boolean
   */
  public Boolean getForumHide() {
    return forum.getHide() == null ? Boolean.FALSE : forum.getHide();
  }

  /**
   * @param Boolean hide
   *          The hide to set.
   */
  public void setForumHide(Boolean hide) {
	  boolean h = (hide == null) ? Boolean.FALSE : Boolean.valueOf(hide);
	  forum.setHide(h);
  }
  
  private String moderated = null;
  /**
   * Returns whether the forum is moderated or not
   * @return
   */
  public String getModerated()
  {
	  LOG.debug("getModerated()");
	  if (moderated == null){
		  if (forum == null || forum.getModerated() == null || 
			  forum.getModerated().booleanValue() == false)
		  {
			  moderated = Boolean.FALSE.toString();
		  }
		  else
		  {
			  moderated = Boolean.TRUE.toString();
		  }
	  }
	  return moderated;
  }
  
  /**
   * Set the "moderated" setting for the forum
   * @param moderated
   */
  public void setModerated(String moderated)
  {
	  LOG.debug("setModerated()");
	  if (moderated.equals(Boolean.TRUE.toString()))
	  {
		  forum.setModerated(Boolean.valueOf(true));
	  }
	  else
	  {
		  forum.setModerated(Boolean.valueOf(false));
	  }
  }
  
  /**
   * Returns boolean value, whether the forum is moderated or not
   * @return
   */
  public Boolean getForumModerated()
  {
	  LOG.debug("getForumModerated()");
	  if (moderated == null){
		  if (forum == null || forum.getModerated() == null || 
			  forum.getModerated().booleanValue() == false)
		  {
			  moderated = Boolean.FALSE.toString();
		  }
		  else
		  {
			  moderated = Boolean.TRUE.toString();
		  }
	  }
	  return Boolean.parseBoolean(moderated);
  }
  
  /**
   * Set the boolean "moderated" setting for the forum
   * @param moderated
   */
  public void setForumModerated(Boolean moderated)
  {
	  LOG.debug("setForumModerated()");
	  forum.setModerated(moderated);
  }
  
  /**
   * Returns whether the forum is postFirst or not
   * @return
   */
  public String getPostFirst()
  {
	  LOG.debug("getPostFirst()");
	  if (postFirst == null){
		  if (forum == null || forum.getPostFirst() == null || 
			  forum.getPostFirst().booleanValue() == false)
		  {
			  postFirst = Boolean.FALSE.toString();
		  }
		  else
		  {
			  postFirst = Boolean.TRUE.toString();
		  }
	  }
	  return postFirst;
  }
  
  /**
   * Set the "postFirst" setting for the forum
   * @param postFirst
   */
  public void setPostFirst(String postFirst)
  {
	  LOG.debug("setPostFirst()");
	  if (postFirst.equals(Boolean.TRUE.toString()))
	  {
		  forum.setPostFirst(Boolean.valueOf(true));
	  }
	  else
	  {
		  forum.setPostFirst(Boolean.valueOf(false));
	  }
  }
  
  /**
   * Returns a boolean, whether the forum is postFirst or not
   * @return
   */
  public Boolean getForumPostFirst()
  {
	  LOG.debug("getForumPostFirst()");
	  if (postFirst == null){
		  if (forum == null || forum.getPostFirst() == null || 
			  forum.getPostFirst().booleanValue() == false)
		  {
			  postFirst = Boolean.FALSE.toString();
		  }
		  else
		  {
			  postFirst = Boolean.TRUE.toString();
		  }
	  }
	  return Boolean.parseBoolean(postFirst);
  }
  
  /**
   * Set the boolean "postFirst" setting for the forum
   * @param postFirst
   */
  public void setForumPostFirst(Boolean postFirst)
  {
	  LOG.debug("setForumPostFirst()");
	  forum.setPostFirst(postFirst);
  }

  /**
   * Return whether or not the forum automatically marks all posts in a topic as read.
   */
  public String getAutoMarkThreadsRead()
  {
	  LOG.debug("getAutoMarkThreadsRead()");
	  return Boolean.toString(forum.getAutoMarkThreadsRead());
  }
  
  /**
   * Set the autoMarkThreadsRead setting for the forum.
   */
  public void setAutoMarkThreadsRead(String autoMarkThreadsRead)
  {
	  LOG.debug("setAutoMarkThreadsRead()");
	  forum.setAutoMarkThreadsRead(Boolean.parseBoolean(autoMarkThreadsRead));
  }
  
  /**
   * Return boolean, whether or not the forum automatically marks all posts in a topic as read.
   */
  public Boolean getForumAutoMarkThreadsRead()
  {
	  LOG.debug("getForumAutoMarkThreadsRead()");
	  return forum.getAutoMarkThreadsRead();
  }
  
  /**
   * Set the boolean autoMarkThreadsRead setting for the forum.
   */
  public void setForumAutoMarkThreadsRead(Boolean autoMarkThreadsRead)
  {
	  LOG.debug("setForumAutoMarkThreadsRead()");
	  forum.setAutoMarkThreadsRead(autoMarkThreadsRead);
  }

  /**
   * @return Returns the markForDeletion.
   */
  public boolean isMarkForDeletion()
  {
    LOG.debug("isMarkForDeletion()");
    return markForDeletion;
  }

  /**
   * @param markForDeletion
   *          The markForDeletion to set.
   */
  public void setMarkForDeletion(boolean markForDeletion)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setMarkForDeletion(boolean"+ markForDeletion+")");
    }
    this.markForDeletion = markForDeletion;
  }

  /**
   * @return Returns the markForDuplication.
   */
  public boolean isMarkForDuplication()
  {
    LOG.debug("isMarkForDuplication()");
    return markForDuplication;
  }

  /**
   * @param markForDuplication
   *          The markForDuplication to set.
   */
  public void setMarkForDuplication(boolean markForDuplication)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setMarkForDuplication(boolean"+ markForDuplication+")");
    }
    this.markForDuplication = markForDuplication;
  }

  /**
   * @return
   */
  public boolean getChangeSettings()
  {
    LOG.debug("getChangeSettings()");
    if (changeSettings == null){
    	changeSettings = uiPermissionsManager.isChangeSettings(forum);
    }
    return changeSettings.booleanValue();
  }
   
  /**
   * @return
   */
  public boolean isNewTopic()
  {
    LOG.debug("isNewTopic()");
    if (newTopic == null){
    	newTopic = uiPermissionsManager.isNewTopic(forum);
    }
    return newTopic.booleanValue();
  }

  /**
   * @return Returns the if ExtendedDesciption is available
   */
  public boolean getHasExtendedDesciption()
  {
    LOG.debug("getHasExtendedDesciption()");
    if (hasExtendedDescription == null){
	    if (forum.getExtendedDescription() != null
	        && forum.getExtendedDescription().trim().length() > 0
	        && (!readFullDesciption))
	    {
	      hasExtendedDescription = true;
	    }
	    hasExtendedDescription = false;
    }
    return hasExtendedDescription.booleanValue();
  }
  
  /**
   * @return Returns the readFullDesciption.
   */
  public boolean isReadFullDesciption()
  {
    LOG.debug("isReadFullDesciption()");
    return readFullDesciption;
  }

  /**
   * @param readFullDesciption The readFullDesciption to set.
   */
  public void setReadFullDesciption(boolean readFullDesciption)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setReadFullDesciption(boolean" +readFullDesciption+")");
    }
    this.readFullDesciption = readFullDesciption;
  }
  
  /**
   * @return
   */
  public ArrayList getContributorsList()
  {
    LOG.debug("getContributorsList()");
  
    Iterator iter= forumManager.getContributorsList(forum).iterator();
    while (iter.hasNext())
    { 
      contributorsList.add((String)iter.next());
     }
    return contributorsList; 

  }
  
  /**
   * @return
   */
  public ArrayList getAccessorList()
  {
    LOG.debug("getAccessorList()");
    Iterator iter= forumManager.getAccessorsList(forum).iterator();
    while (iter.hasNext())
    { 
      accessorList.add((String)iter.next());
     }
    return accessorList; 
  }

  /**
   * @param accessorList The accessorList to set.
   */
  public void setAccessorList(ArrayList accessorList)
  {
    if(LOG.isDebugEnabled())
     {
        LOG.debug("setAccessorList(List"+ accessorList+")");
     }
     forum.getActorPermissions().setAccessors(forumManager.decodeAccessorsList(accessorList));
  }

  /**
   * @param contributorsList The contributorsList to set.
   */
  public void setContributorsList(ArrayList contributorsList)
  {
    if(LOG.isDebugEnabled())
    {
       LOG.debug("setContributorsList(List"+ contributorsList+")");
    }
    forum.getActorPermissions().setContributors(forumManager.decodeContributorsList(contributorsList));
  }

  /**
   * @param forumManager The forumManager to set.
   */
  public void setForumManager(DiscussionForumManager forumManager)
  {
    if(LOG.isDebugEnabled())
    {
       LOG.debug("setForumManager(DiscussionForumManager"+ forumManager+")");
    }
    this.forumManager = forumManager;
  }

  public String getGradeAssign()
  {
    return gradeAssign;
  }

  public void setGradeAssign(String gradeAssign)
  {
    this.gradeAssign = gradeAssign;
  }
  
	public boolean getNonePermission()
	{
		if (nonePermission == null){
			nonePermission = true;
			if(uiPermissionsManager.isChangeSettings(forum) ||  uiPermissionsManager.isNewTopic(forum))
			{
				nonePermission = false;
				return nonePermission.booleanValue();
			}
			
			if(topics != null)
			{
				for(int i=0; i<topics.size(); i++)
				{
					DiscussionTopicBean dtb = (DiscussionTopicBean) topics.get(i);
					if(!dtb.getNonePermission())
					{
						nonePermission = false;
						break;
					}
				}
			}
		}
		return nonePermission.booleanValue();
	}

	public void setNonePermission(boolean nonePermission)
	{
		this.nonePermission = nonePermission;
	}
	
	public ArrayList getAttachList()
	{
		if (decoAttachList == null){
			decoAttachList = new ArrayList();
			List attachList = forum.getAttachments(); 
			if(attachList != null)
			{
				for(int i=0; i<attachList.size(); i++)
				{
					DecoratedAttachment decoAttach = new DecoratedAttachment((Attachment)attachList.get(i));
					decoAttachList.add(decoAttach);
				}
			}
		}
		return decoAttachList;
	}
	
	public String getAvailabilityRestricted()
	  {
		  LOG.debug("getAvailabilityRestricted()");
		  if (forum == null || forum.getAvailabilityRestricted() == null || 
				  forum.getAvailabilityRestricted().booleanValue() == false)
		  {
			  return Boolean.FALSE.toString();
		  }

		  return Boolean.TRUE.toString();
	  }
	  
	  /**
	   * Set the "availabilityRestricted" setting for the forum
	   * @param restricted
	   */
	  public void setAvailabilityRestricted(String restricted)
	  {
		  LOG.debug("setAvailabilityRestricted()");
		  if (restricted.equals(Boolean.TRUE.toString()))
		  {
			  forum.setAvailabilityRestricted(Boolean.valueOf(true));
		  }
		  else
		  {
			  forum.setAvailabilityRestricted(Boolean.valueOf(false));
		  }
	  }
	
	public String getAvailability()
	{
		LOG.debug("getAvailability()");
		if (forum == null || forum.getAvailability() == null || 
				forum.getAvailability().booleanValue() == false)
		{
			return Boolean.FALSE.toString();
		}

		return Boolean.TRUE.toString();
	}

	/**
	 * Set the "Availability" setting for the forum
	 * @param restricted
	 */
	public void setAvailability(String restricted)
	{
		LOG.debug("setAvailability()");
		if (restricted.equals(Boolean.TRUE.toString()))
		{
			forum.setAvailability(Boolean.valueOf(true));
		}
		else
		{
			forum.setAvailability(Boolean.valueOf(false));
		}
	}

	public String getOpenDate(){
		if(forum == null || forum.getOpenDate() == null){
			return "";
		}else{
			StringBuilder dateTimeOpenDate = new StringBuilder( datetimeFormat.format( forum.getOpenDate() ) );			
			return dateTimeOpenDate.toString();
		}
	}	  
	
	public void setOpenDate(String openDateStr){
		if (StringUtils.isNotBlank(openDateStr)) {
			try{
				String hiddenOpenDate = (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("openDateISO8601");
				Date openDate = (Date) datetimeFormat.parse(hiddenOpenDate);
				forum.setOpenDate(openDate);
			}catch (ParseException e) {
				LOG.error("Couldn't convert open date", e);
			}
		}else{
			forum.setOpenDate(null);
		}
	}
	
	public String getUnlockDate(){
		if(forum == null || forum.getUnlockDate() == null){
			return "";
		}else{
			StringBuilder dateTimeUnlockDate = new StringBuilder( datetimeFormat.format( forum.getUnlockDate() ) );			
			return String.valueOf(forum.getUnlockDate());
		}
	}
	
	public void setUnlockDate(String unlockDateStr){
		if (StringUtils.isNotBlank(unlockDateStr)) {
			try{
				String hiddenUnlockDate = (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("unlockDateISO8601");
				Date unlockDate = (Date) datetimeFormat.parse(hiddenUnlockDate);
				forum.setUnlockDate(unlockDate);
			}catch (ParseException e) {
				LOG.error("Couldn't convert open date", e);
			}
		}else{
			forum.setUnlockDate(null);
		}
	}
	
	public String getLockDate(){
		if(forum == null || forum.getLockDate() == null){
			return "";
		}else{
			StringBuilder dateTimeLockDate = new StringBuilder( datetimeFormat.format( forum.getLockDate() ) );			
			return String.valueOf(forum.getLockDate());
		}
	}
	
	public void setLockDate(String lockDateStr){
		if (StringUtils.isNotBlank(lockDateStr)) {
			try{
				String hiddenLockDate = (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("lockDateISO8601");
				Date lockDate = (Date) datetimeFormat.parse(hiddenLockDate);
				forum.setLockDate(lockDate);
			}catch (ParseException e) {
				LOG.error("Couldn't convert open date", e);
			}
		}else{
			forum.setLockDate(null);
		}
	}

	public String getCloseDate(){
		if(forum == null || forum.getCloseDate() == null){
			return "";
		}else{
			StringBuilder dateTimeCloseDate = new StringBuilder( datetimeFormat.format( forum.getCloseDate() ) );
			return dateTimeCloseDate.toString();
		}
	}	  

	public void setCloseDate(String closeDateStr){
		if (StringUtils.isNotBlank(closeDateStr)) {
			try{
				String hiddenCloseDate = (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("closeDateISO8601");
				Date CloseDate = (Date) datetimeFormat.parse(hiddenCloseDate);
				forum.setCloseDate(CloseDate);
			}catch (ParseException e) {
				LOG.error("Couldn't convert Close date", e);
			}
		}else{
			forum.setCloseDate(null);
		}
	}
	
	public String getFormattedCloseDate(){
		if(forum == null || forum.getCloseDate() == null){
			return "";
		}else{
			SimpleDateFormat formatter_date = new SimpleDateFormat(rb.getString("date_format"), new ResourceLoader().getLocale());
			formatter_date.setTimeZone(TimeService.getLocalTimeZone());
			String formattedCloseDate = formatter_date.format(forum.getCloseDate());
			return formattedCloseDate;
		}
	}	

	public String getFormattedOpenDate(){
		if(forum == null || forum.getOpenDate() == null){
			return "";
		}else{
			SimpleDateFormat formatter_date = new SimpleDateFormat(rb.getString("date_format"), new ResourceLoader().getLocale());
			formatter_date.setTimeZone(TimeService.getLocalTimeZone());
			String formattedOpenDate = formatter_date.format(forum.getOpenDate());
			return formattedOpenDate;
		}
	}
}
