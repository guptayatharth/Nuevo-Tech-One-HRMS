package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HrRepository(private val db: AppDatabase) {

    val allEmployees: Flow<List<EmployeeEntity>> = db.employeeDao.getAllEmployees()
    val allAttendance: Flow<List<AttendanceEntity>> = db.attendanceDao.getAllAttendance()
    val allLeaves: Flow<List<LeaveEntity>> = db.leaveDao.getAllLeaves()
    val allPayrolls: Flow<List<PayrollEntity>> = db.payrollDao.getAllPayrolls()
    val allAssets: Flow<List<AssetEntity>> = db.assetDao.getAllAssets()
    val allTickets: Flow<List<TicketEntity>> = db.ticketDao.getAllTickets()
    val allAnnouncements: Flow<List<AnnouncementEntity>> = db.announcementDao.getAllAnnouncements()

    init {
        // Automatically seed the database on separate scope
        CoroutineScope(Dispatchers.IO).launch {
            try {
                seedIfEmpty()
            } catch (e: Exception) {
                // Squelch
            }
        }
    }

    private suspend fun seedIfEmpty() {
        val currentEmps = db.employeeDao.getAllEmployees().first()
        if (currentEmps.isEmpty()) {
            // Seed Employees
            val employees = listOf(
                EmployeeEntity(
                    name = "Arjun Sharma",
                    email = "arjun.sharma@nuevotechone.com",
                    phone = "+91 98765 43210",
                    role = "Super Admin",
                    department = "Management",
                    status = "ACTIVE",
                    skills = "Strategy, Leadership, Operations, Product Planning",
                    pfNumber = "DL/CPM/1023456/789/20124",
                    esiNumber = "31-00-123456-001-0101",
                    joiningDate = "2020-03-15",
                    emergencyContact = "Priya Sharma (Wife) - +91 98765 43211",
                    experienceYears = 12,
                    performanceRating = "9.4/10"
                ),
                EmployeeEntity(
                    name = "Neha Deshmukh",
                    email = "neha.d@nuevotechone.com",
                    role = "HR Administrator",
                    phone = "+91 91234 56789",
                    department = "Human Resources",
                    status = "ACTIVE",
                    skills = "Talent Acquisition, Compliance, Employee Relations, Payroll Admin",
                    pfNumber = "MH/BAN/0014289/341/30421",
                    esiNumber = "53-22-094827-001-0202",
                    joiningDate = "2021-08-01",
                    emergencyContact = "Ramesh Deshmukh (Father) - +91 91234 56780",
                    experienceYears = 7,
                    performanceRating = "8.8/10"
                ),
                EmployeeEntity(
                    name = "Vikram Aditya",
                    email = "vikram.a@nuevotechone.com",
                    role = "IT Administrator",
                    phone = "+91 88888 77777",
                    department = "Information Technology",
                    status = "ACTIVE",
                    skills = "AWS, Linux, Network Security, SaaS Administration, MDM",
                    pfNumber = "KA/BLR/0123984/102/40293",
                    esiNumber = "12-11-230491-001-0303",
                    joiningDate = "2022-05-10",
                    emergencyContact = "Sandhya Aditya (Mother) - +91 88888 77776",
                    experienceYears = 5,
                    performanceRating = "8.5/10"
                ),
                EmployeeEntity(
                    name = "Karthik Subramanian",
                    email = "karthik.s@nuevotechone.com",
                    role = "Employee",
                    phone = "+91 99001 12233",
                    department = "Engineering",
                    status = "ACTIVE",
                    skills = "Kotlin, Jetpack Compose, Android Architecture, Coroutines",
                    pfNumber = "TN/MAS/0043819/002/50212",
                    esiNumber = "20-44-883921-002-0404",
                    joiningDate = "2023-01-15",
                    emergencyContact = "Shalini Subramanian (Sister) - +91 99001 12234",
                    experienceYears = 4,
                    performanceRating = "9.0/10"
                ),
                EmployeeEntity(
                    name = "Priya Gopal",
                    email = "priya.g@nuevotechone.com",
                    role = "Employee",
                    phone = "+91 98840 12345",
                    department = "Finance",
                    status = "ACTIVE",
                    skills = "Corporate Taxation, Auditing, Razorpay Integration, Financial Reporting",
                    pfNumber = "KA/BLR/1049281/204/60392",
                    esiNumber = "12-99-887766-001-0505",
                    joiningDate = "2023-07-20",
                    emergencyContact = "Gopal Krishnan (Father) - +91 98840 54321",
                    experienceYears = 3,
                    performanceRating = "8.2/10"
                ),
                EmployeeEntity(
                    name = "Aditya Rawat",
                    email = "aditya.r@nuevotechone.com",
                    role = "Onboarding Intern",
                    phone = "+91 77777 66666",
                    department = "Engineering",
                    status = "ONBOARDING",
                    skills = "Java, Algorithms, SQL",
                    pfNumber = "Pending (Verification)",
                    esiNumber = "Pending (Verification)",
                    joiningDate = "2026-06-15",
                    emergencyContact = "Sunita Rawat (Mother) - +91 77777 66665",
                    experienceYears = 0,
                    performanceRating = "Under Orientation"
                )
            )

            employees.forEach { db.employeeDao.insertEmployee(it) }

            // Fetch auto-generated IDs to cross reference
            val freshEmps = db.employeeDao.getAllEmployees().first()
            val arjunId = freshEmps.find { it.name.startsWith("Arjun") }?.id ?: 1
            val nehaId = freshEmps.find { it.name.startsWith("Neha") }?.id ?: 2
            val vikramId = freshEmps.find { it.name.startsWith("Vikram") }?.id ?: 3
            val karthikId = freshEmps.find { it.name.startsWith("Karthik") }?.id ?: 4
            val priyaId = freshEmps.find { it.name.startsWith("Priya") }?.id ?: 5
            val adityaId = freshEmps.find { it.name.startsWith("Aditya") }?.id ?: 6

            // Seed Attendance
            val attendanceList = listOf(
                AttendanceEntity(
                    employeeId = karthikId,
                    employeeName = "Karthik Subramanian",
                    date = "2026-06-20",
                    clockInTime = "09:12:00",
                    clockOutTime = "18:05:00",
                    latitude = 12.971598,
                    longitude = 77.594566,
                    isGeofenced = true,
                    selfiePath = "/storage/selfies/karthik_20_06.jpg",
                    attendanceType = "GPS",
                    status = "PRESENT"
                ),
                AttendanceEntity(
                    employeeId = arjunId,
                    employeeName = "Arjun Sharma",
                    date = "2026-06-20",
                    clockInTime = "09:30:00",
                    clockOutTime = "19:00:00",
                    latitude = 12.971600,
                    longitude = 77.594590,
                    isGeofenced = true,
                    selfiePath = "/storage/selfies/arjun_20_06.jpg",
                    attendanceType = "Biometric",
                    status = "PRESENT"
                ),
                AttendanceEntity(
                    employeeId = priyaId,
                    employeeName = "Priya Gopal",
                    date = "2026-06-20",
                    clockInTime = "08:55:00",
                    clockOutTime = "17:30:00",
                    latitude = 12.971591,
                    longitude = 77.594510,
                    isGeofenced = true,
                    selfiePath = null,
                    attendanceType = "QR",
                    status = "PRESENT"
                ),
                // Today's entries (Active Clock Ins)
                AttendanceEntity(
                    employeeId = karthikId,
                    employeeName = "Karthik Subramanian",
                    date = "2026-06-21",
                    clockInTime = "09:05:00",
                    clockOutTime = null,
                    latitude = 12.971598,
                    longitude = 77.594566,
                    isGeofenced = true,
                    selfiePath = null,
                    attendanceType = "GPS",
                    status = "PRESENT"
                )
            )
            attendanceList.forEach { db.attendanceDao.insertAttendance(it) }

            // Seed Leaves
            val leaves = listOf(
                LeaveEntity(
                    employeeId = karthikId,
                    employeeName = "Karthik Subramanian",
                    leaveType = "Annual Leave",
                    startDate = "2026-07-10",
                    endDate = "2026-07-15",
                    reason = "Family wedding in Chennai",
                    status = "PENDING"
                ),
                LeaveEntity(
                    employeeId = priyaId,
                    employeeName = "Priya Gopal",
                    leaveType = "Casual Leave",
                    startDate = "2026-06-18",
                    endDate = "2026-06-18",
                    reason = "Personal work at bank",
                    status = "APPROVED"
                )
            )
            leaves.forEach { db.leaveDao.insertLeave(it) }

            // Seed Assets
            val assets = listOf(
                AssetEntity(
                    assetName = "MacBook Pro M3 Max",
                    category = "Laptop",
                    serialNumber = "NTO-LP-40291",
                    assignedEmployeeId = karthikId,
                    assignedEmployeeName = "Karthik Subramanian",
                    warrantyExpiry = "2027-12-01",
                    status = "ASSIGNED"
                ),
                AssetEntity(
                    assetName = "Dell UltraSharp 27 Monitor",
                    category = "Desktop Peripheral",
                    serialNumber = "NTO-DS-88219",
                    assignedEmployeeId = karthikId,
                    assignedEmployeeName = "Karthik Subramanian",
                    warrantyExpiry = "2026-09-15",
                    status = "ASSIGNED"
                ),
                AssetEntity(
                    assetName = "AWS Enterprise Cloud Server Inst",
                    category = "SaaS subscription",
                    serialNumber = "NTO-SaaS-AWS-001",
                    assignedEmployeeId = vikramId,
                    assignedEmployeeName = "Vikram Aditya",
                    warrantyExpiry = "2027-01-01",
                    status = "ASSIGNED"
                ),
                AssetEntity(
                    assetName = "iPad Air 5th Gen (QA Unit)",
                    category = "Mobile",
                    serialNumber = "NTO-MB-10294",
                    assignedEmployeeId = null,
                    assignedEmployeeName = null,
                    warrantyExpiry = "2026-11-20",
                    status = "AVAILABLE"
                )
            )
            assets.forEach { db.assetDao.insertAsset(it) }

            // Seed Payroll
            val payrolls = listOf(
                PayrollEntity(
                    employeeId = karthikId,
                    employeeName = "Karthik Subramanian",
                    payMonth = "May 2026",
                    basicSalary = 80000.0,
                    allowances = 12000.0,
                    pfDeduction = 9600.0, // 12% of basic
                    esiDeduction = 690.0, // 0.75% of basic+allow
                    tdsDeduction = 5000.0,
                    profTaxDeduction = 200.0,
                    netSalary = 76510.0,
                    isApproved = true,
                    paymentStatus = "PAID"
                ),
                PayrollEntity(
                    employeeId = priyaId,
                    employeeName = "Priya Gopal",
                    payMonth = "May 2026",
                    basicSalary = 70000.0,
                    allowances = 8000.0,
                    pfDeduction = 8400.0,
                    esiDeduction = 585.0,
                    tdsDeduction = 3500.0,
                    profTaxDeduction = 200.0,
                    netSalary = 65315.0,
                    isApproved = true,
                    paymentStatus = "PAID"
                ),
                // Payroll for current active cycle (June 2026) -> Pending Approval
                PayrollEntity(
                    employeeId = karthikId,
                    employeeName = "Karthik Subramanian",
                    payMonth = "June 2026",
                    basicSalary = 80000.0,
                    allowances = 15000.0, // Extra compensation
                    pfDeduction = 9600.0,
                    esiDeduction = 712.5,
                    tdsDeduction = 5500.0,
                    profTaxDeduction = 200.0,
                    netSalary = 79187.5,
                    isApproved = false,
                    paymentStatus = "PENDING"
                )
            )
            payrolls.forEach { db.payrollDao.insertPayroll(it) }

            // Seed Support Tickets
            val tickets = listOf(
                TicketEntity(
                    creatorEmployeeId = karthikId,
                    creatorName = "Karthik Subramanian",
                    title = "Xcode licensing error on assigned MacBook",
                    description = "Need enterprise Apple developer portal certification permissions loaded.",
                    category = "IT support",
                    priority = "MEDIUM",
                    status = "OPEN",
                    createdAt = "2026-06-19 14:22:00"
                ),
                TicketEntity(
                    creatorEmployeeId = priyaId,
                    creatorName = "Priya Gopal",
                    title = "PF Contribution Misalignment In May Payslip",
                    description = "My Provident Fund deduction has a difference of INR 400 according to HR handbook rules. Kindly inspect.",
                    category = "HR support",
                    priority = "HIGH",
                    status = "IN_PROGRESS",
                    createdAt = "2026-06-21 10:00:00"
                )
            )
            tickets.forEach { db.ticketDao.insertTicket(it) }

            // Seed Announcements
            val announcements = listOf(
                AnnouncementEntity(
                    title = "Nuevo Tech One Annual Townhall 2026",
                    content = "Dear Employees, our annual townhall will be held virtually on July 1st at 3:00 PM IST. We will present our yearly accomplishments and share our expansion strategy into European markets. Attendance is mandatory.",
                    author = "Arjun Sharma (CEO)",
                    timestamp = "2026-06-18 10:00:00"
                ),
                AnnouncementEntity(
                    title = "Policy Refresher - Hybrid Attendance Strategy",
                    content = "Please note that biometric and QR/GPS entries must be keyed in before 9:30 AM to mark a Full Day footprint. In case of field client visits, use GPS Geofencing with Selfie upload to request instant approval from Managers.",
                    author = "Neha Deshmukh (HR Head)",
                    timestamp = "2026-06-20 09:00:00"
                )
            )
            announcements.forEach { db.announcementDao.insertAnnouncement(it) }
        }
    }

    // Wrap Database DAO helper functions
    suspend fun insertEmployee(employee: EmployeeEntity) = db.employeeDao.insertEmployee(employee)
    suspend fun updateEmployee(employee: EmployeeEntity) = db.employeeDao.updateEmployee(employee)
    suspend fun deleteEmployee(employee: EmployeeEntity) = db.employeeDao.deleteEmployee(employee)

    suspend fun insertAttendance(attendance: AttendanceEntity) = db.attendanceDao.insertAttendance(attendance)
    suspend fun updateAttendance(attendance: AttendanceEntity) = db.attendanceDao.updateAttendance(attendance)
    suspend fun getTodayAttendanceForEmployee(employeeId: Int, date: String): AttendanceEntity? =
        db.attendanceDao.getTodayAttendanceForEmployee(employeeId, date)

    suspend fun insertLeave(leave: LeaveEntity) = db.leaveDao.insertLeave(leave)
    suspend fun updateLeave(leave: LeaveEntity) = db.leaveDao.updateLeave(leave)

    suspend fun insertPayroll(payroll: PayrollEntity) = db.payrollDao.insertPayroll(payroll)
    suspend fun updatePayroll(payroll: PayrollEntity) = db.payrollDao.updatePayroll(payroll)

    suspend fun insertAsset(asset: AssetEntity) = db.assetDao.insertAsset(asset)
    suspend fun updateAsset(asset: AssetEntity) = db.assetDao.updateAsset(asset)
    suspend fun deleteAsset(asset: AssetEntity) = db.assetDao.deleteAsset(asset)

    suspend fun insertTicket(ticket: TicketEntity) = db.ticketDao.insertTicket(ticket)
    suspend fun updateTicket(ticket: TicketEntity) = db.ticketDao.updateTicket(ticket)

    suspend fun insertAnnouncement(announcement: AnnouncementEntity) = db.announcementDao.insertAnnouncement(announcement)
}
