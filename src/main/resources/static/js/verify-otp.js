/**
 * Verify OTP Page JavaScript
 * Handles OTP input functionality
 */

document.addEventListener('DOMContentLoaded', function() {
    const inputs = document.querySelectorAll('.otp-input');
    const hiddenInput = document.getElementById('hiddenOtp');
    
    inputs.forEach((input, index) => {
        input.addEventListener('input', function(e) {
            const value = e.target.value;
            if (!/^[0-9]$/.test(value)) {
                e.target.value = '';
                return;
            }
            
            e.target.classList.add('filled');
            
            if (index < inputs.length - 1) {
                inputs[index + 1].focus();
            }
            
            updateHiddenInput();
        });
        
        input.addEventListener('keydown', function(e) {
            if (e.key === 'Backspace' && !e.target.value && index > 0) {
                inputs[index - 1].focus();
                inputs[index - 1].value = '';
                inputs[index - 1].classList.remove('filled');
                updateHiddenInput();
            } else if (e.key === 'Backspace' && e.target.value) {
                e.target.classList.remove('filled');
            }
        });
        
        input.addEventListener('paste', function(e) {
            e.preventDefault();
            const paste = e.clipboardData.getData('text');
            if (/^[0-9]{6}$/.test(paste)) {
                for (let i = 0; i < 6; i++) {
                    inputs[i].value = paste[i];
                    inputs[i].classList.add('filled');
                }
                updateHiddenInput();
            }
        });
    });
    
    function updateHiddenInput() {
        const otp = Array.from(inputs).map(input => input.value).join('');
        hiddenInput.value = otp;
    }
});