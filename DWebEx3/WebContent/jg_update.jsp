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
	//String name = request.getParameter("name");//넘어온 적이 없으니까 사용불가!! null 뜸
	

	String sql = "select count(idx) AS cnt from guestbook where idx="+idx+" and pw='"+pw+"'";
	stmt = conn.prepareStatement(sql);
	rs = stmt.executeQuery();
	
	int result=0;
	if(rs.next()){
		//rs.getInt("cnt");
		result = rs.getInt(1);// 숫자의 의미는 결과 줄의 컬럼 위치 번호
		// result에는 총 개수 (count 한 개수)가 담긴다. 0 / 1
	}
	
	if(result==0){// 어차피 결과는 0/1 둘중 하나뿐
%>
	<script>
		alert("비밀번호가 틀립니다.");
		history.back();
	</script>
<%		
	}else{ // 비밀번호 맞았을 때
	// 넘어온 idx로 값을 select 한다음에 결과를 밑에 table에 넣어준다.
	
	String sql2 = "select * from guestbook where idx="+idx;
	                         // db에서int 타입이기때문에 '' <= 이거 없어도 됨
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
				<td style="width:70px;"><%=idx %>) 이름</td>
				<td><input type="text" name="name" value="<%=name %>"></td>
				<td style="width:70px;">비밀번호</td>
				<td><input type="password" name="pw" value="<%=password %>"></td>
			</tr>
			<tr>
				<td>이메일</td>
				<td colspan="3"><input type="text" name="email" value="<%=email %>"></td>			
			</tr>
			<tr>
				<td>내용</td>
				<td colspan="3"><textarea name="content"><%=content %></textarea></td>			
			</tr>
			<tr>
				<td colspan="4"><input type="submit" value="수정"></td>			
			</tr>
		</table>
		<input type="hidden" name="idx" value="<%=idx %>">
	</form>


<%		
		
	}
%>
	
</body>
</html>




