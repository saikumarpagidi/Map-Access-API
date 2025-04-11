document.getElementById("signupForm").addEventListener("submit", (event) => {
    event.preventDefault();

    const fields = [
        { id: "username", message: "Please enter your username." },
        { id: "institute_name", message: "Please enter your institute name." },
        { id: "department_name", message: "Please enter your department name." },
        { id: "email", message: "Please enter a valid email." },
        { id: "phone", message: "Please enter a valid mobile number." },
        { id: "password", message: "Please enter a password." },
        { id: "passwordcon", message: "Passwords do not match." },
    ];

    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    const phoneRegex = /^[0-9]{10}$/;

    let isValid = true;

    // Clear previous error messages
    document.querySelectorAll(".error-message").forEach((span) => (span.textContent = ""));

    fields.forEach(({ id, message }) => {
        const input = document.getElementById(id);
        const errorSpan = input.nextElementSibling.nextElementSibling;

        if (id === "email" && !emailRegex.test(input.value.trim())) {
            errorSpan.textContent = "Please enter a valid email.";
            isValid = false;
        } else if (id === "phone" && !phoneRegex.test(input.value.trim())) {
            errorSpan.textContent = "Please enter a valid mobile number.";
            isValid = false;
        } else if (id === "passwordcon" && input.value !== document.getElementById("password").value) {
            errorSpan.textContent = message;
            isValid = false;
        } else if (!input.value.trim()) {
            errorSpan.textContent = message;
            isValid = false;
        }

        input.addEventListener("focus", () => {
            errorSpan.textContent = "";
        });
    });

    if (isValid) {
        const data = {
            username: document.getElementById("username").value.trim(),
            institute_name: document.getElementById("institute_name").value.trim(),
            department_name: document.getElementById("department_name").value.trim(),
            email: document.getElementById("email").value.trim(),
            phone: document.getElementById("phone").value.trim(),
            password: document.getElementById("password").value.trim(),
        };

        fetch("/req/signup", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data),
        })
            .then((response) => {
                if (response.ok) {
                    alert("Signup successful!");
                    window.location.href = "/req/login";
                } else {
                    response.text().then((error) => {
                        const errorMapping = {
                            "Error: Username already exists.": "username",
                            "Error: Email already exists.": "email",
                            "Error: Phone number already exists.": "phone",
                        };

                        let isMapped = false;
                        Object.keys(errorMapping).forEach((key) => {
                            if (error.includes(key)) {
                                const field = document.getElementById(errorMapping[key]);
                                const errorSpan = field.nextElementSibling.nextElementSibling;
                                errorSpan.textContent = key.replace("Error: ", "");
                                isMapped = true;
                            }
                        });

                        if (!isMapped) {
                            alert("An unexpected error occurred: " + error);
                        }
                    });
                }
            })
            .catch((error) => {
                console.error("Error:", error);
                alert("Unable to process your request. Please try again later.");
            });
    }
});
