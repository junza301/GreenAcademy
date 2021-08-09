<%@page import="com.company.FDao"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<jsp:useBean id="bean" class="com.company.FDto"/>
<jsp:setProperty property="*" name="bean"/>

테스트 코드
${bean.id}/${bean.pw}/${bean.name}/${bean.email}

<%
	FDao dao = new FDao();
	int result = dao.fregist(bean);
	if(result>0){
		response.sendRedirect("fmain.jsp");
	}else{
%>
	<script>alert("문제발생"); location.href="fregist.jsp"</script>
<%		
	}
%>
