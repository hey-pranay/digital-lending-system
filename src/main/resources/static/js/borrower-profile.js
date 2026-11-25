
document.addEventListener("DOMContentLoaded", () => {

    const profileForm = document.getElementById("borrowerProfileForm");

    if (!profileForm) {
        return;
    }

    profileForm.addEventListener("submit", async (event) => {

        event.preventDefault();

        const messageElement =
            document.getElementById("profileMessage");

        const csrfToken =
            document.getElementById("csrfToken").value;

        const profileData = {
            phone: document.getElementById("phone").value.trim(),
            address: document.getElementById("address").value.trim(),
            monthlyIncome: Number(
                document.getElementById("monthlyIncome").value
            ),
            employmentType:
            document.getElementById("employmentType").value
        };

        try {

            const response = await fetch("/api/borrower/profile", {
                method: "POST",

                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": csrfToken
                },

                body: JSON.stringify(profileData)
            });

            const result = await response.text();

            if (!response.ok) {
                throw new Error(
                    result || "Unable to save borrower profile."
                );
            }

            messageElement.textContent =
                "Borrower profile saved successfully!";

            messageElement.className = "form-message success";

            profileForm.reset();

        } catch (error) {

            messageElement.textContent = error.message;
            messageElement.className = "form-message error";

        }

    });

});
