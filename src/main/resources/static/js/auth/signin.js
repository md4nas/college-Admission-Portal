// Signin page functionality
document.addEventListener('DOMContentLoaded', function() {
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

    // Add form validation feedback
    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', function(e) {
            const email = form.querySelector('input[name="email"]');
            const password = form.querySelector('input[name="password"]');
            
            if (!email.value || !password.value) {
                e.preventDefault();
                alert('Please fill in all required fields.');
            }
        });
    }

    // Add loading state to login button
    const loginBtn = document.querySelector('button[type="submit"]');
    if (loginBtn) {
        loginBtn.addEventListener('click', function() {
            this.innerHTML = '<i class="bx bx-loader-alt bx-spin me-2"></i>Signing in...';
            this.disabled = true;
        });
    }
});