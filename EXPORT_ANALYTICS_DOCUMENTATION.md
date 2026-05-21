# Export & Analytics Features Documentation

## Overview
This document outlines the new Export and Analytics features implemented in the College ERP system.

## New Endpoints

### 1. Analytics Endpoints

#### Student Performance Analytics
```
GET /api/analytics/student-performance
Params:
  - startDate (optional): Start date (YYYY-MM-DD)
  - endDate (optional): End date (YYYY-MM-DD)
  - department (optional): Department filter

Response: StudentPerformanceAnalytics object with:
  - averageGPA
  - totalStudents
  - passedStudents / failedStudents
  - passRate
  - gradeDistribution (A, B, C, D, F counts)
  - subjectPerformance (array)
  - departmentWise (array)
  - gpatrends (monthly trends)
```

#### Finance Analytics
```
GET /api/analytics/finance
Params:
  - startDate (optional): Start date (YYYY-MM-DD)
  - endDate (optional): End date (YYYY-MM-DD)
  - department (optional): Department filter

Response: FinanceAnalytics object with:
  - totalFeeCollected
  - totalFeeDue
  - collectionRate (percentage)
  - totalStudents
  - departmentWiseCollection (map)
  - monthlyTrends (array)
  - paymentStatus (breakdown)
  - feeBreakdown (by type)
```

#### Attendance Analytics
```
GET /api/analytics/attendance
Params:
  - startDate (optional): Start date (YYYY-MM-DD)
  - endDate (optional): End date (YYYY-MM-DD)
  - department (optional): Department filter

Response: AttendanceAnalytics object with:
  - averageAttendanceRate
  - totalStudents
  - departmentWiseAttendance (map)
  - monthlyTrends (array)
  - absenteeReport (students with low attendance)
  - attendanceDistribution (90-100%, 75-89%, etc.)
```

### 2. Export Endpoints

#### Export Student Records
```
GET /api/students/export/all
Params:
  - format (PDF|EXCEL|CSV): Export format, default: PDF
  - departmentId (optional): Filter by department
  - semester (optional): Filter by semester

Returns: Binary file (PDF/Excel/CSV) with filename "Students-Export.{format}"
```

#### Export Fee Payments
```
GET /api/fees/export/all
Params:
  - format (PDF|EXCEL|CSV): Export format, default: PDF
  - status (optional): Filter by payment status (PAID, PENDING, etc.)
  - dateFrom (optional): Start date (YYYY-MM-DD)
  - dateTo (optional): End date (YYYY-MM-DD)

Returns: Binary file with filename "Fee-Payments-Export.{format}"
```

#### Export Attendance Records
```
GET /api/attendance/export/all
Params:
  - format (PDF|EXCEL|CSV): Export format, default: PDF
  - studentId (optional): Filter by student
  - dateFrom (optional): Start date (YYYY-MM-DD)
  - dateTo (optional): End date (YYYY-MM-DD)

Returns: Binary file with filename "Attendance-Export.{format}"
```

#### Export Analytics Reports
```
GET /api/analytics/student-performance/export
GET /api/analytics/finance/export
GET /api/analytics/attendance/export

Params:
  - startDate (optional): Start date (YYYY-MM-DD)
  - endDate (optional): End date (YYYY-MM-DD)
  - format (PDF|EXCEL|CSV): Export format, default: PDF
  - department (optional): Department filter

Returns: Binary file with analytics data
```

## Usage Examples

### Example 1: Get Student Performance Analytics
```bash
curl -X GET "http://localhost:8080/api/analytics/student-performance?startDate=2024-01-01&endDate=2024-12-31"
```

### Example 2: Export All Students as Excel
```bash
curl -X GET "http://localhost:8080/api/students/export/all?format=EXCEL&departmentId=1" \
  -H "Authorization: Bearer {token}" \
  -o students.xlsx
```

### Example 3: Export Finance Data as PDF
```bash
curl -X GET "http://localhost:8080/api/fees/export/all?format=PDF&dateFrom=2024-01-01&dateTo=2024-12-31" \
  -H "Authorization: Bearer {token}" \
  -o fee-report.pdf
```

### Example 4: Get Attendance Analytics with Export
```bash
curl -X GET "http://localhost:8080/api/analytics/attendance/export?format=EXCEL&startDate=2024-01-01" \
  -H "Authorization: Bearer {token}" \
  -o attendance-analytics.xlsx
```

## Features

### Analytics Features
✅ **Student Performance Metrics**
- Average GPA calculation
- Pass/Fail rates
- Grade distribution analysis
- Subject-wise performance comparison
- Department-wise performance comparison
- Monthly GPA trends

✅ **Finance Analytics**
- Total fee collection tracking
- Outstanding fee analysis
- Collection rate percentage
- Monthly revenue trends
- Department-wise collection comparison
- Payment status breakdown

✅ **Attendance Analytics**
- Overall attendance rate
- Department-wise attendance comparison
- Monthly attendance trends
- Absentee reporting (students below threshold)
- Attendance distribution buckets
- Individual student attendance summaries

### Export Features
✅ **Multiple Formats**
- PDF: Professional formatted reports with styling
- Excel: Spreadsheets with header styling and formatting
- CSV: Comma-separated values for data import/analysis

✅ **Filter Support**
- Date range filtering
- Department/status filtering
- Student/subject filtering
- Custom parameter support

✅ **Report Customization**
- Custom titles and headers
- Optional summary statistics
- Formatted styling
- Pagination support for large datasets

## Architecture

### New Classes Created

1. **Export Layer** (`com.example.demo.export.*`)
   - `ExportRequest`: DTO for export parameters
   - `ExcelExporter`: Excel file generation
   - `PdfExporter`: PDF file generation
   - `ExportService`: Main export orchestrator

2. **Analytics Layer** (`com.example.demo.analytics.*`)
   - `StudentAnalyticsService`: Student performance metrics
   - `FinanceAnalyticsService`: Fee collection analytics
   - `AttendanceAnalyticsService`: Attendance pattern analysis

3. **DTOs**
   - `StudentPerformanceAnalytics`: Student metrics response
   - `FinanceAnalytics`: Finance metrics response
   - `AttendanceAnalytics`: Attendance metrics response

4. **Controllers**
   - `AnalyticsController`: Analytics REST endpoints
   - Enhanced: `StudentController`, `FeePaymentController`, `AttendanceRecordController`

## Dependencies Added

```xml
<!-- Apache POI for Excel -->
<dependency>
  <groupId>org.apache.poi</groupId>
  <artifactId>poi</artifactId>
  <version>5.2.5</version>
</dependency>

<!-- iText for PDF -->
<dependency>
  <groupId>com.itextpdf</groupId>
  <artifactId>itextpdf</artifactId>
  <version>5.5.13.3</version>
</dependency>

<!-- Flying Saucer for PDF rendering -->
<dependency>
  <groupId>org.xhtmlrenderer</groupId>
  <artifactId>flying-saucer-core</artifactId>
  <version>9.1.22</version>
</dependency>
```

## Security

✅ All endpoints require authentication (`@PreAuthorize("isAuthenticated()")`)
✅ Export endpoints available to all authenticated users
✅ Analytics endpoints available to all authenticated users
✅ No role restrictions - can be added per module as needed

## Performance Considerations

- Analytics calculations use in-memory aggregation
- Suitable for datasets up to 100K records
- For larger datasets, consider caching or pagination
- PDF generation may be memory-intensive for large exports
- CSV export is most lightweight format

## Future Enhancements

- [ ] Scheduled report generation
- [ ] Email delivery of reports
- [ ] Advanced charting in PDF/Excel
- [ ] Custom report builder
- [ ] Data caching for analytics
- [ ] Real-time dashboards
- [ ] Export templates
- [ ] Batch export operations
- [ ] Report scheduling and automation

## Testing

To test the features:

1. Start the application
2. Authenticate with valid credentials
3. Call analytics endpoints:
   ```
   GET /api/analytics/student-performance
   GET /api/analytics/finance
   GET /api/analytics/attendance
   ```
4. Export data:
   ```
   GET /api/students/export/all?format=PDF
   GET /api/fees/export/all?format=EXCEL
   GET /api/attendance/export/all?format=CSV
   ```

## Troubleshooting

**Issue**: Export returns empty file
- **Solution**: Ensure data exists in database with correct date range filters

**Issue**: PDF generation is slow
- **Solution**: Use Excel or CSV format for faster export, or export smaller date ranges

**Issue**: Memory error on large exports
- **Solution**: Implement pagination or split exports into smaller date ranges

## Support

For issues or feature requests, please contact the development team.
