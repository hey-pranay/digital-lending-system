document.addEventListener("DOMContentLoaded", () => {

    const loanForm = document.getElementById("loanApplicationForm");

    loanForm.addEventListener("submit", async (event) => {

        // Prevent the browser's default form submission
        event.preventDefault();

        const amount = document.getElementById("loanAmount").value;
        const tenureMonths = document.getElementById("tenureMonths").value;

        const csrfToken = document.getElementById("csrfToken").value;

        const messageElement = document.getElementById("applicationMessage");

        // Prepare request data according to LoanRequest
        const loanData = {
            amount: Number(amount),
            tenureMonths: Number(tenureMonths)
        };

        try {

            const response = await fetch("/api/loans", {
                method: "POST",

                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": csrfToken
                },

                body: JSON.stringify(loanData)
            });

            const result = await response.json();

            if (!response.ok) {
                throw new Error(
                    result.message || "Unable to submit loan application."
                );
            }

            messageElement.textContent =
                "Loan application submitted successfully!";

            messageElement.className = "form-message success";

            loanForm.reset();

        } catch (error) {

            messageElement.textContent = error.message;
            messageElement.className = "form-message error";

        }

    });

});
