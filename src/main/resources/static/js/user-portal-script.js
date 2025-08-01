document.addEventListener("DOMContentLoaded", () => {
    // Enhanced message animation
    const message = document.querySelector(".text-success");
    if (message) {
        message.style.opacity = "0";
        message.style.transform = "translateY(20px)";
        message.style.transition = "all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94)";

        setTimeout(() => {
            message.style.opacity = "1";
            message.style.transform = "translateY(0)";
        }, 100);

        // Auto-dismiss after 5 seconds
        setTimeout(() => {
            message.style.opacity = "0";
            setTimeout(() => {
                message.style.display = "none";
            }, 600);
        }, 5000);
    }

    // Add smooth transitions for all cards
    const cards = document.querySelectorAll(".card");
    cards.forEach(card => {
        card.style.transform = "translateY(20px)";
        card.style.opacity = "0";
        card.style.transition = "all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94)";

        setTimeout(() => {
            card.style.transform = "translateY(0)";
            card.style.opacity = "1";
        }, 150);
    });

    // Add hover effects to buttons
    const buttons = document.querySelectorAll(".btn");
    buttons.forEach(button => {
        button.style.transition = "all 0.3s ease";
        button.addEventListener("mouseenter", () => {
            button.style.transform = "translateY(-2px)";
            button.style.boxShadow = "0 5px 15px rgba(0, 0, 0, 0.1)";
        });
        button.addEventListener("mouseleave", () => {
            button.style.transform = "translateY(0)";
            button.style.boxShadow = "none";
        });
    });

    // Add subtle animation to form inputs
    const inputs = document.querySelectorAll(".form-control");
    inputs.forEach(input => {
        input.style.transition = "all 0.3s ease";
        input.addEventListener("focus", () => {
            input.parentElement.style.transform = "scale(1.02)";
        });
        input.addEventListener("blur", () => {
            input.parentElement.style.transform = "scale(1)";
        });
    });

    // Real-time password confirmation validation
    const password = document.querySelector('input[name="password"]');
    const confirmPassword = document.querySelector('input[name="confirmPassword"]');
    
    if (password && confirmPassword) {
        function validatePasswords() {
            if (password.value && confirmPassword.value) {
                if (password.value === confirmPassword.value) {
                    confirmPassword.setCustomValidity('');
                    confirmPassword.classList.remove('is-invalid');
                    confirmPassword.classList.add('is-valid');
                } else {
                    confirmPassword.setCustomValidity('Passwords do not match');
                    confirmPassword.classList.remove('is-valid');
                    confirmPassword.classList.add('is-invalid');
                }
            }
        }
        
        password.addEventListener('input', validatePasswords);
        confirmPassword.addEventListener('input', validatePasswords);
    }
});