<%@page import="com.company.FBDto"%>
<%@page import="java.util.Vector"%>
<%@page import="com.company.FDao"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<table border="1">   
	<tr>
		<td>번호</td>
		<td>제목</td>
		<td>글쓴이</td>
		<td>날짜</td>
	</tr> 
<%
	FDao dao = new FDao();
	Vector<FBDto> v= dao.getFboardAll("regdate", 10);// order by regdate 이렇게 쓸려고 넘겨줌
	if(v.size()>0){// 내용이 하나라도 있다면
%>			
			<c:forEach var="i" items="<%=v %>">
				<tr>
					<td>${i.idx}</td>
					<td>${i.title}</td>
					<td>${i.fmember_name}</td>
					<td>${i.regdate}</td>
				</tr>
			</c:forEach>
<%}else{ %>
			<tr>
				<td colspan="4">게시글이 없습니다.</td>
			</tr>
<%} %>
		</table>
			
			
			