package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // Super Admin, HR Admin, Employee, IT Admin, etc.
    val department: String, // Engineering, Sales, HR, Finance
    val status: String, // ONBOARDING, ACTIVE, PIP, OFFBOARDED
    val skills: String, // Comma-separated list
    val pfNumber: String,
    val esiNumber: String,
    val joiningDate: String,
    val emergencyContact: String,
    val experienceYears: Int,
    val performanceRating: String = "Not Rated" // OKR score / performance level
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val employeeId: Int,
    val employeeName: String,
    val date: String, // yyyy-MM-dd
    val clockInTime: String, // HH:mm:ss
    val clockOutTime: String?, // HH:mm:ss
    val latitude: Double,
    val longitude: Double,
    val isGeofenced: Boolean,
    val selfiePath: String?,
    val attendanceType: String, // GPS, QR, WFH, Face Recognition
    val status: String, // PRESENT, CORRECTION_PENDING, LATE, ABSENT
    val correctionReason: String? = null
)

@Entity(tableName = "leaves")
data class LeaveEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val employeeId: Int,
    val employeeName: String,
    val leaveType: String, // Annual, Sick, Casual, Maternity, Paternity
    val startDate: String, // yyyy-MM-dd
    val endDate: String, // yyyy-MM-dd
    val reason: String,
    val status: String // PENDING, APPROVED, REJECTED
)

@Entity(tableName = "payroll")
data class PayrollEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val employeeId: Int,
    val employeeName: String,
    val payMonth: String, // June 2026
    val basicSalary: Double,
    val allowances: Double, // Incentives, Bonuses
    val pfDeduction: Double, // Provident Fund (12%)
    val esiDeduction: Double, // Employee State Insurance (0.75%)
    val tdsDeduction: Double, // Tax Deducted at Source
    val profTaxDeduction: Double, // Professional Tax
    val netSalary: Double,
    val isApproved: Boolean = false,
    val paymentStatus: String = "PENDING" // PENDING, PAID
)

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val assetName: String, // MacBook Pro, SIM Card, iPhone 15
    val category: String, // Laptop, Desktop, Mobile, SIM Card, SaaS subscription
    val serialNumber: String,
    val assignedEmployeeId: Int?, // Null if in inventory
    val assignedEmployeeName: String?,
    val warrantyExpiry: String, // yyyy-MM-dd
    val status: String // AVAILABLE, ASSIGNED, UNDER_MAINTENANCE, DISPOSED
)

@Entity(tableName = "support_tickets")
data class TicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val creatorEmployeeId: Int,
    val creatorName: String,
    val title: String,
    val description: String,
    val category: String, // IT support, HR support
    val priority: String, // LOW, MEDIUM, HIGH, CRITICAL
    val status: String, // OPEN, IN_PROGRESS, RESOLVED
    val createdAt: String
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val author: String,
    val timestamp: String
)
