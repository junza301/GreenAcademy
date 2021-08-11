<%@page import="com.company.FDto"%>
<%@page import="com.company.FDao"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>

	<%
		/* if(session.getAttribute("logindata") == null){
			<script>alert("로그인 해주세요");</script>		
		} 
			이런식으로 처리하면 아무 아이디나 로그인 한 이후에 idx를 넘겨서 지울수 있는 문제가 생김
		*/
	
		FDto dto = (FDto)session.getAttribute("logindata");
	
		int idx = Integer.parseInt(request.getParameter("idx"));
		
		FDao dao = new FDao();
		int result=dao.deleteBoard(idx, dto.getId());
		// delete from fBoard where idx=? and fmember_id=?;		
		// delete from fBoard where idx=3 and fmember_id=null;
		// delete from fBoard where idx=3 and fmember_id=로그인한사람의 id;
		if(result>0){
			response.sendRedirect("flist.jsp");
		}else{
%>
		<script>alert("문제발생");location.href="flist.jsp"</script>
<%			
		}
	%>