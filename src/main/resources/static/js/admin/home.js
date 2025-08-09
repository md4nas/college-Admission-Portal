function showMaintenanceMode() {
    if (confirm('Enable maintenance mode? This will temporarily disable the system for all users except admins.')) {
        alert('Maintenance mode feature - Would enable system-wide maintenance mode');
    }
}

function exportSystemData() {
    const exportType = prompt('Export type:\n1. Users\n2. Applications\n3. Payments\n4. Full System\n\nEnter number (1-4):');
    
    const types = {
        '1': 'Users',
        '2': 'Applications', 
        '3': 'Payments',
        '4': 'Full System'
    };
    
    if (types[exportType]) {
        alert(`Exporting ${types[exportType]} data...\nThis would generate a downloadable report in production.`);
    } else {
        alert('Invalid selection. Please choose 1-4.');
    }
}