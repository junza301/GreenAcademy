<%@page import="com.company.FDto"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<jsp:include page="ftop.jsp"/>   
<div class="member">
<% 
   FDto dto = (FDto)session.getAttribute("logindata");
   if(dto==null){ 
%>      
         <form action="flogin_proc.jsp">
            ���̵� <input type="text" name="id"> ��й�ȣ <input type="password" name="pw">
            <input type="submit" value="�α���">
            <input type="button" value="ȸ������" onclick="location.href='fregist.jsp'">
         </form>   
<%} else {%>
      <%=dto.getName() %>�� �ݰ����ϴ�. <input type="button" value="�α׾ƿ�" onclick="location.href='flogout.jsp'">
      <%
         if(dto.getLv()==10){
      %>
         <input type="button" value="������" onclick="location.href='fadmin.jsp'">
      <%
         }      
   } %>   
   </div>   
      <div class="newtext">
      	�ֽű�(10)�� - <a href="flist.jsp">������</a><br>
      	<jsp:include page="fmainnew.jsp"></jsp:include>
      	
      	<%
      		if(session.getAttribute("logindata") != null) {
      	%>
      		<a href="fwrite.jsp">�۾���</a>
      	<%
      		}
      	%>
      	
      </div>
      
      
<jsp:include page="fbottom.jsp"/>   