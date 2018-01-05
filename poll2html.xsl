<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
<xsl:template match="/">
	<xsl:variable name="pageCount">
		<xsl:value-of select="count(poll/page)"/>	
	</xsl:variable>
	<html>
		<head>
			<style>
				table, th, td {
    				border: 1px solid black;
    				border-collapse: collapse;}
			</style>
			<script>
				function next(){
					var current = document.getElementById("current");
					var pages = document.getElementsByClassName("page");
					for(var pageIndex = 0;pages.length > pageIndex; pageIndex++){
						if(pages[pageIndex] == current){
							if(pages.length - 1 > pageIndex){
								current.removeAttribute("id");
								current.setAttribute("style","display: none;");
								pages[pageIndex+1].setAttribute("id","current");
								pages[pageIndex+1].removeAttribute("style");
								document.getElementById("pageDiv").innerHTML= (pageIndex + 2) + "/" + <xsl:value-of select="$pageCount"/>
								if(pageIndex+2 == pages.length){
									document.getElementById("submit").removeAttribute("style");
								}
							}

						}
					}
				}
				function prev(){
					var current = document.getElementById("current");
					var pages = document.getElementsByClassName("page");
					for(var pageIndex = 0;pages.length > pageIndex; pageIndex++){
						if(pages[pageIndex] == current){
							if(pageIndex > 0){
								current.removeAttribute("id");
								current.setAttribute("style","display: none;");
								pages[pageIndex-1].setAttribute("id","current");
								pages[pageIndex-1].removeAttribute("style");
								document.getElementById("submit").setAttribute("style","display:none;");
								document.getElementById("pageDiv").innerHTML= (pageIndex) + "/" + <xsl:value-of select="$pageCount"/>
							}

						}
					}
				}
			</script>

		</head>
		<body>
			<form action="handle.php" method="post">
				<input type="hidden" name="branch"><xsl:value-of select="poll/@branch"/> </input>
				<input type="hidden" name="pollname"> <xsl:value-of select="poll/@name"/> </input>
				<xsl:apply-templates/>
				<div>
					<xsl:attribute name="style">
						position: absolute;
						top: 815px;
						left: 550px;					
					</xsl:attribute>				
					<input id="submit" type="submit" value="Abschicken" style="display:none;"/>	
				</div>
			</form>
			<div>
				<xsl:attribute name="style">
					position: absolute;
					top: 815px;
					left: 200px;
				</xsl:attribute>
				<button onClick="prev()">prev</button>
			</div>	
			<div>
				<xsl:attribute name="style">
					position: absolute;
					top: 815px;
					left: 350px;				
				</xsl:attribute>
				<button onClick="next()">next</button>
			</div>
			<div id="pageDiv">
				<xsl:attribute name="style">
					position: absolute;
					top: 820px;
					left: 290px;				
				</xsl:attribute>
				1/<xsl:value-of select="$pageCount"/>		
			</div>
		</body>
	</html>
</xsl:template>

<xsl:template match="page">
	<div class="page">
		<xsl:attribute name="style">
			display: none;
		</xsl:attribute>
		<xsl:apply-templates/>
	</div>
</xsl:template>

<xsl:template match="page[1]">
	<div class="page" id="current">
		<xsl:apply-templates/>
	</div>
</xsl:template>


<xsl:template match="Group[@id='t']">
	<xsl:variable name="rows">
		<xsl:value-of select="count(children/Group[@id='v']/children/Line)"/>
	</xsl:variable>
	<xsl:variable name="layoutX">
		<xsl:value-of select="(children/Group[@id='h']/children/Line/@startX)[1]"/>
	</xsl:variable>
	<xsl:variable name="layoutY">
		<xsl:value-of select="(children/Group[@id='v']/children/Line/@startY)[1]"/>
	</xsl:variable>
	<xsl:variable name="areas" select="children/Group[@id='a']/children/TextArea"/>
	
	<table>
		<xsl:attribute name="style">
			position: absolute;
			left: <xsl:value-of select="$layoutX"/>px;
			top: <xsl:value-of select="$layoutY"/>px;		
		</xsl:attribute>
		
		<xsl:variable name="title">
			<xsl:value-of select="(children/Group[@id='a']/children/TextArea/@text)[1]"/>		
		</xsl:variable>

		<!-- Table header -->		
		<tr>
			<xsl:for-each select="children/Group[@id='a']/children/TextArea[$rows > position()]">
				
				<th>
					<xsl:attribute name="style">
						width: <xsl:value-of select="@prefWidth"/>px;
						height: <xsl:value-of select="@prefHeight"/>px;					
					</xsl:attribute>
					<xsl:value-of select="@text"/>
				</th>
			</xsl:for-each>
		</tr>

		<!-- Table Body -->
		<xsl:for-each select="children/Group[@id='a']/children/TextArea[position() >= $rows]">
			<xsl:variable name="row">
				<xsl:value-of select="position()"/>
			</xsl:variable>
			<xsl:variable name="question">
				<xsl:value-of select="@text"/>
			</xsl:variable>
			<tr>
				<xsl:attribute name="style">
					height: <xsl:value-of select="@prefHeight"/>px;						
				</xsl:attribute>
				<td>
					<xsl:value-of select="$question"/>
				</td>

				<xsl:for-each select="(//node())[$rows -1 > position()]">
					<xsl:variable name="position" select="position()+1"/>
					<td align="center">
						<input type="radio">
							<xsl:attribute name="name">
								<xsl:value-of select="$row"/>
							</xsl:attribute>
							<xsl:attribute name="value">
								<xsl:value-of select="$title"/>|<xsl:value-of select="$question"/>|<xsl:value-of select="($areas/@text)[$position]"/>
							</xsl:attribute>
	
						</input>
							
					</td>				
				</xsl:for-each>
			</tr>
		</xsl:for-each>

	</table>
</xsl:template>


<xsl:template match="Group[@id='i']">
	
	<xsl:variable name="question">
		I<xsl:value-of select="(children/TextArea/@text)[1]"/>
	</xsl:variable>
	<xsl:for-each select="children/TextArea[1]">
		<div>
			<xsl:attribute name="style">
				position: absolute;
				left: <xsl:value-of select="@layoutX"/>px;
				top: <xsl:value-of select="@layoutY"/>px;			
			</xsl:attribute>
			<p>
				<xsl:attribute name="style">
					height: <xsl:value-of select="@prefHeight"/>px;
					width: <xsl:value-of select="@prefWidth"/>px;				
					font-size: 12px;
					word-wrap: break-word;
				</xsl:attribute>
				<xsl:value-of select="@text"/>
			</p>
			
		</div>	
	</xsl:for-each>

	<xsl:for-each select="children/TextArea[2]">
		<div>
			<xsl:attribute name="style">
				position: absolute;
				left: <xsl:value-of select="@layoutX"/>px;
				top: <xsl:value-of select="@layoutY"/>px;
				font-size:12px;
			</xsl:attribute>

 			<textarea>
				<xsl:attribute name="name">
					<xsl:value-of select="$question"/>
				</xsl:attribute>
				<xsl:attribute name="style">
					resize: none;
					height: <xsl:value-of select="@prefHeight"/>px;
					width: <xsl:value-of select="@prefWidth"/>px;
				</xsl:attribute>
			</textarea>
		</div>
	</xsl:for-each>
</xsl:template>


<xsl:template match="TextArea">
	<div>
		<xsl:attribute name="style">
			position: absolute;
			left: <xsl:value-of select="@layoutX"/>px;
			top: <xsl:value-of select="@layoutY"/>px; 
			height: <xsl:value-of select="@prefHeight"/>px;
			width: <xsl:value-of select="@prefWidth"/>px;
			font-size: 12px;
		</xsl:attribute>

		<p>
			<xsl:value-of select="@text"/>
		</p>

	</div>
</xsl:template>






</xsl:stylesheet>


