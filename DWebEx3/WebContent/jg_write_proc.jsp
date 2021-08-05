<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ include file="/jg_include_iud.jspf" %>

<%
	String name = request.getParameter("name");
	String pw = request.getParameter("pw");
	String email = request.getParameter("email");
	String content = request.getParameter("content");	
%>

<%
	try{
		String sql = "insert into guestbook values (null, '"+name+"', '"+pw+"', '"+email+"', '"+content+"')";
		// insert into guestbook (컬럼명, 컬럼명,...) values (값, 값,...); //1:1 매칭 필수
		// 만약 컬럼명 부분을 없이 작성한다면 반드시 전부다 컬럼 순서대로 갯수 맞춰줘야함!! 필수!!
		
		stmt = conn.createStatement();
		result = stmt.executeUpdate(sql);
		
		if(result>0){// 문제없음
			response.sendRedirect("jg_main.jsp");
		}else{
			out.println("에러 발생!");
		}
	}catch(SQLException sqle){
		sqle.printStackTrace();
	}finally{
		try{
			if(stmt != null) stmt.close();
			if(conn != null) conn.close();
		}catch(SQLException sqle){
			sqle.printStackTrace();
		}
		
	}
%>

<%//@ include file="jg_include_iud_close.jspf" %>




