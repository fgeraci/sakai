<!-- RUBRIC INCLUDE -->

<%@page import="org.sakaiproject.portal.util.PortalUtils"%>
<%@page import="org.sakaiproject.tool.cover.SessionManager"%>
<%@page import="org.sakaiproject.component.cover.ComponentManager"%>
<%@page import ="org.sakaiproject.rubrics.api.rubric.RubricsService" %>

<!-- Encoding -->
<%@ page contentType="text/html; charset=UTF-8" %>

<div id="Rubric">


        <%
        	RubricsService service = (RubricsService) ComponentManager.get("org.sakaiproject.rubrics.api.rubric.RubricsService");
        	PortalUtils.includeLatestJQuery("rubrics");
        %>

	<!-- RUBRIC ADDS -->
    <script language="JavaScript" src="/rubrics-tool/js/Rubrics/map.js" type="text/javascript"></script>
    <script language="JavaScript" src="/rubrics-tool/js/Rubrics/RubricsCreator.js" type="text/javascript"></script>
    <script language="JavaScript" src="/rubrics-tool/js/Rubrics/Rubrics.js" type="text/javascript"></script>
    <script language="JavaScript" src="/rubrics-tool/js/Rubrics/jquery.tablesorter.js" type="text/javascript"></script>
    <!-- ----------- -->

	<!-- Inputs  -->
	
	<!-- See if consumer has a input called existingRubric and if so, populate the required DIV for parsing -->
    <div id="existingRubric" style="display:none;"></div> <!-- TODO - add existingRubricData here -->
    <!-- end link -->


	<input type="hidden" name='rubric_chosen' class="rubric_chosen" id="rubric_chosen" />
	<input type="hidden" name="<%=RubricsService.INPUT_RUBRIC_ID%>" id="<%=RubricsService.INPUT_RUBRIC_ID%>" value="noRubric" />
	<input type="hidden" name="<%=RubricsService.RUBRIC_DATA%>" id="<%=RubricsService.RUBRIC_DATA%>" value="" />
	
	<!-- 		 -->
	
	<!--  Interface Selectors (this will be changed to expandable)  -->

        <h4>Grading Rubrics</h4> <!--  TODO - change this to user the rubrics ResourceLoader -->
        
        <br>
        <button type="button" id="displayRubrics">Add Rubric</button>
        <br><br>

		<script>
            $("input:hidden").each(function() {
                var id = $(this).attr('id');
                if (id && id.indexOf("<%=RubricsService.EXISTING_RUBRIC%>") > 0) {
                        if($(this).val().length > 0) $("#displayRubrics").html("Modify Rubric");
                        $("#existingRubric").html($(this).val());
                }
            });
        </script>

        <div id="rubricOptionsDiv" class="indnt1" style="display: none" >
                <div class="checkbox indnt1">
                        <input class="<%=RubricsService.ADD_NO_RUBRIC%>" id="<%=RubricsService.ADD_NO_RUBRIC%>" name="rubricRadio" type="radio" checked="checked" onclick="rubrics('rubric_choice_none')" />
                        <label for="<%=RubricsService.ADD_NO_RUBRIC%>"><%=service.getMessage("rubric.addNoRubric")%></label>
                </div>
                <div class="checkbox indnt1">
                        <input class="<%=RubricsService.ADD_NEW_RUBRIC%>" id="<%=RubricsService.ADD_NEW_RUBRIC%>" name="rubricRadio" type="radio" onclick="rubrics('rubric_choice_modifyOrNew');" />
                        <label for="<%=RubricsService.ADD_NEW_RUBRIC%>"><%=service.getMessage("rubric.addNewRubric")%></label>
                        <u><a class="addNewRubric_edit" style="display:none; color:blue;" >Edit</a></u>
                        <div id="addToMyRubricsCheck" style="margin: 5px 0 0 5%; display: none;">
                                <input id="addToMyRubrics" type="checkbox" id="addToMyTemplates" onchange="makeItMyRubric()" /><label>Add to "My Rubrics"</label>
                        </div>
                </div>
                <div class="checkbox indnt1">
                        <input class="<%=RubricsService.ADD_PREDEFINED_RUBRIC%>" id="<%=RubricsService.ADD_PREDEFINED_RUBRIC%>" name="rubricRadio" type="radio" onclick="rubrics('rubric_choice_predefined');" />
                        <label for="<%=RubricsService.ADD_PREDEFINED_RUBRIC%>"><%=service.getMessage("rubric.addPredefinedRubric")%></label>
                        <u><a class="addPredefinedRubric_edit" style="display:none; color:blue;" ></a></u>
                </div>
                <div class="checkbox indnt1">
                        <input class="<%=RubricsService.ADD_PREDEFINED_RUBRIC%> select_myRubrics" id="<%=RubricsService.ADD_PREDEFINED_RUBRIC%>" name="rubricRadio" type="radio" onclick="rubrics('rubric_my_rubrics');" />
                        <label for="<%=RubricsService.ADD_PREDEFINED_RUBRIC%>"><%=service.getMessage("rubric.addMyRubrics")%></label>
                        <u><a class="addMyRubric_edit" style="display:none; color:blue;" ></a></u>
                </div>
        </div>

        <!--                     -->

        <!-- Listeners -->

        <script>
	        $("#displayRubrics").click(function(){
	            console.log("Handling rubrics options");
	            if($("#rubricOptionsDiv").is(":visible")) {
	                $("#rubricOptionsDiv").fadeOut();
	                $("#displayRubrics").html("Rubrics Options");
	            } else {
	                $("#rubricOptionsDiv").fadeIn();
	                $("#displayRubrics").html("Hide Rubrics");
	            }
	        });
        </script>
	
	<script>
		<!--
			function rubricChanged(option) {
				var inputRubric = document.getElementById("<%=RubricsService.INPUT_RUBRIC_ID%>");
				if(option == "<%=RubricsService.ADD_NO_RUBRIC%>") {
					inputRubric.value = "none";
				} else if (option == "<%=RubricsService.ADD_NEW_RUBRIC%>") {
					inputRubric.value = "newRubric";
				} else {
					inputRubric.value = "preDefRubric";
				}
				console.log(option+" selected - value of "+inputRubric.id+" is: "+inputRubric.value);
			}
		-->
	</script>
	
	<!-- 		 -->

	<div style="display:none;" class="rubric_predefined_data"></div>
	<div style="display:none;" class="rubric_predefined_data_unprocessed"><%=service.getPredefinedRubricsString(null)%></div>

	<div id="blankForm" style="display:none;"></div> <!-- Reset Button -->
	<div style="display:none;" id="rubric_savedInput"></div>
	<div id="rows_and_colsXY" style="display:none;"></div>
	
	
	<div class="rubric_dialog" id="rubric_choice_predefined_dialog" title="Choose from the core rubric templates">
            <div id="rubricsTemplatesTabs">
                    <ul>
                            <li>
                                    <a href="#rubric_options_html">Core Rubrics</a>
                            </li>
                            <li>
                                    <a href="#my_rubrics_options">My Rubrics</a>
                            </li>
                    </ul>
                    <div id="rubric_options_html"></div>
                    <div id="my_rubrics_options"></div>
            </div>

    </div>
	
	<div class="rubric_dialog" id="rubric_choice_modifyOrNew_dialog" title="Create a new rubric">
	<div id="rowsColsTable" name="rowsColsTable">
	<table  style="width:100%;height:100%;empty-cells:show;">
		<tr>
		<td colspan="2" >
			<table style="width:100%;height:100%;">
			<tr>
			<td><label for="rubric_title">Title</label></td>
			<td><input id="rubric_title" onkeypress="return checkEnter(event)" name="rubric_title" type="text" /></td>
			</tr>
			<tr style="display:none;">
			<td><label for="rubric_description">Description</label></td>
			<td><input id="rubric_description" onkeypress="return checkEnter(event)" name="rubric_description" type="text" /></td>
			</tr>
			<tr style="display:none;">
			<td><label for="rubric_icon_link">Image Link</label></td>
			<td><input id="rubric_icon_link" onkeypress="return checkEnter(event)" name="rubric_icon_link" type="text" /></td>
			</tr>
			<tr style="display:none;">
			<td><label for="rubric_coreKey">Key</label></td>
			<td><input id="rubric_coreKey" onkeypress="return checkEnter(event)" name="rubric_coreKey" type="text" /></td>
			</tr>
			</table>
		</td>
		</tr>
		<tr>
			<th style="height: 45px;">Rows</th>
			<th style="height: 45px;">Columns</th>
		</tr>
		<tr>
			<td>
				<table id="rowsTable" style="width:100%;height:100%;">
					<tr class="form_rows new-row_latch_1 ui-state-default">
						<td style="vertical-align:top;">
							<textarea id="rows[]" class="rubric_rows_form" type="text" onkeypress="return checkEnter(event)" value="" ></textarea>
							
						</td>
					</tr>
				</table>
			</td>
			<td>
				<table id="colsTable" style="width:100%;height:100%;">
					<tr class="form_cols new-col_latch_1 ui-state-default">
						<td style="vertical-align:top;">
							<textarea id="cols[]" class="rubric_cols_form" type="text" onkeypress="return checkEnter(event)" value="" ></textarea>
							
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<button type="button" onclick="addRowCol('row')">Add row</button><br />
			</td>
			<td>
				<button type="button" onclick="addRowCol('col')">Add column</button><br />
			</td>
		</tr>
	</table>
	</div>
	</div>
	
	<div class="rubric_dialog" id="rubric_choice_modifyOrNew_dialog-preview" title="Preview">
	<table id="rub_preview_table" border="1" style="width:100%;height:100%;empty-cells:show;">
		<!-- This is overwritten when user clicks save. See Rubrics.js function setupInputAndPreview().
		<tr id="rub_preview_top">
			<td></td>
		</tr>				
		-->
	</table>
	</div>
	
	<div class="rubric_dialog" id="rubric_predefined-preview" title="Preview">
	<table id="rub_predefined_preview_table" border="1" style="width:100%;height:100%;empty-cells:show;"></table>
	</div>
	
	<div class="rubric_dialog" id="rubric_choice_modifyOrNew_dialog-save" title="Save">
		Would you like to save your changes?
	</div>
	
</div>	
<!-- -------------- -->