<%@page import="com.company.FDto"%>
<%@page import="com.company.FDao"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<jsp:useBean id="dto" class="com.company.FBDto"/>
<jsp:setProperty property="*" name="dto"/>
<%
	FDto fdto = (FDto)session.getAttribute("logindata");
	FDao dao = new FDao();
	int result = dao.updateFBoard(dto, fdto.getId());
	if(result>0){
		response.sendRedirect("fread.jsp?idx="+dto.getIdx());
	}else{
%>
	<script>alert("무언가 잘못됨");location.href="fread.jsp?idx="+dto.getIdx();</script>
<%		
	}
%>
