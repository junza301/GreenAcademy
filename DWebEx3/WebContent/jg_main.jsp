<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>

<link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body>
<%
	/* CREATE TABLE guestbook(
		idx  int  primary KEY AUTO_INCREMENT,
		name varchar(100)  not NULL,
		pw varchar(100) not NULL,
		email varchar(100),
		content text 
	); */
	
	/*
	방명록 이름 규칙
	jg _ 눈에보이는 기능 - 처리하는기능(뒤에 proc를 붙인다.) 
	jsp guest book
	*/
%>
	<form action="jg_write_proc.jsp">
	<table>
		<tr>
			<td style="width:70px;">이름</td>
			<td><input type="text" name="name"></td>
			<td style="width:70px;">비밀번호</td>
			<td><input type="password" name="pw"></td>
		</tr>
		<tr>
			<td>이메일</td>
			<td colspan="3"><input type="text" name="email"></td>			
		</tr>
		<tr>
			<td>내용</td>
			<td colspan="3"><textarea name="content"></textarea></td>			
		</tr>
		<tr>			
			<td colspan="4"><input type="submit" value="쓰기"></td>			
		</tr>
	</table>
	</form>
	<br>
	<jsp:include page="jg_select.jsp"/>
</body>
</html>






