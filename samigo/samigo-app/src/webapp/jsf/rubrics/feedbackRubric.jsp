<!-- RUBRIC INCLUDE -->

<%@page import="org.sakaiproject.portal.util.PortalUtils"%>
<%@page import="org.sakaiproject.component.cover.ComponentManager"%>
<%@page import ="org.sakaiproject.rubrics.api.rubric.RubricsService" %>

<div id="Rubric">


        <%
        	RubricsService service = (RubricsService) ComponentManager.get("org.sakaiproject.rubrics.api.rubric.RubricsService");
        	PortalUtils.includeLatestJQuery("feedbackRubric");
        %>

        <!-- RUBRIC ADDS -->
                <script language="JavaScript" src="/rubrics-tool/js/Rubrics/map.js" type="text/javascript"></script>
                <script language="JavaScript" src="/rubrics-tool/js/Rubrics/RubricsCreator.js" type="text/javascript"></script>
                <script language="JavaScript" src="/rubrics-tool/js/Rubrics/Rubrics.js" type="text/javascript"></script>
                <script language="JavaScript" src="/rubrics-tool/js/Rubrics/jquery.tablesorter.js" type="text/javascript"></script>
        <!-- ----------- -->


        <script>
        
                $(".rubDisplayButton").click(function(){
                    var rub = $(this).siblings("#rubDisplayDiv");
                    if(rub.css('display') == 'none') {
                        rub.fadeIn();
                        $(this).html("Hide Rubric");
                    } else {
                        rub.fadeOut();
                        $(this).html("Show Rubric");
                    }
                });

                $(document).ready(function() {
                        $(".rubricContainer").each(function() {
                                if($(this).find("#rubricTable2").length > 0) {
                                        var $rubricTable = $(this).find('#rubricTable2');
                                        $(this).find("#rubricDiv").before('<div id="rubDisplayDiv" style="display: none">' +
                                                '<div id="rubricGradingHeader"></div>' +
                                                '<div id="rubricGrading"></div>' +
                                                '</div>');
                                        var $rubricDiv = $(this).find("#rubricGrading");
                                        $(this).find("#rubricGradingHeader").append('<h4 class="subgroupHeading">Grading Rubric</h4>');
                                        $rubricDiv.append('<div id="gradeRubricResults"></div>');
                                        $rubricDiv.append($rubricTable);
                                        $rubricTable.css("min-width", "500px").css("max-width", "1000px");
                                        $("tr:first", $rubricTable).css("font-weight", "bold");
                                        jQuery('tr', $rubricTable).each(function() {
                                                $("td:first", this).css("font-weight", "bold");
                                        });
                                        var $comments = $(this).find("#hasCommentCell");
                                        if ($comments.html().length > 0) {
                                                addCommentColumn($rubricTable, $comments.text());
                                        }
                                        
                                        /* proxy for grading data, if available */
                                        var data;
                                        $(this).find('input[type=hidden]').each(function() {
                                        	if($(this).attr("id").indexOf("rubricGradingDataProxy") >= 0) {
                                        		data = $(this).val();
                                        		return false;
                                        	}
                                        });
                                        $(this).find("rubricGradeData").val(data);
                                        /* end */
                                        
                                        selectExistingRows(this,data);
                                }
                        });
                });
	
	            function setRubricRowChoice($it,$table,dontRecordInfo,skipPoints) {
                    $it.parent().find("td").css("background-color", "white").removeClass("selectedCell");
                    $it.css("background-color", "lightblue").addClass("selectedCell");
                    var tmpSum = 0;
                    var usingPoints = false;
                    $table.find(".selectedCell").each(function() {
                            usingPoints = !isNaN(parseInt($(this).find("div").text().replace('%','')));
                            tmpSum += usingPoints ? parseInt($(this).find("div").text().replace('%','')) : 0;
                    });
                    
                    /* Here we need to find the 'proxy' for points if available, set it */
                    if(usingPoints && !skipPoints) {
                        var pars = $table.parents();
                        var found = false;
                        pars.each(function(){
                                var it = $(this).find(".questionScoreForRubric"); // proxy - every input for points, must include the class
                                if(it.length != 0) {
	                                it.val(tmpSum);
	                                return false;
                                }
                        });
                    }
                    /* end */
        		}
	            
	            function addCommentColumn($rubricTable, cellIds) {
                    $("tr:first", $rubricTable).each(function() {
                        $(this).append("<td>Comments</td>");
                    });
                    var cells = cellIds.split(","), i = 0;
                    var fromRow = "tr:gt(0)";
                    if($rubricTable.find(".gradingRow").length > 0) {
                        fromRow = "tr:gt(1)";
                        $("tr:eq(1)", $rubricTable).append("<td style=\"background-color: rgba(247, 198, 176, 0.4);\"></td>");
                    }
                    $(fromRow, $rubricTable).each(function() {
                        $(this).append("<td style=\"text-align: center;\" class=\"cellId-" + cells[i++] + "\"></td>");
                    });
                }
	            
	            function selectExistingRows(container,data) {
                    if(!data && container) {
		            	var input;
	                    $(container).find('input[type=hidden]').each(function(){
		                    var curr = $(this).attr("id");
		                    if(curr.indexOf("rubricGradeData") >= 0) {
		                            input = $(this);
		                            return false;
		                    }
	                    });
	                    var data = $(input).val();
                    }
                    if(data){
	                    data = data.split("||");
	                    var cell = $(container).find(".cellId-" + data[i]);
	                    for (var i = 0; i < data.length; i++) {
	                        if (data[i].length == 0) {
	                                continue;
	                        }
	                        equalsIndex = data[i].indexOf("=");
	                        if ($(container).find("#hasCommentCell").html().length > 0 && equalsIndex != -1) {
	                                $(container).find(".cellId-" + data[i].substring(0, equalsIndex)).text(data[i].substring(equalsIndex + 1));
	                        } else {
	                        	if ($(container).find(".cellId-" + data[i]).length > 0)
	                        		setRubricRowChoice($(container).find(".cellId-" + data[i]).parent(), $(container).find("#rubricTable2"),null,true);
	                        }
	                    }
                    }
            	}
                
        </script>

</div>