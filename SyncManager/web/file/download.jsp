<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<%@page import="com.download.server.fileInfo"%>
<%@page import="java.io.*"%>
<%@page import="java.net.URLEncoder"%>
	<jsp:useBean id="fileInfos" class="com.download.server.fileInfoDao"/>
<%  
   fileInfo info=fileInfos.getfileInfoById(Integer.parseInt(request.getParameter("id")));
  //设置为下载application/x-download  
  response.setContentType("application/x-download");
  //即将下载的文件的相对路径  
  String filepath= application.getRealPath("/"); 
   filepath=filepath.replaceAll("\\\\","/");
  filepath=filepath.substring(0,filepath.lastIndexOf("/"));
 
  String filedownload =filepath+info.getFilepath();
  
  //下载文件时显示的文件保存名称  
  String filedisplay = info.getFilename();
  String filenamedisplay = URLEncoder.encode(filedisplay,"UTF-8");  
  response.addHeader("Content-Disposition","attachment;filename=" + filenamedisplay);  
  System.out.println(filedownload);
  try  
  {  
  RequestDispatcher dis = application.getRequestDispatcher(filedownload);  
  if(dis!= null)  
  {  
  dis.forward(request,response);  
  }  
  response.flushBuffer();  
  }  
  catch(Exception e)  
  {  
  e.printStackTrace();  
  }  
  finally  
  {  
     
  }  
%>  

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>My JSP 'download.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
    This is my JSP page. <br>
  </body>
</html>
