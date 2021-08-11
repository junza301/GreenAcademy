package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/*
   create table fmember(
		idx INT PRIMARY KEY AUTO_INCREMENT,
		id VARCHAR(20) NOT NULL,
		pw VARCHAR(20) NOT NULL,
		NAME VARCHAR(20) NOT NULL,
		email VARCHAR(50) 
	);

	CREATE TABLE fboard(
		idx INT PRIMARY KEY AUTO_INCREMENT,
		fmeber_id VARCHAR(20) NOT NULL,
		fmeber_name VARCHAR(20) NOT NULL,
		title VARCHAR(100) NOT NULL,
		content TEXT NOT NULL,
		regdate DATE NOT NULL
	);

	SELECT * FROM fmember;
	*/

public class FDao {
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	int result = 0;

	private Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://127.0.0.1:3306/jspdb";
			String id = "root";
			String pw = "1234";
			conn = DriverManager.getConnection(url, id, pw);
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

	private void freeConnection(ResultSet r, PreparedStatement p, Connection c) {
		// select �Ҷ� close();
		try {
			if (r != null)
				r.close();
			if (p != null)
				p.close();
			if (c != null)
				c.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	private void freeConnection(PreparedStatement p, Connection c) {
		// insert, update, delete �Ҷ� close();
		try {
			if (p != null)
				p.close();
			if (c != null)
				c.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	public int fregist(FDto bean) {

		try {
			conn = getConnection();
			String sql = "insert into fmember values (null, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			// ���ֱ�
			pstmt.setString(1, bean.getId());
			pstmt.setString(2, bean.getPw());
			pstmt.setString(3, bean.getName());
			pstmt.setString(4, bean.getEmail());
			result = pstmt.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			freeConnection(pstmt, conn);
		}

		return result;
	}

	public FDto flogin(FDto inDto) {
		FDto dto = new FDto();

		System.out.println(inDto.getId());
		System.out.println(inDto.getPw());
		try {
			conn = getConnection();
			String sql = "select * from fmember where id=? and pw=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, inDto.getId());
			pstmt.setString(2, inDto.getPw());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				dto.setIdx(rs.getInt(1));
				dto.setId(rs.getString(2));
				dto.setName(rs.getString(4));
				dto.setEmail(rs.getString(5));
				dto.setLv(rs.getInt(6));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			freeConnection(rs, pstmt, conn);
		}

		return dto;
	}

	public Vector<FDto> getAllFmember() {
		Vector<FDto> v = new Vector<>();
		conn = getConnection();

		try {
			String sql = "select * from fmember";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				FDto dto = new FDto();
				dto.setIdx(rs.getInt(1));
				dto.setId(rs.getString(2));
				dto.setName(rs.getString(4));
				dto.setEmail(rs.getString(5));
				dto.setLv(rs.getInt(6));
				v.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			freeConnection(rs, pstmt, conn);
		}
		return v;
	}

	public Vector<FBDto> getFboardAll(String order, int cnt) {// order���� regdate
		Vector<FBDto> v = new Vector<>();

		try {
			conn = getConnection();

			String sql = "select * from fboard order by " + order + " LIMIT " + cnt;

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				FBDto dto = new FBDto();
				dto.setIdx(rs.getInt(1));
				dto.setFmember_id(rs.getString(2));
				dto.setFmember_name(rs.getString(3));
				dto.setTitle(rs.getString(4));
				dto.setContent(rs.getString(5));
				dto.setRegdate(rs.getString(6));// ��¥�� ���ڷ� ó���ϸ� ���ϴ�
				v.add(dto);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			freeConnection(rs, pstmt, conn);
		}

		return v;
	}

	public Vector<FBDto> getFboardAll(int offset, int cnt) {
		Vector<FBDto> v = new Vector<>();
		// 1,2,3,4,5,6,7
		// 0,5,10,15,20,25
		try {
			conn = getConnection();
			String sql = "select * from fboard order by regdate desc limit ?, ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, (offset - 1) * cnt);
			pstmt.setInt(2, cnt);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				FBDto dto = new FBDto();
				dto.setIdx(rs.getInt(1));
				dto.setFmember_id(rs.getString(2));
				dto.setFmember_name(rs.getString(3));
				dto.setTitle(rs.getString(4));
				dto.setContent(rs.getString(5));
				dto.setRegdate(rs.getString(6));
				v.add(dto);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			freeConnection(rs, pstmt, conn);
		}

		return v;
	}

	public int insertFBoard(FDto dto, FBDto bdto) {

		try {
			conn = getConnection();
			String sql = "insert into fboard values (null, ?, ?, ?, ?, now())";
			pstmt = conn.prepareStatement(sql);
			// ?�� ���� �ִ� �ڵ�
			pstmt.setString(1, dto.getId());
			pstmt.setString(2, dto.getName());
			pstmt.setString(3, bdto.getTitle());
			pstmt.setString(4, bdto.getContent());

			result = pstmt.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			freeConnection(pstmt, conn);
		}

		return result;
	}

	public FBDto getContent(int idx) {
		FBDto dto = new FBDto();
		conn = getConnection();

		try {
			String sql = "select * from fboard where idx = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, idx);
			rs = pstmt.executeQuery();
			rs.next();
			dto.setIdx(rs.getInt(1));
			dto.setFmember_id(rs.getString(2));
			dto.setFmember_name(rs.getString(3));
			dto.setTitle(rs.getString(4));
			dto.setContent(rs.getString(5));
			dto.setRegdate(rs.getString(6));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dto;
	}

	public int getTotlaCnt() {
		try {
			conn = getConnection();
			String sql = "select count(idx) from fboard";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			freeConnection(rs, pstmt, conn);
		}

		return result;
	}

	public int deleteBoard(int idx, String id){
		try{
			conn = getConnection();
			String sql = "delete from fboard where idx=? and fmeber_id=?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, idx);
			pstmt.setString(2, id);
			result = pstmt.executeUpdate();
		}catch (SQLException sqle) {
			sqle.printStackTrace();
		}finally {
			freeConnection(pstmt, conn);
		}
		
		return result;
	}
	
	public FBDto getFBoard(int idx, String id){
		FBDto dto = new FBDto();
		try{
			conn=getConnection();
			String sql = "select * from fboard where idx=? and fmeber_id=?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, idx);
			pstmt.setString(2, id);
			rs = pstmt.executeQuery();
			if(rs.next()){
				dto.setIdx(rs.getInt(1));
				dto.setFmember_id(rs.getString(2));
				dto.setFmember_name(rs.getString(3));
				dto.setTitle(rs.getString(4));
				dto.setContent(rs.getString(5));
			}
		}catch (SQLException sqle) {
			sqle.printStackTrace();
		}finally {
			freeConnection(rs, pstmt, conn);
		}
		
		return dto;
	}
	
	public int updateFBoard(FBDto dto, String id){
		try{
			conn = getConnection();
			String sql = "update fboard set title=?, content=?, regdate=now() where fmeber_id=? and idx=?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, dto.getTitle());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, id);
			pstmt.setInt(4, dto.getIdx());
			result = pstmt.executeUpdate();
		}catch (SQLException sqle) {
			sqle.printStackTrace();
		}finally {
			freeConnection(pstmt, conn);
		}
		
		
		return result;
	}
}