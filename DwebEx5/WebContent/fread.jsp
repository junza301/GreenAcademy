<%@page import="com.company.FBDto"%>
<%@page import="com.company.FDao"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="bean" class="com.company.FBDto"/>
<jsp:setProperty property="idx" name="bean"/>    
<jsp:include page="ftop.jsp"/>   
<%
   //int idx = Integer.parseInt(request.getParameter("idx"));   
   // ���������� idx�� ������ select �Ѱ���� ����ϴ� ������ ������ 1�� idx�� ����Ű�ϱ�
%>
   <c:set var="dto" value="<%=new FDao().getContent(bean.getIdx()) %>"/>
   <c:choose>
      <c:when test="${dto ne null }">
         <table border="1">
            <tr>
               <td>${dto.idx }</td>
               <td>${dto.title }</td>         
               <td>${dto.fmember_name }(${dto.fmember_id })</td>
               <td>${dto.regdate }</td>         
            </tr>
            <tr>
               <td colspan="4">${dto.content }</td>
            </tr>
         </table>
            <c:if test="${sessionScope.logindata.id eq dto.fmember_id}">         
               <a href="fupdate.jsp">����</a> <a href="fdelete_proc.jsp?idx=${dto.idx }">����</a>
            </c:if>
      </c:when>
      <c:otherwise>
         <script>alert("�߸��� �����Դϴ�.");location.href="fmain.jsp";</script>
      </c:otherwise>
   </c:choose>
<jsp:include page="fbottom.jsp"/>