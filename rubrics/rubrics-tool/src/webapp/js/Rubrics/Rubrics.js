/*
Created by Stephen Kane (skane9@rutgers.edu) and Kim Huang (kimhuang@rutgers.edu) for Rutgers University Rubrics Project.
This file depends on RubricCreator.js and map.js and jquery.
RU change
*/
var continueHasBeenPressed = false;
var chosen = '';
		$(function() { 
			
			var usedButton = false; //a button that calls dialog("close") can set this variable to true. Set this variable to false upon opening dialog
			jQuery.fn.exists = function(){return this.length>0;}
			
			$("#rubric_savedInput").text($("#rowsColsTable").html());
			
			//Default values for all templates.
			$( ".rubric_dialog" ).dialog({
				autoOpen: false,
				height: 500,
				width: 500,
				modal: true,
				buttons: {
					"Next": function() {
						setupInputAndPreview();
						$("#rubric_choice_modifyOrNew_dialog-preview").dialog( "open" );
						$( this ).dialog( "close" );
					},
					Cancel: function() {
						//alert("Canceling");
						$( this ).dialog( "close" );
					}
				},
				close: function() {
				}
			});
						
			//copy of this line with comments showing different implementations in removed.js
			$( "#rubric_choice_predefined_dialog" ).dialog({
				open: function() {var usedButton = false;},
				width: 800, 
				height: 600,
				buttons: {
					Cancel: function() {
						if(templatesUsed.length==0)
							rubrics("rubric_choice_none");
						$( this ).dialog( "close" );
					},
					"Continue": function() {
						
						/* Clean extra data from table, build table, then re add them */
						cleanPointsSystem();
						removeCommentCol();
						var commentsColAvailable = commentsAvailable();
						var pointsSystemAvailable = pointsAvailable();
						var myRub = false;
						
						 //var rub_id = $('#rubric_options_html input:radio[name=rubric_predefined_choice]:checked').val();
                        var chosenTemplates = new Array(), putCommentsBack=false;
                        $('#rubric_options_html input:checkbox:checked[name=rubric_predefined_choice]').each(function() {
                                chosenTemplates.push($(this).val());
                        });
                        $('#my_rubrics_options input:checkbox:checked[name=rubric_predefined_choice]').each(function() {
                                if(!myRub) {
                                        chosenTemplates.length = 0;
                                }
                                chosenTemplates.push($(this).val());
                                myRub = true;
                        });
						if(chosenTemplates.length>0){
							//Redo- new approach - remove all, then add chosen
							restoreBlankForm();
							for(var i = 0; i<templatesUsed.length; i++){
								$(".rubricIdTable_"+templatesUsed[i]).remove();
							}
							for(var i = 0; i<chosenTemplates.length; i++){
									rubric_table_id_class='class="rubricIdTable_'+chosenTemplates[i]+'"';
									addRowOrRubricHelper(chosenTemplates[i], "#rub_predefined_preview_table");
							}
							rubric_table_id_class="";//reset it for future use.
							templatesUsed=chosenTemplates;
							
							if(templatesUsed.length==1)
								setTitleButton(tempRubricTitle);
							else
								setTitleButton("edit");
							//setTitleButton($('input:radio[name=rubric_predefined_choice]:checked').attr('alt'));
							$("#rubric_predefined-preview").dialog( "open" );
							usedButton = true;
							if(pointsSystemAvailable) insertPointsSystem();
                            if(commentsColAvailable) commentCol();
                            if(myRub) {
                            	putDeleteButtonsBack(0);
                            	//addDeleteColumnsSimple("#rub_predefined_preview_table");
                            }
                            $( this ).dialog( "close" );
						}
						/*The best way to display the table with the paragraph breaks is creating a duplicate table and hide the ugly one so that it will be processed. 
						insertBreaks($("#rub_predefined_preview_table"));*/
					}
				},
				close: function() {
					if(!usedButton)
						rubrics("rubric_choice_none");
				}
			});
			
			$( "#rubric_predefined-preview" ).dialog({ 
				open: 
					function(event, ui){},
				height: 650,
				width: 800,
				buttons: {
					"Add/Remove Comments Column": function(){commentCol();},
					"Add/Remove Points System": function() {insertPointsSystem();},
					"Add custom row": function(){
						cleanPointsSystem();
						if($(".commentCol").length!=0){
							commentCol();
							addRow('#rub_predefined_preview_table', true);
							commentCol();
						}
						else
							addRow('#rub_predefined_preview_table', true);
					},
					"Back": function() {
						$("#rubric_choice_predefined_dialog").dialog( "open" );
						$( this ).dialog( "close" );
						cleanPointsSystem();
						$("#rub_predefined_preview_table tr").remove();
					},
					"Done": function() {
						if(validatePoints()) {
							$( this ).dialog( "close" );
						}
					}
				},
				close:function() {
					$(".commentCol").text('[[COMMENTS]]');
					var count = 0;
					// add here to record points
					deleteDeleteButtons();
					var map = tableCellsToMap('#rub_predefined_preview_table');
					map.put("[rubric_type]","predefined");
					putDeleteButtonsBack();
					addExtrasToMap(map, (templatesUsed.length==1)?tempRubricTitle:null, null,
						tempRubricIcon, "["+templatesUsed.join()+"]", null);
					$(".commentCol").text('You can enter text in here while grading.');
					$(".commentCol:first").text('Comments');
					//javascript: console.log(mapToString(map));
					var procData = mapToString(map);
					$('textarea[id$="rubricData"]').val(procData);
					$("#rubric_data").val(procData);
					$("#rows_and_colsXY").text($("#rubric_data").val());
					// set data back into proxy to populate bean if available
					// find proxy and set it if available
                    handleProxy();
				}
			});
			
			$( "#rubric_choice_modifyOrNew_dialog" ).dialog({ 
				width: 550,
				open: function(event, ui){
					usedButton = false;
				},
				buttons: {
					Cancel: function() {
						usedButton = true;
						$( this ).dialog( "close" );
						if(!rubricNotEmpty() || !continueHasBeenPressed){
							rubrics("rubric_choice_none");
						}
					},
					"Reset Form": function() {
						restoreBlankForm();
					},
					"Next": function() {
						
						/* Clean extra data from table, build table, then re add them */
                        var commentsColAvailable = commentsAvailable();
                        var pointsSystemAvailable = pointsAvailable();
						
						usedButton = true;
						
						setUpNew();
						
						if($("#rubric_title").val().trim()=="" || $("#rubric_title").val().trim()=="t3mpl@te:"){
							alert("Please enter a title.");
							return;
						}
						
						if(rubricNotEmpty()) {
							previewDeleteButtons();
							continueHasBeenPressed = true;
							$("#rubric_choice_modifyOrNew_dialog-preview").dialog( "open" );
							$( this ).dialog( "close" );
						}
						else
							alert("Please enter information first.");
						
						if(commentsColAvailable) commentCol("#rub_preview_table");
						if(pointsSystemAvailable) insertPointsSystem("#rub_preview_table");
					}
				},
				close: function() {
					
					if(!usedButton){
						if(rubricNotEmpty())
							$("#rubric_choice_modifyOrNew_dialog-save").dialog( "open" );
						else
							rubrics("rubric_choice_none");
					}
				}
			});
						
			$( "#rubric_choice_modifyOrNew_dialog-preview" ).dialog({ 
				height: 650,
				width: 800,
				buttons: {
					"Add/Remove Comments Column": function(){commentCol("#rub_preview_table");},
                    "Add/Remove Points System": function() {insertPointsSystem("#rub_preview_table");},
					"Back": function() {
						$("#rubric_choice_modifyOrNew_dialog").dialog( "open" );
						$( this ).dialog( "close" );
					},
					"Done": function() {
						if(validatePoints()) {
							$( this ).dialog( "close" );
						}
					}
				},
				close:function() {
					$(".commentCol").text('[[COMMENTS]]');
					deleteDeleteButtons();
					var map = tableCellsToMap("#rub_preview_table");
					addExtrasToMap(map, $("#rubric_title").val(), $("#rubric_description").val(), $("#rubric_icon_link").val(),null, $("#rubric_coreKey").val());
					map.put("[rubric_type]","new");
					$("#rubric_data").val(mapToString(map));
					$("#rows_and_colsXY").text($("#rubric_data").val());
					// handle proxy for bean creation, if any
					handleProxy();
				}
			});
			
			$(".addNewRubric_edit").click(function(){rubrics('rubric_choice_modifyOrNew');}).css( 'cursor', 'pointer' );;
			$(".addPredefinedRubric_edit").click(function(){rubrics('rubric_choice_predefined');}).css( 'cursor', 'pointer' );;
			$(".addMyRubric_edit").click(function(){rubrics('rubric_my_rubrics');}).css( 'cursor', 'pointer' );;
			
			$( "#rubric_choice_modifyOrNew_dialog-save" ).dialog({ 
				height: 200,
				buttons: {
					"Yes": function() {setUpNew();$( this ).dialog( "close" );},
					"No": function() {$("#rowsColsTable").html($("#rubric_savedInput").text());$( this ).dialog( "close" );}
				}
			});
			
			var templateId= null;
			/* ****** Onload Functions Calls ****** */
			
			saveBlankForm();
			//processPredefined();
			//$(".rubric_predefined_data_unprocessed").text($(".rubric_predefined_data").text());
			createPredefinedModules();
			if ($('[id$=existingRubric]').length>0) {
				if($('[id$=existingRubric]').text().trim()!="")
					handleExisting();
				else
					rubrics("rubric_choice_none");
			}
		});
		
		function previewDeleteButtons()
		{
			addDeleteColumns("#rub_preview_table", 'deleteFromForm("#colsTable", ');
			addDeleteRows("#rub_preview_table", 'deleteFromForm("#rowsTable", ');
		}
		
		function handleExisting()
		{
			var existingData = $('#existingRubric').text();     
            $('#existingRubric').text(existingData.substring(existingData.indexOf(getDelimeter(0))+getDelimeter(0).length));
            $("#rows_and_colsXY").text($('#existingRubric').text());
            $('textarea[id$="rubricData"]').val($("#rows_and_colsXY").text());
            $("#rubric_data").val($("#rows_and_colsXY").text());
			var map = buildMap();
			if(map.size == 0) return;
			var pointsSystemAvailable = pointsAvailable();
			if(getMapValue(map,"rubric_type").indexOf("new") != -1)
			{
				processExisting();
				setUpNew();
				buttonSwap("rubric_choice_modifyOrNew");
			} else {
				//get templatesUsed from the dataset
                var templates = getMapValue(map,"templates");
                templatesUsed=templates.split(",");
                buttonSwap("rubric_choice_predefined");
				
				//check off the chosen templates, create the table
				for(var i=0; i<templatesUsed.length;i++)
				{
					$("#rubric_options_html input[value="+templatesUsed[i]+"]").prop('checked', true);
					$("#my_rubrics_options input[value="+templatesUsed[i]+"]").prop('checked', true);
					rubric_table_id_class='class="rubricIdTable_'+templatesUsed[i]+'"';
					addRowOrRubricHelper(templatesUsed[i], "#rub_predefined_preview_table");
					rubric_table_id_class='';
				}
				
				//create a temp table from the dataset, use countTemplateRows to choose custom rows.
				var countRows = countTemplateRows();
				buildTableFromMap("#tempRubricTable", map, false);
				$("#tempRubricTable tr:gt("+(countRows)+")").each(function(){$("*:first", this).attr("onclick", 'cellText($(this));' )
					.append('<button class="deleteRowBtn" onclick="$(this).parent().parent().remove();">X</button>');});
				
				//Remove the comments column (if exists)
				if($("#tempRubricTable tr:gt("+(countRows)+") *:last").text()=='[[COMMENTS]]'){
					$("#tempRubricTable tr:gt("+(countRows)+")").each(function(){$("*:last", this).remove()});
					hasCommentColumnFirstTimeOnly=true;
				}
				
				$("#rub_predefined_preview_table").append($("#tempRubricTable tr:gt("+(countRows)+")"));
				
				setTitleButton(tempRubricTitle);
			} 
			
			if (map.get("[rubric_type]") == "new") {
				buttonSwap("rubric_choice_modifyOrNew");
				document.getElementById("select_addNewRubric").checked = true;
			} else {
                if(getMapValue(map,'title').indexOf('- My Rubric') != -1) {
                	buttonSwap("rubric_my_rubrics");    
                	$(".select_myRubrics").prop('checked', true);
				} else {
                	buttonSwap("rubric_choice_predefined");
                	document.getElementById("select_addPredefinedRubric").checked = true;
                }
			}
		}
		
		function getMapValue(map,key) {
			var v = map.get("["+key+"]");
			if(v != undefined)
				return map.get("["+key+"]").replace(/(\[|\])/g,"");
			return "";
		}
		
		function putDeleteButtonsBack(val){
            var countRows = (val == undefined ? countTemplateRows() : val); //this fxn call calls buildTableFromMap
            $("#rub_predefined_preview_table tr:gt("+(countRows)+")").each(
                    function(){
                            if($("*:first", this).find("button").length == 0)
                                    $("*:first", this).append('<button class="deleteRowBtn" onclick="$(this).parent().parent().remove();">X</button>');
                    });
		}

		
		function cellText(it)
		{
			var rowDelete = false;//rubric2.0
			if($(".deleteRowBtn", it).length>0)//rubric2.0
			{
				$(".deleteRowBtn", it).text("");
				//alert("This cell has a delete button in it.");
				rowDelete = true;
			}
				
			$(it).html('<textarea>'+$(it).text()+'</textarea>');
			$(it).attr("onclick", "");
			$(it).find("textarea").focus();
			if(rowDelete)//rubric2.0
				$(it).append('<button class="deleteRowBtn" onclick="$(this).parent().parent().remove();">X</button>');
		}
		
		function cellRealNumber(it)
		{
			$(it).css("text-align", "center");	
			$(it).html("<textarea style='text-align: center; resize:none;' maxlength='3' cols='3' rows='1' placeholder='%' onclick='cleanUpPer(event)' onchange='completePercent(event)' onkeypress='validateNumber(event)'>"+$(it).text()+"</textarea>");
			$(it).attr("onclick", "");
			$(it).find("textarea").focus();
		}
		
		function cleanUpPer(e) {
			var val = e.target.value;
			if(val.indexOf('%') != -1) {
				e.target.value = val.replace('%', '');
			}
		}
		
		function validateNumber(e) {
			var a = [];
		    var k = e.which;
		    for (i = 48; i < 58; i++)
		        a.push(i); 
		    if(k == 8 || k == 46) return;
		    else if (!(a.indexOf(k)>=0))
		        e.preventDefault();
		}
		
		function completePercent(event) {
			var val = event.target.value;
			if(val.indexOf('%') == -1) event.target.value = val + '%';
			if(parseInt(val) > 100 || parseInt(val) < 0) {
				event.target.value = 100 + '%';
				alert("A category must be between 0 and 100% of the total weighted value of the row.");
			}
		}
		
		function addTitleDescriptionToXY()
		{
			var title = $("#rubric_title").val();
			var description = $("#rubric_description").val();
			
			setTitleButton(title);
			
			$("#rows_and_colsXY").append("[title]" + delimeter2 + title + delimeter);
			$("#rows_and_colsXY").append("[description]" + delimeter2 + description + delimeter);
			//alert($(".rubric_title").val());
			$("#rubric_title").replaceWith("<input id=\"rubric_title\" name=\"rubric_title\" value=\"" + title +"\" type=\"text\" />");
			$("#rubric_description").replaceWith("<input id=\"rubric_description\" name=\"rubric_description\" value=\"" + description +"\"  type=\"text\" />");
		}
		
		function setTitleButton(title)
		{
			$('.addPredefinedRubric_edit').text(title);
			$('.addNewRubric_edit').text(title);
			$('.addMyRubric_edit').text(title);
		}
		
		function addColsAndRowsToMap(map)
		{
			var count=1;
			$("#rowTable tr").each(function(){
				map.put("[0]["+count+"]",$(this).find("textarea").text());
				count++;
			});
			count=1;
			$("#colsTable tr").each(function(){
				map.put("["+count+"][0]",$(this).find("textarea").text());
				count++;
			});
		}
		
		function appendColsAndRowsToXY(dataSelector)
        {
                var offset = pointsAvailable() ? 1 : 0;
                var count = 1 + offset;
                $("#rowsTable tr").each(function(){
                        $(dataSelector).append("[0]["+count+"]"+DELIMETER2+$(this).find("textarea").val()+DELIMETER1);
                        count++;
                });
                var count= 1;
                $("#colsTable tr").each(function(){
                        var v = $(this).find("textarea").val();
                        if(!isPoints(v)) $(dataSelector).append("["+count+"][0]"+DELIMETER2+$(this).find("textarea").val()+DELIMETER1);
                        count++;
                });
        }

        //Add columns to XY and generate preview.
        function setUpNew(Later)
        {
                setTitleButton($("#rubric_title").val())
                appendColsAndRowsToXY("#rows_and_colsXY");
                buildTableFromMap("#rub_preview_table", buildMap(), false);
        }
		
		/*
		Sample Implementation:
			var sizes = getSizesXY(); //return [colSize,rowSize];
			var colSize = sizes[0];
			var rowSize = sizes[1];		
		//This uses map sizes which could differ from form sizes. ?
		*/
		function getSizesXY()
		{
			var map = buildMap(), colSize=1, rowSize=1;
			
			while(map.get("[0]["+rowSize+"]")!=undefined)
				rowSize++;
			
			while(map.get("["+colSize+"][0]")!=undefined)
				colSize++;
			
			rowSize--;colSize--;
			
			return [colSize,rowSize];
		}
		
		function processExisting()
		{
			$("#rowsTable tr").remove();
			$("#colsTable tr").remove();
			var data = $('[id$=existingRubric]').text().split(DELIMETER1);
			for(var i = 0; i < data.length ; i++)
			{
				theData=data[i]; if(theData=="" || isPoints(theData) || theData.indexOf("[[COMMENTS]]") != -1) continue;
				var dataSplit = theData.split(delimeter2); if(dataSplit.length !=2)continue;
				var left = dataSplit[0], right = dataSplit[1];
				if(left.search("title") != -1 ){
					$("#rubric_title").val(right);
					if(right.indexOf("t3mpl@te:")!=-1)
						showExtras();
				}
				else if(left.search("description") != -1 )
					$("#rubric_description").val(right);
				else if(left.search("icon") != -1 )
					$("#rubric_icon_link").val(right);
				else if(left.search("coreKey") != -1 )
					$("#rubric_coreKey").val(right);
				else if(left.search("rubric_type") != -1 )
					continue;
				else if(left.search("add_to_my_rubrics") != -1)
					continue;
				else
				{
					col = left.split("][");row = col[1];col = col[0].substring(1);row = row.substring(0,row.length-1);
					if(col=="0" && row!="0"){
						addRowColPredefined("row" , right, i);
					}
					if(row=="0" && col!="0"){
						addRowColPredefined("col" , right, i);
					}
				}
			}
		}
		
		function rubricNotEmpty()
		{
			var hasRows = false, hasCols = false;
			$(".rubric_rows_form").each(function(){
				if($(this).val() != "")
					{hasRows = true; return false;/*breaks*/}
			});
			$(".rubric_cols_form").each(function(){
				if($(this).val() != "")
					{hasCols = true;}
			});
			
			return hasRows || hasCols;
		}
		
		//turn into global variables
		function getDelimeter(layer){if(layer==2) return ' =-=- ';return (layer==0)?' ;; ':' == ';}
		
		/*
		This runs through the $("#rows_and_colsXY"), splits the entries and returns a full map.
		*/
		function buildMap()
		{
			var map = createMap($("#rows_and_colsXY").text(), DELIMETER1, DELIMETER2);
			var outputCurrentMap = mapToString(map);
			$("#rows_and_colsXY").text(outputCurrentMap);
			$('textarea[id$="rubricData"]').val($("#rows_and_colsXY").text());
			$("#rubric_data").val($("#rows_and_colsXY").text());
			return map;			
		}
		
		function addRowCol(addWhat)
		{
			var colsLen = $('.rubric_cols_form').length +1;
			var rowsLen = $('.rubric_rows_form').length +1;
			if(addWhat == "row")
			{
				$("#rowsTable").append('<tr class="form_rows new-row_latch_'+rowsLen+' ui-state-default">'
					+'<td style="vertical-align:top;">'
					+	'<textarea id="rows[]" class="rubric_rows_form" onkeypress="return checkEnter(event)" type="text" ></textarea>'
				//	+	'<button class="rubric_delete_rowCol" onclick="$(this).parent().parent().remove();">X</button>'
					+"</td>"
				+"</tr>");
			}
			else if(addWhat == "col")
			{
				$("#colsTable").append('<tr class="form_cols new-col_latch_'+colsLen+' ui-state-default">'
					+'<td style="vertical-align:top;">'
					+	'<textarea id="cols[]" class="rubric_cols_form" onkeypress="return checkEnter(event)" type="text" ></textarea>'
					+"</td>"
				+"</tr>");
			}
		}
		
		function addRowColPredefined(addWhat , value, id)
		{
			if(addWhat == "row")
			{
				$("#rowsTable").append('<tr class="form_rows pre-row_latch_'+ id +' ui-state-default">'
					+'<td style="vertical-align:top;">'
					+	'<textarea id="rows[]" class="rubric_rows_form" onkeypress="return checkEnter(event)" value="" type="text" >'+value+'</textarea>'
				//	+	'<button class="rubric_delete_rowCol" onclick="$(this).parent().parent().remove();">X</button>'
					+"</td>"
				+"</tr>");
			}
			else if(addWhat == "col")
			{
				$("#colsTable").append('<tr class="form_cols pre-col_latch_'+ id +' ui-state-default">'
					+'<td style="vertical-align:top;">'
					+	'<textarea id="cols[]" class="rubric_cols_form" onkeypress="return checkEnter(event)" value="" type="text" >'+value+'</textarea>'
				//	+	'<button class="rubric_delete_rowCol" onclick="$(this).parent().parent().remove();">X</button>'
					+"</td>"
				+"</tr>");
			}
		}
		
		//Clicking enter after entering text was clicking the delete button. This prevents it from happening.
		function checkEnter(e)
		{
			if($("#rubric_title").val()=="t3mpl@te:")
			{
				showExtras();
			}
			e = e || event;
			return (e.keyCode || event.which || event.charCode || 0) !== 13;
		}
		
		function showExtras()
		{
			$("#rubric_description").parent().parent().show();
			$("#rubric_icon_link").parent().parent().show();
			$("#rubric_coreKey").parent().parent().show();
		}
		
		//Reset button. See: function saveBlankForm()
		function restoreBlankForm()
		{
			$("#rows_and_colsXY").text("");
			$("#rub_predefined_preview_table").html("");
			$("#rowsColsTable").html($("#blankForm").text());
		}
		
		//The main action handler relating to the radio buttons.
		function rubrics(caller)
		{
			if(chosen.length == 0) chosen = caller;
			else if (chosen != caller) {
				restoreBlankForm();
			}			
			buttonSwap(caller);
			clearMyRubricsOption();
			if(caller == "rubric_choice_none")
			{
				$('.select_addNoRubric').prop('checked',true);
				$("#rubric_chosen").val("none");
			}
			else if(caller == "rubric_choice_predefined")
			{
				$("#rubricsTemplatesTabs").tabs({active: 0});
				$("#rubric_choice_predefined_dialog").dialog( "open" );
				if($("#rubric_chosen").val()=="new")
					$("#rows_and_colsXY").text("");
				$("#rubric_chosen").val("predefined");
			} else if (caller == "rubric_my_rubrics") {
				$("#rubricsTemplatesTabs").tabs({active: 1 });
				$("#rubric_choice_predefined_dialog").dialog( "open" );
				if($("#rubric_chosen").val()=="new")
					$("#rows_and_colsXY").text("");
				$("#rubric_chosen").val("predefined");

			}
			else if(caller == "rubric_choice_modifyOrNew")
			{
				$("#rubric_choice_modifyOrNew_dialog").dialog( "open" );
				//check previous settings.
				if(!($("#addToMyRubricsCheck").is(":visible"))) $("#addToMyRubricsCheck").fadeIn();
				if($("#rubric_chosen").val()=="predefined")
					$("#rows_and_colsXY").text("");
				$("#rubric_chosen").val("new");
			}
		}
		
		function clearMyRubricsOption() {
			if($("#addToMyRubricsCheck").is(":visible")) $("#addToMyRubricsCheck").fadeOut();
			$("#addToMyRubrics").prop('checked',false);
		}
		
		function makeItMyRubric() {
            var val = $("#addToMyRubrics").is(":checked");
            var map = buildMap();
            if(val)
				map.put("[add_to_my_rubrics]","true");
            else
            	map.remove("[add_to_my_rubrics]");
            var procData = mapToString(map);
			$('textarea[id$="rubricData"]').val(procData);
			$("#rubric_data").val(procData);
			$("#rows_and_colsXY").text($("#rubric_data").val());
			handleProxy();
		}
		
		function buttonSwap(caller)
		{
			if(caller == "rubric_choice_none")
			{
				$(".addNewRubric_edit").hide();
				$(".addPredefinedRubric_edit").hide();
				$(".addMyRubric_edit").hide();
			}
			else if(caller == "rubric_choice_predefined")
			{
				$(".addNewRubric_edit").hide();
				$(".addMyRubric_edit").hide();
				$(".addPredefinedRubric_edit").show();
			}
			else if(caller == "rubric_choice_modifyOrNew")
			{
				$(".addNewRubric_edit").show();
				$(".addMyRubric_edit").hide();
				$(".addPredefinedRubric_edit").hide();
			} else if (caller == "rubric_my_rubrics") {
				$(".addNewRubric_edit").hide();
				$(".addPredefinedRubric_edit").hide();
				$(".addMyRubric_edit").show();
			}
		}
		
		/* ****** Onload Functions ****** */
		//Saving the form on page load guarantees that any future code changes will be reflected in the Reset button.
		function saveBlankForm() {$("#blankForm").text($("#rowsColsTable").html());}
		
		//adds text to the two dialogs that list predefined rubrics. checkboxes for modify/new and radio for predefined.
		function createPredefinedModules()
		{
			delimeter = getDelimeter(0);//;;
			delimeter2 = getDelimeter(1);//==
			var data = $(".rubric_predefined_data_unprocessed").text();
			//var dataWithCheck= "";
			var dataWithRadio= "", myRubData = "";
			var split1 = data.split(delimeter);
			for(var i = 0; i < split1.length ; i++)
			{
				split2 = split1[i].split(delimeter2);
				split2[0]=split2[0].trim();
				
				var rub_id=split2[0].substring(1).substring(0,split2[0].indexOf("]")-1);
				//rub_id="rubric_id-"+rub_id;
				
				//There are always 2 elements in split2
				var title;
				if(split2[0].search("title") != -1)
				{
					title = split2[1];
					var d = '<input type="checkbox" name="rubric_predefined_choice" alt="'+title;
					if(title.indexOf("My Rubric") != -1) {
						d += '" onchange=\"clearCheckBoxes(\'rubrics\');\" class="predefined_rubric_choices" value="'+rub_id+'" />'+title+'</br>';
						myRubData += d;
					} else {
						d += '" onchange=\"clearCheckBoxes(\'my_rubrics\');\" class="predefined_rubric_choices" value="'+rub_id+'" />'+title+'</br>';
						dataWithRadio += d;
					}
				}
			}
			$("#rubric_options_html").html(dataWithRadio);
			$("#my_rubrics_options").html(myRubData);
		}

		function clearCheckBoxes(target) {
			if(target == 'rubrics') {
				target = '#rubric_options_html input:checkbox:checked[name=rubric_predefined_choice]';
			} else {
				target = '#my_rubrics_options input:checkbox:checked[name=rubric_predefined_choice]';
			}
			$(target).each(function() {
                $(this).prop('checked',false);
			});
		}		
		
function insertBreaks($it){
	$it.find("td").each(function(){
		theText=$(this).text();
		while(theText.indexOf("\n") != -1) {theText=theText.replace("\n", "<br>");} 
		$(this).html(theText);
	});
}

function insertPointsSystem(tab) {
	if(tab == undefined) tab = '#rub_predefined_preview_table';
	if($(".pointsCol").length==0)	{
		addColumn(tab, "rw", "pointsCol");
		$(".pointsCol:first").text("% Points per Row");
		$(".pointsCol:first").removeAttr("onclick");
		$(".pointsCol:first").removeAttr("style");
		$(".pointsCol:first").css("text-align","center");
		$(".pointsCol:first").css("font-weight","bold");		
		addPointsRow(tab);
		$(".pointsRow td:first").text("% Grade Per Category");
		$(".pointsRow td:first").css("text-align","center");
		$(".pointsRow td:first").css("font-weight","bold");
		
	} else {
		$(".pointsCol").remove();
		$(".pointsRow").remove();
	}
}

function cleanPointsSystem() {
	if($(".pointsCol").length != 0) $(".pointsCol").remove();
	if($(".pointsRow").length != 0) $(".pointsRow").remove();
	if($(".pointsCol").length != 0) {
		$(".pointsCol").remove();
		$(".pointsHolderRow").remove();
	}
}

function validatePoints() {
	var valid = true;
	var rowTotalWeights = 0;
	/* Are all columns filled up? */
	var colWeights = -2, rowWeights = -1; // for the rowWeights dont count the header
	$(".pointsHolderRow").each(function() {
		colWeights += 1;
	});
	$(".pointsCol").each(function() {
		rowWeights += 1;
	});
	var colVals = 0, rowVals = 0;
	$(".pointsHolderRow textarea").each(function() {
		colVals += 1;
	});
	$(".pointsCol textarea").each(function() {
		rowVals += 1;
		if(this.value.indexOf('%') != -1) {
			var tmp = this.value.replace('%', '');
			rowTotalWeights += parseInt(tmp);
		}
	});
	
	if(colWeights > 0) {
		if(colVals != colWeights || rowVals != rowWeights) {
			alert("Please complete all the points weights or remove the system. Each column must be between 0 and 100% and all the row values must add to 100%.");
			return false;
		}
		
		/* All the row weights add to 100%? */
		if(rowTotalWeights != 100) {
			alert("All the rows weights must add to a 100%");
			return false;
		}
	}
	/* Are all the cells non-empty? */
	$(".pointsValueHor textarea:first-child").each(function() {
		var tmp = this.value.replace('%', '');
		if(tmp.length == 0) {
			alert("Columns weight must be non-empty and values between 0 - 100%");
			return valid = false;
		}
	});
	return valid;
}

/* If there is a proxy defined for populating bean, etc ... find it and replicate the rubric_data value into it */
function handleProxy() {
    $("input:hidden").each(function() {
            var id = $(this).attr('id');
            if (id && id.indexOf("existingRubricData") > 0) {
                    $(this).val($("#rubric_data").val());
            }
    });
}

function commentsAvailable() {
    if($("#rows_and_colsXY").length > 0) {
            var t = $("#rows_and_colsXY").text();
            return t.indexOf("[[COMMENTS]]") != -1 ? true : false;
    } // TODO - this need to be adapted such that is self contained withint a particular tag for samigo
    return false;
}

function pointsAvailable() {
    if($("#rows_and_colsXY").length > 0) {
            var t = $("#rows_and_colsXY").text().toUpperCase();
            return t.indexOf("% POINTS PER ROW") != -1 ? true : false;
    }
    return false;
}

function isMyRub() {
	if($("#rows_and_colsXY").length > 0) {
        return t.indexOf("- My Rubric") != -1;
	}
}
