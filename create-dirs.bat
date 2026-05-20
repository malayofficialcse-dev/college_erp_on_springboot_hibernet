@echo off
REM Create directory structure for sp_boot frontend

cd /d d:\all\Major_Project\sp_boot\frontend\src

REM Create components subdirectories
mkdir components\Shared 2>nul
mkdir components\Common 2>nul

REM Create pages subdirectories
mkdir pages\Academic 2>nul
mkdir pages\Students 2>nul
mkdir pages\Employees 2>nul
mkdir pages\Teachers 2>nul
mkdir pages\Departments 2>nul
mkdir pages\Attendance 2>nul
mkdir pages\Finance 2>nul
mkdir pages\HR 2>nul
mkdir pages\Library 2>nul
mkdir pages\Hostel 2>nul
mkdir pages\Transport 2>nul
mkdir pages\Events 2>nul
mkdir pages\Communication 2>nul
mkdir pages\Reports 2>nul

echo.
echo Directory creation completed!
echo.
echo Created directories:
dir /s /b | find "Shared" & dir /s /b | find "Common" & dir /s /b | find "Academic" & dir /s /b | find "Students" & dir /s /b | find "Employees" & dir /s /b | find "Teachers" & dir /s /b | find "Departments" & dir /s /b | find "Attendance" & dir /s /b | find "Finance" & dir /s /b | find "HR" & dir /s /b | find "Library" & dir /s /b | find "Hostel" & dir /s /b | find "Transport" & dir /s /b | find "Events" & dir /s /b | find "Communication" & dir /s /b | find "Reports"
