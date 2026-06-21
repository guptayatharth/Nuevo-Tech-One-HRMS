package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.viewmodel.HrViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HrmsApp(viewModel: HrViewModel) {
    val employees by viewModel.employees.collectAsStateWithLifecycle()
    val attendanceList by viewModel.attendance.collectAsStateWithLifecycle()
    val leavesList by viewModel.leaves.collectAsStateWithLifecycle()
    val payrollsList by viewModel.payrolls.collectAsStateWithLifecycle()
    val assetsList by viewModel.assets.collectAsStateWithLifecycle()
    val ticketsList by viewModel.tickets.collectAsStateWithLifecycle()
    val announcementsList by viewModel.announcements.collectAsStateWithLifecycle()

    val currentId by viewModel.currentEmployeeId.collectAsStateWithLifecycle()
    val currentEmp by viewModel.currentEmployee.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(0) }
    var selectedViewEmpId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageIcon = Icons.Default.Business,
                            contentDescription = "Nuevo Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                text = "Nuevo Tech One",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Enterprise HRMS v2.6",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                },
                actions = {
                    // Quick User Sim Switcher
                    var expandSim by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = { expandSim = true },
                        modifier = Modifier.testTag("user_sim_switcher")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Simulate Employee Switcher",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    DropdownMenu(
                        expanded = expandSim,
                        onDismissRequest = { expandSim = false }
                    ) {
                        Text(
                            text = "Swtich Persona (Simulate App Experience):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                        employees.forEach { emp ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("${emp.name} [ID: ${emp.id}]", fontWeight = FontWeight.Bold)
                                        Text(emp.role, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                },
                                onClick = {
                                    viewModel.switchUserContext(emp.id)
                                    selectedViewEmpId = null
                                    expandSim = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars,
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Ecosystem", maxLines = 1, fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.People, contentDescription = "Onboarding") },
                    label = { Text("Talent", maxLines = 1, fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Default.Fingerprint, contentDescription = "Attendance") },
                    label = { Text("Clock", maxLines = 1, fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Default.Payments, contentDescription = "HR Portal") },
                    label = { Text("Finance", maxLines = 1, fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 4,
                    onClick = { activeTab = 4 },
                    icon = { Icon(Icons.Default.Computer, contentDescription = "Assets") },
                    label = { Text("Assets", maxLines = 1, fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 5,
                    onClick = { activeTab = 5 },
                    icon = { Icon(Icons.Default.Psychology, contentDescription = "AI Core") },
                    label = { Text("AI Core", maxLines = 1, fontSize = 11.sp) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Current User Quick Context Ban
            currentEmp?.let { emp ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = emp.name.chunked(1).firstOrNull()?.uppercase() ?: "U",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Logged in: ${emp.name}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Badge(containerColor = MaterialTheme.colorScheme.secondary) {
                                    Text(
                                        text = emp.role,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "| Dept : ${emp.department}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.switchUserContext(4) }) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Reset sim context",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Tabs Router
            when (activeTab) {
                0 -> MainEcosystemView(
                    viewModel = viewModel,
                    employees = employees,
                    assets = assetsList,
                    announcements = announcementsList,
                    tickets = ticketsList,
                    currentEmp = currentEmp
                )
                1 -> EmployeeDirectoryView(
                    viewModel = viewModel,
                    employees = employees,
                    selectedEmpId = selectedViewEmpId,
                    onSelectEmp = { selectedViewEmpId = it },
                    currentEmp = currentEmp
                )
                2 -> AttendanceClockingView(
                    viewModel = viewModel,
                    attendanceList = attendanceList,
                    currentEmp = currentEmp
                )
                3 -> PayrollFinanceView(
                    viewModel = viewModel,
                    payrolls = payrollsList,
                    leaves = leavesList,
                    employees = employees,
                    currentEmp = currentEmp
                )
                4 -> AssetSaaSManagerView(
                    viewModel = viewModel,
                    assets = assetsList,
                    employees = employees,
                    currentEmp = currentEmp
                )
                5 -> AiCoreView(
                    viewModel = viewModel
                )
            }
        }
    }
}

// Wrap Image helper since we don't hold custom resources
@Composable
fun Icon(imageIcon: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String, tint: Color, modifier: Modifier = Modifier) {
    Icon(imageVector = imageIcon, contentDescription = contentDescription, tint = tint, modifier = modifier)
}

// ==========================================
// Tab 1: Ecosystem Dashboard & Ticketing
// ==========================================
@Composable
fun MainEcosystemView(
    viewModel: HrViewModel,
    employees: List<EmployeeEntity>,
    assets: List<AssetEntity>,
    announcements: List<AnnouncementEntity>,
    tickets: List<TicketEntity>,
    currentEmp: EmployeeEntity?
) {
    var searchQuery by remember { mutableStateOf("") }
    val showSearchResults = searchQuery.isNotEmpty()
    val searchResults by viewModel.policySearchResult.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "HRMS Dashboard Analytics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            // Enterprise metrics layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Active workforce", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${employees.size} active",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Scalable to 10k+", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("IT SaaS Inventory", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${assets.size} assets",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Warranty verified", fontSize = 10.sp, color = Color(0xFF10B981))
                    }
                }
            }
        }

        // AI Policy Search Bar (Direct Rest)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "AI Policy Search Ask Assistant",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Instant search into hybrid work conditions, India compliance formulas, PF accruals, or expense policies.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("policy_search_input"),
                        label = { Text("Ask policies (e.g. 'tell me about hybrid rules')") },
                        trailingIcon = {
                            if (isAiLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                IconButton(
                                    onClick = { viewModel.performPolicySearch(searchQuery) },
                                    enabled = searchQuery.isNotEmpty()
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "Query Gemini")
                                }
                            }
                        }
                    )
                    if (showSearchResults) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Gemini AI Policy insights:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "Clear",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.clickable {
                                    searchQuery = ""
                                    viewModel.clearAiStates()
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                        ) {
                            Text(
                                text = if (searchResults.isEmpty()) "Click send to query AI Policy..." else searchResults,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Board Bulletins / Announcements
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Broadcasts & Bulletins",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                // If HR or Owner, offer adding bulletin
                val hasBroadcastPower = currentEmp != null && (
                        currentEmp.role.lowercase().contains("admin") ||
                        currentEmp.role.lowercase().contains("owner")
                )
                if (hasBroadcastPower) {
                    var showAddBulletin by remember { mutableStateOf(false) }
                    Text(
                        text = "+ Broadcast",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { showAddBulletin = !showAddBulletin }
                    )
                    if (showAddBulletin) {
                        var titleText by remember { mutableStateOf("") }
                        var contentText by remember { mutableStateOf("") }
                        AlertDialog(
                            onDismissRequest = { showAddBulletin = false },
                            title = { Text("Publish Company-Wide Broadcast") },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = titleText,
                                        onValueChange = { titleText = it },
                                        label = { Text("Title") }
                                    )
                                    OutlinedTextField(
                                        value = contentText,
                                        onValueChange = { contentText = it },
                                        label = { Text("Details / Bulletins") }
                                    )
                                }
                            },
                            confirmButton = {
                                Button(onClick = {
                                    if (titleText.isNotEmpty() && contentText.isNotEmpty()) {
                                        viewModel.postAnnouncement(titleText, contentText)
                                        showAddBulletin = false
                                    }
                                }) { Text("Publish") }
                            }
                        )
                    }
                }
            }
        }

        if (announcements.isEmpty()) {
            item {
                Text("No announcements posted yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(announcements) { ann ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(ann.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(ann.timestamp.split(" ").firstOrNull() ?: "", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(ann.content, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Author: ${ann.author}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // HR/IT Helpdesk Ticketing System
        item {
            Text(
                text = "Helpdesk Support Centre",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        item {
            var showAddTicket by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Raise IT/HR support ticket", fontWeight = FontWeight.Bold)
                        Button(
                            onClick = { showAddTicket = true },
                            modifier = Modifier.testTag("raise_ticket_btn")
                        ) {
                            Text("Raise Ticket")
                        }
                    }

                    if (showAddTicket) {
                        var ticketTitle by remember { mutableStateOf("") }
                        var ticketDesc by remember { mutableStateOf("") }
                        var ticketCat by remember { mutableStateOf("IT support") }
                        var ticketPriority by remember { mutableStateOf("MEDIUM") }

                        AlertDialog(
                            onDismissRequest = { showAddTicket = false },
                            title = { Text("Submit New Support Ticket") },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = ticketTitle,
                                        onValueChange = { ticketTitle = it },
                                        label = { Text("Issue Title") }
                                    )
                                    OutlinedTextField(
                                        value = ticketDesc,
                                        onValueChange = { ticketDesc = it },
                                        label = { Text("Details & Diagnostics") }
                                    )
                                    Text("Category:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilterChip(
                                            selected = ticketCat == "IT support",
                                            onClick = { ticketCat = "IT support" },
                                            label = { Text("IT Tech") }
                                        )
                                        FilterChip(
                                            selected = ticketCat == "HR support",
                                            onClick = { ticketCat = "HR support" },
                                            label = { Text("HR Payroll") }
                                        )
                                    }
                                    Text("Priority:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf("LOW", "MEDIUM", "HIGH").forEach { p ->
                                            FilterChip(
                                                selected = ticketPriority == p,
                                                onClick = { ticketPriority = p },
                                                label = { Text(p) }
                                            )
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Button(onClick = {
                                    if (ticketTitle.isNotEmpty() && ticketDesc.isNotEmpty()) {
                                        viewModel.submitSupportTicket(ticketTitle, ticketDesc, ticketCat, ticketPriority)
                                        showAddTicket = false
                                    }
                                }) { Text("Submit Ticket") }
                            }
                        )
                    }
                }
            }
        }

        val relevantTickets = if (currentEmp != null && (
                    currentEmp.role.lowercase().contains("admin") ||
                    currentEmp.role.lowercase().contains("owner")
            )) {
            tickets // Admins see everything
        } else {
            tickets.filter { it.creatorEmployeeId == (currentEmp?.id ?: 0) } // Standard users see their own
        }

        if (relevantTickets.isEmpty()) {
            item {
                Text("No support tickets raised yet.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(relevantTickets) { tk ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(tk.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Badge(
                                containerColor = when (tk.priority) {
                                    "LOW" -> Color.Gray
                                    "MEDIUM" -> Color(0xFFF2994A)
                                    "HIGH" -> Color.Red
                                    else -> Color.DarkGray
                                }
                            ) {
                                Text(tk.priority, color = Color.White, fontSize = 9.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(tk.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Raised by: ${tk.creatorName} [${tk.category}]", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            
                            // Status Badge
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Badge(
                                    containerColor = when (tk.status) {
                                        "OPEN" -> Color(0xFFF2994A)
                                        "IN_PROGRESS" -> Color(0xFF3B82F6)
                                        "RESOLVED" -> Color(0xFF10B981)
                                        else -> Color.Gray
                                    }
                                ) {
                                    Text(tk.status, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp))
                                }

                                val isITAdmin = currentEmp != null && currentEmp.role.lowercase().contains("it")
                                val isHRAdmin = currentEmp != null && currentEmp.role.lowercase().contains("hr")
                                val isSuper = currentEmp != null && currentEmp.role.lowercase().contains("super")

                                if (tk.status != "RESOLVED" && (isITAdmin || isHRAdmin || isSuper)) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Resolve",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable { viewModel.updateTicketStatus(tk.id, "RESOLVED") }
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// Tab 2: Directory, Onboarding & Offboarding
// ==========================================
@Composable
fun EmployeeDirectoryView(
    viewModel: HrViewModel,
    employees: List<EmployeeEntity>,
    selectedEmpId: Int?,
    onSelectEmp: (Int?) -> Unit,
    currentEmp: EmployeeEntity?
) {
    var queryText by remember { mutableStateOf("") }
    var filterDept by remember { mutableStateOf("All") }
    var showAddEmployee by remember { mutableStateOf(false) }

    val filteredList = employees.filter {
        (filterDept == "All" || it.department == filterDept) &&
                (it.name.contains(queryText, ignoreCase = true) || it.role.contains(queryText, ignoreCase = true))
    }

    if (selectedEmpId != null) {
        val emp = employees.find { it.id == selectedEmpId }
        if (emp != null) {
            EmployeeProfileDetailView(
                viewModel = viewModel,
                emp = emp,
                currentEmp = currentEmp,
                onBack = { onSelectEmp(null) }
            )
        } else {
            onSelectEmp(null)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enterprise Team Directory",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    val canAdd = currentEmp != null && (
                            currentEmp.role.lowercase().contains("admin") ||
                            currentEmp.role.lowercase().contains("owner")
                    )
                    if (canAdd) {
                        Button(
                            onClick = { showAddEmployee = true },
                            modifier = Modifier.testTag("add_employee_toggle")
                        ) {
                            Text("+ Add Employee")
                        }
                    }
                }
            }

            if (showAddEmployee) {
                item {
                    AddEmployeeStructure(
                        viewModel = viewModel,
                        onCancel = { showAddEmployee = false }
                    )
                }
            }

            item {
                // Seek Bar Filter
                OutlinedTextField(
                    value = queryText,
                    onValueChange = { queryText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("employee_search_input"),
                    label = { Text("Search by name, role or skills...") },
                    leadingIcon = { Icon(Icons.Default.Search, "Search") }
                )
            }

            // Department filter chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All", "Engineering", "Finance", "Human Resources", "Information Technology", "Management").forEach { dp ->
                        FilterChip(
                            selected = filterDept == dp,
                            onClick = { filterDept = dp },
                            label = { Text(dp, fontSize = 11.sp) }
                        )
                    }
                }
            }

            if (filteredList.isEmpty()) {
                item {
                    Text("No records matched.", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(30.dp))
                }
            } else {
                items(filteredList) { emp ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectEmp(emp.id) }
                            .testTag("employee_card_${emp.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar block
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(
                                        if (emp.status == "ONBOARDING") Color(0xFFF2994A) else MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emp.name.take(1).uppercase(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(emp.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(emp.role, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Dept: ${emp.department} • Exp: ${emp.experienceYears}y", fontSize = 11.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                Badge(
                                    containerColor = when (emp.status) {
                                        "ACTIVE" -> Color(0xFF10B981)
                                        "ONBOARDING" -> Color(0xFFF2994A)
                                        "PIP" -> Color.Red
                                        else -> Color.LightGray
                                    }
                                ) {
                                    Text(emp.status, color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(2.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(Icons.Default.ArrowForwardIos, "Detail", tint = Color.LightGray, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeeProfileDetailView(
    viewModel: HrViewModel,
    emp: EmployeeEntity,
    currentEmp: EmployeeEntity?,
    onBack: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val isAdmin = currentEmp != null && (
        currentEmp.role.lowercase().contains("admin") ||
        currentEmp.role.lowercase().contains("owner") ||
        currentEmp.role.lowercase().contains("manager")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Detailed Employee Profile", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }

                if (isAdmin) {
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.testTag("shortcut_edit_btn")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile Quick Link", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(emp.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(emp.role, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Badge(
                            containerColor = when (emp.status) {
                                "ACTIVE" -> Color(0xFF10B981)
                                "ONBOARDING" -> Color(0xFFF2994A)
                                "PIP" -> Color.Red
                                else -> Color.LightGray
                            }
                        ) {
                            Text(emp.status, color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(4.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rating: ${emp.performanceRating}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Text("Lifecycle & Corporate Details", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Email Address: ", fontWeight = FontWeight.Bold)
                        Text(emp.email)
                    }
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Contact Number: ", fontWeight = FontWeight.Bold)
                        Text(emp.phone)
                    }
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Department: ", fontWeight = FontWeight.Bold)
                        Text(emp.department)
                    }
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Joining Date: ", fontWeight = FontWeight.Bold)
                        Text(emp.joiningDate)
                    }
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Experience Years: ", fontWeight = FontWeight.Bold)
                        Text("${emp.experienceYears} Years")
                    }
                }
            }
        }

        item {
            Text("India statutory Compliance Identifiers", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("EPF Number (EPFO Delhi): ", fontWeight = FontWeight.Bold)
                        Text(emp.pfNumber, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    }
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ESIC Registration ID: ", fontWeight = FontWeight.Bold)
                        Text(emp.esiNumber, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Text("Skills Matrix", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    val skillParts = emp.skills.split(",")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        skillParts.forEach { skill ->
                            Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                Text(skill.trim(), color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 11.sp, modifier = Modifier.padding(4.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Text("Emergency contact", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(emp.emergencyContact, fontSize = 12.sp)
                }
            }
        }

        if (isAdmin) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Admin Badge",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🛡️ Administrative Actions",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "As an HR Administrator or Manager, you possess authorization to modify compliance IDs (EPF/ESIC), experience matrices, performance tags, contact details, or permanently offboard this candidate from the local system.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showEditDialog = true },
                                modifier = Modifier.weight(1f).testTag("admin_edit_profile_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Profile", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit Profile", maxLines = 1)
                            }
                            Button(
                                onClick = { showDeleteConfirm = true },
                                modifier = Modifier.weight(1f).testTag("admin_delete_profile_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Profile", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Delete User", maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        EditEmployeeDialog(
            emp = emp,
            onDismiss = { showEditDialog = false },
            onConfirm = { updated ->
                viewModel.updateEmployee(updated)
                showEditDialog = false
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = {
                Text(
                    text = "Confirm Employee Purge",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text("Are you sure you want to permanently delete ${emp.name}? This will erase their active credentials, metadata, experience parameters, and India compliance logs. This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEmployee(emp)
                        showDeleteConfirm = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("admin_delete_confirm_btn")
                ) {
                    Text("Offboard & Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Keep Active")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmployeeDialog(
    emp: EmployeeEntity,
    onDismiss: () -> Unit,
    onConfirm: (EmployeeEntity) -> Unit
) {
    var name by remember { mutableStateOf(emp.name) }
    var email by remember { mutableStateOf(emp.email) }
    var phone by remember { mutableStateOf(emp.phone) }
    var role by remember { mutableStateOf(emp.role) }
    var department by remember { mutableStateOf(emp.department) }
    var status by remember { mutableStateOf(emp.status) }
    var skills by remember { mutableStateOf(emp.skills) }
    var pfNumber by remember { mutableStateOf(emp.pfNumber) }
    var esiNumber by remember { mutableStateOf(emp.esiNumber) }
    var experienceYears by remember { mutableStateOf(emp.experienceYears.toString()) }
    var performanceRating by remember { mutableStateOf(emp.performanceRating) }
    var emergencyContact by remember { mutableStateOf(emp.emergencyContact) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Employee Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Professional Details",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_emp_name")
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Designated Role") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_emp_role")
                )
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Status selection
                var showStatusDropdown by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        label = { Text("Status") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showStatusDropdown = true },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showStatusDropdown = true }) {
                                Icon(Icons.Default.ArrowDropDown, "Select Status")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = showStatusDropdown,
                        onDismissRequest = { showStatusDropdown = false }
                    ) {
                        listOf("ACTIVE", "ONBOARDING", "PIP", "OFFBOARDED").forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    status = item
                                    showStatusDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = experienceYears,
                    onValueChange = { experienceYears = it },
                    label = { Text("Experience (Years)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = performanceRating,
                    onValueChange = { performanceRating = it },
                    label = { Text("Performance Rating") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))
                Divider()
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Contact & Compliance",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Work Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = skills,
                    onValueChange = { skills = it },
                    label = { Text("Skills Matrix (Comma-Separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pfNumber,
                    onValueChange = { pfNumber = it },
                    label = { Text("EPF Number (Compliance)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = esiNumber,
                    onValueChange = { esiNumber = it },
                    label = { Text("ESIC Number (Registration)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    label = { Text("Emergency Contact") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val updated = emp.copy(
                            name = name,
                            email = email,
                            phone = phone,
                            role = role,
                            department = department,
                            status = status,
                            skills = skills,
                            pfNumber = pfNumber,
                            esiNumber = esiNumber,
                            experienceYears = experienceYears.toIntOrNull() ?: emp.experienceYears,
                            performanceRating = performanceRating,
                            emergencyContact = emergencyContact
                        )
                        onConfirm(updated)
                    }
                },
                modifier = Modifier.testTag("edit_emp_save_btn")
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddEmployeeStructure(viewModel: HrViewModel, onCancel: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Employee") }
    var dept by remember { mutableStateOf("Engineering") }
    var experienceStr by remember { mutableStateOf("") }
    var salaryStr by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Onboard New Employee Record", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth().testTag("add_emp_name")
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Company Email") },
                modifier = Modifier.fillMaxWidth().testTag("add_emp_email")
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Contact Number") },
                modifier = Modifier.fillMaxWidth().testTag("add_emp_phone")
            )
            OutlinedTextField(
                value = role,
                onValueChange = { role = it },
                label = { Text("Role Designation") },
                modifier = Modifier.fillMaxWidth().testTag("add_emp_role")
            )
            OutlinedTextField(
                value = dept,
                onValueChange = { dept = it },
                label = { Text("Department") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = experienceStr,
                onValueChange = { experienceStr = it },
                label = { Text("Years of Experience") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = salaryStr,
                onValueChange = { salaryStr = it },
                label = { Text("Basic Salary (Monthly INR)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("add_emp_salary")
            )
            OutlinedTextField(
                value = skills,
                onValueChange = { skills = it },
                label = { Text("Skills Matrix (Comma Separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                TextButton(onClick = onCancel) { Text("Cancel") }
                Button(
                    onClick = {
                        val exp = experienceStr.toIntOrNull() ?: 0
                        val sal = salaryStr.toDoubleOrNull() ?: 50000.0
                        if (name.isNotEmpty() && email.isNotEmpty()) {
                            viewModel.createEmployee(
                                name, email, phone, role, dept, "2026-06-21", skills, exp, sal
                            )
                            onCancel()
                        }
                    },
                    modifier = Modifier.testTag("add_emp_save_btn")
                ) { Text("Onboard Employee") }
            }
        }
    }
}

// ==========================================
// Tab 3: Attendance, WFH Geo clocking
// ==========================================
@Composable
fun AttendanceClockingView(
    viewModel: HrViewModel,
    attendanceList: List<AttendanceEntity>,
    currentEmp: EmployeeEntity?
) {
    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd").format(Date()) }
    val todayRecord = attendanceList.find { it.employeeId == (currentEmp?.id ?: 0) && it.date == todayDate }
    
    // Simulate current device location (slightly moving inside geofence)
    var testLat by remember { mutableStateOf(12.971598) }
    var testLng by remember { mutableStateOf(77.594566) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "GPS Real-Time Attendance Tracker",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Live Geofence Mapping Simulator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("HQ Geofence Radar Simulator", fontWeight = FontWeight.Bold)
                    Text("Corporate HQ Coordinates: Lat ${viewModel.hqLatitude}, Lng ${viewModel.hqLongitude}", fontSize = 11.sp, color = Color.Gray)
                    Text("Lock Geofence radius: 200m Limit", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Coordinates editor to simulate walking away out of boundary
                    Text("Your Simulating Coordinates:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                // Inside HQ
                                testLat = 12.971598
                                testLng = 77.594566
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (testLat == 12.971598) MaterialTheme.colorScheme.primary else Color.Gray
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Inside HQ (0m)", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                // Outside (About 1.2km away)
                                testLat = 12.981598
                                testLng = 77.604566
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (testLat == 12.981598) MaterialTheme.colorScheme.primary else Color.Gray
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Outside HQ (1.4km)", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Clock In Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (todayRecord == null) {
                        Text("You haven't clocked in today yet.", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.performClockIn(testLat, testLng, "GPS", "/storage/selfie_${System.currentTimeMillis()}.jpg")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("clock_in_gps_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MyLocation, "In")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clock In (GPS)", fontSize = 12.sp)
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.performClockIn(testLat, testLng, "WFH", null)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("clock_in_wfh_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.HomeWork, "WFH")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Work From Home", fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        Text("Today's Shift: ACTIVE • Date: ${todayRecord.date}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Clocked InAt: ${todayRecord.clockInTime} • Auth: ${todayRecord.attendanceType}", fontSize = 13.sp)
                        if (todayRecord.clockOutTime != null) {
                            Text("Clocked OutAt: ${todayRecord.clockOutTime}", fontSize = 13.sp, color = Color.Gray)
                        } else {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.performClockOut() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.testTag("clock_out_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ExitToApp, "Clock out")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clock Out")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Submitting Attendance Correction
        if (todayRecord != null && todayRecord.status == "CORRECTION_PENDING") {
            item {
                var correctionReasonText by remember { mutableStateOf("") }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Color.Red),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Geofence Flagged: You registered Outside HQ!", fontWeight = FontWeight.Bold, color = Color.Red)
                        Text("Your punch was outside the 200m zone. To prevent salary deductions, submit a correction verification request.", fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = correctionReasonText,
                            onValueChange = { correctionReasonText = it },
                            label = { Text("Reason (e.g., 'Met client in Connaught Place')") },
                            modifier = Modifier.fillMaxWidth().testTag("correction_reason_input")
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = {
                                if (correctionReasonText.isNotEmpty()) {
                                    viewModel.requestAttendanceCorrection(todayRecord.id, correctionReasonText)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2994A))
                        ) {
                            Text("Request Manager Approval")
                        }
                    }
                }
            }
        }

        // Historic Footprints (Role Based)
        item {
            Text(
                text = "Ecosystem Daily Clocking Logs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        val itemsToDis = if (currentEmp != null && (
                    currentEmp.role.lowercase().contains("admin") ||
                    currentEmp.role.lowercase().contains("owner")
            )) {
            attendanceList
        } else {
            attendanceList.filter { it.employeeId == (currentEmp?.id ?: 0) }
        }

        if (itemsToDis.isEmpty()) {
            item {
                Text("No fingerprints logs.", fontSize = 11.sp, color = Color.Gray)
            }
        } else {
            items(itemsToDis) { roll ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(roll.employeeName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Date: ${roll.date} | ${roll.attendanceType}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Badge(
                                    containerColor = when (roll.status) {
                                        "PRESENT" -> Color(0xFF10B981)
                                        "CORRECTION_PENDING" -> Color(0xFFF2994A)
                                        else -> Color.Red
                                    }
                                ) {
                                    Text(roll.status, fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(2.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("In: ${roll.clockInTime} • Out: ${roll.clockOutTime ?: "Active"}", fontSize = 11.sp)
                            Text("Geofenced: ${if (roll.isGeofenced) "YES (HQ)" else "NO"}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (roll.correctionReason != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Reason: ${roll.correctionReason}", fontSize = 11.sp, color = Color(0xFFF2994A))
                            
                            // Let Head Approve Correction
                            val isHR = currentEmp != null && (
                                    currentEmp.role.lowercase().contains("admin") ||
                                    currentEmp.role.lowercase().contains("owner")
                            )
                            if (isHR && roll.status == "CORRECTION_PENDING") {
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { viewModel.approveAttendanceCorrection(roll.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    Text("Accept & Approve Attendance", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// Tab 4: Leaves, Payroll & Compliance
// ==========================================
@Composable
fun PayrollFinanceView(
    viewModel: HrViewModel,
    payrolls: List<PayrollEntity>,
    leaves: List<LeaveEntity>,
    employees: List<EmployeeEntity>,
    currentEmp: EmployeeEntity?
) {
    var leaveType by remember { mutableStateOf("Annual Leave") }
    var startDate by remember { mutableStateOf("2026-07-01") }
    var endDate by remember { mutableStateOf("2026-07-03") }
    var reasonText by remember { mutableStateOf("") }
    
    var selectedTabSub by remember { mutableStateOf(0) } // 0 = Payslip & India Compliance, 1 = Leave Request

    val dec = DecimalFormat("##,##,##0.00")
    val isAdmin = currentEmp != null && (
            currentEmp.role.lowercase().contains("admin") ||
            currentEmp.role.lowercase().contains("owner") ||
            currentEmp.role.lowercase().contains("finance")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Finance, Payroll & Leave Controls",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            TabRow(selectedTabIndex = selectedTabSub) {
                Tab(selected = selectedTabSub == 0, onClick = { selectedTabSub = 0 }) {
                    Text("India Compliance Payslips", modifier = Modifier.padding(12.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedTabSub == 1, onClick = { selectedTabSub = 1 }) {
                    Text("Apply Leave", modifier = Modifier.padding(12.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (selectedTabSub == 0) {
            // Payslips & Compliance variables
            item {
                Text(
                    text = "India Statutory Payroll Slabs",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("Deduction parameters calculated accurately according to statutory codes:", fontSize = 11.sp, color = Color.Gray)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("• Provident Fund (EPF): 12% contribution matching basic salary limit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("• ESIC: 0.75% contribution of Gross (Only for employees grossing <= ₹21,000/mo)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("• Professional Tax (PT): Flat ₹200/mo deduction under Delhi/Maharashtra compliance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("• Surcharges (TDS): Flat estimate calculated against slab rates estimated annually", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Expose a quick Payroll creator slider/tool for Administrate role
            if (isAdmin) {
                item {
                    var selectedEmpPayId by remember { mutableStateOf(1) }
                    var basicRateText by remember { mutableStateOf("60000") }
                    var customBonusText by remember { mutableStateOf("5000") }
                    var otherDeductionText by remember { mutableStateOf("0") }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Admin Tool: Compute Monthly Corporate Run", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            
                            // Selected employee
                            Text("Select Employee for computation:", fontSize = 11.sp)
                            var expandPayEmps by remember { mutableStateOf(false) }
                            val targetedEmployee = employees.find { it.id == selectedEmpPayId }
                            Button(onClick = { expandPayEmps = true }, modifier = Modifier.fillMaxWidth()) {
                                Text(targetedEmployee?.name ?: "Select Target")
                            }
                            DropdownMenu(expanded = expandPayEmps, onDismissRequest = { expandPayEmps = false }) {
                                employees.forEach { ep ->
                                    DropdownMenuItem(
                                        text = { Text("${ep.name} (${ep.role})") },
                                        onClick = {
                                            selectedEmpPayId = ep.id
                                            expandPayEmps = false
                                        }
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = basicRateText,
                                    onValueChange = { basicRateText = it },
                                    label = { Text("Basic (INR)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = customBonusText,
                                    onValueChange = { customBonusText = it },
                                    label = { Text("Incentive (INR)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            OutlinedTextField(
                                value = otherDeductionText,
                                onValueChange = { otherDeductionText = it },
                                label = { Text("Custom Deductions (INR)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    val basic = basicRateText.toDoubleOrNull() ?: 50000.0
                                    val bonuses = customBonusText.toDoubleOrNull() ?: 0.0
                                    val factor = otherDeductionText.toDoubleOrNull() ?: 0.0
                                    if (targetedEmployee != null) {
                                        viewModel.recalculateAndAddCustomPayrollRule(
                                            targetedEmployee.id,
                                            targetedEmployee.name,
                                            basic,
                                            bonuses,
                                            factor
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().testTag("compile_payroll_btn")
                            ) {
                                Text("Approve & Post compliance payroll Structure")
                            }
                        }
                    }
                }
            }

            // Expose appropriate pay records
            val relevantPayList = if (isAdmin) {
                payrolls
            } else {
                payrolls.filter { it.employeeId == (currentEmp?.id ?: 0) }
            }

            if (relevantPayList.isEmpty()) {
                item {
                    Text("No payslips tracked in this cycle.", fontSize = 11.sp, color = Color.Gray)
                }
            } else {
                items(relevantPayList) { pay ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Pay Slip: ${pay.payMonth}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Employee: ${pay.employeeName} [ID: ${pay.employeeId}]", fontSize = 12.sp, color = Color.Gray)
                                }
                                Badge(
                                    containerColor = if (pay.isApproved) Color(0xFF10B981) else Color(0xFFF2994A)
                                ) {
                                    Text(pay.paymentStatus, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(3.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Statutory Compliance Deductions:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            
                            // Grid metrics
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Basic Salary", fontSize = 10.sp, color = Color.Gray)
                                    Text("₹ ${dec.format(pay.basicSalary)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Allowances", fontSize = 10.sp, color = Color.Gray)
                                    Text("₹ ${dec.format(pay.allowances)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("EPF (12%)", fontSize = 10.sp, color = Color.Gray)
                                    Text("-₹ ${dec.format(pay.pfDeduction)}", fontSize = 12.sp, color = Color.Red)
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("ESIC (0.75%)", fontSize = 10.sp, color = Color.Gray)
                                    Text("-₹ ${dec.format(pay.esiDeduction)}", fontSize = 12.sp, color = Color.Red)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("TDS (Tax Slab)", fontSize = 10.sp, color = Color.Gray)
                                    Text("-₹ ${dec.format(pay.tdsDeduction)}", fontSize = 12.sp, color = Color.Red)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Prof Tax (PT)", fontSize = 10.sp, color = Color.Gray)
                                    Text("-₹ ${dec.format(pay.profTaxDeduction)}", fontSize = 12.sp, color = Color.Red)
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("NET DISBURSED PAY:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                Text("₹ ${dec.format(pay.netSalary)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            if (!pay.isApproved && isAdmin) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.approvePayroll(pay.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Approve and Mark payment as Paid")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Apply Leave UI
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Apply for Leave Accruals", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        
                        Text("Select Leave Type:", fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Annual Leave", "Sick Leave", "Casual Leave").forEach { lt ->
                                FilterChip(
                                    selected = leaveType == lt,
                                    onClick = { leaveType = lt },
                                    label = { Text(lt, fontSize = 11.sp) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = { Text("From Date (yyyy-MM-dd)") },
                            modifier = Modifier.fillMaxWidth().testTag("leave_start_date")
                        )
                        OutlinedTextField(
                            value = endDate,
                            onValueChange = { endDate = it },
                            label = { Text("To Date (yyyy-MM-dd)") },
                            modifier = Modifier.fillMaxWidth().testTag("leave_end_date")
                        )
                        OutlinedTextField(
                            value = reasonText,
                            onValueChange = { reasonText = it },
                            label = { Text("Reason for absence") },
                            modifier = Modifier.fillMaxWidth().testTag("leave_reason")
                        )

                        Button(
                            onClick = {
                                if (reasonText.isNotEmpty()) {
                                    viewModel.requestLeave(leaveType, startDate, endDate, reasonText)
                                    reasonText = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("submit_leave_btn")
                        ) {
                            Text("Submit Leave Application")
                        }
                    }
                }
            }

            // Expose leaves
            val relevantLeaves = if (isAdmin) {
                leaves
            } else {
                leaves.filter { it.employeeId == (currentEmp?.id ?: 0) }
            }

            if (relevantLeaves.isEmpty()) {
                item {
                    Text("No leaves currently filed.", fontSize = 11.sp, color = Color.Gray)
                }
            } else {
                items(relevantLeaves) { lv ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("${lv.leaveType} Request", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Duration: ${lv.startDate} to ${lv.endDate}", fontSize = 12.sp)
                                    Text("Employee: ${lv.employeeName}", fontSize = 11.sp, color = Color.Gray)
                                }
                                Badge(
                                    containerColor = when (lv.status) {
                                        "PENDING" -> Color(0xFFF2994A)
                                        "APPROVED" -> Color(0xFF10B981)
                                        else -> Color.Red
                                    }
                                ) {
                                    Text(lv.status, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Reason: ${lv.reason}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            if (lv.status == "PENDING" && isAdmin) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.updateLeaveStatus(lv.id, false) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Deny") }

                                    Button(
                                        onClick = { viewModel.updateLeaveStatus(lv.id, true) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Approve") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// Tab 5: Asset & IT cloud SaaS Manager
// ==========================================
@Composable
fun AssetSaaSManagerView(
    viewModel: HrViewModel,
    assets: List<AssetEntity>,
    employees: List<EmployeeEntity>,
    currentEmp: EmployeeEntity?
) {
    var showAddAsset by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hardware Asset & Subscription Tracker",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                val isAdmin = currentEmp != null && (
                        currentEmp.role.lowercase().contains("admin") ||
                        currentEmp.role.lowercase().contains("owner") ||
                        currentEmp.role.lowercase().contains("it")
                )
                if (isAdmin) {
                    Button(onClick = { showAddAsset = true }) {
                        Text("+ New Asset")
                    }
                }
            }
        }

        if (showAddAsset) {
            item {
                var assetName by remember { mutableStateOf("") }
                var assetCat by remember { mutableStateOf("Laptop") }
                var serialNum by remember { mutableStateOf("") }
                var warrantyStr by remember { mutableStateOf("2027-01-01") }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Add Hardware / Service Subscription", fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = assetName, onValueChange = { assetName = it }, label = { Text("Name (e.g. iPhone 15)") })
                        
                        Text("Category:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Laptop", "Mobile", "SaaS subscription").forEach { sc ->
                                FilterChip(
                                    selected = assetCat == sc,
                                    onClick = { assetCat = sc },
                                    label = { Text(sc, fontSize = 11.sp) }
                                )
                            }
                        }

                        OutlinedTextField(value = serialNum, onValueChange = { serialNum = it }, label = { Text("Serial / Registry Number") })
                        OutlinedTextField(value = warrantyStr, onValueChange = { warrantyStr = it }, label = { Text("Warranty Expiry date") })

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                            TextButton(onClick = { showAddAsset = false }) { Text("Cancel") }
                            Button(onClick = {
                                if (assetName.isNotEmpty() && serialNum.isNotEmpty()) {
                                    viewModel.createAsset(assetName, assetCat, serialNum, warrantyStr)
                                    showAddAsset = false
                                }
                            }) { Text("Register Asset") }
                        }
                    }
                }
            }
        }

        if (assets.isEmpty()) {
            item {
                Text("No assets in registry.")
            }
        } else {
            items(assets) { asset ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageIcon = when (asset.category.lowercase()) {
                                "laptop" -> Icons.Default.Laptop
                                "mobile" -> Icons.Default.Smartphone
                                else -> Icons.Default.CloudQueue
                            },
                            contentDescription = asset.category,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(asset.assetName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("S/N: ${asset.serialNumber} • Cat: ${asset.category}", fontSize = 11.sp, color = Color.Gray)
                            Text("Warranty Until: ${asset.warrantyExpiry}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            
                            if (asset.assignedEmployeeId != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, "Assigned to", tint = Color.Gray, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Assigned: ${asset.assignedEmployeeName}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Badge(
                                containerColor = when (asset.status) {
                                    "AVAILABLE" -> Color(0xFF10B981)
                                    "ASSIGNED" -> Color(0xFF3B82F6)
                                    else -> Color.Gray
                                }
                            ) {
                                Text(asset.status, color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(2.dp))
                            }

                            // Assign/Release Action triggers
                            val isAdmin = currentEmp != null && (
                                    currentEmp.role.lowercase().contains("admin") ||
                                    currentEmp.role.lowercase().contains("owner") ||
                                    currentEmp.role.lowercase().contains("it")
                            )
                            if (isAdmin) {
                                Spacer(modifier = Modifier.height(6.dp))
                                if (asset.assignedEmployeeId == null) {
                                    var showAssignDropper by remember { mutableStateOf(false) }
                                    Text(
                                        text = "Assign",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable { showAssignDropper = true }
                                            .padding(4.dp)
                                    )
                                    DropdownMenu(
                                        expanded = showAssignDropper,
                                        onDismissRequest = { showAssignDropper = false }
                                    ) {
                                        employees.forEach { ep ->
                                            DropdownMenuItem(
                                                text = { Text(ep.name) },
                                                onClick = {
                                                    viewModel.assignAsset(asset.id, ep.id)
                                                    showAssignDropper = false
                                                }
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "Release",
                                        color = Color.Red,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable { viewModel.releaseAsset(asset.id) }
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// Tab 6: AI Core powered by GEMINI
// ==========================================
@Composable
fun AiCoreView(viewModel: HrViewModel) {
    var selectedAiSubTab by remember { mutableStateOf(0) } // 0 = Chatbot assistant, 1 = ATS screen, 2 = Attrition risk

    val aiResponse by viewModel.aiResponse.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val resumeScoreResult by viewModel.resumeScoreResult.collectAsStateWithLifecycle()
    val attritionPredictionResult by viewModel.attritionPredictionResult.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Gemini LLM Active Intel Hub",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            TabRow(selectedTabIndex = selectedAiSubTab) {
                Tab(selected = selectedAiSubTab == 0, onClick = { selectedAiSubTab = 0 }) {
                    Text("HR Chatbot", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedAiSubTab == 1, onClick = { selectedAiSubTab = 1 }) {
                    Text("ATS Resume Fits", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedAiSubTab == 2, onClick = { selectedAiSubTab = 2 }) {
                    Text("People Analytics", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (selectedAiSubTab == 0) {
            // HR executive chat
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Interactive Employee Companion", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Draft exit templates, calculate custom salary PF formulas, or inspect recruitment workflow guidelines.", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        var chatMsg by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = chatMsg,
                            onValueChange = { chatMsg = it },
                            label = { Text("What can I draft or calculate for you today?") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_chat_input"),
                            trailingIcon = {
                                if (isAiLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                } else {
                                    IconButton(
                                        onClick = {
                                            viewModel.performAiChat(chatMsg)
                                            chatMsg = ""
                                        },
                                        enabled = chatMsg.isNotEmpty()
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "Query")
                                    }
                                }
                            }
                        )

                        if (aiResponse.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Assistant Response:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Reset Dialog", color = Color.Red, fontSize = 11.sp, modifier = Modifier.clickable { viewModel.clearAiStates() })
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Text(aiResponse, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        } else if (selectedAiSubTab == 1) {
            // Recruiter ATS resume Parser & Fitting scoring
            item {
                var jobRole by remember { mutableStateOf("Senior Android Developer") }
                var cvRawText by remember { mutableStateOf("Vikesh Mehta\nEducation: Bachelor of Computer Application (BCA), Delhi\nSkills: Kotlin syntax, Android platform core, Java, Git, XML.\nExperience: 3 Years crafting client apps at startup.") }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("AI Candidate Resume Screener (ATS)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Paste candidate resumes directly to trigger AI CV matching algorithms & gap diagnostics.", fontSize = 11.sp, color = Color.Gray)
                        
                        OutlinedTextField(
                            value = jobRole,
                            onValueChange = { jobRole = it },
                            label = { Text("Target Position") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = cvRawText,
                            onValueChange = { cvRawText = it },
                            label = { Text("Resume details / Copy Pasted text") },
                            modifier = Modifier.fillMaxWidth().height(120.dp).testTag("ats_resume_input")
                        )

                        if (isAiLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        } else {
                            Button(
                                onClick = { viewModel.performCandidateScreening(jobRole, cvRawText) },
                                modifier = Modifier.fillMaxWidth().testTag("ats_screen_btn")
                            ) {
                                Text("Fit Test candidate resume")
                            }
                        }

                        if (resumeScoreResult.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Gemini Screening Verdict:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Clear Screener", color = Color.Red, fontSize = 11.sp, modifier = Modifier.clickable { viewModel.clearAiStates() })
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Text(resumeScoreResult, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        } else {
            // Attrition Analytics & Predictive review
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("AI Retention Risk Prediction Model", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Analyze the enterprise local database in real-time. Automatically predicts attrition tendencies based on skills configurations, tenures, and performance score adjustments.", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))

                        if (isAiLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        } else {
                            Button(
                                onClick = { viewModel.performAttritionAnalytic() },
                                modifier = Modifier.fillMaxWidth().testTag("attrition_run_btn")
                            ) {
                                Text("Inspect Attrition Vulnerabilities")
                            }
                        }

                        if (attritionPredictionResult.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("People Predictive Report:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Clear", color = Color.Red, fontSize = 11.sp, modifier = Modifier.clickable { viewModel.clearAiStates() })
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Text(attritionPredictionResult, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
