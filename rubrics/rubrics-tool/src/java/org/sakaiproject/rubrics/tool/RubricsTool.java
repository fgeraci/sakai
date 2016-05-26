package org.sakaiproject.rubrics.tool;

import org.sakaiproject.rubrics.api.rubric.RubricsService;

public class RubricsTool {
	
	private static RubricsTool mInstance;
	
	public void init() {
		RubricsTool.mInstance = this;
	}
	
	public static RubricsTool getInstance() {
		return RubricsTool.mInstance;
	}
	
	private RubricsService rubricsService;
	public void setRubricsService(RubricsService service) {
		this.rubricsService = service;
	}
	
	public RubricsService getService() {
		return this.rubricsService;
	}

}
