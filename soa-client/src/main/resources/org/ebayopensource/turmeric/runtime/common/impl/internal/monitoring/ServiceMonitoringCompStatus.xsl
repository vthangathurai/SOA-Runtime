<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" />
	<!--********************************************************
		** "Component" template used for Service Monitoring
		**********************************************************-->
	<xsl:template match="Component[@name = 'TurmericClientMonitoring' or @name = 'TurmericServerMonitoring']">
		<xsl:apply-templates select="ClientServiceMonitoring_Root"/>
		<xsl:apply-templates select="ServerServiceMonitoring_Root"/>
	</xsl:template>

	<xsl:template match="Error">
		<xsl:param name="errorColSpan"/>
		<td bgcolor="#D02090" colspan='{$errorColSpan}'>
			<pre><font color="#FFFFFF"><xsl:value-of select="."/></font></pre>
		</td>
	</xsl:template>

	<xsl:template match="ClientServiceMonitoring_Root">
		<xsl:choose>
			<xsl:when test="@single-service">
				<xsl:call-template name="ServiceMetricsHeading_detail">
					<xsl:with-param name="compName">Client</xsl:with-param>
				</xsl:call-template>
				<xsl:apply-templates select="ClientServiceMonitoring" mode="detail"/>
			</xsl:when>
			<xsl:otherwise>
				<form name="resetForm" action="ValidateInternals?component=TurmericClientMonitoring" method="post">
				<input id="action" name="action" value="reset" type="hidden"/>
				<input id="target" name="target" type="hidden"/>
				<table align="center" bgcolor="#FFCC00" border='1' width='100%'>
					<tr>
						<td colspan='1'>
							<H4><CENTER>Client Turmeric Monitoring</CENTER></H4>
						</td>
					</tr>
					<tr>
						<xsl:apply-templates select="Error">
							<xsl:with-param name="errorColSpan">1</xsl:with-param>
						</xsl:apply-templates>
					</tr>
					<xsl:apply-templates select="ClientServiceMonitoring" mode="list"/>
				</table>
				</form>
				<br/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="ServerServiceMonitoring_Root">
		<xsl:choose>
			<xsl:when test="@single-service">
				<xsl:call-template name="ServiceMetricsHeading_detail">
					<xsl:with-param name="compName">Server</xsl:with-param>
				</xsl:call-template>
				<xsl:apply-templates select="ServerServiceMonitoring" mode="detail"/>
			</xsl:when>
			<xsl:otherwise>
				<form name="resetForm" action="ValidateInternals?component=TurmericServerMonitoring" method="post">
				<input id="action" name="action" value="reset" type="hidden"/>
				<input id="target" name="target" type="hidden"/>
				<table align="center" bgcolor="#FFCC00" border='1' width='100%'>
					<tr>
						<td colspan='1'>
							<H4><CENTER>Server Turmeric Monitoring</CENTER></H4>
						</td>
					</tr>
					<tr>
						<xsl:apply-templates select="Error">
							<xsl:with-param name="errorColSpan">3</xsl:with-param>
						</xsl:apply-templates>
					</tr>
					<xsl:apply-templates select="ServerServiceMonitoring" mode="list"/>
				</table>
				</form>
				<br/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	

	<xsl:template match="ClientServiceMonitoring" mode="list">
		<tr><td>
			<table border="0"><tr><td width="100px" align="center">
			<a href="ValidateInternals?component=TurmericClientMonitoring&amp;detail={@name}"><xsl:value-of select="@name"/></a>
			</td><td width="100px" align="center">
			<a href="javascript:void(0)" onclick="document.getElementById('target').value='{@name}';document.forms['resetForm'].submit();return false">Reset Metrics</a><br/>
			</td></tr></table>			
			<xsl:apply-templates select="ServiceMetrics" mode="SOA_list"/>
		</td></tr>
	</xsl:template>

	<xsl:template match="ServerServiceMonitoring" mode="list">
		<tr><td>
			<table border="0"><tr><td width="100px" align="center">
			<a href="ValidateInternals?component=TurmericServerMonitoring&amp;detail={@name}"><xsl:value-of select="@name"/></a>
			</td><td width="100px" align="center">
			<a href="javascript:void(0)" onclick="document.getElementById('target').value='{@name}';document.forms['resetForm'].submit();return false">Reset Metrics</a><br/>
			</td></tr></table>
			<xsl:apply-templates select="ServiceMetrics" mode="SOA_list"/>
		</td></tr>
	</xsl:template>
	

	<xsl:template match="ClientServiceMonitoring" mode="detail">
		<xsl:call-template name="CommonServiceMetrics_detail">
			<xsl:with-param name="compName">Client</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="ServerServiceMonitoring" mode="detail">
		<xsl:call-template name="CommonServiceMetrics_detail">
			<xsl:with-param name="compName">Server</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="ServiceMetricsHeading_detail">
		<xsl:param name="compName"/>

		<xsl:variable name="categoryAdd">
			<xsl:choose>
				<xsl:when test="@category">
					&amp;category=<xsl:value-of select="@category"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="modeAdd">
			<xsl:choose>
				<xsl:when test="@mode">
					&amp;mode=<xsl:value-of select="@mode"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="baseUrl">
			ValidateInternals?component=Turmeric<xsl:value-of select="$compName"/>Monitoring&amp;detail=<xsl:value-of select="@single-service"/>
		</xsl:variable>

		<font face="arial" size="2">
			Mode: <a href="{$baseUrl}{$categoryAdd}">Latest Data</a> /
			<a href="{$baseUrl}{$categoryAdd}&amp;mode=diff">Diff</a>
			<br/>
			Category: <a href="{$baseUrl}{$modeAdd}&amp;category=all">All</a> /
			<a href="{$baseUrl}{$modeAdd}&amp;category=timing">Timing</a> /
			<a href="{$baseUrl}{$modeAdd}&amp;category=error">Errors</a> /
			<a href="{$baseUrl}{$modeAdd}&amp;category=other">Other</a>
			<br/>
			<a href="ValidateInternals?component=Tuemeric{$compName}Monitoring">Back to List</a>
			<br/>
		</font>
		<br/>

		<xsl:if test="@diff_error">Diff Error: <xsl:value-of select="@diff_error"/><br/><br/></xsl:if>
	</xsl:template>

	<xsl:template match="ServiceMetrics" mode="SOA_list">
		<table border="1" width="100%" bgcolor="#E0FFFF">
			<tr>
				<th align="center" width="10%">
					Status
				</th>
				<th align="center" width="10%">
					Metric
				</th>
				<th align="center" width="10%">
					Current Value
				</th>
				<th align="center" width="10%">
					Total Call Count
				</th>
				<th align="center" width="10%">
					Average Time Per Call
				</th>
				<th align="center" width="10%">
					Average Execution Time Per Call
				</th>
				<th align="center" width="10%">
					Total Error Count
				</th>
				<th align="center" width="10%">
					Execution Error Count
				</th>
				<th align="center" width="10%">
					Average Request Processing Time
				</th>
				<th align="center" width="10%">
					Average Response Processing Time
				</th>
			</tr>
			<tr>
				<xsl:choose>
					<xsl:when test="Error">
						<xsl:apply-templates select="Error">
							<xsl:with-param name="errorColSpan">7</xsl:with-param>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="timeTotalCount"
							select="metric[@name='SoaFwk.Time.Total']/component[@name='count']"/>

						<td><xsl:value-of select="metric[@name]/component[@name='serviceStatus']"/></td>
						<td><xsl:value-of select="metric[@name]/component[@name='metricName']"/></td>
						<td><xsl:value-of select="metric[@name]/component[@name='currentvalue']"/></td>

						<td><xsl:value-of select="$timeTotalCount"/></td>
						<td>
							<xsl:if test="$timeTotalCount!=0">
								<xsl:variable name="average"
									select="metric[@name='SoaFwk.Time.Total']/component[@name='totalTime'] div $timeTotalCount div 1000000.0" />
								<xsl:value-of select="format-number($average, '#.00')"/>ms
							</xsl:if>
						</td>
						<td>
							<xsl:variable name="bizTimeTotalCount"
								select="metric[@name='SoaFwk.Time.Call']/component[@name='count']"/>
							<xsl:if test="$bizTimeTotalCount!=0">
								<xsl:variable name="averageBiz"
									select="metric[@name='SoaFwk.Time.Call']/component[@name='totalTime'] div $bizTimeTotalCount div 1000000.0" />
								<xsl:value-of select="format-number($averageBiz, '#.00')"/>ms
							</xsl:if>
						</td>
						<td><xsl:value-of
							select="metric[@name='SoaFwk.Err.Total']/component[@name='value']" />
						</td>
						<td><xsl:value-of
							select="metric[@name='SoaFwk.FailedCalls']/component[@name='value']" />
						</td>
						<td>
							<xsl:variable name="reqTimeTotalCount"
								select="metric[@name='SoaFwk.Time.Pipeline_Request']/component[@name='count']"/>
							<xsl:if test="$reqTimeTotalCount!=0">
								<xsl:variable name="averageReq"
									select="metric[@name='SoaFwk.Time.Pipeline_Request']/component[@name='totalTime'] div $reqTimeTotalCount div 1000000.0" />
								<xsl:value-of select="format-number($averageReq, '#.00')"/>ms
							</xsl:if>
						</td>
						<td>
							<xsl:variable name="respTimeTotalCount"
								select="metric[@name='SoaFwk.Time.Pipeline_Response']/component[@name='count']"/>
							<xsl:if test="$respTimeTotalCount!=0">
								<xsl:variable name="averageResp"
									select="metric[@name='SoaFwk.Time.Pipeline_Response']/component[@name='totalTime'] div $respTimeTotalCount div 1000000.0" />
								<xsl:value-of select="format-number($averageResp, '#.00')"/>ms
							</xsl:if>
						</td>

					</xsl:otherwise>
				</xsl:choose>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="CommonServiceMetrics_detail">
		<xsl:param name="compName"/>

		<table border="1" width="100%">
			<tr bgcolor="#FFCC00">
				<td colspan='3'>
					<H4><CENTER>Detailed <xsl:value-of select="$compName"/> Monitoring Data for <xsl:value-of select="@name"/></CENTER></H4>
					<xsl:if test="../@category or ../@interval">
						<xsl:if test="../@category">Category Filter: <xsl:value-of select="../@category"/><br/></xsl:if>
						<xsl:if test="../@interval">Diff Interval: <xsl:value-of select="../@interval"/><br/></xsl:if>
					</xsl:if>
				</td>
			</tr>
			<tr bgcolor="#FFCC00">
				<th width="40%">Metric</th>
				<th width="30%">Operation</th>
				<th width="30%">Value</th>
			</tr>
			<xsl:apply-templates select="ServiceMetrics" mode="SOA_detail"/>
			<xsl:apply-templates select="OperationMetrics" mode="SOA_detail"/>
		</table>
	</xsl:template>

	<xsl:template match="ServiceMetrics" mode="SOA_detail">
		<xsl:call-template name="CommonOperationMetrics_detail">
			<xsl:with-param name="opName">ALL</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="OperationMetrics" mode="SOA_detail">
		<xsl:call-template name="CommonOperationMetrics_detail">
			<xsl:with-param name="opName"><xsl:value-of select="@name"/></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="CommonOperationMetrics_detail">
		<xsl:param name="opName"/>
		<xsl:apply-templates select="metric" mode="CommonOperationMetric_detail">
			<xsl:with-param name="opName"><xsl:value-of select="$opName"/></xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="metric" mode="CommonOperationMetric_detail">
		<xsl:param name="opName"/>
		<xsl:apply-templates select="component" mode="CommonOperationMetricComponent_detail">
			<xsl:with-param name="opName"><xsl:value-of select="$opName"/></xsl:with-param>
			<xsl:with-param name="metricName"><xsl:value-of select="@name"/></xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="component" mode="CommonOperationMetricComponent_detail">
		<xsl:param name="opName"/>
		<xsl:param name="metricName"/>
		<tr>
			<td><xsl:value-of select="$metricName"/>.<xsl:value-of select="@name"/></td>
			<td><xsl:value-of select="$opName"/></td>
			<td><xsl:value-of select="."/></td>
		</tr>
	</xsl:template>

</xsl:stylesheet>
