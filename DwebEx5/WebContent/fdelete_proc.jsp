<%@page import="com.company.FDto"%>
<%@page import="com.company.FDao"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>

	<%
		/* if(session.getAttribute("logindata") == null){
			<script>alert("�α��� ���ּ���");</script>		
		} 
			�̷������� ó���ϸ� �ƹ� ���̵� �α��� �� ���Ŀ� idx�� �Ѱܼ� ����� �ִ� ������ ����
		*/
	
		FDto dto = (FDto)session.getAttribute("logindata");
	
		int idx = Integer.parseInt(request.getParameter("idx"));
		
		FDao dao = new FDao();
		int result=dao.deleteBoard(idx, dto.getId());
		// delete from fBoard where idx=? and fmember_id=?;		
		// delete from fBoard where idx=3 and fmember_id=null;
		// delete from fBoard where idx=3 and fmember_id=�α����ѻ���� id;
		if(result>0){
			response.sendRedirect("flist.jsp");
		}else{
%>
		<script>alert("�����߻�");location.href="flist.jsp"</script>
<%			
		}
	%>