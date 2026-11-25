document.addEventListener("DOMContentLoaded", () => {

    // Dashboard statistic elements
    const totalApplicationsElement = document.getElementById("totalApplications");

    const pendingApplicationsElement = document.getElementById("pendingApplications");

    const approvedLoansElement = document.getElementById("approvedLoans");

    const rejectedApplicationsElement = document.getElementById("rejectedApplications");

    // Fetch all loan applications when the dashboard loads.
    loadDashboardStatistics();

    async function loadDashboardStatistics() {

        try {

            const response = await fetch("/api/admin/loans");

            const applications = await response.json();

            if (!response.ok) {
                throw new Error(applications.message || "Unable to load dashboard statistics.");
            }

            updateStatistics(applications);

        } catch (error) {

            console.error("Dashboard loading error:", error);

            totalApplicationsElement.textContent = "-";
            pendingApplicationsElement.textContent = "-";
            approvedLoansElement.textContent = "-";
            rejectedApplicationsElement.textContent = "-";
        }
    }

    // Calculate statistics from the loan application records.
    function updateStatistics(applications) {

        const total = applications.length;

        const pending = applications.filter(application => application.status === "APPLIED" || application.status === "PENDING").length;

        const approved = applications.filter(application => application.status === "APPROVED").length;

        const rejected = applications.filter(application => application.status === "REJECTED").length;

        // Update dashboard cards.
        totalApplicationsElement.textContent = total;
        pendingApplicationsElement.textContent = pending;
        approvedLoansElement.textContent = approved;
        rejectedApplicationsElement.textContent = rejected;
    }


// Financial Overview elements
    const totalLoanAmountElement = document.getElementById("totalLoanAmount");

    const totalAmountRepaidElement = document.getElementById("totalAmountRepaid");

    const outstandingAmountElement = document.getElementById("outstandingAmount");

    const overdueInstalmentsElement = document.getElementById("overdueInstalments");

    // Load financial statistics.
    loadFinancialOverview();

    async function loadFinancialOverview() {

        try {

            // Fetch loans and repayment records by status.
            const [loansResponse, paidResponse, pendingResponse, overdueResponse] = await Promise.all([

                fetch("/api/admin/loans"),

                fetch("/api/admin/repayments/status/PAID"),

                fetch("/api/admin/repayments/status/PENDING"),

                fetch("/api/admin/repayments/status/OVERDUE")]);

            const responses = [loansResponse, paidResponse, pendingResponse, overdueResponse];

            // Check whether all API requests succeeded.
            if (responses.some(response => !response.ok)) {
                throw new Error("Unable to load financial statistics.");
            }

            const loans = await loansResponse.json();
            const paidRepayments = await paidResponse.json();
            const pendingRepayments = await pendingResponse.json();
            const overdueRepayments = await overdueResponse.json();

            // Calculate total amount of approved loans.
            const totalLoanAmount = loans
                .filter(loan => loan.status === "APPROVED")
                .reduce((total, loan) => total + Number(loan.loanAmount || 0), 0);

            // Calculate total amount already repaid.
            const totalAmountRepaid = paidRepayments
                .reduce((total, repayment) => total + Number(repayment.amount || 0), 0);

            // Calculate outstanding repayment amount.
            const outstandingAmount = [...pendingRepayments, ...overdueRepayments]
                .reduce((total, repayment) => total + Number(repayment.amount || 0), 0);

            // Update financial cards.
            totalLoanAmountElement.textContent = formatCurrency(totalLoanAmount);

            totalAmountRepaidElement.textContent = formatCurrency(totalAmountRepaid);

            outstandingAmountElement.textContent = formatCurrency(outstandingAmount);

            overdueInstalmentsElement.textContent = overdueRepayments.length;

        } catch (error) {

            console.error("Financial overview error:", error);

            totalLoanAmountElement.textContent = "-";
            totalAmountRepaidElement.textContent = "-";
            outstandingAmountElement.textContent = "-";
            overdueInstalmentsElement.textContent = "-";
        }
    }

    // Format values using Indian currency.
    function formatCurrency(amount) {

        return new Intl.NumberFormat("en-IN", {
            style: "currency", currency: "INR", minimumFractionDigits: 2
        }).format(amount);
    }


});