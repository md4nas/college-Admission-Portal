// Forget password page functionality
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const submitBtn = document.querySelector('button[type="submit"]');
    const emailInput = document.querySelector('input[name="email"]');

    // Form submission
    form.addEventListener('submit', function(e) {
        if (!emailInput.value) {
            e.preventDefault();
            alert('Please enter your email address.');
            return;
        }

        // Add loading state
        submitBtn.innerHTML = '<i class="bi bi-hourglass-split me-2"></i>Sending OTP...';
        submitBtn.disabled = true;
    });

    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            if (alert.parentNode) {
                alert.classList.remove('show');
                setTimeout(() => {
                    if (alert.parentNode) {
                        alert.remove();
                    }
                }, 150);
            }
        }, 5000);
    });
});