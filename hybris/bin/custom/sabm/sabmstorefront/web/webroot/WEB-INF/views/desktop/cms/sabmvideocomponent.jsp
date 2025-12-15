<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<div class="content">
    <div class="faq-video-component clearfix">
        <a href="${video.url}">
            <svg class="icon-play-button">
                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-play-button"></use>
            </svg>
            <img src="${video.url}" alt="${video.altText}" title="${video.altText}" />
        </a>
    </div>
</div>