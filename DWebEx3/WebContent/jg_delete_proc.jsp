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
	String pw = request.getParameter("pw");	
	// ����̹� �ε� / ���� ���� /connection��������	
	try{
		String sql = "delete from guestbook where idx="+idx+" and pw='"+pw+"'";
		result = stmt.executeUpdate(sql);
		
		if(result>0){// ��������
			response.sendRedirect("jg_main.jsp");
		}else{
%>
		<script>
			alert("��й�ȣ�� Ʋ�Ƚ��ϴ�.");
			history.back();
		</script>
<%		
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
</body>
</html>