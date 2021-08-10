<%@page import="com.company.FDto"%>
<%@page import="com.company.FDao"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<jsp:useBean id="bean" class="com.company.FBDto"/>
<jsp:setProperty property="*" name="bean"/>    
<%
	FDto dto = (FDto)session.getAttribute("logindata");
	FDao dao = new FDao();
	int result = dao.insertFBoard(dto,bean);//bean은 게시판 데이터, dto는 회원데이터
	if(result>0){
		response.sendRedirect("fmain.jsp");
	}else{
%>
	<script>alert("문제발생");location.href="fmain.jsp";</script>
<%		
	}
%>