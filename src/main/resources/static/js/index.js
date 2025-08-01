/**
 * UserAuth Index Page JavaScript
 * 
 * This JavaScript file provides interactive functionality for the index page:
 * - Smooth scrolling navigation for anchor links
 * - Scroll-based navbar styling changes
 * - Intersection Observer for entrance animations
 * - Newsletter subscription form handling
 * - Enhanced user experience with visual feedback
 * 
 * Dependencies: None (Vanilla JavaScript)
 * Browser Support: Modern browsers (ES6+)
 * 
 * @author UserAuth Team
 * @version 1.0.0
 */

// Smooth scrolling for anchor links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Add scroll effect to navbar
window.addEventListener('scroll', function() {
    const navbar = document.querySelector('.navbar');
    if (navbar) {
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    }
});

// Initialize animations on page load
document.addEventListener('DOMContentLoaded', function() {
    // Add entrance animations to elements
    const animatedElements = document.querySelectorAll('.animate-fade-in, .animate-slide-in');
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    });

    animatedElements.forEach(el => {
        observer.observe(el);
    });
});

// Newsletter subscription (placeholder functionality)
document.addEventListener('DOMContentLoaded', function() {
    const subscribeBtn = document.querySelector('footer .btn-primary');
    const emailInput = document.querySelector('footer input[type="email"]');
    
    if (subscribeBtn && emailInput) {
        subscribeBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const email = emailInput.value.trim();
            
            if (email && email.includes('@')) {
                // Show success message
                const originalText = subscribeBtn.innerHTML;
                subscribeBtn.innerHTML = '<i class="bi bi-check"></i>';
                subscribeBtn.classList.add('btn-success');
                subscribeBtn.classList.remove('btn-primary');
                
                setTimeout(() => {
                    subscribeBtn.innerHTML = originalText;
                    subscribeBtn.classList.remove('btn-success');
                    subscribeBtn.classList.add('btn-primary');
                    emailInput.value = '';
                }, 2000);
            } else {
                // Show error state
                emailInput.classList.add('is-invalid');
                setTimeout(() => {
                    emailInput.classList.remove('is-invalid');
                }, 2000);
            }
        });
    }
});