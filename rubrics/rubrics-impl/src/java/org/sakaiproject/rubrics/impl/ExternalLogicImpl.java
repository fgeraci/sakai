/**********************************************************************************
 * $URL: 
 * $Id: ExternalLogicImpl.java 79823 2012-06-01 19:25:12Z wagnermr@iupui.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2007 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.rubrics.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.rubrics.api.rubric.ExternalLogic;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.util.FormattedText;

/**
 * This is the implementation for logic which is external to our app logic
 */
public class ExternalLogicImpl implements org.sakaiproject.rubrics.api.rubric.ExternalLogic {

    private static Log log = LogFactory.getLog(ExternalLogicImpl.class);

    /**
     * Encoding method to use when URL encoding
     */
    public static final String URL_ENCODING = "UTF-8";

    private ToolManager toolManager;
    public void setToolManager(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    private SessionManager sessionManager;
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    private SiteService siteService;
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    private UserDirectoryService userDirectoryService;
    public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }
    
    private ServerConfigurationService serverConfigurationService;
    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }
    
    private TimeService timeService;
    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    /**
     * Place any code that should run when this class is initialized by spring here
     */
    public void init() {
    	log.warn("ExternalLogicImpl bean initialized for Rubrics standalone");
        if (log.isDebugEnabled()) log.debug("init");
    }

    public String getCurrentContextId() {
        if (toolManager != null && toolManager.getCurrentPlacement() != null && toolManager.getCurrentPlacement().getContext() != null){
            return toolManager.getCurrentPlacement().getContext();

        } else {
            return null;
        }
    }
    
    public String getCurrentLocationId() {
        try {
           if (toolManager.getCurrentPlacement() == null)
           {
              return NO_LOCATION;
           }
           Site s = siteService.getSite(toolManager.getCurrentPlacement().getContext());
           return s.getReference(); // get the entity reference to the site
        } catch (IdUnusedException e) {
           return NO_LOCATION;
        }
     };

    public Site getSite(String contextId) {
        Site site = null;
        try {
            site = siteService.getSite(contextId);
        } catch (IdUnusedException iue) {
            log.warn("IdUnusedException attempting to find site with id: " + contextId);
        }

        return site;
    }

    public String getSiteTitle(String contextId) {
        String siteTitle = null;
        Site site = getSite(contextId);
        if (site != null) {
            siteTitle = site.getTitle();
        }

        return siteTitle;
    }

    public String getCurrentUserId() {
        return sessionManager.getCurrentSessionUserId();
    }

    public String getUserDisplayName(String userId) {
        try {
            User user = userDirectoryService.getUser(userId);
            return user.getDisplayName();
        } catch (UserNotDefinedException ex) {
            log.error("Could not get user from userId: " + userId, ex);
        }

        return "----------";
    }

    public String cleanupUserStrings(String userSubmittedString, boolean cleanupHtml) {
        // clean up the string
        if (userSubmittedString != null && !"".equals(userSubmittedString)) {
            if (cleanupHtml) {
                userSubmittedString = cleanupHtmlText(userSubmittedString);
            }
            
            userSubmittedString = FormattedText.processFormattedText(userSubmittedString, new StringBuilder(), true, false);
        } 

        return userSubmittedString;
    }

    public Collection<Group> getSiteGroups(String contextId) {
        try {
            Site s = siteService.getSite(contextId);
            return s.getGroups();
        } catch (IdUnusedException e){
            log.warn("IdUnusedException attempting to find site with id: " + contextId);
            return new ArrayList<Group>();
        }
    }

    public Collection<Group> getUserMemberships(String userId, String contextId) {
        if (userId == null || contextId == null) {
            throw new IllegalArgumentException("Null userId or contextId passed to getUserMemberships");
        }
        try {
            Site s = siteService.getSite(contextId);
            return s.getGroupsWithMember(userId);
        } catch (IdUnusedException e){
            log.error("IdUnusedException attempting to find site with id: " + contextId);
            return new ArrayList<Group>();
        }
    }

    public List<String> getUserMembershipGroupIdList(String userId, String contextId) {
        if (userId == null || contextId == null) {
            throw new IllegalArgumentException("Null userId or contextId passed to getUserMembershipGroupIdList");
        }
        List<Group> memberships = new ArrayList<Group>(getUserMemberships(userId, contextId));
        List<String> groupIds = new ArrayList<String>();
        if (memberships != null) {
            for (Group group : memberships) {
                if (group != null) {
                    groupIds.add(group.getId());
                }
            }
        }

        return groupIds;
    }

    public Map<String, String> getGroupIdToNameMapForSite(String contextId) {
        if (contextId == null) {
            throw new IllegalArgumentException("Null contextId passed to getGroupIdToNameMapForSite");
        }

        Collection<Group> siteGroups = getSiteGroups(contextId);

        Map<String, String> groupIdToNameMap = new HashMap<String, String>();
        if (siteGroups != null && !siteGroups.isEmpty()) {
            for (Group siteGroup : siteGroups) {
                if (siteGroup != null) {
                    groupIdToNameMap.put(siteGroup.getId(), siteGroup.getTitle());
                }
            }
        }

        return groupIdToNameMap;
    }

    public boolean siteHasTool(String contextId, String toolId) {
        boolean siteHasTool = false;
        try {
            Site currSite = siteService.getSite(contextId);
            if (currSite.getToolForCommonId(toolId) != null) {
                siteHasTool = true;
            }
        } catch (IdUnusedException ide) {
            log.warn("IdUnusedException caught in siteHasTool with contextId: " + contextId + " and toolId: " + toolId);
        }
        return siteHasTool;
    }

    public List<String> getUsersInGroup(String contextId, String groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("null groupId passed to getStudentsInSection");
        }

        List<String> usersInGroup = new ArrayList<String>();
        
        Site site = getSite(contextId);
        if (site == null) {
            log.error("Error retrieving site with contextId:" + contextId);
        } else {
            Group group = site.getGroup(groupId);
            if (group != null) {
                Set<Member> members = group.getMembers();
                if (members != null) {
                    for (Member member : members) {
                        usersInGroup.add(member.getUserId());
                    }
                }
            }
        }

        return usersInGroup;
    }
    
    public String getGraderPermissionsUrl(String contextId) {
        StringBuilder url = new StringBuilder();
        
        Site site = getSite(contextId);
        if (site != null) {
            // we need to retrieve the placement of the gradebook tool
            ToolConfiguration gbToolConfig = site.getToolForCommonId(TOOL_ID_GRADEBOOK);
            if (gbToolConfig != null) {
                String gbPlacement = gbToolConfig.getId();
                
                // now build the url
                url.append(serverConfigurationService.getToolUrl());
                url.append(Entity.SEPARATOR);
                url.append(gbPlacement);
                url.append(Entity.SEPARATOR);
                url.append("sakai.gradebook.permissions.helper/graderRules");
            }
        }
        
        return url.toString();
    }

    public String getUserSortName(String userId) {
        String userSortName = ", ";
        try {
            User user = userDirectoryService.getUser(userId);
            userSortName = user.getSortName();
        } catch (UserNotDefinedException ex) {
            log.error("Could not get user from userId: " + userId, ex);
        }

        return userSortName;
    }

    public String getUserEmail(String userId) {
        String userEmail = null;

        try {
            User user = userDirectoryService.getUser(userId);
            userEmail =  user.getEmail();
        } catch (UserNotDefinedException ex) {
            log.error("Could not get user from userId: " + userId + "Returning null email address.", ex);
        }

        return userEmail;
    }

    public User getUser(String userId)
    {
        User user = null;

        try {
            user = userDirectoryService.getUser(userId);
        } catch (UserNotDefinedException ex) {
            log.error("Could not get user from userId: " + userId, ex);
        }

        return user;
    }

    public Map<String, User> getUserIdUserMap(List<String> userIds) {
        Map<String, User> userIdUserMap = new HashMap<String, User>();
        if (userIds != null) {
            List<User> userList = new ArrayList<User>();
            userList = userDirectoryService.getUsers(userIds);

            if (userList != null) {
                for (User user : userList) {
                    userIdUserMap.put(user.getId(), user);
                }
            }
        }

        return userIdUserMap;
    }

    public Map<String, String> getUserDisplayIdUserIdMapForUsers(Collection<String> userIds) {
        Map<String, String> userDisplayIdUserIdMap = new HashMap<String, String>();

        if (userIds != null) {
            List<User> userList = new ArrayList<User>();
            userList = userDirectoryService.getUsers(userIds);

            if (userList != null) {
                for (User user : userList) {
                    userDisplayIdUserIdMap.put(user.getDisplayId(), user.getId());
                }
            }
        }

        return userDisplayIdUserIdMap;
    }

    public String getMyWorkspaceSiteId(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Null userId passed to getMyWorkspaceSiteId");
        }

        String myWorkspaceId = siteService.getUserSiteId(userId);

        return myWorkspaceId;
    }

    public Map<String, String> getUserIdToSortNameMap(Collection userIds) {      
        Map<String, String> userIdSortNameMap = new HashMap<String, String>();

        if (userIds != null) {
            List<User> users = userDirectoryService.getUsers(userIds);
            if (users != null) {
                for (User user : users) {
                	//TODO Should paren formatting be i18n-ized?
                    userIdSortNameMap.put(user.getId(), user.getSortName() 
                    		+ " (" + user.getEid() + ")");
                }
            }
        }

        return userIdSortNameMap;
    }
    
    public String getServerUrl() {
        return serverConfigurationService.getServerUrl();
    }
    
    public void addToSession(String attribute, Object value) {
        sessionManager.getCurrentSession().setAttribute(attribute, value);
    }
    
    public DateFormat getDateFormat(Integer optionalDateStyle, Integer optionalTimeStyle, Locale locale, boolean currentUserTimezone) {
        int dateStyle = optionalDateStyle != null ? optionalDateStyle : DateFormat.MEDIUM;
        int timeStyle = optionalTimeStyle != null ? optionalTimeStyle : DateFormat.SHORT;
        
        DateFormat df = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
        if (currentUserTimezone) {
            df.setTimeZone(timeService.getLocalTimeZone());
        }
        
        return df;
    }
    
    public void setLocalTimeZone(DateFormat df) {
        if (df != null) {
            df.setTimeZone(timeService.getLocalTimeZone());
        }
    }
    
    public static final String MOZILLA_BR = "<br type=\"_moz\" />";

    /**
     * Attempts to remove all unnecessary tags from html strings.
     * 
     * @param cleanup an html string to clean up. may be null
     * @return the cleaned up string
     */
    public static String cleanupHtmlText(String cleanup) {
        if (cleanup == null) {
            // nulls are ok
            return null;
        } else if (cleanup.trim().length() == 0) {
            // nothing to do
            return cleanup;
        }
        cleanup = cleanup.trim();

        //remove the unnecessary <br type="_moz" />
        cleanup = cleanup.replace(MOZILLA_BR, "");

        return cleanup;
    }

    /**
     * 
     * @param existingString
     * @return a "versioned" string based upon the given existingString. If the existingString
     * ends with a space then number, for example "Homework 1", will increment the existing
     * versioning and return "Homework 2". If no version exists (no space and then
     * number > 0), it will return the existingString plus a space 1 (ie "Persuasive Essay"
     * would be returned as "Persuasive Essay 1").  Returns null if existingString is null.
     * 
     */
    public static String getVersionedString(String existingString) {
        String duplicatedString = null;
        if (existingString != null) {
            int numToAppend = 1;

            // first, let's see if there is an existing version number on this
            // string (such as "Homework 1")
            String[] stringPieces = existingString.split(" ");
            if (stringPieces.length > 1) {
                String possibleNumber = stringPieces[stringPieces.length - 1];
                try {
                    int existingNumber = Integer.parseInt(possibleNumber);
                    if (existingNumber >= 0) {
                        numToAppend = existingNumber + 1;

                        // rebuild the string without the ending version info
                        String unversionedString = "";
                        for (int i = 0; i < stringPieces.length-1; i++) {
                            if (i != 0) {
                                unversionedString += " ";
                            }
                            unversionedString += stringPieces[i];
                        }

                        existingString = unversionedString;
                    }
                } catch (NumberFormatException nfe) {
                    // not an integer so not really versioned
                }
            } 

            duplicatedString = existingString + " " + numToAppend;
        }

        return duplicatedString;
    }
    
}