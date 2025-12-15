<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<!-- <div class="tabs js-tabs tabs-responsive"> -->
<div class="panel-group" id="accordion" role="tablist"
	aria-multiselectable="true">
	<div class="panel panel-default">
		<section class="tabs">
			<ul class="nav nav-tabs" role="tablist">
				<li role="presentation" class="">
					<a href="#about"
					aria-controls="about" role="tab" data-toggle="tab"
					aria-expanded="false">
						<span class="h3">
							<spring:theme code="text.product.detail.tab.about.title" />
						</span>
					</a>
				</li>
				<li role="presentation" class="active"><a href="#config"
					aria-controls="config" role="tab" data-toggle="tab"
					aria-expanded="true">
					<span class="h3">
						<spring:theme code="text.product.detail.tab.packconfiguration.title" />
					</span>
					</a>
				</li>
			</ul>

			<!-- the following tab content will be replace by the real data later -->

			<div class="tab-content">
				<div role="tabpanel" class="tab-pane wysiwyg active" id="about">
					<p>Carlton Draught is a traditional, full-strength lager that
						is crisp on the mid-palate with a good malt character and smooth
						full-bodied flavour. Clean hop bitterness gives the brew a
						slightly dry finish. Synonymous with the origins of the brewery
						itself, Carlton Draught is renowned for tasting as fresh as it did
						the day it was brewed. And it's that kind of consistency in taste
						that's been important to Australians for generations.</p>

					<div class="row">
						<div class="col-md-6">
							<h4 class="h4-alt">
								<spring:theme code="text.product.detail.tab.about.details" />
							</h4>
							<table>
								<tbody>
									<tr>
										<td>Brand Family</td>
										<td>Carlton</td>
									</tr>
									<tr>
										<td>Style</td>
										<td>Lager</td>
									</tr>
									<tr>
										<td>Dimensions</td>
										<td>75cm x 50cm x 24cm</td>
									</tr>
									<tr>
										<td>Weight Case</td>
										<td>25kg</td>
									</tr>
									<tr>
										<td>Alcohol Volume</td>
										<td>4.6%</td>
									</tr>
									<tr>
										<td>Size</td>
										<td>375ml</td>
									</tr>
									<tr>
										<td>Country</td>
										<td>Australia</td>
									</tr>
									<tr>
										<td>SKU</td>
										<td>1234456</td>
									</tr>
									<tr>
										<td>EAN</td>
										<td>76862487754</td>
									</tr>
								</tbody>
							</table>
						</div>
						<div class="col-md-6">
							<h4 class="h4-alt">Food Match</h4>
							<span>Try Carlton Draught with:</span>
							<ul>
								<li>BBQ Meats</li>
								<li>Pizza</li>
								<li>Cajun Food</li>
								<li>Meat Pie</li>
							</ul>

							<h4 class="h4-alt">Find out more</h4>
							<span>More in <a href="#">CUB Customer Portal</a></span> <span>Visit
								<a href="#">www.carltondraught.com</a>
							</span>
						</div>
					</div>
				</div>


				<div role="tabpanel" class="tab-pane wysiwyg" id="config">
					<ul>
						<li>4 x 6 x 325ml Bottles</li>
					</ul>
				</div>
			</div>













			<cms:pageSlot position="Tabs" var="tabs">
				<cms:component component="${tabs}" />
			</cms:pageSlot>

		</section>
	</div>

</div>
