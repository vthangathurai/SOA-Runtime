<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" />
	<!--********************************************************
		** "Component" template used for SOA Log Viewer
		**********************************************************-->
	<xsl:template match="Component[@name = 'SOA Metric Viewer']">
		<table bgcolor="#FFCC00" border="1">
			<tr>
				<td colspan="100%">
					<H4>
						<CENTER>
							<B>
								<xsl:value-of select="@name" />
							</B>
						</CENTER>
					</H4>
				</td>
			</tr>
			<tr>
				<td colspan="5">
					<B>Metric File Path =</B>
					<xsl:value-of select="@logFilePath" />
				</td>
			</tr>
			<tr bgcolor="#ffffcc">
				<th>Snapshot Time</th>
				<th>Service Name</th>
				<th>Operation Name</th>
				<th>Metric</th>
				<th>Use Case</th>
				<th>Client Data Center</th>
				<th>Server Data Center</th>
				<th>Value Part 1 (Count)</th>
				<th>Value Part 2</th>
				<th>Average</th>
			</tr>
			<xsl:apply-templates select="Properties" />
		</table>
		<br />
	</xsl:template>
	<xsl:template match="Properties">
		<tr bgcolor="ffffff">
			<xsl:apply-templates select="Property" />
		</tr>
	</xsl:template>
	<xsl:template match="Property">
		<!-- Invoke templates according to table column headings -->
		<td>
			<xsl:value-of select="." />
		</td>
	</xsl:template>
</xsl:stylesheet>
