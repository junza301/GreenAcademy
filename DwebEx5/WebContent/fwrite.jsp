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
			<td>글쓴이</td>
			<td><%=dto.getName() %></td>
		</tr>
		<tr>
			<td>글제목</td>
			<td><input type="text" name="title"></td>
		</tr>
		<tr>
			<td>내용</td>
			<td><textarea name="content"></textarea></td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="submit" value="글쓰기 완료">
				<input type="button" value="처음으로" onclick="location.href='fmain.jsp'">
			</td>			
		</tr>
	</table>
	</form>
<%}else{ %>
	<script>alert("로그인을 해야 쓸수 있습니다.");location.href="fmain.jsp";</script>
<%} %>	
<jsp:include page="fbottom.jsp"/>