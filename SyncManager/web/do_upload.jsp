<%@ page contentType="text/html; charset=gb2312" language="java"
         import="java.util.*" errorPage="" %>
<%@page import="com.syncmanager.dao.orm.FileInfo" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@ page import="com.jspsmart.upload.SmartUpload" %>
<%@ page import="java.io.File" %>
<jsp:useBean id="fileInfoq" class="com.syncmanager.dao.FileInfoDao"/>
<html>
<head>
    <title>�ļ��ϴ�����ҳ��</title>
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
    // �½�һ��SmartUpload����
    SmartUpload su = new SmartUpload();
    // �ϴ���ʼ��
    su.initialize(pageContext);
    // �趨�ϴ�����
    // 1.����ÿ���ϴ��ļ�����󳤶ȡ�
    //su.setMaxFileSize(1000000);
    // 2.�������ϴ����ݵĳ��ȡ�
    //su.setTotalMaxFileSize(20000);
    // 3.�趨�����ϴ����ļ���ͨ����չ�����ƣ�,������doc,txt�ļ���
    su.setAllowedFilesList("docx,doc,txt,rtf,jpg,xls,pdf,pptx,ppt");
    // 4.�趨��ֹ�ϴ����ļ���ͨ����չ�����ƣ�,��ֹ�ϴ�����exe,bat, jsp,htm,html��չ�����ļ���û����չ�����ļ���
    su.setDeniedFilesList("exe,bat,jsp,htm,html");
    // �ϴ��ļ�
    su.upload();
    // ���ϴ��ļ�ȫ�����浽ָ��Ŀ¼
    File upload_dir = new File(System.getProperty("catalina.base"), "upload");
    if (upload_dir.exists() && upload_dir.isFile()) {
        //upload���Ǹ�Ŀ¼��ɾ��
        upload_dir.delete();
    }
    if (!upload_dir.exists())
        //upload Ŀ¼�����ڣ���Ҫ�½�
        upload_dir.mkdirs();

    int count = su.save(upload_dir.getAbsolutePath());
    out.println(count + "���ļ��ϴ��ɹ���<br>");

    // ����Request�����ȡ����ֵ֮
    out.println("TEST=" + su.getRequest().getParameter("TEST") + "<BR><BR>");

    // ��һ��ȡ�ϴ��ļ���Ϣ��ͬʱ�ɱ����ļ���
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
        // ���ļ������������
        if (file.isMissing())
            continue;

        // ��ʾ��ǰ�ļ���Ϣ
        out.println("<TABLE BORDER=1>");
        out.println("<TR><TD>��������FieldName��</TD><TD>"
                + file.getFieldName() + "</TD></TR>");
        out.println("<TR><TD>�ļ����ȣ�Size��</TD><TD>" + file.getSize()
                + "</TD></TR>");
        out.println("<TR><TD>�ļ�����FileName��</TD><TD>"
                + file.getFileName() + "</TD></TR>");
        out.println("<TR><TD>�ļ���չ����FileExt��</TD><TD>"
                + file.getFileExt() + "</TD></TR>");
        out.println("<TR><TD>�ļ�ȫ����FilePathName��</TD><TD>"
                + file.getFilePathName() + "</TD></TR>");
        out.println("</TABLE><BR>");
    }
%>
</body>
</html>