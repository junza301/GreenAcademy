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
		// insert into guestbook (�÷���, �÷���,...) values (��, ��,...); //1:1 ��Ī �ʼ�
		// ���� �÷��� �κ��� ���� �ۼ��Ѵٸ� �ݵ�� ���δ� �÷� ������� ���� ���������!! �ʼ�!!
		
		stmt = conn.createStatement();
		result = stmt.executeUpdate(sql);
		
		if(result>0){// ��������
			response.sendRedirect("jg_main.jsp");
		}else{
			out.println("���� �߻�!");
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




