/**
* For toggling the Minimize and Maximize tools menu in Morpheus: Adds classes to the <body> and changes the label text for accessibility
*/

function toggleMinimizeNav(){

  $PBJQ('body').toggleClass('Mrphs-toolMenu-collapsed');

  var el = $PBJQ(this);
  var label = $PBJQ('.accessibility-btn-label' , el);

  el.toggleClass('min max');
  
  if (label.text() == el.data("title-expand") || collapsed) {
	document.cookie = "sakai_nav_minimized=false; path=/";
	collapsed = false;
	label.text(el.data("text-original"));
    el.attr('title', (el.data("text-original")));
    el.attr('aria-pressed', true);
  } else {
	document.cookie = "sakai_nav_minimized=true; path=/";
	collapsed = true;
	el.data("text-original", label.text());
    label.text(el.data("title-expand"));
    el.attr('title', (el.data("title-expand")));
    el.attr('aria-pressed', false);
  }
}

$PBJQ(".js-toggle-nav").on("click", toggleMinimizeNav);

var collapsed = false;

var $window = $PBJQ(window),
	$tools	= $("#toolMenu"),
	$bread = $(".Mrphs-siteHierarchy"),
	padding	= $bread.height() 
		+ getNumPart($bread.css('padding-top'))
		+ getNumPart($bread.css('padding-bottom'))
		+ $(".Mrphs-topHeader").height();

$PBJQ(document).ready(function(){
	if(getCookieVal('sakai_nav_minimized') === 'true') {
		$PBJQ(".js-toggle-nav").click();
		collapsed = true;
	}
});

$PBJQ(window).scroll(function(){
	var topPad = $(".pasystem-banner-alerts").height();
	var follow = ($window.height()- (padding + topPad)) > $tools.height() 
					&& ($window.scrollTop() > padding);
	if($("#toolMenuWrap").css('position') !== 'fixed'
		&& follow && $window.scrollTop() > 0) {
		$("#toolMenu").stop().animate({
            top: $window.scrollTop() + topPad - padding
        });
	} else {
		$("#toolMenu").stop().animate({
			top: 0
	    });
	}
});

function getCookieVal(cookieName) {
	var cks = document.cookie.split(';');
	for (var i = 0; i < cks.length; ++i) {
		var curCookie = (cks[i].substring(0,cks[i].indexOf('='))).trim();
		if(curCookie === cookieName) {
			return ((cks[i].split('='))[1]).trim();;
		}
	}
	return 'false';
}

function getNumPart(val) {
	for(var i = val.length - 1; i >= 0; i--) {
		if(!isNaN(Number(val.charAt(i)))) {
			return Number(val.substring(0,i+1));
		}
	}
}
