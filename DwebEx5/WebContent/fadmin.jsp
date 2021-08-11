<%@page import="com.company.FDao"%>
<%@page import="com.company.FDto"%>
<%@page import="java.util.Vector"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
    
<jsp:include page="ftop.jsp"/>
<%
   FDao dao = new FDao();
	Vector<FDto> v = dao.getAllFmember();
%>
   관리자 페이지
   <table border="1">
   <tr>
      <td>번호</td>
      <td>아이디</td>
      <td>이름</td>
      <td>이메일</td>
      <td>권한</td>
   </tr>
<%
	for(int i = 0; i < v.size(); i++) {
		FDto tmp = v.get(i);
%>
		<tr>
			<td><%=tmp.getIdx() %></td>
			<td><%=tmp.getId() %></td>
			<td><%=tmp.getName() %></td>
			<td><%=tmp.getEmail() %></td>
			<td><%=tmp.getLv() %></td>
		</tr>
<%
	}
%>
   
   </table>
   <a href="fmain.jsp">처음으로</a>
<jsp:include page="fbottom.jsp"/>
