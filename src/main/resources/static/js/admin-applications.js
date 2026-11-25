
document.addEventListener("DOMContentLoaded", () => {
    loadApplications();
});

async function loadApplications() {
    const tableBody = document.getElementById("applicationsTableBody");

    try {
        const response = await fetch("/api/admin/loans");

        if (!response.ok) {
            throw new Error("Failed to load loan applications");
        }

        const applications = await response.json();

        displayApplications(applications);
        updateApplicationSummary(applications);

    } catch (error) {
        console.error("Error loading applications:", error);

        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-table-message">
                    Unable to load applications. Please try again.
                </td>
            </tr>
        `;
    }
}


function displayApplications(applications) {
    const tableBody = document.getElementById("applicationsTableBody");

    tableBody.innerHTML = "";

    if (applications.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-table-message">
                    No loan applications found.
                </td>
            </tr>
        `;
        return;
    }

    applications.forEach(application => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>#${application.loanId}</td>
            <td>${application.borrowerName}</td>
            <td>₹${Number(application.loanAmount).toLocaleString("en-IN")}</td>
            <td>${application.tenureMonths} months</td>
            <td>${application.status}</td>
            <td>
                ${
            application.status === "APPLIED" ||
            application.status === "PENDING"
                ? `    
                    <div class="application-actions">
                        <button type="button"
                            class="action-btn approve-btn"
                            onclick="approveLoan(${application.loanId})">
                            ✓ Approve
                        </button>
                    
                        <button type="button"
                            class="action-btn reject-btn"
                            onclick="rejectLoan(${application.loanId})">
                            ✕ Reject
                        </button>
                    </div>
                    `
                : "No action"
        }
            </td>
        `;

        tableBody.appendChild(row);
    });
}

function updateApplicationSummary(applications) {
    document.getElementById("totalApplications").textContent =
        applications.length;

    document.getElementById("pendingApplications").textContent =
        applications.filter(app =>
            app.status === "APPLIED" || app.status === "PENDING"
        ).length;

    document.getElementById("approvedApplications").textContent =
        applications.filter(app => app.status === "APPROVED").length;

    document.getElementById("rejectedApplications").textContent =
        applications.filter(app => app.status === "REJECTED").length;
}


async function approveLoan(loanId) {

    // Ask the admin for confirmation
    const confirmed = confirm(
        `Are you sure you want to approve loan application #${loanId}?`
    );

    if (!confirmed) {
        return;
    }

    try {
        // Get CSRF token
        const csrfToken = document.getElementById("csrfToken").value;

        // Call approve API
        const response = await fetch(`/api/admin/loans/${loanId}/approve`, {
            method: "PATCH",
            headers: {
                "X-CSRF-TOKEN": csrfToken
            }
        });

        if (!response.ok) {
            throw new Error("Failed to approve loan application.");
        }

        alert("Loan application approved successfully!");

        // Reload applications to show updated status
        loadApplications();

    } catch (error) {
        console.error("Error approving loan:", error);
        alert(error.message);
    }
}

async function rejectLoan(loanId) {

    // Ask admin for rejection remarks
    const remarks = prompt(
        `Enter rejection remarks for loan application #${loanId}:`
    );

    // Stop if admin cancels
    if (remarks === null) {
        return;
    }

    // Validate remarks
    if (remarks.trim() === "") {
        alert("Rejection remarks are required.");
        return;
    }

    if (remarks.trim().length > 500) {
        alert("Rejection remarks cannot exceed 500 characters.");
        return;
    }

    try {
        const csrfToken = document.getElementById("csrfToken").value;

        const response = await fetch(`/api/admin/loans/${loanId}/reject`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": csrfToken
            },
            body: JSON.stringify({
                remarks: remarks.trim()
            })
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(
                `Rejection failed (${response.status}): ${errorText}`
            );
        }

        alert("Loan application rejected successfully!");

        // Refresh the applications table
        loadApplications();

    } catch (error) {
        console.error("Error rejecting loan:", error);
        alert(error.message);
    }
}