<%@page import="com.company.FBDto"%>
<%@page import="java.util.Vector"%>
<%@page import="com.company.FDao"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<table border="1">   
	<tr>
		<td>��ȣ</td>
		<td>����</td>
		<td>�۾���</td>
		<td>��¥</td>
	</tr> 
<%
	FDao dao = new FDao();
	Vector<FBDto> v= dao.getFboardAll("regdate", 10);// order by regdate �̷��� ������ �Ѱ���
	if(v.size()>0){// ������ �ϳ��� �ִٸ�
%>			
			<c:forEach var="i" items="<%=v %>">
				<tr>
					<td><a href="fread.jsp?idx=${i.idx}">${i.title}</a></td>
					<td>${i.title}</td>
					<td>${i.fmember_name}</td>
					<td>${i.regdate}</td>
				</tr>
			</c:forEach>
<%}else{ %>
			<tr>
				<td colspan="4">�Խñ��� �����ϴ�.</td>
			</tr>
<%} %>
		</table>
			
			
			