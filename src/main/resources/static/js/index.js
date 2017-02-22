$(document).ready(function(){

	$(".info__try-block").click(function(event){
		event.preventDefault();
				var top = $(this).offset().top;  // определяем отступ блока от верхнего края окна 
				$("html, body").animate({
					scrollTop: top
				},1000);
			});

});