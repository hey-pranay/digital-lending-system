
document.addEventListener("DOMContentLoaded", () => {

    const tableBody = document.getElementById("loansTableBody");
    const messageElement = document.getElementById("loanMessage");

    // Format amounts in Indian currency format
    function formatCurrency(amount) {
        return new Intl.NumberFormat("en-IN", {
            style: "currency",
            currency: "INR",
            minimumFractionDigits: 2
        }).format(amount || 0);
    }

    // Get status badge class
    function getStatusClass(status) {

        switch (status.toUpperCase()) {

            case "APPLIED":
            case "PENDING":
                return "status-applied";

            case "APPROVED":
                return "status-approved";

            case "REJECTED":
                return "status-rejected";

            default:
                return "status-default";
        }
    }

    // Create a table cell safely
    function createCell(value, className = "") {

        const cell = document.createElement("td");

        cell.textContent = value ?? "-";

        if (className) {
            cell.className = className;
        }

        return cell;
    }

    // Update summary cards
    function updateSummary(loans) {

        const total = loans.length;

        const approved = loans.filter(
            loan => loan.status?.toUpperCase() === "APPROVED"
        ).length;

        const pending = loans.filter(
            loan => ["APPLIED", "PENDING"].includes(
                loan.status?.toUpperCase()
            )
        ).length;

        const rejected = loans.filter(
            loan => loan.status?.toUpperCase() === "REJECTED"
        ).length;

        document.getElementById("totalLoans").textContent = total;
        document.getElementById("approvedLoans").textContent = approved;
        document.getElementById("pendingLoans").textContent = pending;
        document.getElementById("rejectedLoans").textContent = rejected;
    }

    // Display loan applications
    function displayLoans(loans) {

        tableBody.replaceChildren();

        if (!loans || loans.length === 0) {

            const row = document.createElement("tr");
            const cell = document.createElement("td");

            cell.colSpan = 7;
            cell.className = "empty-table-message";
            cell.textContent = "You have not submitted any loan applications yet.";

            row.appendChild(cell);
            tableBody.appendChild(row);

            return;
        }

        loans.forEach((loan, index) => {

            const row = document.createElement("tr");

            // Serial number starts from 1.
            row.appendChild(createCell(index + 1));

            row.appendChild(createCell(formatCurrency(loan.amount)));

            row.appendChild(createCell(`${loan.interestRate}%`));

            row.appendChild(createCell(`${loan.tenureMonths} Months`));

            row.appendChild(createCell(formatCurrency(loan.emi)));

            // Status badge
            const statusCell = document.createElement("td");
            const statusBadge = document.createElement("span");

            const status = loan.status || "UNKNOWN";

            statusBadge.className =
                `status-badge ${getStatusClass(status)}`;

            statusBadge.textContent = status.replaceAll("_", " ");

            statusCell.appendChild(statusBadge);
            row.appendChild(statusCell);

            // Remarks
            row.appendChild(createCell(loan.remark || "-"));

            tableBody.appendChild(row);
        });
    }


    // Fetch logged-in borrower's loans
    async function loadLoans() {

        try {

            const response = await fetch("/api/loans");

            if (!response.ok) {
                throw new Error("Unable to load your loan applications.");
            }

            const loans = await response.json();

            updateSummary(loans);
            displayLoans(loans);

        } catch (error) {

            messageElement.textContent = error.message;
            messageElement.className = "loan-message error";

            tableBody.innerHTML = `
                <tr>
                    <td colspan="7" class="empty-table-message">
                        Unable to load loan applications.
                    </td>
                </tr>
            `;

        }
    }

    // Load loans when page opens
    loadLoans();

});