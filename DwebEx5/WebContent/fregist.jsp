<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<jsp:include page="ftop.jsp"/>
	<form action="fregist_proc.jsp" method="post">
	<table border="1">
		<tr>
			<td>���̵�</td>
			<td><input type="text" name="id"></td>
		</tr>
		<tr>
			<td>��й�ȣ</td>
			<td><input type="password" name="pw"></td>
		</tr>
		<tr>
			<td>�̸�</td>
			<td><input type="text" name="name"></td>
		</tr>
		<tr>
			<td>�̸���</td>
			<td><input type="text" name="email"></td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="submit" value="���ԿϷ�">
				<input type="reset" value="���">
				<input type="button" value="���ư���" onclick="location.href='fmain.jsp'">
			</td>
		</tr>
	</table>
	</form>
	
<jsp:include page="fbottom.jsp"/>