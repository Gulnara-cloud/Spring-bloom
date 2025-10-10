// ===== Basic Client-Side Validation for Register Form =====

// Wait until the page is fully loaded
document.addEventListener("DOMContentLoaded", function () {
  const form = document.querySelector("form");
  const username = document.getElementById("username");
  const email = document.getElementById("email");
  const password = document.getElementById("password");

  form.addEventListener("submit", function (event) {
    // Remove previous error highlights
    [username, email, password].forEach(input => input.classList.remove("is-invalid"));

    let valid = true;

    // Check username
    if (username.value.trim() === "") {
      showError(username, "Username cannot be empty");
      valid = false;
    }

    // Check email (basic regex for @ symbol)
    if (!email.value.includes("@")) {
      showError(email, "Please enter a valid email address");
      valid = false;
    }

    // Check password length
    if (password.value.length < 6) {
      showError(password, "Password must be at least 6 characters long");
      valid = false;
    }

    // Stop submission if invalid
    if (!valid) {
      event.preventDefault();
    }
  });

  // Helper: show red border + message
  function showError(input, message) {
    input.classList.add("is-invalid");
    input.placeholder = message;
  }
});
