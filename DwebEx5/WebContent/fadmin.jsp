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
   ������ ������
   <table border="1">
   <tr>
      <td>��ȣ</td>
      <td>���̵�</td>
      <td>�̸�</td>
      <td>�̸���</td>
      <td>����</td>
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
   
<jsp:include page="fbottom.jsp"/>
