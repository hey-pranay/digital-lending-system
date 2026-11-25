
document.addEventListener("DOMContentLoaded", () => {

    loadLoans();

    // Handle Generate Schedule button clicks.
    document.getElementById("loansTableBody").addEventListener("click", async (event) => {

        const button = event.target.closest(".generate-schedule-btn");

        if (!button) {
            return;
        }

        const loanId = button.dataset.loanId;

        const confirmed = confirm(
            `Generate repayment schedule for Loan #${loanId}?`
        );

        if (!confirmed) {
            return;
        }

        await generateRepaymentSchedule(loanId, button);
    });

});


// Fetch all loans from backend.
async function loadLoans() {

    const tableBody = document.getElementById("loansTableBody");

    try {

        const response = await fetch("/api/admin/loans");

        if (!response.ok) {
            throw new Error("Failed to load loan records.");
        }

        const loans = await response.json();

        displayLoans(loans);
        updateLoanSummary(loans);

    } catch (error) {

        console.error("Error loading loans:", error);

        tableBody.innerHTML = `
            <tr>
                <td colspan="8" class="empty-table-message">
                    Unable to load loan records. Please try again.
                </td>
            </tr>
        `;
    }
}


// Display loans in table.
async function displayLoans(loans) {

    const tableBody = document.getElementById("loansTableBody");

    tableBody.replaceChildren();

    if (loans.length === 0) {

        tableBody.innerHTML = `
            <tr>
                <td colspan="8" class="empty-table-message">
                    No loan records found.
                </td>
            </tr>
        `;

        return;
    }

    for (const [index, loan] of loans.entries()) {

        const row = document.createElement("tr");

        const actionCell = document.createElement("td");

        if (loan.status === "APPROVED") {

            try {

                // Check whether a repayment schedule already exists.
                const response = await fetch(
                    `/api/admin/repayments/loans/${loan.loanId}`
                );

                if (!response.ok) {
                    throw new Error("Unable to check repayment schedule.");
                }

                const repayments = await response.json();

                if (repayments.length > 0) {

                    actionCell.innerHTML = `
                        <span class="schedule-generated">
                            Schedule Generated
                        </span>
                    `;

                } else {

                    const button = document.createElement("button");

                    button.type = "button";
                    button.className = "generate-schedule-btn";
                    button.textContent = "Generate Schedule";
                    button.dataset.loanId = loan.loanId;

                    actionCell.appendChild(button);
                }

            } catch (error) {

                console.error(
                    `Error checking schedule for loan ${loan.loanId}:`,
                    error
                );

                actionCell.textContent = "Unable to check schedule";
            }

        } else {

            actionCell.textContent = "-";
        }

        row.innerHTML = `
            <td>${index + 1}</td>
            <td>${loan.borrowerName ?? "-"}</td>
            <td>${formatCurrency(loan.loanAmount)}</td>
            <td>${loan.interestRate}%</td>
            <td>${loan.tenureMonths} months</td>
            <td>${formatCurrency(loan.emi)}</td>
            <td>
                <span class="loan-status status-${loan.status.toLowerCase()}">
                    ${loan.status}
                </span>
            </td>
        `;

        row.appendChild(actionCell);
        tableBody.appendChild(row);
    }
}


// Generate repayment schedule.
async function generateRepaymentSchedule(loanId, button) {

    const csrfToken = document.getElementById("csrfToken")?.value;

    if (!csrfToken) {

        alert("CSRF token not found. Please refresh the page.");
        return;
    }

    try {

        button.disabled = true;
        button.textContent = "Generating...";

        const response = await fetch(
            `/api/admin/repayments/loans/${loanId}/generate`,
            {
                method: "POST",
                headers: {
                    "X-CSRF-TOKEN": csrfToken
                }
            }
        );

        const result = await response.json();

        if (!response.ok) {

            throw new Error(
                result.message || "Failed to generate repayment schedule."
            );
        }

        alert(
            `Repayment schedule generated successfully for Loan #${loanId}.`
        );

        // Refresh loans and check the generated schedule.
        await loadLoans();

    } catch (error) {

        console.error("Error generating repayment schedule:", error);

        alert(error.message);

        button.disabled = false;
        button.textContent = "Generate Schedule";
    }
}


// Update loan summary cards.
function updateLoanSummary(loans) {

    document.getElementById("totalLoans").textContent =
        loans.length;

    document.getElementById("approvedLoans").textContent =
        loans.filter(loan => loan.status === "APPROVED").length;

    document.getElementById("pendingLoans").textContent =
        loans.filter(loan =>
            loan.status === "APPLIED" ||
            loan.status === "PENDING"
        ).length;

    document.getElementById("rejectedLoans").textContent =
        loans.filter(loan => loan.status === "REJECTED").length;
}


// Format amounts in Indian currency.
function formatCurrency(amount) {

    return Number(amount).toLocaleString("en-IN", {
        style: "currency",
        currency: "INR",
        minimumFractionDigits: 2
    });
}