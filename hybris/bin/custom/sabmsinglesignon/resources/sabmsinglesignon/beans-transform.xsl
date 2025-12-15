<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bean="http://www.springframework.org/schema/beans">

    <xsl:output omit-xml-declaration="no" method="xml" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <!-- Identity Transform -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- Remove existing beans -->
    <xsl:template match="bean:beans/bean:bean[@id='defaultSamlAuthenticationProvider' or @id='samlAuthenticationProvider']" />

    <!-- Add new beans -->
    <xsl:template match="bean:beans">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
            <bean:bean id="defaultSamlAuthenticationProvider" class="de.hybris.platform.samlsinglesignon.security.SamlObjectsFactory" factory-method="getAuthenticationProvider"/>
            <bean:bean id="samlAuthenticationProvider" class="com.sabmiller.staff.singlesignon.AsahiSamlObjectsFactory" factory-method="getAuthenticationProvider">
                <bean:constructor-arg>
                    <bean:ref bean="defaultSamlAuthenticationProvider"/>
                </bean:constructor-arg>
            </bean:bean>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>