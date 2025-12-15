<%@ page trimDirectiveWhitespaces="true"%>
<%@ page session="false" trimDirectiveWhitespaces="true" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>


<c:url value="/" var="baseUrl"/>
<c:url value="/_ui/desktop/SABMiller/img/error/full-logo-white.png" var="fullLogoWhiteUrl" />
<c:url value="/serviceRequest" var="serviceRequestUrl"/>
<c:url value="/_ui/desktop/SABMiller/img/error/small-logo.png" var="smallLogoUrl" />
<c:url value="/_ui/desktop/SABMiller/img/error/500.jpg" var="error500Url" />
<c:url value="/_ui/desktop/SABMiller/css/style.css" var="styleSheetUrl" />


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<title>Server Error</title>
<link rel="stylesheet" type="text/css" media="all"	href="${styleSheetUrl}" />
</head>
<body>
	<div class="main-content">
		<div id="header" class="global-header hidden-xs">
			<div class="container">
				<div class="row">
					<div class="col-sm-3">
						<div class="margin-top-20 offset-bottom-small">
							<div class="simple_disp-img simple-banner">
								<a href="${baseUrl}">
									<img src="${fullLogoWhiteUrl}" alt="CUB" class="visible-md-block visible-lg-block"/>
								</a>
							</div>
						</div>
					</div>
					<div class="col-sm-9">
						<div class="global-header-list">
							<ul class="select-items header">
								<li><a href="${serviceRequestUrl}">Contact Us</a></li>
							</ul>
						</div>

					</div>
				</div>
			</div>
		</div>

		<nav id="nav" class="global-navigation navbar megamenu">
			<div class="navbar-header hidden-sm hidden-md hidden-lg">
				<div class="row">
					<div class="col-xs-4"></div>
					<div class="col-xs-4">
						<div class="simple_disp-img simple-banner text-center">
								<a href="${baseUrl}">
									<img src="${smallLogoUrl}" alt="CUB"/>
								</a>
							</div>
					</div>
					<div class="col-xs-4">
						<div class="global-header-list margin-top-20 icon-offset-medium">
							<ul class="select-items header">
								<li><a href="${serviceRequestUrl}">Contact Us</a></li>
							</ul>
						</div>
					</div>
				</div>
			</div>

		</nav>


		<div class="container">
			<div>
				<a href="${baseUrl}" class="btn btn-primary">Home</a>
			</div>
			<div class="banner-image banner-top">
				<img src="${error500Url}" alt="ErrorPage" class="visible-sm-block visible-md-block visible-lg-block" />
				<img src="${error500Url}" alt="ErrorPage" class="visible-xs-block" />
			</div>
		</div>
		<div class="footer-wrap container-lg light-grey-backround margin-top-30">
			<div class="container">
				<div id="footer" class="footer">
					<div class="copyright">&copy; Carlton &amp; United Breweries</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>