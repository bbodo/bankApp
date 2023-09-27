<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

v <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
  <script src="https://cdn.jsdelivr.net/npm/jquery@3.6.4/dist/jquery.slim.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
<div class="col-sm-8">
	<h2>나의 계좌 목록</h2>
	<h5>어서오세요 환영합니다</h5>
	<div class="p-md-5 h-75">
		<c:choose>
			<c:when test="${accountList != null}">
				<table class="table table-bordered table-striped table-hover">
					<thead>
						<tr  class="thead-dark">
							<td>계좌 번호</td>
							<td>잔액</td>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="account" items="${accountList}">
							<tr>
								<td><a href="/account/detail/${account.id}">${account.number}</a>
								</td>
								
								<td><a href="/account/detail/${account.id}">${account.balance}</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<p>아직 생성된 계좌가 없습니다</p>
			</c:otherwise>
		</c:choose>
	</div>
</div>
<%@ include file="/WEB-INF/view/layout/footer.jsp"%>

