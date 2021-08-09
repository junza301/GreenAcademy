<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<jsp:include page="ftop.jsp"/>	


		<div class="member">
			<form action="flogin_proc.jsp">
				아이디 <input type="text"> 비밀번호 <input type="password">
				<input type="submit" value="로그인">
				<input type="button" value="회원가입" onclick="location.href='fregist.jsp'">
			</form>
		</div>
		<div class="newtext"></div>
		
		
<jsp:include page="fbottom.jsp"/>	