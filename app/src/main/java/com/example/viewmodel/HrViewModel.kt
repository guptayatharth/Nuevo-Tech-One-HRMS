package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HrViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = HrRepository(db)

    // UI exposed state flows
    val employees: StateFlow<List<EmployeeEntity>> = repository.allEmployees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendance: StateFlow<List<AttendanceEntity>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaves: StateFlow<List<LeaveEntity>> = repository.allLeaves
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payrolls: StateFlow<List<PayrollEntity>> = repository.allPayrolls
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assets: StateFlow<List<AssetEntity>> = repository.allAssets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tickets: StateFlow<List<TicketEntity>> = repository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<AnnouncementEntity>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mock/Simulated GPS Coords (Nuevo Tech One HQ)
    val hqLatitude = 12.971598
    val hqLongitude = 77.594566
    val geofenceRadiusMeters = 200.0

    // Session Role & User simulation switcher state
    private val _currentEmployeeId = MutableStateFlow(4) // Default is Karthik (Employee)
    val currentEmployeeId: StateFlow<Int> = _currentEmployeeId.asStateFlow()

    val currentEmployee: StateFlow<EmployeeEntity?> = combine(employees, currentEmployeeId) { empList, id ->
        empList.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // AI Feature States
    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _policySearchResult = MutableStateFlow("")
    val policySearchResult: StateFlow<String> = _policySearchResult.asStateFlow()

    private val _resumeScoreResult = MutableStateFlow("")
    val resumeScoreResult: StateFlow<String> = _resumeScoreResult.asStateFlow()

    private val _attritionPredictionResult = MutableStateFlow("")
    val attritionPredictionResult: StateFlow<String> = _attritionPredictionResult.asStateFlow()

    init {
        // Init database seeding and check active status
    }

    // Role switcher helper
    fun switchUserContext(id: Int) {
        viewModelScope.launch {
            _currentEmployeeId.value = id
        }
    }

    // Employee Management Actions
    fun createEmployee(
        name: String,
        email: String,
        phone: String,
        role: String,
        department: String,
        joiningDate: String,
        skills: String,
        experienceYears: Int,
        basicSalary: Double
    ) {
        viewModelScope.launch {
            val isIntern = role.lowercase().contains("intern")
            val status = if (isIntern) "ONBOARDING" else "ACTIVE"
            
            // Generate standard PF and ESI numbers for trial compliance
            val randomSuf = (1000..9999).random()
            val pfNum = "MH/BAN/0014289/341/$randomSuf"
            val esiNum = "53-22-094827-001-0$randomSuf"

            val emp = EmployeeEntity(
                name = name,
                email = email,
                phone = phone,
                role = role,
                department = department,
                status = status,
                skills = skills,
                pfNumber = pfNum,
                esiNumber = esiNum,
                joiningDate = joiningDate,
                emergencyContact = "Primary Spouses/Relatives - +91 99000 88000",
                experienceYears = experienceYears,
                performanceRating = "Not Rated"
            )
            val newId = repository.insertEmployee(emp).toInt()

            // Automatically generate matching Salary Structure for payroll simulation
            createPayrollStructure(newId, name, basicSalary)
        }
    }

    fun updateEmployee(employee: EmployeeEntity) {
        viewModelScope.launch {
            repository.updateEmployee(employee)
        }
    }

    fun deleteEmployee(employee: EmployeeEntity) {
        viewModelScope.launch {
            repository.deleteEmployee(employee)
        }
    }

    // India Compliance & Payroll Logic
    private suspend fun createPayrollStructure(empId: Int, name: String, basic: Double) {
        // PF Contribution: 12% of basic
        val pf = basic * 0.12
        
        // ESI Contribution: 0.75% of (basic + allowances) if total gross <= ₹21,000, otherwise 0
        val allowance = basic * 0.15 // Default allowances (HRA/Medical etc)
        val gross = basic + allowance
        val esi = if (gross <= 21000.0) gross * 0.0075 else 0.0

        // Professional Tax: Flat ₹200 (standard lab tax)
        val profTax = 200.0

        // In India, Tax Deducted at Source (TDS) depends on annual slabs (tax regime).
        // Let's create an elegant slab simulation for monthly calculations:
        val annualEstimated = gross * 12
        val monthlyTds = when {
            annualEstimated <= 300000 -> 0.0
            annualEstimated <= 700000 -> (gross * 0.05)
            annualEstimated <= 1000000 -> (gross * 0.10)
            else -> (gross * 0.20)
        }

        val net = gross - (pf + esi + profTax + monthlyTds)

        val payroll = PayrollEntity(
            employeeId = empId,
            employeeName = name,
            payMonth = "June 2026",
            basicSalary = basic,
            allowances = allowance,
            pfDeduction = pf,
            esiDeduction = esi,
            tdsDeduction = monthlyTds,
            profTaxDeduction = profTax,
            netSalary = net,
            isApproved = false,
            paymentStatus = "PENDING"
        )
        repository.insertPayroll(payroll)
    }

    fun recalculateAndAddCustomPayrollRule(
        empId: Int,
        name: String,
        basic: Double,
        bonuses: Double,
        deductionFactor: Double
    ) {
        viewModelScope.launch {
            val pf = basic * 0.12
            val allowances = basic * 0.15 + bonuses
            val gross = basic + allowances
            val esi = if (gross <= 21000.0) gross * 0.0075 else 0.0
            val profTax = 200.0
            
            val annualEstimate = gross * 12
            val tds = when {
                annualEstimate <= 300000 -> 0.0
                annualEstimate <= 700000 -> (gross * 0.05)
                annualEstimate <= 1000000 -> (gross * 0.10)
                else -> (gross * 0.20)
            }

            val totalDeductionVal = pf + esi + profTax + tds + deductionFactor
            val net = gross - totalDeductionVal

            val payroll = PayrollEntity(
                employeeId = empId,
                employeeName = name,
                payMonth = "June 2026",
                basicSalary = basic,
                allowances = allowances,
                pfDeduction = pf,
                esiDeduction = esi,
                tdsDeduction = tds,
                profTaxDeduction = profTax,
                netSalary = net,
                isApproved = false,
                paymentStatus = "PENDING"
            )
            repository.insertPayroll(payroll)
        }
    }

    fun approvePayroll(payrollId: Int) {
        viewModelScope.launch {
            val currentList = payrolls.value
            val match = currentList.find { it.id == payrollId }
            if (match != null) {
                repository.updatePayroll(
                    match.copy(isApproved = true, paymentStatus = "PAID")
                )
            }
        }
    }

    // Attendance Management Logic
    fun performClockIn(
        latitude: Double,
        longitude: Double,
        attendanceType: String, // "GPS" or "WFH" or "QR" or "Face"
        selfiePath: String? = null
    ) {
        viewModelScope.launch {
            val emp = currentEmployee.value ?: return@launch
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val clockInString = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            // Distance calculation using Haversine formula
            val theta = longitude - hqLongitude
            var dist = Math.sin(Math.toRadians(latitude)) * Math.sin(Math.toRadians(hqLatitude)) +
                    Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(hqLatitude)) * Math.cos(Math.toRadians(theta))
            dist = Math.acos(dist)
            dist = Math.toDegrees(dist)
            val distanceInMeters = dist * 60 * 1.1515 * 1609.344

            val inGeofence = (distanceInMeters <= geofenceRadiusMeters) || attendanceType == "WFH"
            val statusVal = if (inGeofence) "PRESENT" else "CORRECTION_PENDING"

            val existing = repository.getTodayAttendanceForEmployee(emp.id, todayDate)
            if (existing != null) {
                // Already clocked in for today
                return@launch
            }

            val attendanceRecord = AttendanceEntity(
                employeeId = emp.id,
                employeeName = emp.name,
                date = todayDate,
                clockInTime = clockInString,
                clockOutTime = null,
                latitude = latitude,
                longitude = longitude,
                isGeofenced = inGeofence,
                selfiePath = selfiePath,
                attendanceType = attendanceType,
                status = statusVal
            )
            repository.insertAttendance(attendanceRecord)
        }
    }

    fun performClockOut() {
        viewModelScope.launch {
            val emp = currentEmployee.value ?: return@launch
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val clockOutString = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            val record = repository.getTodayAttendanceForEmployee(emp.id, todayDate)
            if (record != null && record.clockOutTime == null) {
                repository.updateAttendance(
                    record.copy(clockOutTime = clockOutString)
                )
            }
        }
    }

    fun requestAttendanceCorrection(attendanceId: Int, reason: String) {
        viewModelScope.launch {
            val list = attendance.value
            val match = list.find { it.id == attendanceId }
            if (match != null) {
                repository.updateAttendance(
                    match.copy(
                        status = "CORRECTION_PENDING",
                        correctionReason = reason
                    )
                )
            }
        }
    }

    fun approveAttendanceCorrection(attendanceId: Int) {
        viewModelScope.launch {
            val list = attendance.value
            val match = list.find { it.id == attendanceId }
            if (match != null) {
                repository.updateAttendance(
                    match.copy(
                        status = "PRESENT",
                        correctionReason = "Approved - Correction request accepted"
                    )
                )
            }
        }
    }

    // Leave Actions
    fun requestLeave(leaveType: String, startStr: String, endStr: String, reason: String) {
        viewModelScope.launch {
            val emp = currentEmployee.value ?: return@launch
            val request = LeaveEntity(
                employeeId = emp.id,
                employeeName = emp.name,
                leaveType = leaveType,
                startDate = startStr,
                endDate = endStr,
                reason = reason,
                status = "PENDING"
            )
            repository.insertLeave(request)
        }
    }

    fun updateLeaveStatus(leaveId: Int, approved: Boolean) {
        viewModelScope.launch {
            val list = leaves.value
            val match = list.find { it.id == leaveId }
            if (match != null) {
                repository.updateLeave(
                    match.copy(status = if (approved) "APPROVED" else "REJECTED")
                )
            }
        }
    }

    // Assets Actions
    fun createAsset(name: String, category: String, serial: String, warranty: String) {
        viewModelScope.launch {
            val asset = AssetEntity(
                assetName = name,
                category = category,
                serialNumber = serial,
                assignedEmployeeId = null,
                assignedEmployeeName = null,
                warrantyExpiry = warranty,
                status = "AVAILABLE"
            )
            repository.insertAsset(asset)
        }
    }

    fun assignAsset(assetId: Int, empId: Int) {
        viewModelScope.launch {
            val assetList = assets.value
            val match = assetList.find { it.id == assetId }
            val empList = employees.value
            val empMatch = empList.find { it.id == empId }
            if (match != null && empMatch != null) {
                repository.updateAsset(
                    match.copy(
                        assignedEmployeeId = empMatch.id,
                        assignedEmployeeName = empMatch.name,
                        status = "ASSIGNED"
                    )
                )
            }
        }
    }

    fun releaseAsset(assetId: Int) {
        viewModelScope.launch {
            val assetList = assets.value
            val match = assetList.find { it.id == assetId }
            if (match != null) {
                repository.updateAsset(
                    match.copy(
                        assignedEmployeeId = null,
                        assignedEmployeeName = null,
                        status = "AVAILABLE"
                    )
                )
            }
        }
    }

    fun deleteAsset(asset: AssetEntity) {
        viewModelScope.launch {
            repository.deleteAsset(asset)
        }
    }

    // Ticketing Actions
    fun submitSupportTicket(title: String, description: String, category: String, priority: String) {
        viewModelScope.launch {
            val emp = currentEmployee.value ?: return@launch
            val nowStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val ticket = TicketEntity(
                creatorEmployeeId = emp.id,
                creatorName = emp.name,
                title = title,
                description = description,
                category = category,
                priority = priority,
                status = "OPEN",
                createdAt = nowStr
            )
            repository.insertTicket(ticket)
        }
    }

    fun updateTicketStatus(ticketId: Int, status: String) {
        viewModelScope.launch {
            val list = tickets.value
            val match = list.find { it.id == ticketId }
            if (match != null) {
                repository.updateTicket(
                    match.copy(status = status)
                )
            }
        }
    }

    // Communication Actions
    fun postAnnouncement(title: String, content: String) {
        viewModelScope.launch {
            val emp = currentEmployee.value ?: return@launch
            val nowStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val ann = AnnouncementEntity(
                title = title,
                content = content,
                author = "${emp.name} (${emp.role})",
                timestamp = nowStr
            )
            repository.insertAnnouncement(ann)
        }
    }

    // AI - Core Integration triggers using Gemini Client

    // 1. Policy Search Assistant
    fun performPolicySearch(query: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val systemStr = "You are the AI HR Policy Search Assistant for Nuevo Tech One. Answer questions accurately based on standard corporate policies, employee handbooks, hybrid attendance mandates, annual leaves (24 days/year, max 8 rollover), and India compliance laws (PF, ESI, and Tax slabs). Ensure a supportive, definitive tone."
            val responseText = GeminiClient.generateContent(
                prompt = "Corporate Policy Query: $query",
                systemInstruction = systemStr
            )
            _policySearchResult.value = responseText
            _isAiLoading.value = false
        }
    }

    // 2. Chatbot Assistant
    fun performAiChat(promptText: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val systemStr = "You are the primary AI Executive HR Assistant for Nuevo Tech One HRMS. You can assist employees with queries on onboarding checklist status, basic PF/ESI compliance computations, leave availability workflows, asset return instructions, or drafting emails for managers. Answer clearly and concisely."
            val responseText = GeminiClient.generateContent(
                prompt = promptText,
                systemInstruction = systemStr
            )
            _aiResponse.value = responseText
            _isAiLoading.value = false
        }
    }

    // 3. AI CV Screening fitting
    fun performCandidateScreening(jobTitle: String, resumeText: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val systemStr = "You are an AI Application Tracking System (ATS) Parser for Nuevo Tech One. Analyze the matching ratio of the resume string with the requested job title. Extract skills match, and output: 1) Score out of 100%, 2) Key Skill Matches, 3) Critical Gaps, and 4) Overall Fit recommendation (Shortlist, Hold, or Reject) in a neat structured layout."
            val responseText = GeminiClient.generateContent(
                prompt = "Analyze Resume fit for '$jobTitle' Position. Resume Content:\n$resumeText",
                systemInstruction = systemStr
            )
            _resumeScoreResult.value = responseText
            _isAiLoading.value = false
        }
    }

    // 4. State Attrition Predictive Intelligence
    fun performAttritionAnalytic() {
        viewModelScope.launch {
            _isAiLoading.value = true
            val emps = db.employeeDao.getAllEmployees().first()
            val promptInput = StringBuilder()
            promptInput.append("Analyze attrition vulnerabilities for the following employee roster (evaluating years, skills, rating adjustments):\n")
            emps.forEach {
                promptInput.append("- Name: ${it.name}, Dept: ${it.department}, Role: ${it.role}, Rating: ${it.performanceRating}, Tenure Years: ${it.experienceYears}, Current Status: ${it.status}\n")
            }
            promptInput.append("\nPredict the top 2 employees most at risk of attrition, calculate estimated attrition probability (%), provide sentiment context, and suggest urgent proactive retention recommendations (e.g. Compensation audit, Career mapping or OKR adjustment). Output in elegant bullets.")

            val systemStr = "You are the advanced Predictive People Analytics engine for Nuevo Tech One. Evaluate retention risk metrics safely based on experience, performance status, and structural role configurations. Be precise, empathetic, and objective."
            val responseText = GeminiClient.generateContent(
                prompt = promptInput.toString(),
                systemInstruction = systemStr
            )
            _attritionPredictionResult.value = responseText
            _isAiLoading.value = false
        }
    }

    fun clearAiStates() {
        _policySearchResult.value = ""
        _aiResponse.value = ""
        _resumeScoreResult.value = ""
        _attritionPredictionResult.value = ""
    }
}
