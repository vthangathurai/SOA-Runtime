<?xml version="1.0" encoding="UTF-8"?>
<!--
  Purpose: Code-Generate service error descriptor registration file  to 
  	v3core/MarketPlaceServiceCommon/src/com/ebay/marketplace/services/common/error/MarketPlaceCommonErrorDescriptor
  Author:  Vaibhav Joshi
  Date:    21/28/2007
-->
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsla="urn:xsl-alias">
  <xsl:namespace-alias stylesheet-prefix="xsla" result-prefix="xsl"/>
  <xsl:output method="text" indent="yes" encoding="UTF-8"/>
  <xsl:key name="entities-id-value" match="ErrorDescriptors/ErrorDescriptor" use="@id"/>
  <xsl:template match="/">
	<xsl:variable name ="subDomain" >
		<xsl:value-of select="ErrorDescriptors/SubDomain"/>
	</xsl:variable>
	<xsl:variable name ="package" >
		<xsl:value-of select="ErrorDescriptors/Package"/>
	</xsl:variable>
	<xsl:variable name ="className" >
		<xsl:value-of select="ErrorDescriptors/ClassName"/>
	</xsl:variable>
  <xsl:variable name ="serviceEntityFile" >
    <xsl:text>../error/</xsl:text><xsl:value-of select="$className"/><xsl:text>.xml</xsl:text>
  </xsl:variable>
 	<xsl:text>package </xsl:text><xsl:value-of select="$package"/><xsl:text>;

import com.ebay.kernel.CodeGenerated;
import com.ebay.marketplace.services.ErrorCategory;
import com.ebay.marketplace.services.ErrorSeverity;
import com.ebay.marketplace.services.common.error.ServiceBaseErrorDescriptor;


/**
 * Please DONOT EDIT/CHECKIN this file. If you want to add new Errors to this file
 * please reserve the error in Entity File </xsl:text><xsl:value-of select="$className"/><xsl:text>.xml 
 *   
 * For more information please see wiki - http://wiki.arch.ebay.com/index.php?page=SOAErrorDescriptors
 * 
 * @author codegen
 */
public final class </xsl:text><xsl:value-of select="$className"/><xsl:text> extends ServiceBaseErrorDescriptor implements CodeGenerated{

	</xsl:text>
	
<xsl:variable name="unique-key-list"
select="//ErrorArgument[not(@name=following::ErrorArgument/@name)]" />
<xsl:for-each select="$unique-key-list">
	<xsl:text>public static final String PARAM_</xsl:text><xsl:value-of select="@name"/><xsl:text> = "</xsl:text><xsl:value-of select="@name"/><xsl:text>";
	</xsl:text>
</xsl:for-each>

	<xsl:text>
	private static final long serialVersionUID = 1L;
	private static final String SUB_DOMAIN = "</xsl:text><xsl:value-of select="$subDomain"/><xsl:text>";

	private </xsl:text><xsl:value-of select="$className"/><xsl:text>(long errorId, String errorName, String subDomain, ErrorSeverity errorSeverity, 
			ErrorCategory category, String message){
		super(errorId, errorName, subDomain, errorSeverity, category, message);
	}
 
</xsl:text>

<xsl:for-each select="ErrorDescriptors/ErrorDescriptor">
    <xsl:variable name="metadataCode" select="@id"/>
    <xsl:variable name="value">
      <xsl:for-each select="document($serviceEntityFile)">
        <xsl:value-of select="key('entities-id-value',$metadataCode)/@value" disable-output-escaping = "yes"/>
      </xsl:for-each>
    </xsl:variable>    
    <xsl:text>	public static final </xsl:text><xsl:value-of select="$className"/><xsl:text> </xsl:text><xsl:value-of select="$metadataCode"/><xsl:text> = new </xsl:text><xsl:value-of select="$className"/><xsl:text>(
			</xsl:text><xsl:value-of select="$value"/><xsl:text>,
			"</xsl:text><xsl:value-of select="$metadataCode"/><xsl:text>",
			SUB_DOMAIN,
			ErrorSeverity.</xsl:text><xsl:value-of select="@severity"/><xsl:text>,
			ErrorCategory.</xsl:text><xsl:value-of select="@category"/><xsl:text>,
			"</xsl:text><xsl:value-of select="Description"/><xsl:text>");
</xsl:text>
 </xsl:for-each>
  <xsl:text>
}</xsl:text>
 </xsl:template>
</xsl:stylesheet>
