<%@page import="com.company.FDto"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<jsp:include page="ftop.jsp"/>	
<%
	FDto dto = (FDto)session.getAttribute("logindata");
	if(dto != null){
%>
	<form action="fwrite_proc.jsp">
	<table border="1">
		<tr>
			<td>�۾���</td>
			<td><%=dto.getName() %></td>
		</tr>
		<tr>
			<td>������</td>
			<td><input type="text" name="title"></td>
		</tr>
		<tr>
			<td>����</td>
			<td><textarea name="content"></textarea></td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="submit" value="�۾��� �Ϸ�">
				<input type="button" value="ó������" onclick="location.href='fmain.jsp'">
			</td>			
		</tr>
	</table>
	</form>
<%}else{ %>
	<script>alert("�α����� �ؾ� ���� �ֽ��ϴ�.");location.href="fmain.jsp";</script>
<%} %>	
<jsp:include page="fbottom.jsp"/>