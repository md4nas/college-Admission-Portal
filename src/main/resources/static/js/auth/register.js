// Register page functionality
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const password = document.querySelector('input[name="password"]');
    const confirmPassword = document.querySelector('input[name="confirmPassword"]');
    const submitBtn = document.querySelector('button[type="submit"]');

    // Password confirmation validation
    function validatePasswords() {
        if (password.value !== confirmPassword.value) {
            confirmPassword.setCustomValidity('Passwords do not match');
            return false;
        } else {
            confirmPassword.setCustomValidity('');
            return true;
        }
    }

    // Real-time password validation
    confirmPassword.addEventListener('input', validatePasswords);
    password.addEventListener('input', validatePasswords);

    // Form submission
    form.addEventListener('submit', function(e) {
        if (!validatePasswords()) {
            e.preventDefault();
            alert('Passwords do not match!');
            return;
        }

        // Add loading state
        submitBtn.innerHTML = '<i class="bx bx-loader-alt bx-spin me-2"></i>Creating Account...';
        submitBtn.disabled = true;
    });

    // Password strength indicator
    password.addEventListener('input', function() {
        const strength = getPasswordStrength(this.value);
        showPasswordStrength(strength);
    });

    function getPasswordStrength(password) {
        let strength = 0;
        if (password.length >= 8) strength++;
        if (/[a-z]/.test(password)) strength++;
        if (/[A-Z]/.test(password)) strength++;
        if (/[0-9]/.test(password)) strength++;
        if (/[^A-Za-z0-9]/.test(password)) strength++;
        return strength;
    }

    function showPasswordStrength(strength) {
        const colors = ['#dc3545', '#fd7e14', '#ffc107', '#20c997', '#28a745'];
        const texts = ['Very Weak', 'Weak', 'Fair', 'Good', 'Strong'];
        
        let indicator = document.getElementById('password-strength');
        if (!indicator) {
            indicator = document.createElement('div');
            indicator.id = 'password-strength';
            indicator.className = 'mt-1 small';
            password.parentNode.parentNode.appendChild(indicator);
        }
        
        if (password.value.length > 0) {
            indicator.innerHTML = `Password Strength: <span style="color: ${colors[strength]}">${texts[strength]}</span>`;
        } else {
            indicator.innerHTML = '';
        }
    }
});