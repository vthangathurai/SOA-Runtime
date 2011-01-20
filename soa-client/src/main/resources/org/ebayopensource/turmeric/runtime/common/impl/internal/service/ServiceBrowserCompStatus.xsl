<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" />
	<!--********************************************************
		** "Component" template used for Service Browser
		**********************************************************-->
	<xsl:template match="Component[@name = 'TurmericClientBrowser' or @name = 'TurmericServerBrowser']">
		<xsl:apply-templates select="ClientServiceBrowser"/>
		<xsl:apply-templates select="ServerServiceBrowser"/>
	</xsl:template>

	<xsl:template match="Error">
		<xsl:param name="errorColSpan"/>
		<td bgcolor="#D02090" colspan='{$errorColSpan}'>
			<pre><font color="#FFFFFF"><xsl:value-of select="."/></font></pre>
		</td>
	</xsl:template>

	<xsl:template match="ClientServiceBrowser">
		<table align="center" bgcolor="#FFCC00" border='1' width='100%'>
			<tr>
				<td colspan='4'>
					<H4><CENTER>Client Service Browser</CENTER></H4>
				</td>
			</tr>
			<tr bgcolor="#ffffcc">
				<th>Client Name</th>
				<th>Service UID</th>
				<th>URL</th>
				<th>Status</th>
			</tr>
			<tr>
				<xsl:apply-templates select="Error">
					<xsl:with-param name="errorColSpan">4</xsl:with-param>
				</xsl:apply-templates>
			</tr>
			<xsl:apply-templates select="ClientServiceDesc"/>
		</table>
		<br/>
	</xsl:template>

	<xsl:template match="ServerServiceBrowser">
		<table align="center" bgcolor="#FFCC00" border='1' width='100%'>
			<tr>
				<td colspan='3'>
					<H4><CENTER>Server Service Browser</CENTER></H4>
				</td>
			</tr>
			<tr bgcolor="#ffffcc">
				<th>Service Name</th>
				<th>Service UID</th>
				<th>Status</th>
			</tr>
			<tr>
				<xsl:apply-templates select="Error">
					<xsl:with-param name="errorColSpan">3</xsl:with-param>
				</xsl:apply-templates>
			</tr>
			<xsl:apply-templates select="ServerServiceDesc"/>
		</table>
		<br/>
	</xsl:template>

	<xsl:template match="ClientServiceDesc">
		<xsl:variable name="dsBgColor">
			<xsl:call-template name="SelectStatusColor"/>
		</xsl:variable>

		<tr bgcolor="{$dsBgColor}">
			<td><a href="ValidateInternals?component=TurmericClientBrowser&amp;forceXml=true&amp;detail={@name}"><xsl:value-of select="@name"/></a></td>

			<xsl:choose>
				<xsl:when test="Error">
					<xsl:apply-templates select="Error">
						<xsl:with-param name="errorColSpan">3</xsl:with-param>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<td><xsl:value-of select="qname"/></td>
					<td><a href="{url}?X-TURMERIC-SERVICE-NAME={@admin-name}&amp;X-TURMERIC-OPERATION-NAME=getServiceVersion"><xsl:value-of select="url"/></a></td>
					<td><xsl:call-template name="RenderStatusText"/></td>
				</xsl:otherwise>
			</xsl:choose>
		</tr>

		<tr>
			<th></th>
			<td colspan="3">
				<table border="1" width="100%" bgcolor="#E0FFFF">
					<xsl:call-template name="RenderBaseServiceDesc"/>
				</table>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="ServerServiceDesc">
		<xsl:variable name="dsBgColor">
			<xsl:call-template name="SelectStatusColor"/>
		</xsl:variable>

		<tr bgcolor="{$dsBgColor}">
			<td><a href="ValidateInternals?component=TurmericServerBrowser&amp;forceXml=true&amp;detail={@name}"><xsl:value-of select="@name"/></a></td>

			<xsl:choose>
				<xsl:when test="Error">
					<xsl:apply-templates select="Error">
						<xsl:with-param name="errorColSpan">3</xsl:with-param>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<td><xsl:value-of select="qname"/></td>
					<td><xsl:call-template name="RenderStatusText"/></td>
				</xsl:otherwise>
			</xsl:choose>
		</tr>

		<tr>
			<th></th>
			<td colspan="2">
				<table border="1" width="100%" bgcolor="#E0FFFF">
					<xsl:call-template name="RenderBaseServiceDesc"/>
					<tr><td>Impl</td><td><xsl:value-of select="impl-class"/></td></tr>
				</table>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="RenderBaseServiceDesc">
		<tr><td>Interface</td><td>
		<xsl:choose>
			<xsl:when test="interface-class"><xsl:value-of select="interface-class"/></xsl:when>
			<xsl:otherwise>N/A</xsl:otherwise>
		</xsl:choose>
		</td></tr>
	</xsl:template>

	<xsl:template name="SelectStatusColor">
			<xsl:choose>
				<xsl:when test="@is-down='true'">
					<xsl:text>#FF7700</xsl:text>
				</xsl:when>
				<xsl:when test="@is-partial-down='true'">
					<xsl:text>#FFF077</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>#7FFF00</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderStatusText">
		<xsl:choose>
			<xsl:when test="@is-down='true'">
				<xsl:value-of select="markdown-status"/>
			</xsl:when>
			<xsl:when test="@is-partial-down='true'">Partial Markdown</xsl:when>
			<xsl:otherwise>Up</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
