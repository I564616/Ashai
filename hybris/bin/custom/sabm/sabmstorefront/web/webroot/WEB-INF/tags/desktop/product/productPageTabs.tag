<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<section class="tabs border-divider visible-sm-block visible-md-block visible-lg-block margin-top-30">
  <ul class="nav nav-tabs" role="tablist">
      <li role="presentation" class="active"><a href="#aboutSection" aria-controls="aboutSection" role="tab" data-toggle="tab"><span class="h3"><spring:theme code="text.product.detail.tab.about.title" /></span></a></li>
      <li role="presentation"><a href="#configSection" aria-controls="configSection" role="tab" data-toggle="tab"><span class="h3"><spring:theme code="text.product.detail.tab.packconfiguration.title" /></span></a></li>
  </ul>
  <div class="tab-content">
      <div role="tabpanel" class="tab-pane wysiwyg active" id="aboutSection">
          <product:productDetailsTab product="${product}"/>
      </div>
      <div role="tabpanel" class="tab-pane wysiwyg" id="configSection">
          <product:productPackConfigurationTab product="${product}"/>
      </div>
  </div>
</section>