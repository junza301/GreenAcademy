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
            아이디 <input type="text" name="id"> 비밀번호 <input type="password" name="pw">
            <input type="submit" value="로그인">
            <input type="button" value="회원가입" onclick="location.href='fregist.jsp'">
         </form>   
<%} else {%>
      <%=dto.getName() %>님 반갑습니다. <input type="button" value="로그아웃" onclick="location.href='flogout.jsp'">
      <%
         if(dto.getLv()==10){
      %>
         <input type="button" value="관리자" onclick="location.href='fadmin.jsp'">
      <%
         }      
   } %>   
   </div>   
      <div class="newtext"></div>
      
      
<jsp:include page="fbottom.jsp"/>   