<%@page import="com.company.FDto"%>
<%@page import="com.company.FDao"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<jsp:useBean id="bean" class="com.company.FDto"/>
<jsp:setProperty property="*" name="bean"/>
    
<%
	FDao dao = new FDao(); 
	FDto dto = dao.flogin(bean);// �α����� ���� ���ٸ�  dto�� ���� ����.
		
	if(dto.getIdx() != 0){
		session.setAttribute("logindata", dto);
		response.sendRedirect("fmain.jsp");
	}else{
	%>
		<script>alert("���̵� ��� Ʋ��!"); location.href="fmain.jsp"</script>
	<%		
	}
	
	
	
%>