<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="org.irdresearch.tbreach.server.EventHandler" %>
<%
String xmlResponse = EventHandler.getService().handleEvent(request);
System.out.println(xmlResponse);
//out.println(xmlResponse.substring(54,xmlResponse.length()).trim());
out.println(xmlResponse);
%>
