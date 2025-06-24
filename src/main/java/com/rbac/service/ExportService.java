package com.rbac.service;

import com.rbac.dto.role.RoleResponse;
import com.rbac.dto.user.UserResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportService {

    public byte[] exportUsersToExcel(List<UserResponse> users) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Username", "Email", "First Name", "Last Name", "Active", "Email Verified", "Roles", "Created At"};
        
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data rows
        int rowNum = 1;
        for (UserResponse user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getUsername());
            row.createCell(2).setCellValue(user.getEmail());
            row.createCell(3).setCellValue(user.getFirstName() != null ? user.getFirstName() : "");
            row.createCell(4).setCellValue(user.getLastName() != null ? user.getLastName() : "");
            row.createCell(5).setCellValue(user.getIsActive());
            row.createCell(6).setCellValue(user.getEmailVerified());
            row.createCell(7).setCellValue(String.join(", ", user.getRoles()));
            row.createCell(8).setCellValue(user.getCreatedAt().toString());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    public byte[] exportRolesToExcel(List<RoleResponse> roles) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Roles");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Name", "Description", "System Role", "User Count", "Permissions", "Created At"};
        
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data rows
        int rowNum = 1;
        for (RoleResponse role : roles) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(role.getId());
            row.createCell(1).setCellValue(role.getName());
            row.createCell(2).setCellValue(role.getDescription() != null ? role.getDescription() : "");
            row.createCell(3).setCellValue(role.getIsSystemRole());
            row.createCell(4).setCellValue(role.getUserCount());
            row.createCell(5).setCellValue(String.join(", ", role.getPermissions()));
            row.createCell(6).setCellValue(role.getCreatedAt().toString());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    public String exportUsersToCSV(List<UserResponse> users) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Username,Email,First Name,Last Name,Active,Email Verified,Roles,Created At\n");

        for (UserResponse user : users) {
            csv.append(user.getId()).append(",")
               .append(user.getUsername()).append(",")
               .append(user.getEmail()).append(",")
               .append(user.getFirstName() != null ? user.getFirstName() : "").append(",")
               .append(user.getLastName() != null ? user.getLastName() : "").append(",")
               .append(user.getIsActive()).append(",")
               .append(user.getEmailVerified()).append(",")
               .append("\"").append(String.join(", ", user.getRoles())).append("\"").append(",")
               .append(user.getCreatedAt()).append("\n");
        }

        return csv.toString();
    }

    public String exportRolesToCSV(List<RoleResponse> roles) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Name,Description,System Role,User Count,Permissions,Created At\n");

        for (RoleResponse role : roles) {
            csv.append(role.getId()).append(",")
               .append(role.getName()).append(",")
               .append(role.getDescription() != null ? role.getDescription() : "").append(",")
               .append(role.getIsSystemRole()).append(",")
               .append(role.getUserCount()).append(",")
               .append("\"").append(String.join(", ", role.getPermissions())).append("\"").append(",")
               .append(role.getCreatedAt()).append("\n");
        }

        return csv.toString();
    }
}