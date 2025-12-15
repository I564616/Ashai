<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css"
          href="${contextPath}/_ui/addons/apbcmsaddon/responsive/common/css/slideshow-style.css"/>
    <script type="text/javascript" src="${commonResourcePath}/js/jquery-3.5.1.min.js"></script>
</head>
<body style="background-color:#d7d7d7;margin:auto">

<div id="wowslider-container1">
    <div class="ws_images">
        <ul>
            <c:forEach items="${banners}" var="banner" varStatus="status">
                <li>
                    <c:choose>
                        <c:when test="${not empty banner.urlLink}">
                            <a href="<c:url value='${banner.urlLink}'/>" target="_blank">
                                <img src="${banner.media.url}"
                                     alt="attachment ${status.count}"
                                     id="wows1_${status.count}" title="${banner.title}"/>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <img src="${banner.media.url}"
                                 alt="attachment ${status.count}"
                                 id="wows1_${status.count}" title="${banner.title}"/>
                        </c:otherwise>
                    </c:choose>
                </li>
            </c:forEach>
        </ul>
    </div>
    <div class="ws_bullets">
        <div>
            <c:forEach items="${banners}" var="banner" varStatus="status">
                <a href="#"><span class="dot"></span></a>
            </c:forEach>
        </div>
    </div>
</div>

<script type="text/javascript"
        src="${contextPath}/_ui/addons/apbcmsaddon/responsive/common/js/slider.js"></script>
<script type="text/javascript"
        src="${contextPath}/_ui/addons/apbcmsaddon/responsive/common/js/${animatedEffect}/script.js"></script>


<script>
    $('.ws_next div').css('margin-top', '9px').append('&#10095;');
    $('.ws_prev div').css('margin-top', '9px').append('&#10094;');
    object.duration = ${duration};
    object.delay = ${timeout};
    $("#wowslider-container1").wowSlider(object);
</script>

<style>
    @media all and (max-width: 576px) {
        #wowslider-container1 {
            width: ${mobile_width};
            height: ${mobile_height};
        }

        #wowslider-container1 .ws_images {
            height: ${mobile_height};
        }

        #wowslider-container1 .ws_images .ws_list img {
            height: ${mobile_height};
        }
    }


    @media all and (min-width: 769px) {
        #wowslider-container1 {
            width: ${desktop_width};
            height: ${desktop_height};
        }

        #wowslider-container1 .ws_images {
            height: ${desktop_height};
        }

        #wowslider-container1 .ws_images .ws_list img {
            height: ${desktop_height};
        }
    }

</style>
</body>
</html>