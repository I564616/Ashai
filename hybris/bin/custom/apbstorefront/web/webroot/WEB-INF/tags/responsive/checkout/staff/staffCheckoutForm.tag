<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="staff-checkout" tagdir="/WEB-INF/tags/responsive/checkout/staff"%>
<%@ taglib prefix="single-checkout" tagdir="/WEB-INF/tags/responsive/checkout/single"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>


<script>
    var selectedEmails = [ <c:forEach items="${selectedCustomerEmailIds}" var="email">'${email}',</c:forEach> ];
    var custEmails = [ <c:forEach items="${customerEmailIds}" var="email">'${email}',</c:forEach > ];
</script>

<div class="checkout-shipping">
    <div class="checkout-indent">
        <div class="customer-permission">
            <!--Customer permission section-->
            <div class="checkout_subheading"><spring:theme code="staff.checkout.sga.customer.permission.heading"/></div>
            
            <label for="customerPermission">
                <input type="checkbox" name="customerPermission" id="customerPermission">
                <spring:theme code="staff.checkout.sga.customer.permission.text"/>
            </label>
        </div>
        
        <div id="checkoutForm" class="hide">
            <div class="order-details-form">
                <div class="row">
                    <div class="col-sm-12 col-md-12">
                        <p>
                            <strong><spring:theme code="staff.checkout.sga.order.details.heading"/></strong><br>
                            <spring:theme code="staff.checkout.sga.order.details.text"/>
                        </p>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-3 col-sm-6">
                        <form:form method="post" modelAttribute="customerCheckoutForm.bdeCheckoutForm" id="bdeCheckoutForm">
                            <form:textarea rows="3" cols="40" path="emailText" id="orderDetailsText"/>
                            <input type="hidden" name="bdeCheckoutForm.users[0].email" value="${bdeUserEmailId}"/>
                        </form:form>
                        
                    </div>
                </div>
            </div>

            <multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
                <jsp:body>
                    <ycommerce:testId code="checkoutStepOne">
                        <div class="checkout-shipping">
                            <div class="checkout-indent">
                                <div class="payment-type">
                                    <div class="checkout_subheading">
                                        <spring:theme code="checkout.summary.payment"/>
                                    </div>
                                    <p id="ptest">
                                        <b>
                                            <spring:theme code="checkout.summary.delivery.reference.number"/>
                                        </b>
                                        <span id="optional-text">
                                            <spring:theme code="checkout.summary.optional.field"/>
                                        </span>
                                    </p>
                                    <div class="row">
                                        <div class="col-md-3 col-sm-6">
                                            <form:input class="form-control" type="text" path="poNumber" maxlength="20"/>
                                        </div>
                                    </div>
                                    <p></p>
                                    <single-checkout:paymentTypeForm/>
                                </div>
                                <div class="checkout_subheading">
                                    <spring:theme code="checkout.summary.delivery"/>
                                </div>
                                <single-checkout:addressDataForm deliveryAddresses="${deliveryAddresses}"/>
                                <single-checkout:deliveryMethodForm/>
                            </div>
                        </div>
                    </ycommerce:testId>
                </jsp:body>
            </multi-checkout:checkoutSteps>

            <!--Customer email section-->
            <div class="customer-emails-section">
                <div class="checkout_subheading"><spring:theme code="staff.checkout.sga.customer.emails.heading"/></div>
                <div class="row">
                    <div class="col-md-12 col-sm-12">
                        <p><spring:theme code="staff.checkout.sga.customer.emails.text"/></p>
                    </div>
                </div>
                <div class="customer-emails" id="customerEmailsList">
                    <%-- customer emails checkbox list go here --%>
                </div>
                <div id="customerEmailsFormData">
                    <%-- customer emails form data goes here --%>
                </div>
                <div>
                    <p id="ptest">
                        <b><spring:theme code="staff.checkout.sga.customer.emails.additional.text"/></b>
                        <span id="optional-text"><spring:theme code="checkout.summary.optional.field"/></span>
                    </p>
                </div>
                <div class="row">
                    <div class="col-md-3 col-sm-6">
                        <input type="text" name="newEmail" id="newEmail" class="form-control">
                        <div class="help-block newemail" id="newEmailError">
                            <span><spring:theme code="staff.checkout.sga.customer.emails.invalid"/></span>
                        </div>
                    </div>
                    <div class="col-md-3 col-sm-6">
                        <button type="button" name="addNewEmailBtn" id="addNewEmailBtn" class="btn btn-primary btn-small form-control"><spring:theme code="staff.checkout.sga.add.email.button.text"/></button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
