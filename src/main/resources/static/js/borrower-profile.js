
document.addEventListener("DOMContentLoaded", () => {

    const profileForm = document.getElementById("borrowerProfileForm");

    if (!profileForm) {
        return;
    }

    const editButton = document.getElementById("editProfileButton");
    const submitButton = document.getElementById("profileSubmitButton");

    const title = document.getElementById("profileFormTitle");
    const description = document.getElementById("profileFormDescription");

    const messageElement = document.getElementById("profileMessage");
    const csrfToken = document.getElementById("csrfToken").value;

    let profileExists = false;

    // Get all form fields
    const phone = document.getElementById("phone");
    const address = document.getElementById("address");
    const monthlyIncome = document.getElementById("monthlyIncome");
    const employmentType = document.getElementById("employmentType");

    // Initially disable fields until profile status is checked
    function setFormDisabled(disabled) {
        phone.disabled = disabled;
        address.disabled = disabled;
        monthlyIncome.disabled = disabled;
        employmentType.disabled = disabled;
    }

    // Load existing borrower profile
    async function loadProfile() {

        try {

            const response = await fetch("/api/borrower/profile");

            if (response.status === 404) {

                profileExists = false;

                title.textContent = "Complete Your Borrower Profile";
                description.textContent =
                    "Provide these details before applying for a loan.";

                submitButton.textContent = "Save Profile";

                editButton.style.display = "none";

                setFormDisabled(false);

                return;
            }

            if (!response.ok) {
                throw new Error("Unable to load borrower profile.");
            }

            const profile = await response.json();

            // Populate form fields
            phone.value = profile.phone;
            address.value = profile.address;
            monthlyIncome.value = profile.monthlyIncome;
            employmentType.value = profile.employmentType;

            profileExists = true;

            title.textContent = "Your Borrower Profile";
            description.textContent =
                "Your profile information is saved.";

            submitButton.textContent = "Update Profile";

            editButton.style.display = "inline-block";

            // Keep fields disabled until Edit is clicked
            setFormDisabled(true);

        } catch (error) {

            messageElement.textContent = error.message;
            messageElement.className = "form-message error";

        }
    }

    // Enable editing
    editButton.addEventListener("click", () => {

        setFormDisabled(false);

        submitButton.textContent = "Update Profile";

        editButton.style.display = "none";

        description.textContent =
            "Update your borrower profile information.";

        messageElement.textContent = "";
        messageElement.className = "form-message";

    });

    // Submit create or update request
    profileForm.addEventListener("submit", async (event) => {

        event.preventDefault();

        const profileData = {
            phone: phone.value.trim(),
            address: address.value.trim(),
            monthlyIncome: Number(monthlyIncome.value),
            employmentType: employmentType.value
        };

        try {

            const response = await fetch("/api/borrower/profile", {

                method: profileExists ? "PUT" : "POST",

                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": csrfToken
                },

                body: JSON.stringify(profileData)

            });

            const result = await response.text();

            if (!response.ok) {
                throw new Error(result || "Unable to save borrower profile.");
            }

            messageElement.textContent = profileExists
                ? "Borrower profile updated successfully!"
                : "Borrower profile created successfully!";

            messageElement.className = "form-message success";

            // After successful creation or update, reload profile
            await loadProfile();

        } catch (error) {

            messageElement.textContent = error.message;
            messageElement.className = "form-message error";

        }

    });

    // Load profile when page opens
    loadProfile();

});