<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:encoder="xalan://java.net.URLEncoder" version="1.0"
                exclude-result-prefixes="xalan encoder i18n">

  <xsl:param name="WebApplicationBaseURL" />

  <!--<xsl:template match="//similarityList">
    <script src="{concat($WebApplicationBaseURL, 'js/mir/similarity-list.js')}" />
    <script>
      similarity.init("<xsl:value-of select='$WebApplicationBaseURL' />");
    </script>

    <div class="test"></div>
  </xsl:template>-->



  <!--
  <xsl:template match="object">
    <div>
    <a href="{concat($WebApplicationBaseURL, @id)}">
      <xsl:value-of select="@id" />
    </a>
    </div>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="object/similarityObject">
    <div>
      <a href="{concat($WebApplicationBaseURL, @id)}">
        <xsl:value-of select="@id" />
      </a>
    </div>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="object/similarityObject/@score">
    <div>
      <p>
    <xsl:value-of select="concat('Score: ', .)"/>
      </p>
    </div>
  </xsl:template>
-->
</xsl:stylesheet>