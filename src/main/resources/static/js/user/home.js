// User home page functionality
document.addEventListener('DOMContentLoaded', function() {
    // Add any user home specific JavaScript functionality here
    console.log('User home page loaded');
    
    // Example: Add click tracking for quick actions
    const quickActionButtons = document.querySelectorAll('.btn[href*="/user/"]');
    quickActionButtons.forEach(button => {
        button.addEventListener('click', function() {
            console.log('Quick action clicked:', this.href);
        });
    });
});