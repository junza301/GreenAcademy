<%@page import="com.company.FDto"%>
<%@page import="com.company.FBDto"%>
<%@page import="com.company.FDao"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<jsp:include page="ftop.jsp"/>	
<%
	FDto fdto = (FDto)session.getAttribute("logindata");
	int idx = Integer.parseInt(request.getParameter("idx"));
	FDao dao = new FDao();
	FBDto dto = dao.getFBoard(idx, fdto.getId());
	if(dto != null){
%>
		<form action="fupdate_proc.jsp">
		<table border="1">
			<tr>
				<td><%=dto.getIdx() %></td>
				<td><input type="text" name="title" value="<%=dto.getTitle()%>"></td>			
				<td><%=dto.getFmember_name() %>(<%=dto.getFmember_id() %>)</td>							
			</tr>
			<tr>
				<td colspan="3"><textarea name="content"><%=dto.getContent() %></textarea></td>
			</tr>
		</table>
			<input type="hidden" name="idx" value="<%=idx %>">
			<input type="submit" value="수정">
		</form>
<%}else{ %>
	<script>alert("문제발생");location.href="fread.jsp"</script>
<%} %>		
<jsp:include page="fbottom.jsp"/>	