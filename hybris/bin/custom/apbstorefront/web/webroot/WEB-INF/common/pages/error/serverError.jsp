<!-- render to home page  -->
<script>
if(window.location.href.indexOf("/storefront") <= -1) {
	window.location.href = "../storefront/serverError";
} else{
	window.location.href = "../serverError";
}
</script>
