<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ include file="/jg_include_sel.jspf" %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body>
<%
	String idx = request.getParameter("idx");
	String pw = request.getParameter("pw");
	//String name = request.getParameter("name");//�Ѿ�� ���� �����ϱ� ���Ұ�!! null ��
	

	String sql = "select count(idx) AS cnt from guestbook where idx="+idx+" and pw='"+pw+"'";
	stmt = conn.prepareStatement(sql);
	rs = stmt.executeQuery();
	
	int result=0;
	if(rs.next()){
		//rs.getInt("cnt");
		result = rs.getInt(1);// ������ �ǹ̴� ��� ���� �÷� ��ġ ��ȣ
		// result���� �� ���� (count �� ����)�� ����. 0 / 1
	}
	
	if(result==0){// ������ ����� 0/1 ���� �ϳ���
%>
	<script>
		alert("��й�ȣ�� Ʋ���ϴ�.");
		history.back();
	</script>
<%		
	}else{ // ��й�ȣ �¾��� ��
	// �Ѿ�� idx�� ���� select �Ѵ����� ����� �ؿ� table�� �־��ش�.
	
	String sql2 = "select * from guestbook where idx="+idx;
	                         // db����int Ÿ���̱⶧���� '' <= �̰� ��� ��
	rs = stmt.executeQuery(sql2);
	                         
	String name = null;
	String password = null;
	String email = null;
	String content = null;
	
	if(rs.next()){
		name = rs.getString("name");
		password = rs.getString(3);
		email = rs.getString(4);
		content = rs.getString("content");
	}
%>
	<form action="jg_update_proc.jsp">
		<table>
			<tr>
				<td style="width:70px;"><%=idx %>) �̸�</td>
				<td><input type="text" name="name" value="<%=name %>"></td>
				<td style="width:70px;">��й�ȣ</td>
				<td><input type="password" name="pw" value="<%=password %>"></td>
			</tr>
			<tr>
				<td>�̸���</td>
				<td colspan="3"><input type="text" name="email" value="<%=email %>"></td>			
			</tr>
			<tr>
				<td>����</td>
				<td colspan="3"><textarea name="content"><%=content %></textarea></td>			
			</tr>
			<tr>
				<td colspan="4"><input type="submit" value="����"></td>			
			</tr>
		</table>
		<input type="hidden" name="idx" value="<%=idx %>">
	</form>


<%		
		
	}
%>
	
</body>
</html>




