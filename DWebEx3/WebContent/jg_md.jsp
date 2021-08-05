<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
</head>
<body>
	<%
		String idx=request.getParameter("idx");
	%>
	<div>
		<input type="button" value="수정" onclick="jg_update()" id="btn_update">
		<input type="button" value="삭제" onclick="jg_delete()" id="btn_delete">
	</div>
	<div id="update">
		<form action="jg_update.jsp" method="post">
		
			<input type="password" name="pw">			
			<input type="hidden" name="idx" value="<%=idx %>">
			
			<input type="submit" value="수정">
		</form>
	</div>
	<div id="delete">
		<form action="jg_delete_proc.jsp" method="post">
			<input type="password" name="pw">
			<input type="submit" value="삭제">
			<input type="hidden" name="idx" value="<%=idx %>">
		</form>
	</div>	
	
	<script>
		document.getElementById("update").style.display="none";
		document.getElementById("delete").style.display="none";		
		
		function jg_update(){
			document.getElementById("btn_update").style.backgroundColor="orange";
			document.getElementById("btn_delete").style.backgroundColor="";
			
			document.getElementById("update").style.display="block";
			document.getElementById("delete").style.display="none";			
		}
		function jg_delete(){
			document.getElementById("btn_update").style.backgroundColor="";
			document.getElementById("btn_delete").style.backgroundColor="orange";
			
			document.getElementById("update").style.display="none";
			document.getElementById("delete").style.display="block";
		}
	</script>	
</body>
</html>




