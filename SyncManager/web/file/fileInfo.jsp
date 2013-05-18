<%@ page contentType="text/html;charset=gb2312" pageEncoding="gb2312" %>
<%@page import="java.util.*"%>
	<%@page import="com.syncmanager.dao.orm.FileInfo"%>
<jsp:useBean id="fileInfos" class="com.syncmanager.dao.FileInfoDao"/>
<%
   
    List userList=(ArrayList)fileInfos.getAllfileInfoByUserName((String)request.getSession().getAttribute("adminUser"));
 %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>


<title>查询用户信息</title>
<style type="text/css">
/* CSS Document */
body {
	font: normal 11px auto "Trebuchet MS", Verdana, Arial, Helvetica,
		sans-serif;
	color: #4f6b72;
	background: #E6EAE9;
}

a {
	color: #c75f3e;
}

#mytable {
	width: 100%;
	padding: 0;
	margin: 0;
}

caption {
	padding: 0 0 5px 0;
	width: 700px;
	font: italic 11px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
	text-align: right;
}

th {
	font: bold 11px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
	color: #4f6b72;
	border-right: 1px solid #C1DAD7;
	border-bottom: 1px solid #C1DAD7;
	border-top: 1px solid #C1DAD7;
	letter-spacing: 2px;
	text-transform: uppercase;
	text-align: left;
	padding: 6px 6px 6px 12px;
	background: #CAE8EA url(images/bg_header.jpg) no-repeat;
}

th.nobg {
	border-top: 0;
	border-left: 0;
	border-right: 1px solid #C1DAD7;
	background: none;
}

td {
	border-right: 1px solid #C1DAD7;
	border-bottom: 1px solid #C1DAD7;
	background: #fff;
	font-size: 11px;
	padding: 6px 6px 6px 12px;
	color: #4f6b72;
}

td.alt {
	background: #F5FAFA;
	color: #797268;
}

th.spec {
	border-left: 1px solid #C1DAD7;
	border-top: 0;
	background: #fff url(images/bullet1.gif) no-repeat;
	font: bold 10px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
}

th.specalt {
	border-left: 1px solid #C1DAD7;
	border-top: 0;
	background: #f5fafa url(images/bullet2.gif) no-repeat;
	font: bold 10px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
	color: #797268;
}

/*---------for IE 5.x bug*/
html>body td {
	font-size: 11px;
}
</style>
</head>
<body>
<br /><br />
      <table width="100%" border="1" align="center" cellpadding="0" cellspacing="0" bordercolor="#FFFFFF" bordercolordark="#819BBC" bordercolorlight="#FFFFFF">
        <tr align="center" bgcolor="#EFF6FE">
          <td height="28" colspan="7"><strong>下载文件</strong></td>
        
        </tr>
		<tr align="center" bgcolor="#EFF6FE">
          <td width="5%" height="25">编号</td>
          <td width="20%">文件名称</td>
          <td width="5%">文件大小</td>
          <td width="5%">文件的版本</td>
          <td width="20%">文件的生成时间</td>
		   <td width="20%">文件的路径 </td>
		   <td width="20%">操作 </td>
        </tr>
         	<%
         	     for(int index=0;index<userList.size();index++){
         	        FileInfo user=(FileInfo)userList.get(index);%>
         	        <tr  align="center">
					<td>
						<%= index+1%>
					</td>
					<td>
						<%= user.getFilename()%>
					</td>
						<td>
						<%= user.getFilesize()%>
					</td>
					<td>
						<%= user.getVersion()%>
					</td>
					<td>
						<%= user.getCreatedate()%>
					</td>
					<td>
						<%= user.getFilepath()%>
					</td>
					<td>
						<a href="download1.jsp?id=<%=user.getId()%>">下载</a>
					</td>
				</tr>
         	     <%}
         	 %>
				
          
      
    
 </table>
		</center>



 
 
</body>
</html>