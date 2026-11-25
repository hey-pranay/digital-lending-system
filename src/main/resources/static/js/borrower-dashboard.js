
document.addEventListener("DOMContentLoaded", () => {
    loadDashboard();
    loadRecentActivities();
});
async function loadDashboard() {

    try {
        const response = await fetch("/api/loans/dashboard");

        if (!response.ok) {
            throw new Error("Unable to load dashboard data.");
        }

        const dashboard = await response.json();

        document.getElementById("totalLoanAmount").textContent =
            formatCurrency(dashboard.totalLoanAmount);

        document.getElementById("outstandingBalance").textContent =
            formatCurrency(dashboard.outstandingBalance);

        document.getElementById("monthlyEmi").textContent =
            formatCurrency(dashboard.monthlyEmi);

    } catch (error) {
        console.error("Dashboard error:", error);
    }
}

function formatCurrency(amount) {

    return "₹" + Number(amount || 0).toLocaleString("en-IN", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}


async function loadRecentActivities() {

    const container = document.getElementById("recentActivityContainer");

    try {

        const response = await fetch("/api/loans/activities");

        if (!response.ok) {
            throw new Error("Unable to load recent activities.");
        }

        const activities = await response.json();

        if (activities.length === 0) {
            container.innerHTML = `
                <div class="activity-empty">
                    <p>No recent activity to display.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = activities.map(activity => {

            const isRepayment = activity.activityType === "REPAYMENT";

            const icon = isRepayment ? "credit-card" : "file-text";

            const date = new Date(activity.activityDate).toLocaleString("en-IN", {
                day: "2-digit",
                month: "short",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit"
            });

            return `
                <div class="activity-item">

                    <div class="activity-icon">
                        <span>${isRepayment ? "₹" : "📄"}</span>
                    </div>

                    <div class="activity-details">
                        <h3>${activity.description}</h3>
                        <p>Loan #${activity.loanId} · ${date}</p>
                    </div>

                    <div class="activity-info">
                        <strong>${formatCurrency(activity.amount)}</strong>
                        <span class="activity-status status-${activity.status.toLowerCase()}">
                            ${activity.status}
                        </span>
                    </div>

                </div>
            `;

        }).join("");

    } catch (error) {

        console.error("Recent activity error:", error);

        container.innerHTML = `
            <div class="activity-empty">
                <p>Unable to load recent activities. Please try again.</p>
            </div>
        `;
    }
}