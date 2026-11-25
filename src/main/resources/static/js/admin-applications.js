
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
            <td>View Details</td>
        `;

        tableBody.appendChild(row);
    });
}

function updateApplicationSummary(applications) {
    document.getElementById("totalApplications").textContent =
        applications.length;

    document.getElementById("pendingApplications").textContent =
        applications.filter(app => app.status === "PENDING").length;

    document.getElementById("approvedApplications").textContent =
        applications.filter(app => app.status === "APPROVED").length;

    document.getElementById("rejectedApplications").textContent =
        applications.filter(app => app.status === "REJECTED").length;
}
