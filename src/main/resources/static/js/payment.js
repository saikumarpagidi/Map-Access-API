document.addEventListener("DOMContentLoaded", () => {
  const planSelect = document.getElementById("plan");
  const amountField = document.getElementById("amount");

  // Update amount field based on selected plan
  planSelect.addEventListener("change", () => {
    const selectedOption = planSelect.options[planSelect.selectedIndex];
    const amount = selectedOption.getAttribute("data-amount");

    if (amount) {
      amountField.value = `₹${amount}`; // Update amount field dynamically
    } else {
      amountField.value = "₹0"; // Default value if no valid option is selected
    }
  });
});

document.getElementById("paymentForm").addEventListener("submit", async (event) => {
  event.preventDefault();

  const username = document.getElementById("username").value.trim();
  const email = document.getElementById("email").value.trim();
  const address = document.getElementById("address").value.trim();
  const plan = document.getElementById("plan").value;
  const amount = document.getElementById("amount").value.replace(/[^0-9]/g, "").trim();

  if (!username || !email || !plan || !amount || !address) {
    alert("Please fill all the required fields.");
    return;
  }

  try {
    const response = await fetch("http://localhost:8080/api/payment/create", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, email, address, plan, amount }),
    });

    if (response.status === 403) {
      alert("You need to register before making a payment. Redirecting to the registration page...");
      window.location.href = "/req/signup"; // URL of your registration page
      return;
    }

    if (!response.ok) {
      const errorMessage = await response.text();
      throw new Error(errorMessage || "Failed to create payment order.");
    }

    const orderDetails = await response.json();

    const options = {
      key: "rzp_test_s6cs686HmZNPL4", // Replace with your Razorpay Key ID
      amount: orderDetails.amount, // Amount in paise
      currency: orderDetails.currency,
      name: "Subscription Payment",
      description: `Plan: ${plan}`,
      order_id: orderDetails.id, // Razorpay order ID
      handler: async (response) => {
        const successResponse = await fetch("http://localhost:8080/api/payment/success", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(response),
        });

        if (successResponse.ok) {
          alert("Payment successful and your account is now active!");
          window.location.href = "/req/login"; // URL of your login page
        } else {
          alert("Payment verification failed. Please contact support.");
        }
      },
      prefill: {
        name: username,
        email: email,
      },
      theme: {
        color: "#3399cc",
      },
    };

    const razorpay = new Razorpay(options);
    razorpay.open();
  } catch (error) {
    console.error("Error:", error);
    alert("Error: " + error.message);
  }
});
