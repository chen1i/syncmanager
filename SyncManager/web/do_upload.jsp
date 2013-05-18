<%@ page contentType="text/html; charset=gb2312" language="java"
         import="java.util.*" errorPage="" %>
<%@page import="com.syncmanager.dao.orm.FileInfo" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@ page import="com.jspsmart.upload.SmartUpload" %>
<%@ page import="java.io.File" %>
<jsp:useBean id="fileInfoq" class="com.syncmanager.dao.FileInfoDao"/>
<html>
<head>
    <title>文件上传处理页面</title>
    <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
    <style type="text/css">
            /* CSS Document */
        body {
            font: normal 11px auto "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
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
            background: #CAE8EA url(images/bg.gif) no-repeat;
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
            background: #fff url(images/bullet.gif) no-repeat;
            font: bold 10px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
        }

        th.specalt {
            border-left: 1px solid #C1DAD7;
            border-top: 0;
            background: #f5fafa url(images/bullet.gif) no-repeat;
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
<%
    // 新建一个SmartUpload对象
    SmartUpload su = new SmartUpload();
    // 上传初始化
    su.initialize(pageContext);
    // 设定上传限制
    // 1.限制每个上传文件的最大长度。
    //su.setMaxFileSize(1000000);
    // 2.限制总上传数据的长度。
    //su.setTotalMaxFileSize(20000);
    // 3.设定允许上传的文件（通过扩展名限制）,仅允许doc,txt文件。
    su.setAllowedFilesList("docx,doc,txt,rtf,jpg,xls,pdf,pptx,ppt");
    // 4.设定禁止上传的文件（通过扩展名限制）,禁止上传带有exe,bat, jsp,htm,html扩展名的文件和没有扩展名的文件。
    su.setDeniedFilesList("exe,bat,jsp,htm,html");
    // 上传文件
    su.upload();
    // 将上传文件全部保存到指定目录
    File upload_dir = new File(System.getProperty("catalina.base"), "upload");
    if (upload_dir.exists() && upload_dir.isFile()) {
        //upload不是个目录，删除
        upload_dir.delete();
    }
    if (!upload_dir.exists())
        //upload 目录不存在，需要新建
        upload_dir.mkdirs();

    int count = su.save(upload_dir.getAbsolutePath());
    out.println(count + "个文件上传成功！<br>");

    // 利用Request对象获取参数之值
    out.println("TEST=" + su.getRequest().getParameter("TEST") + "<BR><BR>");

    // 逐一提取上传文件信息，同时可保存文件。
    for (int i = 0; i < su.getFiles().getCount(); i++) {
        com.jspsmart.upload.File file = su.getFiles().getFile(i);
        FileInfo info = fileInfoq.getfileInfoByFileName(file.getFileName());
        if (null == info) {
            info = new FileInfo();
            info.setFilename(file.getFileName());
            info.setFilepath("/upload/" + file.getFileName());
            info.setFilesize(String.valueOf(file.getSize()));
            info.setVersion("1");
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            info.setCreatedate(sdf.format(date));
            System.out.println();
            info.setUsername((String) request.getSession()
                    .getAttribute("adminUser"));
            info.setOldpath(upload_dir.getAbsolutePath() + File.separator + file.getFileName());
            fileInfoq.savefileInfo(info);
        } else {
            info.setFilesize(String.valueOf(file.getSize()));
            info.setVersion(String.valueOf(Integer.parseInt(info.getVersion()) + 1));
            fileInfoq.updatefileInfo(info);
        }
        // 若文件不存在则继续
        if (file.isMissing())
            continue;

        // 显示当前文件信息
        out.println("<TABLE BORDER=1>");
        out.println("<TR><TD>表单项名（FieldName）</TD><TD>"
                + file.getFieldName() + "</TD></TR>");
        out.println("<TR><TD>文件长度（Size）</TD><TD>" + file.getSize()
                + "</TD></TR>");
        out.println("<TR><TD>文件名（FileName）</TD><TD>"
                + file.getFileName() + "</TD></TR>");
        out.println("<TR><TD>文件扩展名（FileExt）</TD><TD>"
                + file.getFileExt() + "</TD></TR>");
        out.println("<TR><TD>文件全名（FilePathName）</TD><TD>"
                + file.getFilePathName() + "</TD></TR>");
        out.println("</TABLE><BR>");
    }
%>
</body>
</html>