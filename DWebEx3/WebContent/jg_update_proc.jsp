<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/jg_include_iud.jspf" %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
</head>
<body>
<%
	String idx = request.getParameter("idx");
	String name = request.getParameter("name");
	String pw = request.getParameter("pw");
	String email = request.getParameter("email");
	String content = request.getParameter("content");
	
	// 업데이트 프로세스 페이지
	String sql = "update guestbook set name='"+name+"', pw='"+pw+"', email='"+email+"', content='"+content+"' where idx="+idx;
	result = stmt.executeUpdate(sql);
	if(result>0){
		response.sendRedirect("jg_main.jsp");
	}else{
		out.println("오류 발생!");
	}
%>
</body>
</html>


