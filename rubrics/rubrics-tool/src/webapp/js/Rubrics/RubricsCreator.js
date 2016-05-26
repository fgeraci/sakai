/*
Created by Stephen Kane (skane9@rutgers.edu) and Kim Huang (kimhuang@rutgers.edu) for Rutgers University Rubrics Project.
This file holds generic functions that help to make tables and maps related to rubrics.
RU change
*/

var DELIMETER1 = ' ;; ';
var DELIMETER2 = ' == ';
var DELIMETER3 = ' =-=- ';
var templatesUsed = new Array();
//var emptyCol = '<td style="width:100px;" onclick="cellText($(this));"></td>';
var emptyRow = '<tr></tr>';
var pointsRow = "<tr class='pointsRow'></tr>";
var hasCommentColumnFirstTimeOnly=false;

function emptyCol(ReadWrite , optionalClass, optionalValue){
	var onclickCol = (optionalClass == 'pointsCol' ? 'onclick="cellRealNumber($(this));"' : 'onclick="cellText($(this));"'), colClass='', xtraStyle ='';
	if(ReadWrite=='r') onclickCol = '';
	if(optionalClass!=undefined && optionalClass!=null)colClass='class="'+optionalClass+'"';
	if(optionalValue==undefined || optionalValue==null)optionalValue="";
	xtraStyle = (optionalClass == 'pointsCol' ? 'background-color: #DFFED2;' : '');
	return '<td style="width:100px;'+xtraStyle+'" '+onclickCol+' '+colClass+'>'+optionalValue+'</td>';
}

$(function(){
	if (typeof String.prototype.startsWith != 'function') {
		// see below for better implementation!
		String.prototype.startsWith = function (str){
			return this.indexOf(str) == 0;
		};
	}
});

//special is an optional attribute where specialized calls can be added to the onclick of a button.	
function addDeleteRows(tableSelector, special){
	if(special == undefined)special = "";
	$(".deleteRowBtn").remove();
	var trCount=1, tableSelectorWO=tableSelector.substring(1)+"_deleteRow_";
	$(tableSelector+" tr:gt(0)").each(function(){
		$("*:first", this).append('<button class="deleteRowBtn '+tableSelectorWO+trCount+'">X</button>');
		var special2= (special!="")?'"'+tableSelector+'"'+","+trCount+");":"";
		$("."+tableSelectorWO+trCount).attr("onclick", '$(this).parent().parent().css("background-color","blue").remove(); '+special+special2+ 'previewDeleteButtons();');
		trCount++;
	});
}

function addDeleteColumnsSimple(tableSelector, special){
	if(special == undefined)special = "";
	$(".deleteColBtn").remove();
	var tdCount=2, tableSelectorWO=tableSelector.substring(1)+"_deleteCol_";
	$(tableSelector+" tr:first th").each(function(){
			$(this).append('<button class="deleteColBtn '+tableSelectorWO+tdCount+'">X</button>');
			var special2= (special!="")?'"'+tableSelector+'"'+","+(tdCount-1)+");":"";
			$("."+tableSelectorWO+tdCount).attr("onclick", 'deleteColumn("'+tableSelector+'","'+tdCount+'");');
			tdCount++;
	});
}

function addDeleteColumns(tableSelector, special){
	if(special == undefined)special = "";
	$(".deleteColBtn").remove();
	var tdCount=2, tableSelectorWO=tableSelector.substring(1)+"_deleteCol_";
	$(tableSelector+" tr:first th").each(function(){
			$(this).append('<button class="deleteColBtn '+tableSelectorWO+tdCount+'">X</button>');
			var special2= (special!="")?'"'+tableSelector+'"'+","+(tdCount-1)+");":"";
			$("."+tableSelectorWO+tdCount).attr("onclick", 'deleteColumn("'+tableSelector+'","'+tdCount+'"); '+special+special2+'previewDeleteButtons();');
			tdCount++;
	});
}

function deleteDeleteButtons(){$(".deleteRowBtn").remove();$(".deleteColBtn").remove();}

function deleteColumn(tableSelector,tdCount){
	javascript: console.log("deleteColumn("+tableSelector+","+tdCount+")"+" $("+tableSelector+" tr:gt(0) td:nth-child("+tdCount+")).remove()");
	$(tableSelector+" tr").each(function(){
		$("*:nth-child("+tdCount+")" , this).css("background-color","blue").remove();
	});	
	//addDeleteColumns(tableSelector);
}

function deleteFromForm(formSelector,tableSelector, targetIndex)
{
	$(formSelector+" tr:nth-child("+targetIndex+")" ).css("background-color","blue").remove();
}

function tableCellsToMap(tableSelector){     
	embedAllTextareas(tableSelector);
	var data = '';
	var map = new Map;
	var trCount=0;
	var tdCount=0;
	$(tableSelector + " tr").each(function(){
		tdCount=0;
		$($("td, th", this)).each(function(){
			map.put("["+tdCount+"]["+trCount+"]",$(this).text());
			tdCount++;
		});
		trCount++;
	});
	//javascript: console.log(mapToString(map));
	return map;
}

function embedAllTextareas(tableSelector){
	$(tableSelector).find("textarea").each(function(){
		$(this).parent().text($(this).val());
	});
}

function addExtrasToMap(map, title, desc, icon, tUsed, coreKey){
	if(title==null||title==undefined)	title="";	if(desc==null||desc==undefined)desc="";
	
	map.put("[title]",title);
	map.put("[description]",desc);
	if(icon != undefined && icon!=null && icon!="" && icon!="null")
		map.put("[icon]",icon);
	if(tUsed != undefined)
		map.put("[templates]",tUsed);
	if(coreKey != undefined && coreKey!=null && coreKey!="" && coreKey!="null")
		map.put("[coreKey]",coreKey);
}

function removeArray(arr, index){arr.splice(index,1); return arr;}
function searchArray(arr, target){
	for(var i=0 ; i<arr.length; i++){if(arr[i]==target)return i;}return -1;
}

function addRowOrRubricHelper(rubricId, tableId){
	if($(tableId+ " tr").length == 0) 	addPredefinedRubric(rubricId, tableId);
	else 								addTemplateRows(rubricId, tableId);
}

function addPredefinedRubric(rubricId, tableId){$(tableId).append($(buildTempTable(rubricId) + " tr"));}

function addTemplateRows(rubricId, tableId){$(tableId + " tr:first").after($(buildTempTable(rubricId) + " tr:gt(0)"));}

function addRow(tableId, deletable){
	$(tableId).append(emptyRow);
	$(tableId +" tr:first td , " + tableId +" tr:first th").each(function(){$(tableId + " tr:last").append(emptyCol());});
	if(deletable)
		$(tableId + " tr:last *:first").append('<button class="deleteRowBtn" onclick="$(this).parent().parent().remove();">X</button>');
}

function addPointsRow(tableId) {
	$(tableId +" tr:first").after(pointsRow);
	var count = 0;
	var flagA = -1;
	var flagB = -1;
	$(tableId + " tr:first th").each(function() {
		count += 1;
		if( $(this).attr('class') == 'commentCol') flagA = count-1;
		if( $(this).attr('class') == 'pointsCol') flagB = count-1;
	});
	$(tableId + " tr:first td").each(function() {
		count += 1;
		if( $(this).attr('class') == 'commentCol') flagA = count-1;
		if( $(this).attr('class') == 'pointsCol') flagB = count-1;
	});
	for(var i = 0; i < count; ++i) {
		if(i == 0) {
			$(".pointsRow").append("<td class='pointsHolderRow' ></td>");
		} else if (i == flagA || i == flagB) {	
			if(flagA != i) $(".pointsRow").append("<td class='pointsHolderRow' style='background-color: #C99696;'></td>");
			else $(".pointsRow").append("<td class='commentCol' style='background-color: #C99696;'></td>");
		} else {
			$(".pointsRow").append("<td id='ptsWeightCol-"+i+"' class='pointsHolderRow pointsValueHor' style='height: 60px; background-color: #DFFED2;' placeholder='%' onclick='cellRealNumber($(this));'></td>");
		}
	}
}

function addColumn(tableId, colRW, colClass) { 
	if(colRW==undefined&&colClass==undefined)$(tableId + " tr").append(emptyCol()); 
	else{$(tableId + " tr").append(emptyCol(colRW, colClass))}
}

function refreshTableStyle(){$("#rubric_rc_table tr td:first-child").css("background-color","lightblue");}

function countTemplateRows(){
	var totalRows = 0;
	for(var i=0;i<templatesUsed.length;i++){
		totalRows+=$(buildTempTable(templatesUsed[i]) + " tr").length-1;
	}
	return totalRows; 
}

var tempRubricTitle;
var tempRubricDescription;
var tempRubricIcon;
function buildTempTable(rubricId){
	if($("#tempRubricDiv").length>0) $("#tempRubricDiv").remove();
	$("body").append('<div id="tempRubricDiv" style="display:none;"><div id="tempRubricTitle"></div><div id="tempRubricDesc"></div><div id="tempRubricIcon"></div><div id="tempDataRepo"></div><table id="tempRubricTable" border="1" ></table></div>');
	
	var rubricData=extractRubricFromDataSet(rubricId);
		var map = createMap(rubricData, DELIMETER1, DELIMETER2);
			tempRubricTitle = map.get("[title]");
			tempRubricDescription = map.get("[description]");
			tempRubricIcon = map.get("[icon]");
	
	$("#tempDataRepo").text(rubricData);
	$("#tempRubricTitle").text(tempRubricTitle);
	$("#tempRubricDesc").text(tempRubricDescription);
	$("#tempRubricIcon").text(tempRubricIcon);
	
	buildTableFromMap("#tempRubricTable", map)
	return "#tempRubricTable";
}

//removes [rubricId] part of [][][] and returns only where = the given rubricId
function extractRubricFromDataSet(rubricId){
	rubricId = "["+rubricId+"]";
	var data = $(".rubric_predefined_data_unprocessed").text();
	var newData = "";
	data=data.split(DELIMETER1);
	for(i=0; i<data.length;i++){
		var thisData = data[i].split(DELIMETER2);
		var temp=thisData[0];
		if(thisData[0].startsWith(rubricId))
			newData+=data[i].substring(rubricId.length)+DELIMETER1;
	}
	return newData;
}

var rubric_table_id_class = "";
function buildTableFromMap(tableId, map, readOnly){
	var myRub = getMapValue(map,"title").indexOf("- My Rubric") != -1 ? true : false;
	if(readOnly==undefined && !myRub) readOnly="";else{readOnly=(readOnly)?"":' onclick="cellText($(this));"';}
    var icon = map.get("[icon]");
    var withPoints = pointsAvailable();
    var withComments = commentsAvailable();
    icon = (icon == undefined || icon == "null")?"":'<img src="'+icon+'" width="150px" height="150px" style="display: block;margin-left: auto;margin-right: auto;" alt="rubric icon">';
    $(tableId).html('<tr >'
            +"<td>"+icon+"</td>"
            +"</tr>");
    var offset = withPoints ? 1 : 0;
    var sizes = mapSizes(map); //return [colSize,rowSize];
    var colSize = sizes[0];
    var rowSize = sizes[1];

    for(var i = 1 ;i < colSize+1 ; i++){
                if(map.get("["+i+"][0]").indexOf("[[COMMENTS]]") != -1 || isPoints(map.get("["+i+"][0]"))) continue;
                else $(tableId + " tr").append("<th>"+map.get("["+i+"][0]")+"</th>");
    };
    var v = map.get("[0][1]");
    var offset = (withPoints && isPoints(v)) ? 1 : 0;
    for(var i = 1 + offset ;i < rowSize+1 ; i++){
            var rowData = '<tr '+rubric_table_id_class+' ><th>' + map.get("[0]["+i+"]") +"</th>"
            if(isPoints(rowData)) continue; // skip row
            else {
                for(var j = 1 ;j < colSize+1 ; j++){
                    var theData = map.get("["+j+"]["+i+"]"); if(theData==undefined)theData="";
                    if(theData.indexOf("[[COMMENTS]]") != -1 || isPoints(theData)) continue;
                    rowData += '<td style="text-align: center;"'+readOnly+'>' + theData +"</td>";
                }
                rowData += "</tr>";
                $(tableId).append(rowData);
            }
    }
    rubric_table_id = "";
}

//Most common use:
//	createMap(data, DELIMETER1, DELIMETER2)
function createMap(data, delimeter, delimeter2){
	var map = new Map;
	var data = data.split(delimeter)
	
	for(var i = 0 ; i < data.length && data.length > 1 ; i++){
		if(data[i]==null || data[i].trim()=="undefined"
			|| data[i].trim()=="" || data[i].trim()==delimeter.trim()
			|| data[i].search(delimeter2)==-1)
			continue;
		
		var entrySplit = data[i].split(delimeter2);
		if(entrySplit.length==2)
			map.put(entrySplit[0].trim(),entrySplit[1].trim());
	}
	
	return map;			
}

function mapToString(map){
	var outputCurrentMap ='';
	for(var l = 0; l++ < map.size; map.next())
		outputCurrentMap += (map.hash(map.key())).substring(7) + DELIMETER2 + map.value() + DELIMETER1;
	//javascript: console.log(outputCurrentMap);
	return outputCurrentMap;
}

function mapSizes(map){
	var colSize=1, rowSize=1;
	
	while(map.get("[0]["+rowSize+"]")!=undefined) rowSize++;
	while(map.get("["+colSize+"][0]")!=undefined) colSize++;
	colSize--;rowSize--;
	return [colSize,rowSize];
}

function commentCol(tab){
    if(tab == undefined) tab = '#rub_predefined_preview_table';
    if($(".commentCol").length==0)  {
            addColumn( tab, "r", "commentCol");
            $(".commentCol:gt(0)").text("You can enter text in here while grading.");
            $(".commentCol:first").text("Comments");
    }
    else
            $(".commentCol").remove();
}

function removeCommentCol() {
    if($(".commentCol").length != 0) $(".commentCol").remove();
} 

function isPoints(val) {
    val = val.toUpperCase();
    return val.indexOf("% POINTS PER ROW") != -1 || 
    (val.indexOf("%")!= -1 && !isNaN(Number(val.replace(/(%)/g,"")))) || 
    val.indexOf("% GRADE PER CATEGORY") != -1;
}