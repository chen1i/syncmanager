<%@ page contentType="text/html;charset=gb2312" pageEncoding="gb2312"%>
<%@page import="com.download.server.userInfo"%>
<jsp:useBean id="userInfoDq" class="com.download.server.userInfoDao"/>

<meta http-equiv="Content-Type" content="text/html; gb2312">
<%
	if (null != request.getParameter("username") && null != request.getParameter("password")) {
	     //�õ��û�������û���
    String username=(String)request.getParameter("username");
    //�õ��û����������
    String password=(String)request.getParameter("password");
     String type=(String)request.getParameter("type");
  com.download.server.userInfo user=new com.download.server.userInfo();
	      user.setUsername(username);
	      user.setUserRealName(password);
	      user.setRemark(type);
	      if(userInfoDq.saveUserInfo(user)){
	         response.sendRedirect("index.jsp");
	         return;
	      }
	}
%>
<html>
	<head>

		<link href="../css/style.css" rel="stylesheet" type="text/css">
		<title>�û�ע��</title>
		<style type="text/css">
body {
	margin: 5px, 0, 0, 0;
}
</style>
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
		<script language="JavaScript">
function insert(){
var studname=document.getElementById('studname').value;
var stupwd=document.getElementById('stupwd').value;
if(isNull(studname))
{
 alert("������Ϣ����");
 return false;
}
if(isNull(stupwd))
{
 alert("������Ϣ����");
 return false;
}

function isNull( str ){ 
if ( str == "" ) return true; 
var regu ="^[ ] $"; 
var re = new RegExp(regu); 
return re.test(str); 
} 

</script>
	</head>
	<body>
		<form name="form" method="post" action="addregister.jsp"
			onSubmit="return insert()">
			<table width="100%" border="1" cellspacing="0" cellpadding="0"
				bordercolor="#FFFFFF" bordercolordark="#819BBC"
				bordercolorlight="#FFFFFF" align="center">
				<tr align="center" bgcolor="#EFF6FE">
					<td height="28" colspan="2">
						<strong><font size="+3">���û�ע��</font> </strong>
					</td>
				</tr>
				<tr>
					<td width="35%" height="30" bgcolor="#EFF6FE" align="center">
					    ��½����
					</td>
					<td width="63%" bgcolor="#EFF6FE">
					   <input name="username" class="editbox4" value="" size="20"/>       
					</td>
				</tr>
				<tr>
					<td height="30" bgcolor="#EFF6FE" align="center">
						 ��½����
					</td>
					<td bgcolor="#EFF6FE">
						<input class="editbox4" type="password" size="20" name="password">
					</td>
				</tr>
				<tr>
                           	<td height="30" bgcolor="#EFF6FE" align="center">ע�����ͣ�&nbsp;&nbsp;</td>
                            <td height="38" colspan="2" class="top_hui_text"><select name="type">
                             <option value="1">����Ա</option>
                             <option value="2">��ͨ�û�</option>
                            </select>                       </td>
                          </tr>
				<tr>
					<td height="53" colspan="2" align="center">
						&nbsp;&nbsp;&nbsp;
						<input type="submit" name="Submit2" value="�ύ">
						&nbsp;&nbsp;&nbsp;
						<input type="reset" name="Submit3" value="����">
						&nbsp;&nbsp;&nbsp;
						<input type="button" name="Submit3" value="����"
							onClick="javascript:history.back();">
						&nbsp;
					</td>
				</tr>
			</table>
		</form>

	</body>
</html>
