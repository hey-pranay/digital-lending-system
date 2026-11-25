
document.addEventListener("DOMContentLoaded", () => {
    loadRepayments();
});

// Fetch all repayment records
async function loadRepayments() {

    const tableBody = document.getElementById("repaymentsTableBody");

    try {

        // First, trigger overdue detection and wait for it to finish.
        const overdueResponse = await fetch(
            "/api/admin/repayments/status/OVERDUE"
        );

        if (!overdueResponse.ok) {
            throw new Error("Overdue API failed: " + overdueResponse.status);
        }

        const overdue = await overdueResponse.json();

        // Now fetch the remaining records.
        const [paidResponse, pendingResponse, loansResponse] =
            await Promise.all([
                fetch("/api/admin/repayments/status/PAID"),
                fetch("/api/admin/repayments/status/PENDING"),
                fetch("/api/admin/loans")
            ]);

        if (!paidResponse.ok || !pendingResponse.ok || !loansResponse.ok) {
            throw new Error("One or more API requests failed.");
        }

        const [paid, pending, loans] = await Promise.all([
            paidResponse.json(),
            pendingResponse.json(),
            loansResponse.json()
        ]);

        const repayments = [...paid, ...pending, ...overdue];

        console.log("Paid:", paid);
        console.log("Pending:", pending);
        console.log("Overdue:", overdue);
        console.log("Combined repayments:", repayments);

        updateRepaymentSummary(repayments);
        updateFinancialSummary(repayments);
        displayRepayments(repayments, loans);

    } catch (error) {

        console.error("Error loading repayments:", error);

        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-table-message">
                    Unable to load repayment records. Please try again.
                </td>
            </tr>
        `;
    }
}


// Update instalment count cards
function updateRepaymentSummary(repayments) {

    document.getElementById("totalRepayments").textContent =
        repayments.length;

    document.getElementById("paidRepayments").textContent =
        repayments.filter(r => r.status === "PAID").length;

    document.getElementById("pendingRepayments").textContent =
        repayments.filter(r => r.status === "PENDING").length;

    document.getElementById("overdueRepayments").textContent =
        repayments.filter(r => r.status === "OVERDUE").length;
}

// Update financial summary cards
function updateFinancialSummary(repayments) {

    const totalExpected = repayments.reduce(
        (sum, r) => sum + Number(r.amount), 0
    );

    const totalCollected = repayments
        .filter(r => r.status === "PAID")
        .reduce((sum, r) => sum + Number(r.amount), 0);

    const pendingAmount = repayments
        .filter(r => r.status === "PENDING")
        .reduce((sum, r) => sum + Number(r.amount), 0);

    const overdueAmount = repayments
        .filter(r => r.status === "OVERDUE")
        .reduce((sum, r) => sum + Number(r.amount), 0);

    document.getElementById("totalExpectedAmount").textContent =
        formatCurrency(totalExpected);

    document.getElementById("totalCollectedAmount").textContent =
        formatCurrency(totalCollected);

    document.getElementById("pendingCollection").textContent =
        formatCurrency(pendingAmount);

    document.getElementById("overdueAmount").textContent =
        formatCurrency(overdueAmount);
}

// Display repayment records
function displayRepayments(repayments, loans) {

    const tableBody = document.getElementById("repaymentsTableBody");

    tableBody.innerHTML = "";

    if (repayments.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-table-message">
                    No repayment records found.
                </td>
            </tr>
        `;
        return;
    }

    // Create a lookup between Loan ID and Borrower Name
    const borrowerMap = {};

    loans.forEach(loan => {
        borrowerMap[loan.loanId] = loan.borrowerName;
    });

    repayments.forEach((repayment, index) => {

        const borrowerName =
            borrowerMap[repayment.loanId] || "Unknown";

        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${index + 1}</td>
            <td>${borrowerName}</td>
            <td>${formatDate(repayment.dueDate)}</td>
            <td>${formatCurrency(repayment.amount)}</td>
            <td>${formatDate(repayment.paymentDate)}</td>
            <td>
                <span class="loan-status status-${repayment.status.toLowerCase()}">
                    ${repayment.status}
                </span>
            </td>
        `;

        tableBody.appendChild(row);
    });
}

// Format currency in Indian Rupees
function formatCurrency(amount) {

    return Number(amount).toLocaleString("en-IN", {
        style: "currency",
        currency: "INR",
        minimumFractionDigits: 2
    });
}

// Format dates
function formatDate(date) {

    if (!date) {
        return "-";
    }

    const [year, month, day] = date.split("-");

    return `${day}-${month}-${year}`;
}