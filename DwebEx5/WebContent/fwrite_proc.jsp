<%@page import="com.company.FDto"%>
<%@page import="com.company.FDao"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<jsp:useBean id="bean" class="com.company.FBDto"/>
<jsp:setProperty property="*" name="bean"/>    
<%
	FDto dto = (FDto)session.getAttribute("logindata");
	FDao dao = new FDao();
	int result = dao.insertFBoard(dto,bean);//bean�� �Խ��� ������, dto�� ȸ��������
	if(result>0){
		response.sendRedirect("fmain.jsp");
	}else{
%>
	<script>alert("�����߻�");location.href="fmain.jsp";</script>
<%		
	}
%>