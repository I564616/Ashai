<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<nav>
	<div class="row">
		<div class="col-md-3 col-xs-6 nav-item">
			<button data-id="contact-us" data-label="<spring:theme code="text.credit.tab.contactus"/>" class="nav-link btn-block">
				<svg class="icon-support">
				  <use xlink:href="#icon-support"></use>    
				</svg>
				<span><spring:theme code="text.credit.tab.contactus"/></span>
			</button>
		</div>
		<div class="col-md-3 col-xs-6 nav-item">
			<button data-id="faqs" data-label="<spring:theme code="text.credit.tab.faqs"/>" class="nav-link btn-block">
				<svg class="icon-search-line">
				  <use xlink:href="#icon-search-line"></use>    
				</svg>
				<span><spring:theme code="text.credit.tab.faqs"/></span>
			</button>
		</div>
		<div class="col-md-3 col-xs-6 nav-item">
			<button data-id="video-tutorials" data-label="<spring:theme code="text.credit.tab.videotutorials"/>" class="nav-link btn-block">
				<svg class="icon-play-line">
				  <use xlink:href="#icon-play-line"></use>    
				</svg>
				<span><spring:theme code="text.credit.tab.videotutorials"/></span>
			</button>
		</div>
		<div class="col-md-3 col-xs-6 nav-item">
			<button data-id="learn-more" data-label="<spring:theme code="text.credit.tab.learnmore"/>" class="nav-link btn-block">
				<svg class="icon-learnMore-line">
				  <use xlink:href="#icon-learnMore-line"></use>    
				</svg>
				<span><spring:theme code="text.credit.tab.learnmore"/></span>
			</button>
		</div>
	</div>
</nav>