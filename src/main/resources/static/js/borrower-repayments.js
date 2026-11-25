
document.addEventListener("DOMContentLoaded", () => {

    // HTML elements
    const tableBody = document.getElementById("repaymentsTableBody");
    const messageElement = document.getElementById("repaymentMessage");

    const totalElement = document.getElementById("totalRepayments");
    const paidElement = document.getElementById("paidRepayments");
    const pendingElement = document.getElementById("pendingRepayments");
    const overdueElement = document.getElementById("overdueRepayments");

    // Fetch repayment history when the page loads.
    loadRepayments();

    async function loadRepayments() {

        try {
            const response = await fetch("/api/borrower/repayments");

            const repayments = await response.json();

            if (!response.ok) {
                throw new Error(
                    repayments.message || "Unable to load repayment history."
                );
            }

            updateSummary(repayments);
            displayRepayments(repayments);

        } catch (error) {

            messageElement.textContent = error.message;
            messageElement.className = "form-message error";

            tableBody.innerHTML = `
                <tr>
                    <td colspan="7" class="empty-table-message">
                        Unable to load repayment records.
                    </td>
                </tr>
            `;
        }
    }

    // Update repayment summary cards.
    function updateSummary(repayments) {

        totalElement.textContent = repayments.length;

        paidElement.textContent = repayments.filter(
            repayment => repayment.status === "PAID"
        ).length;

        pendingElement.textContent = repayments.filter(
            repayment => repayment.status === "PENDING"
        ).length;

        overdueElement.textContent = repayments.filter(
            repayment => repayment.status === "OVERDUE"
        ).length;
    }

    // Display repayment records in the table.
    function displayRepayments(repayments) {

        tableBody.replaceChildren();

        if (repayments.length === 0) {

            const row = document.createElement("tr");
            const cell = document.createElement("td");

            cell.colSpan = 7;
            cell.className = "empty-table-message";
            cell.textContent = "No repayment records found.";

            row.appendChild(cell);
            tableBody.appendChild(row);

            return;
        }

        repayments.forEach(repayment => {

            const row = document.createElement("tr");

            row.appendChild(createCell(repayment.repaymentId));
            row.appendChild(createCell(repayment.loanId));
            row.appendChild(createCell(formatDate(repayment.dueDate)));
            row.appendChild(createCell(formatCurrency(repayment.amount)));

            row.appendChild(createCell(
                repayment.paymentDate
                    ? formatDate(repayment.paymentDate)
                    : "-"
            ));

            // Create status badge.
            const statusCell = document.createElement("td");
            const statusBadge = document.createElement("span");

            statusBadge.className =
                `status-badge ${getStatusClass(repayment.status)}`;

            statusBadge.textContent =
                repayment.status.replaceAll("_", " ");

            statusCell.appendChild(statusBadge);
            row.appendChild(statusCell);

            // Create payment action.
            const actionCell = document.createElement("td");

            if (repayment.status === "PENDING" ||
                repayment.status === "OVERDUE") {

                const payButton = document.createElement("button");

                payButton.type = "button";
                payButton.className = "pay-btn";
                payButton.textContent = "Pay Now";
                payButton.dataset.repaymentId = repayment.repaymentId;

                actionCell.appendChild(payButton);

            } else {

                actionCell.textContent = "-";
            }

            row.appendChild(actionCell);

            tableBody.appendChild(row);
        });
    }

    // Handle Pay Now button clicks.
    tableBody.addEventListener("click", async (event) => {

        const payButton = event.target.closest(".pay-btn");

        if (!payButton) {
            return;
        }

        const repaymentId = payButton.dataset.repaymentId;

        const confirmed = confirm(
            `Are you sure you want to pay instalment #${repaymentId}?`
        );

        if (!confirmed) {
            return;
        }

        await payInstalment(repaymentId, payButton);
    });

    // Send payment request to the backend.
    async function payInstalment(repaymentId, payButton) {

        const csrfToken = document.getElementById("csrfToken")?.value;

        if (!csrfToken) {
            messageElement.textContent = "Security token not found. Please refresh the page.";
            messageElement.className = "form-message error";
            return;
        }

        try {

            payButton.disabled = true;
            payButton.textContent = "Processing...";

            const response = await fetch(
                `/api/borrower/repayments/${repaymentId}/pay`,
                {
                    method: "PATCH",
                    headers: {
                        "X-CSRF-TOKEN": csrfToken
                    }
                }
            );

            const result = await response.json();

            if (!response.ok) {
                throw new Error(
                    result.message || "Unable to process payment."
                );
            }

            // Refresh table and summary after successful payment.
            await loadRepayments();

            messageElement.textContent =
                `Instalment #${repaymentId} paid successfully (demo payment).`;

            messageElement.className = "form-message success";

        } catch (error) {

            messageElement.textContent = error.message;
            messageElement.className = "form-message error";

            payButton.disabled = false;
            payButton.textContent = "Pay Now";
        }
    }

    // Create a table cell safely.
    function createCell(value) {

        const cell = document.createElement("td");
        cell.textContent = value ?? "-";

        return cell;
    }

    // Format amount in Indian currency.
    function formatCurrency(amount) {

        return new Intl.NumberFormat("en-IN", {
            style: "currency",
            currency: "INR",
            minimumFractionDigits: 2
        }).format(amount);
    }

    // Format date.
    function formatDate(date) {

        if (!date) {
            return "-";
        }

        return new Date(date + "T00:00:00").toLocaleDateString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric"
        });
    }

    // Return CSS class based on repayment status.
    function getStatusClass(status) {

        switch (status) {
            case "PAID":
                return "status-approved";

            case "PENDING":
                return "status-pending";

            case "OVERDUE":
                return "status-rejected";

            default:
                return "status-pending";
        }
    }

});