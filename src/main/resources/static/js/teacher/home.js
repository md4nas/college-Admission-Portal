// Update current time
function updateTime() {
    const now = new Date();
    const timeElement = document.getElementById('currentTime');
    if (timeElement) {
        timeElement.textContent = now.toLocaleString();
    }
}

// Clear teacher announcement form
function clearTeacherForm() {
    if (confirm('Are you sure you want to clear all form data?')) {
        document.getElementById('teacherAnnouncementForm').reset();
        
        // Set default values
        document.getElementById('teacherTargetAudience').value = 'STUDENT';
        document.getElementById('teacherAnnouncementType').value = 'EVENT';
        
        // Set today's date
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('teacherEventDate').value = today;
        
        // Focus on title field
        document.getElementById('teacherTitle').focus();
        
        // Show confirmation
        const alert = document.createElement('div');
        alert.className = 'alert alert-info alert-dismissible fade show mt-3';
        alert.innerHTML = `
            <i class='bx bx-info-circle me-2'></i>
            Form cleared successfully!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        document.querySelector('#announcementForm .card-body').appendChild(alert);
        
        // Auto-remove alert after 3 seconds
        setTimeout(() => {
            if (alert.parentNode) {
                alert.remove();
            }
        }, 3000);
    }
}

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    // Set today's date as default when page loads
    const today = new Date().toISOString().split('T')[0];
    const eventDateField = document.getElementById('teacherEventDate');
    if (eventDateField && !eventDateField.value) {
        eventDateField.value = today;
    }
    
    // Start time updates
    updateTime();
    setInterval(updateTime, 60000); // Update every minute
});