<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--  
  Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
-->

<xsl:template name='page-layout'>
<xsl:param name='root-node'/>
<xsl:param name='title-text'/>
    <html>
    <head>
    <title><xsl:value-of select='$title-text'/></title>
    <script language="JavaScript">
        function getLogs(theForm) {
            document.forms[theForm].submit();
        }
    </script>
    </head>
      <body>
        <table>
        <tr>
    		<td valign="middle"><a href="ValidateInternals"><IMG SRC="resources/images/turmeric_logo.gif" border="0"/></a></td>
    		<td valign="middle"><font face="arial" size="5" valign="middle">
    			<b>Turmeric Administration Console</b></font>
    			<br/>
                <font face="arial" size="3" valign="middle"><b>Host: <xsl:value-of select="$root-node/@host"/></b>
                <xsl:if test='boolean($root-node/@client-ip)'>
                (client IP <xsl:value-of select="$root-node/@client-ip"/>)
                </xsl:if>
                <xsl:if test="$root-node/@more-info">
                    <xsl:apply-templates select="$root-node/HeaderPH"/>                 
                </xsl:if>
                </font>
            </td>
          </tr>
        </table>
        <table cellpadding="5" cellspacing="5">
          <tr>
            <xsl:apply-templates select="$root-node/HeaderConfig"/>
          </tr>
        </table>
        <xsl:if test="$root-node/Logs/@isProd">
            <form name="ebayLogForm" action="{$root-node/Logs}" method="post">
                <input type="hidden" name="caller" value="{$root-node/@client-ip}"/>
            </form> 
        </xsl:if>
        <xsl:if test="$root-node/Logs/. or $root-node/OldLogs/. or $root-node/CAL/. or $root-node/APOC/.">
            <center>
            <table cellspacing="5" cellpadding="5">
               <TR>
                <xsl:choose>
                    <xsl:when test="$root-node/Logs/@isProd = 'true'">
                        <td bgcolor="#FFCC00">
                            <font face="arial" size="3">
                            <a href="javascript:getLogs('ebayLogForm')" title="Logs"><b>Logs</b></a></font>
                        </td>
                    </xsl:when>
                    <xsl:otherwise>
                        <td bgcolor="#FFCC00">
                            <font face="arial" size="3">
                            <a link="none" href="{$root-node/Logs}" title="Logs"><b>Logs</b></a></font>
                        </td>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="$root-node/OldLogs/.">
                    <td bgcolor="#FFCC00">
                        <font face="arial" size="3">
                        <a link="none" href="{$root-node/OldLogs}" title="Logs"><b>Logs(Shovel)</b></a></font>
                    </td>
                </xsl:if>
                <xsl:if test="$root-node/CAL/.">
                <td bgcolor="#FFCC00"><font face="arial" size="3"><a link="none" href="{$root-node/CAL}" title="CAL"><b>CAL</b></a></font></td>
                </xsl:if>
                <xsl:if test="$root-node/APOC/.">
                <td bgcolor="#FFCC00"><font face="arial" size="3"><a link="none" href="{$root-node/APOC}" title="APOC"><b>APOC</b></a></font></td>
                </xsl:if>
              </TR>
            </table>
            </center>
        </xsl:if>
        <p/><div>
        <xsl:call-template name='page-content'/></div>
      </body>
    </html>
</xsl:template>
 <xsl:template match="HeaderPH">  
        <br/><b><xsl:value-of select="@value"/></b>     
  </xsl:template>
</xsl:stylesheet>