<%@page import="com.company.FBDto"%>
<%@page import="java.util.Vector"%>
<%@page import="com.company.FDao"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>    
<jsp:include page="ftop.jsp"/>

<table border="1">   
   <tr>
      <td>��ȣ</td>
      <td>����</td>
      <td>�۾���</td>
      <td>��¥</td>
   </tr> 
<%
   FDao dao = new FDao();
   
	double totalCnt = (double)dao.getTotlaCnt();//�� ����
   int curPage = 5;// ���������� ����� ����
   int totalPage = (int)Math.ceil(totalCnt/curPage);// �� ������ ����
   
   int pnum=1;
   
   if(request.getParameter("pnum") != null){
      pnum = Integer.parseInt(request.getParameter("pnum"));   
   }
    
   
   
   Vector<FBDto> v= dao.getFboardAll(pnum,curPage);
   if(v.size()>0){     
%>         
         <c:forEach var="i" items="<%=v %>">
            <tr>
               <td>${i.idx}</td>
               <td><a href="fread.jsp?idx=${i.idx}">${i.title}</a></td>
               <td>${i.fmember_name}</td>
               <td>${i.regdate}</td>
            </tr>
         </c:forEach>         
<%      
   }else{ %>
         <tr>
            <td colspan="4">�Խñ��� �����ϴ�.</td>
         </tr>
<%} %>
      </table>

<%
   for(int i=1;i<=totalPage;i++){
%>
   <a href="flist.jsp?pnum=<%=i %>">[<%=i %>]</a>
<%            
   }
%>   

<!-- // ���������� ���� ���� = 5�� ������ - �̰Ŵ� �̸� ���ؾ� �մϴ�.
// ���� ������ 1~34 ���� �� 34 ���Դϴ�.
// ���������� 5�� �� 34���� / ��ü �������� �? 7������

34 / 5 ������ =?  6.8 = ������ �ø� > 7
//db���� �Ѱ��� ��������(select count(idx) from fboard);
// 5�� ������ �� => ������ �ø� --> 
      

<jsp:include page="fbottom.jsp"/>
