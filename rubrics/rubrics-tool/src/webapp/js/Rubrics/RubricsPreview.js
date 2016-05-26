/*
Created by Stephen Kane (skane9@rutgers.edu) and Kim Huang (kimhuang@rutgers.edu) for Rutgers University Rubrics Project.
Created for AssignmentInstructions.html
RU change
*/

$(function(){
	$("#rubricTable2").find("span").each(function(){
		theText=$(this).text();
		while(theText.indexOf("\n") != -1) {theText=theText.replace("\n", "<br>");} 
		$(this).html(theText);
	});
});

function rubricIsReleased(){
	var rubricReleased = false;
	$(".toggleSubsection h4").each(function(){
		if($(this).text()=="Feedback"){rubricReleased = true; return false; /*return false is equivalent to break*/}
	});
	return rubricReleased;
}
