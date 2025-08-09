/**
 * Verify OTP Page JavaScript
 * Handles single OTP input functionality
 */

document.addEventListener('DOMContentLoaded', function() {
    const otpInput = document.querySelector('.otp-input');
    const hiddenInput = document.getElementById('hiddenOtp');
    
    otpInput.addEventListener('input', function(e) {
        const value = e.target.value;
        // Only allow numeric input and max 6 digits
        const numericValue = value.replace(/[^0-9]/g, '').substring(0, 6);
        e.target.value = numericValue;
        
        // Update hidden input
        hiddenInput.value = numericValue;
        
        // Add visual feedback
        if (numericValue.length === 6) {
            e.target.classList.add('filled');
        } else {
            e.target.classList.remove('filled');
        }
    });
    
    // Handle paste event
    otpInput.addEventListener('paste', function(e) {
        e.preventDefault();
        const paste = e.clipboardData.getData('text');
        const numericPaste = paste.replace(/[^0-9]/g, '').substring(0, 6);
        e.target.value = numericPaste;
        hiddenInput.value = numericPaste;
        
        if (numericPaste.length === 6) {
            e.target.classList.add('filled');
        }
    });
});