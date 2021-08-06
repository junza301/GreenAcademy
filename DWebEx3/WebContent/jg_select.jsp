<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ include file="/jg_include_sel.jspf" %>    
<table>    
    
<%	
	boolean change = false;
	String select_url = "";
	String condition = request.getParameter("condition");
	String content_condition = request.getParameter("ccon");
	if(condition != null && content_condition != null) {
		select_url = " where "+ condition + " like '%"+ content_condition + "%'";
	}
		

	try{	
		String sql = "select * from guestbook" + select_url;
		stmt = conn.prepareStatement(sql);
		System.out.println(sql);
		rs = stmt.executeQuery();
		while(rs.next()){
%>
		<tr style="background-color: orange;">
			<td><%=rs.getInt("idx")%></td>
			<td><%=rs.getString("name")%></td>
			<td><%=rs.getString("email")%>
				<%-- <a href="jg_md.jsp?idx=<%//=rs.getInt("idx")%>">M/D</a> --%>
<input type="button" value="M/D" style="width: 50px;" onclick="location.href='jg_md.jsp?idx=<%=rs.getInt("idx")%>'">
			</td>
		</tr>		
		<tr>
			<td colspan="3">
			<%=rs.getString("content").replace("\r\n", "<br>") %></td>			
		</tr>
<%		
		}		
	}catch(SQLException sqle){
		sqle.printStackTrace();
	}finally{
		try{
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(conn != null) conn.close();
		}catch(SQLException sqle){
			sqle.printStackTrace();
		}
	}
%>

</table>