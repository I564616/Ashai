<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<div class="clearfix"></div>
<div class="row phone-livechat">
	<div class="col-md-4 col-sm-5 col-xs-6">
		<div class="phone">
			<h3>
				<!--<svg class="icon-phone-contactus">
                  	<use xlink:href="#icon-phone-contactus"></use>
				</svg>-->
				<img src="/_ui/desktop/SABMiller/img/phone-contactus.png" class="contact-us-icon"  alt="Contact Us" />
				<spring:theme code="text.contactus.phone" />				
			</h3>
			<p>
				<spring:theme code="text.contactus.phoneFirstLine" /><br/>
				
                <spring:theme code="text.contactus.phoneThirdLine" />
			</p>
		</div>
	</div>
	<div class="col-md-4 col-sm-7 col-xs-6">
		<div class="live-chat">
			<h3>
				<svg class="icon-livechat-contactus">
                  	<use xlink:href="#icon-livechat-contactus"></use>
				</svg>
				<spring:theme code="text.contactus.livechat" />	
			</h3>
			<p>
				<spring:theme code="text.contactus.livechat.hours" />
			</p>
		</div>
	</div>
</div>