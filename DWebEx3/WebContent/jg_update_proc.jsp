<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
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
	String name;
	String pw;
	String email;
	String content;
	
	// 업데이트 프로세스 페이지
	String sql = "";
	result = stmt.executeUpdate(sql);
	if(result>0){
		//돌아가기 - 메인으로
	}else{
		out.println("오류 발생!");
	}
%>
</body>
</html>


