/**
 * Announcements Page JavaScript
 * Handles form interactions and validation
 */

// Clear form function
function clearForm() {
    if (confirm('Are you sure you want to clear all form data?')) {
        document.getElementById('announcementForm').reset();
        
        // Set default values
        document.getElementById('targetAudience').value = 'STUDENT';
        document.getElementById('announcementType').value = 'EVENT';
        
        // Focus on title field
        document.getElementById('title').focus();
        
        // Show confirmation
        const alert = document.createElement('div');
        alert.className = 'alert alert-info alert-dismissible fade show mt-3';
        alert.innerHTML = `
            <i class='bx bx-info-circle me-2'></i>
            Form cleared successfully!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        document.querySelector('.card-body').appendChild(alert);
        
        // Auto-remove alert after 3 seconds
        setTimeout(() => {
            if (alert.parentNode) {
                alert.remove();
            }
        }, 3000);
    }
}

// Set today's date as default and form validation
document.addEventListener('DOMContentLoaded', function() {
    // Set today's date as default
    const today = new Date().toISOString().split('T')[0];
    const eventDateField = document.getElementById('eventDate');
    if (eventDateField) {
        eventDateField.value = today;
    }
    
    // Form validation
    const form = document.getElementById('announcementForm');
    if (form) {
        form.addEventListener('submit', function(e) {
            let isValid = true;
            
            // Validate title
            const title = document.getElementById('title');
            if (title.value.trim().length < 5) {
                title.classList.add('is-invalid');
                isValid = false;
            } else {
                title.classList.remove('is-invalid');
                title.classList.add('is-valid');
            }
            
            // Validate content
            const content = document.getElementById('content');
            if (content.value.trim().length < 10) {
                content.classList.add('is-invalid');
                isValid = false;
            } else {
                content.classList.remove('is-invalid');
                content.classList.add('is-valid');
            }
            
            // Validate event date
            const eventDate = document.getElementById('eventDate');
            const selectedDate = new Date(eventDate.value);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            
            if (selectedDate < today) {
                eventDate.classList.add('is-invalid');
                isValid = false;
            } else {
                eventDate.classList.remove('is-invalid');
                eventDate.classList.add('is-valid');
            }
            
            if (!isValid) {
                e.preventDefault();
                e.stopPropagation();
            }
        });
    }
    
    // Auto-expand textarea
    const textarea = document.getElementById('content');
    if (textarea) {
        textarea.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = (this.scrollHeight) + 'px';
        });
    }
});