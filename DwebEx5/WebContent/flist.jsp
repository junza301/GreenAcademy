<%@page import="com.company.FBDto"%>
<%@page import="java.util.Vector"%>
<%@page import="com.company.FDao"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>    
<jsp:include page="ftop.jsp"/>

<table border="1">   
   <tr>
      <td>번호</td>
      <td>제목</td>
      <td>글쓴이</td>
      <td>날짜</td>
   </tr> 
<%
   FDao dao = new FDao();
   
	double totalCnt = (double)dao.getTotlaCnt();//총 개수
   int curPage = 5;// 한페이지에 출력할 개수
   int totalPage = (int)Math.ceil(totalCnt/curPage);// 총 페이지 개수
   
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
            <td colspan="4">게시글이 없습니다.</td>
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

<!-- // 한페이지의 나올 개수 = 5개 나오게 - 이거는 미리 정해야 합니다.
// 현재 개수는 1~34 까지 총 34 개입니다.
// 한페이지에 5개 총 34개면 / 전체 페이지는 몇개? 7페이지

34 / 5 나누면 =?  6.8 = 무조건 올림 > 7
//db에서 총개수 가져오기(select count(idx) from fboard);
// 5는 정해진 값 => 무조건 올림 --> 
      

<jsp:include page="fbottom.jsp"/>
